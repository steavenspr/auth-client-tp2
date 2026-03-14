package com.example.autclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/autclient/views/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 500);
        stage.setTitle("Auth Client - TP2");
        stage.setScene(scene);
        stage.show();
    }
}