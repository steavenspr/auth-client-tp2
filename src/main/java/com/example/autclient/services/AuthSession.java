package com.example.autclient.services;

/**
 * Représente la session obtenue après authentification forte.
 *
 * @param email email de l'utilisateur
 * @param nonce nonce utilisé pour la preuve HMAC
 * @param timestamp horodatage de la requête de connexion
 * @param accessToken jeton d'accès renvoyé par le backend
 * @param expiresAt date d'expiration du token renvoyée par le backend
 */
public record AuthSession(
        String email,
        String nonce,
        long timestamp,
        String accessToken,
        String expiresAt) {
}

