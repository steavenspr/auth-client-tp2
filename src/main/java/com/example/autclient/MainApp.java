package com.example.autclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/autclient/views/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 520);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(getClass().getResource(
                "/com/example/autclient/styles.css").toExternalForm());
        stage.setTitle("Auth Client - TP2");
        stage.setScene(scene);
        stage.show();
    }
}