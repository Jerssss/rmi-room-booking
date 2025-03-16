module Lendify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires com.google.gson;
    requires javafx.graphics;
    requires java.desktop;

    exports client;
    exports client.utility;
    exports shared;
    exports shared.interfaces;

    // Export the shared.callback package to java.rmi
    exports shared.callback to java.rmi;

    opens shared to com.google.gson;
    opens client.landingpage to javafx.fxml;
    opens client.signup to javafx.fxml;
    opens client.login to javafx.fxml;
    opens client to javafx.fxml;
    opens client.admin.view to javafx.fxml;
    opens client.admin.controller to javafx.fxml;
    opens client.student.view to javafx.fxml;
    opens client.student.controller to javafx.fxml;
    exports shared.interfaces.admin;
    exports shared.interfaces.student;
}