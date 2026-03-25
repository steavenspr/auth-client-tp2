package com.example.autclient.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Service d'authentification côté client.
 * <p>
 * Gère les appels HTTP vers le backend pour l'inscription, la connexion forte (HMAC) et la récupération du profil utilisateur.
 * Implémente le protocole d'authentification forte du TP3 côté client.
 * <p>
 * Limite pédagogique : le mot de passe est transmis uniquement lors de l'inscription, jamais lors de la connexion.
 */
public class AuthService {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Inscrit un nouvel utilisateur auprès du backend.
     * @param email email de l'utilisateur
     * @param password mot de passe en clair (sera chiffré côté serveur)
     * @return "success" si inscription OK
     * @throws java.io.IOException erreur réseau
     * @throws java.lang.InterruptedException interruption
     * @throws AuthServiceException si l'inscription échoue (email déjà utilisé, etc.)
     */
    public String register(String email, String password) throws java.io.IOException, java.lang.InterruptedException, AuthServiceException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/register?email=" + email + "&password=" + password))
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
    public String login(String email, String nonce, long timestamp, String hmac) throws java.io.IOException, java.lang.InterruptedException, AuthServiceException {
        String jsonPayload = String.format(
                "{\"email\":\"%s\",\"nonce\":\"%s\",\"timestamp\":%d,\"hmac\":\"%s\"}",
                email, nonce, timestamp, hmac);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            // On suppose que la réponse contient le token dans le champ accessToken (JSON)
            String body = response.body();
            // Extraire le token du JSON
            int idx = body.indexOf("\"accessToken\":");
            if (idx != -1) {
                int start = body.indexOf('"', idx + 14) + 1;
                int end = body.indexOf('"', start);
                if (start > 0 && end > start) {
                    return body.substring(start, end);
                }
            }
            return body;
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
    public String getProfile(String token) throws java.io.IOException, java.lang.InterruptedException, AuthServiceException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/me?token=" + token))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        throw new AuthServiceException(response.body());
    }
}