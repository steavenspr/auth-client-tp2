package com.example.autclient.controllers;

import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordConfirmField;
    @FXML private ProgressBar strengthBar;
    @FXML private Label strengthLabel;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateStrengthIndicator(newVal);
        });
    }

    private void updateStrengthIndicator(String password) {
        if (password.isEmpty()) {
            strengthBar.setProgress(0);
            strengthBar.setStyle("");
            strengthLabel.setText("Force du mot de passe : —");
            strengthLabel.setStyle("-fx-font-size: 12px;");
            return;
        }

        int score = 0;
        if (password.length() >= 12) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0)) score++;

        if (score <= 2) {
            strengthBar.setProgress(0.33);
            strengthBar.setStyle("-fx-accent: red;");
            strengthLabel.setText("Force du mot de passe : Faible");
            strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
        } else if (score <= 3) {
            strengthBar.setProgress(0.66);
            strengthBar.setStyle("-fx-accent: orange;");
            strengthLabel.setText("Force du mot de passe : Moyen");
            strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: orange;");
        } else {
            strengthBar.setProgress(1.0);
            strengthBar.setStyle("-fx-accent: green;");
            strengthLabel.setText("Force du mot de passe : Fort");
            strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
        }
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String passwordConfirm = passwordConfirmField.getText();

        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            authService.register(email, password);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Inscription réussie ! Vous pouvez vous connecter.");
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Échec de l'inscription. Email déjà utilisé ou mot de passe invalide.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/login-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 580));
        } catch (Exception e) {
            messageLabel.setText("Erreur de navigation.");
        }
    }
}