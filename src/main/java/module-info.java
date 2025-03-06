module Lendify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.logging;
    requires java.xml;
    requires java.desktop;
    requires java.rmi;

    exports client;
    exports client.utility;
    exports shared;

    opens shared to java.rmi;
    opens client.landingpage to javafx.fxml;
    opens client.signup to javafx.fxml;
    opens client.login to javafx.fxml;
    opens client to javafx.fxml;
    opens client.admin.view to javafx.fxml;
    opens client.admin.controller to javafx.fxml;
    opens server.utility to javafx.base;
}
