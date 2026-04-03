package com.example.autclient.controllers;

import com.example.autclient.services.AuthSession;
import com.example.autclient.services.AuthService;
import com.example.autclient.services.AuthServiceException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

/**
 * Contrôleur de la vue de changement de mot de passe.
 */
public class ChangePasswordController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();
    private AuthSession session;

    /**
     * Injecte la session courante et pré-remplit l'email.
     *
     * @param session session d'authentification courante
     */
    public void initData(AuthSession session) {
        this.session = session;
        if (emailField != null && session != null) {
            emailField.setText(session.email());
        }
    }

    @FXML
    private void handleChangePassword() {
        String email = emailField.getText().trim();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Les nouveaux mots de passe ne correspondent pas.");
            return;
        }

        try {
            String response = authService.changePassword(email, oldPassword, newPassword, confirmPassword);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(response);
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } catch (AuthServiceException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Échec du changement de mot de passe : " + e.getMessage());
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur réseau ou technique lors du changement de mot de passe.");
        }
    }

    @FXML
    private void handleBackToProfile() {
        if (session == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Session introuvable.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/profile-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 500, 680);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/com/example/autclient/styles.css").toExternalForm());
            ProfileController controller = loader.getController();
            controller.initData(session);
            stage.setScene(scene);
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur de navigation vers le profil.");
        }
    }
}

