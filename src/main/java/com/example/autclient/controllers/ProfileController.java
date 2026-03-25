package com.example.autclient.controllers;

import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


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

    private final AuthService authService = new AuthService();

    /**
     * Initialise la vue de profil avec le token d'accès et charge les informations utilisateur.
     * @param token accessToken JWT reçu après authentification
     */
    public void initData(String token) {
        try {
            String response = authService.getProfile(token);
            welcomeLabel.setText(response);
        } catch (Exception e) {
            welcomeLabel.setText("Erreur lors du chargement du profil.");
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
            stage.setScene(new Scene(loader.load(), 400, 500));
        } catch (Exception e) {
            welcomeLabel.setText("Erreur de navigation.");
        }
    }
}