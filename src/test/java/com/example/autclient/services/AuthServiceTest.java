package com.example.autclient.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

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
}

