module com.example.autclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires java.net.http;

    opens com.example.autclient to javafx.fxml;
    opens com.example.autclient.controllers to javafx.fxml;
    exports com.example.autclient;
}