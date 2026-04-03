package com.example.autclient.controllers;

import com.example.autclient.services.AuthSession;
import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * Contrôleur de la vue de profil utilisateur.
 * <p>
 * Affiche les informations du profil après authentification forte.
 * Utilise le token d'accès JWT pour récupérer les données utilisateur auprès du backend.
 * Permet la déconnexion et le retour à l'écran de login.
 * </p>
 */
public class ProfileController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label nonceLabel;
    @FXML
    private Label timestampLabel;
    @FXML
    private Label expiresAtLabel;
    @FXML
    private Label tokenLabel;
    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();
    private AuthSession session;

    /**
     * Initialise la vue de profil avec le token d'accès et charge les informations utilisateur.
     * @param token accessToken JWT reçu après authentification
     */
    public void initData(AuthSession session) {
        this.session = session;
        try {
            String response = authService.getProfile(session.accessToken());
            welcomeLabel.setText(response);
            emailLabel.setText(session.email());
            nonceLabel.setText(session.nonce());
            timestampLabel.setText(formatTimestamp(session.timestamp()));
            expiresAtLabel.setText(session.expiresAt());
            tokenLabel.setText(session.accessToken());
            if (messageLabel != null) {
                messageLabel.setText("Connexion vérifiée avec succès.");
            }
        } catch (Exception e) {
            welcomeLabel.setText("Erreur lors du chargement du profil.");
            if (messageLabel != null) {
                messageLabel.setText("Impossible de charger les preuves de connexion.");
            }
        }
    }

    private String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(timestamp));
    }

    @FXML
    private void handleChangePassword() {
        if (session == null) {
            if (messageLabel != null) {
                messageLabel.setText("Session introuvable.");
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/autclient/views/change-password-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 500, 620);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/com/example/autclient/styles.css").toExternalForm());
            ChangePasswordController controller = loader.getController();
            controller.initData(session);
            stage.setScene(scene);
        } catch (Exception e) {
            if (messageLabel != null) {
                messageLabel.setText("Erreur de navigation vers le changement de mot de passe.");
            }
        }
    }

    /**
     * Gère la déconnexion et la navigation vers la vue de login.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/autclient/views/login-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 400, 520);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/com/example/autclient/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            welcomeLabel.setText("Erreur de navigation.");
        }
    }
}