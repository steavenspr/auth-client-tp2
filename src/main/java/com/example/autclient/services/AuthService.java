package com.example.autclient.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service d'authentification côté client.
 * <p>
 * Gère les appels HTTP vers le backend pour l'inscription, la connexion forte (HMAC) et la récupération du profil utilisateur.
 * Implémente le protocole d'authentification forte du TP3 côté client.
 * <p>
 * Limite pédagogique : le mot de passe est transmis uniquement lors de l'inscription, jamais lors de la connexion.
 */
public class AuthService {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final String baseUrl;

    public AuthService() {
        this(HttpClient.newHttpClient(), DEFAULT_BASE_URL);
    }

    AuthService(HttpClient client, String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    /**
     * Inscrit un nouvel utilisateur auprès du backend.
     * @param email email de l'utilisateur
     * @param password mot de passe en clair (sera chiffré côté serveur)
     * @return "success" si inscription OK
     * @throws java.io.IOException erreur réseau
     * @throws java.lang.InterruptedException interruption
     * @throws AuthServiceException si l'inscription échoue (email déjà utilisé, etc.)
     */
    public String register(String email, String password) throws IOException, InterruptedException, AuthServiceException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/register?email=" + email + "&password=" + password))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return "success";
        }
        throw new AuthServiceException(response.body());
    }

    /**
     * Authentifie l'utilisateur via le protocole fort (HMAC, nonce, timestamp).
     * @param email email utilisateur
     * @param nonce nonce unique (UUID)
     * @param timestamp epoch secondes
     * @param hmac HMAC-SHA256 calculé côté client
     * @return accessToken JWT si authentification OK
     * @throws java.io.IOException erreur réseau
     * @throws java.lang.InterruptedException interruption
     * @throws AuthServiceException si l'authentification échoue
     */
    public AuthSession login(String email, String nonce, long timestamp, String hmac) throws IOException, InterruptedException, AuthServiceException {
        String jsonPayload = String.format(
                "{\"email\":\"%s\",\"nonce\":\"%s\",\"timestamp\":%d,\"hmac\":\"%s\"}",
                email, nonce, timestamp, hmac);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseLoginResponse(response.body(), email, nonce, timestamp);
        }
        throw new AuthServiceException(response.body());
    }

    static AuthSession parseLoginResponse(String body, String email, String nonce, long timestamp)
            throws AuthServiceException {
        String accessToken = extractJsonStringField(body, "accessToken");
        String expiresAt = extractJsonStringField(body, "expiresAt");
        if (accessToken == null || expiresAt == null) {
            throw new AuthServiceException("Réponse de connexion invalide.");
        }
        return new AuthSession(email, nonce, timestamp, accessToken, expiresAt);
    }

    /**
     * Change le mot de passe d'un utilisateur.
     * @param email email de l'utilisateur
     * @param oldPassword ancien mot de passe
     * @param newPassword nouveau mot de passe
     * @param confirmPassword confirmation du nouveau mot de passe
     * @return message de succès du backend
     * @throws IOException erreur réseau
     * @throws InterruptedException interruption
     * @throws AuthServiceException si le backend refuse le changement
     */
    public String changePassword(String email, String oldPassword, String newPassword, String confirmPassword)
            throws IOException, InterruptedException, AuthServiceException {
        String jsonPayload = String.format(
                "{\"email\":\"%s\",\"oldPassword\":\"%s\",\"newPassword\":\"%s\",\"confirmPassword\":\"%s\"}",
                email, oldPassword, newPassword, confirmPassword);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/change-password"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        throw new AuthServiceException(response.body());
    }

    /**
     * Récupère les informations du profil utilisateur via le token d'accès.
     * @param token accessToken JWT
     * @return informations du profil (JSON ou texte)
     * @throws java.io.IOException erreur réseau
     * @throws java.lang.InterruptedException interruption
     * @throws AuthServiceException si le token est invalide ou expiré
     */
    public String getProfile(String token) throws IOException, InterruptedException, AuthServiceException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/me?token=" + token))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        throw new AuthServiceException(response.body());
    }

    static String extractJsonStringField(String body, String fieldName) {
        Matcher matcher = Pattern.compile(String.format("\\\"%s\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"", Pattern.quote(fieldName))).matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }
}