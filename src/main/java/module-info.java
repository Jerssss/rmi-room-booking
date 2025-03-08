module Lendify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.desktop;
    requires java.rmi;
    requires com.google.gson;

    exports client;
    exports client.utility;
    exports shared;
    exports shared.interfaces;

    opens shared to com.google.gson;
    opens client.landingpage to javafx.fxml;
    opens client.signup to javafx.fxml;
    opens client.login to javafx.fxml;
    opens client to javafx.fxml;
    opens client.admin.view to javafx.fxml;
    opens client.admin.controller to javafx.fxml;
    opens server.utility to javafx.base;
}
