module com.example.autclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;

    requires java.net.http;
    requires jdk.httpserver;

    opens com.example.autclient to javafx.fxml;
    opens com.example.autclient.controllers to javafx.fxml;
    exports com.example.autclient;
}