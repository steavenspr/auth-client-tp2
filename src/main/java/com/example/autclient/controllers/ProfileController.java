package com.example.autclient.controllers;

import com.example.autclient.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ProfileController {

    @FXML private Label welcomeLabel;

    private final AuthService authService = new AuthService();
    private String currentToken;

    public void initData(String token) {
        this.currentToken = token;
        try {
            String response = authService.getProfile(token);
            welcomeLabel.setText(response);
        } catch (Exception e) {
            welcomeLabel.setText("Erreur lors du chargement du profil.");
        }
    }

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