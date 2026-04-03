package com.example.autclient.controllers;

import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

/**
 * Contrôleur de la vue d'inscription.
 * <p>
 * Permet à l'utilisateur de créer un compte en saisissant un email et un mot de passe.
 * Affiche un indicateur de force du mot de passe pour encourager l'utilisation de mots de passe robustes.
 * Appelle le service AuthService pour l'inscription côté backend.
 */

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordConfirmField;
    @FXML private HBox strengthBar;
    @FXML private Label strengthLabel;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    /**
     * Initialise l'écouteur sur le champ mot de passe pour mettre à jour l'indicateur de force.
     */
    @FXML
    public void initialize() {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> updateStrengthIndicator(newVal));
    }

    /**
     * Met à jour l'indicateur visuel de force du mot de passe.
     * @param password mot de passe saisi par l'utilisateur
     */
    private void updateStrengthIndicator(String password) {
        if (password.isEmpty()) {
            strengthBar.setStyle("-fx-background-color: #dee2e6; -fx-background-radius: 4;");
            strengthBar.setPrefWidth(0);
            strengthLabel.setText("Force du mot de passe : —");
            strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
            return;
        }

        int score = 0;
        if (password.length() >= 12) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0)) score++;

        boolean longEnough = password.length() >= 12;

        if (!longEnough || score <= 2) {
            strengthBar.setStyle("-fx-background-color: red; -fx-background-radius: 4;");
            strengthBar.setPrefWidth(100);
            strengthLabel.setText("Force du mot de passe : Faible");
            strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
        } else if (score <= 3) {
            strengthBar.setStyle("-fx-background-color: orange; -fx-background-radius: 4;");
            strengthBar.setPrefWidth(200);
            strengthLabel.setText("Force du mot de passe : Moyen");
            strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: orange;");
        } else {
            strengthBar.setStyle("-fx-background-color: green; -fx-background-radius: 4;");
            strengthBar.setPrefWidth(300);
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Operation interrompue, veuillez reessayer.");
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
            Scene scene = new Scene(loader.load(), 400, 520);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/com/example/autclient/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            messageLabel.setText("Erreur de navigation.");
        }
    }
}