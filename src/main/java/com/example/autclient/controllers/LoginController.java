package com.example.autclient.controllers;

import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

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
        } catch (Exception e) {
            messageLabel.setText("Échec de la connexion. Vérifiez vos identifiants.");
        }
    }

    // Utilitaire pour calculer le HMAC-SHA256
    public String calculateHmacSHA256(String secret, String data) throws Exception {
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

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/register-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 500));
        } catch (Exception e) {
            messageLabel.setText("Erreur de navigation.");
        }
    }

    private void goToProfile(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/profile-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 400, 500);

            ProfileController profileController = loader.getController();
            profileController.initData(token);

            stage.setScene(scene);
        } catch (Exception e) {
            messageLabel.setText("Erreur de navigation.");
        }
    }
}