package com.example.autclient.controllers;

import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Contrôleur de la vue de connexion.
 * <p>
 * Implémente le protocole d'authentification forte du TP3 : le mot de passe n'est jamais transmis au serveur.
 * Le client prouve la connaissance du secret (mot de passe) en calculant un HMAC-SHA256 sur le message
 * "email:nonce:timestamp" avec le mot de passe comme clé. Le payload envoyé au serveur contient uniquement
 * l'email, le nonce, le timestamp et le HMAC. Ce protocole protège contre l'interception, le rejeu et la fuite du mot de passe.
 * <p>
 * Limite pédagogique : le mot de passe est utilisé comme clé HMAC côté client, et le serveur le stocke chiffré de façon réversible.
 * En production, on préférerait un hash non réversible et un protocole éprouvé (ex : SRP, OPAQUE).
 */

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    /**
     * Gère la tentative de connexion de l'utilisateur.
     * <ul>
     *   <li>Vérifie les champs</li>
     *   <li>Génère un nonce (UUID) et un timestamp (epoch secondes)</li>
     *   <li>Construit le message à signer et calcule le HMAC-SHA256 avec le mot de passe comme clé</li>
     *   <li>Envoie la requête d'authentification au backend</li>
     *   <li>Affiche le token ou un message d'erreur</li>
     * </ul>
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // Générer un nonce unique (UUID)
        String nonce = java.util.UUID.randomUUID().toString();
        // Timestamp en secondes depuis epoch
        long timestamp = System.currentTimeMillis() / 1000L;

        try {
            // Calculer le HMAC-SHA256 sur la concaténation email:nonce:timestamp
            String data = email + ":" + nonce + ":" + timestamp;
            String hmac = calculateHmacSHA256(password, data); // Utilise le mot de passe comme clé HMAC côté client
            String token = authService.login(email, nonce, timestamp, hmac);
            goToProfile(token);
        } catch (java.security.NoSuchAlgorithmException | java.security.InvalidKeyException e) {
            messageLabel.setText("Erreur technique lors du calcul HMAC.");
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            if (e instanceof java.lang.InterruptedException) {
                Thread.currentThread().interrupt();
            }
            messageLabel.setText("Erreur réseau ou interruption.");
        } catch (com.example.autclient.services.AuthServiceException e) {
            messageLabel.setText("Erreur d'authentification : " + e.getMessage());
        } catch (RuntimeException e) {
            messageLabel.setText("Erreur inattendue lors de la connexion.");
        }
    }

    /**
     * Calcule le HMAC-SHA256 d'une chaîne de caractères avec une clé secrète.
     *
     * @param secret clé secrète (ici, le mot de passe utilisateur)
     * @param data   message à signer (format : email:nonce:timestamp)
     * @return HMAC hexadécimal (en minuscules)
     * @throws java.security.NoSuchAlgorithmException si l'algorithme HmacSHA256 n'est pas disponible
     * @throws java.security.InvalidKeyException      si la clé est invalide
     */
    public String calculateHmacSHA256(String secret, String data) throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hmacBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Navigue vers la vue d'inscription.
     */
    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/register-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 500));
        } catch (java.io.IOException e) {
            messageLabel.setText("Erreur de navigation.");
        }
    }

    /**
     * Navigue vers la vue de profil après connexion réussie et transmet le token d'accès.
     * @param token accessToken JWT reçu du backend après authentification forte
     */
    private void goToProfile(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/profile-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 400, 500);
            ProfileController profileController = loader.getController();
            profileController.initData(token);
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            messageLabel.setText("Erreur de navigation.");
        }
    }
}

