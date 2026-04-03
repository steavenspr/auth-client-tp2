package com.example.autclient.services;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @FunctionalInterface
    private interface ServerConsumer {
        void accept(HttpServer server) throws Exception;
    }

    @Test
    void extractJsonStringField_shouldReturnValueWhenPresent() {
        String body = "{\"accessToken\":\"abc.def.ghi\",\"expiresAt\":\"2026-04-03T12:34:56\"}";

        String token = AuthService.extractJsonStringField(body, "accessToken");
        String expiresAt = AuthService.extractJsonStringField(body, "expiresAt");

        assertEquals("abc.def.ghi", token);
        assertEquals("2026-04-03T12:34:56", expiresAt);
    }

    @Test
    void parseLoginResponse_shouldBuildAuthSession() throws AuthServiceException {
        String body = "{\"accessToken\":\"token-123\",\"expiresAt\":\"2026-04-03T12:34:56\"}";

        AuthSession session = AuthService.parseLoginResponse(
                body,
                "user@example.com",
                "nonce-123",
                1712140000L);

        assertEquals("user@example.com", session.email());
        assertEquals("nonce-123", session.nonce());
        assertEquals(1712140000L, session.timestamp());
        assertEquals("token-123", session.accessToken());
        assertEquals("2026-04-03T12:34:56", session.expiresAt());
    }

    @Test
    void parseLoginResponse_shouldFailWhenFieldsAreMissing() {
        String body = "{\"token\":\"oops\"}";

        AuthServiceException exception = assertThrows(AuthServiceException.class,
                () -> AuthService.parseLoginResponse(body, "user@example.com", "nonce", 1L));

        assertTrue(exception.getMessage().contains("Réponse de connexion invalide"));
    }

    @Test
    void register_shouldReturnSuccessOn200() throws Exception {
        withServer("/api/auth/register", "POST", 200, "ok", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            assertEquals("success", service.register("user@example.com", "pwd1234"));
        });
    }

    @Test
    void register_shouldThrowOnNon200() throws Exception {
        withServer("/api/auth/register", "POST", 409, "email already used", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            AuthServiceException ex = assertThrows(AuthServiceException.class,
                    () -> service.register("user@example.com", "pwd1234"));
            assertTrue(ex.getMessage().contains("email already used"));
        });
    }

    @Test
    void login_shouldReturnSessionOn200() throws Exception {
        String response = "{\"accessToken\":\"token-xyz\",\"expiresAt\":\"2026-04-03T12:34:56\"}";
        withServer("/api/auth/login", "POST", 200, response, server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            AuthSession session = service.login("user@example.com", "nonce-1", 1712140000L, "hmac");
            assertEquals("token-xyz", session.accessToken());
            assertEquals("2026-04-03T12:34:56", session.expiresAt());
        });
    }

    @Test
    void login_shouldThrowWhenResponseIsInvalid() throws Exception {
        withServer("/api/auth/login", "POST", 200, "{\"token\":\"bad\"}", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            AuthServiceException ex = assertThrows(AuthServiceException.class,
                    () -> service.login("user@example.com", "nonce-1", 1712140000L, "hmac"));
            assertTrue(ex.getMessage().contains("Réponse de connexion invalide"));
        });
    }

    @Test
    void changePassword_shouldReturnMessageOn200() throws Exception {
        withServer("/api/auth/change-password", "PUT", 200, "Password changed successfully", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            String response = service.changePassword("user@example.com", "old", "new", "new");
            assertEquals("Password changed successfully", response);
        });
    }

    @Test
    void changePassword_shouldThrowOnNon200() throws Exception {
        withServer("/api/auth/change-password", "PUT", 400, "invalid password", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            AuthServiceException ex = assertThrows(AuthServiceException.class,
                    () -> service.changePassword("user@example.com", "old", "new", "new"));
            assertTrue(ex.getMessage().contains("invalid password"));
        });
    }

    @Test
    void getProfile_shouldReturnBodyOn200() throws Exception {
        withServer("/api/me", "GET", 200, "Bienvenue user@example.com", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            String response = service.getProfile("token-abc");
            assertEquals("Bienvenue user@example.com", response);
        });
    }

    @Test
    void getProfile_shouldThrowOnNon200() throws Exception {
        withServer("/api/me", "GET", 401, "invalid token", server -> {
            AuthService service = new AuthService(HttpClient.newHttpClient(), baseUrl(server));
            AuthServiceException ex = assertThrows(AuthServiceException.class,
                    () -> service.getProfile("token-abc"));
            assertTrue(ex.getMessage().contains("invalid token"));
        });
    }

    private static void withServer(String path, String method, int status, String responseBody, ServerConsumer consumer)
            throws Exception {
        HttpServer server = startServer(path, method, status, responseBody);
        try {
            consumer.accept(server);
        } finally {
            server.stop(0);
        }
    }

    private static HttpServer startServer(String path, String method, int status, String responseBody) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext(path, exchange -> {
            assertEquals(method, exchange.getRequestMethod());
            byte[] response = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        server.start();
        return server;
    }

    private static String baseUrl(HttpServer server) {
        return "http://localhost:" + server.getAddress().getPort() + "/api";
    }
}

