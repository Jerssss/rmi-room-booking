package client.admin.controller;

import client.admin.model.AdminMainMenuModel;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.ServerMain;
import server.utility.LogsXMLHandler;

import java.io.IOException;

public class AdminMainMenuController {
    private final AdminMainMenuView view;
    private final AdminMainMenuModel model;
    private final String loggedInUserName;
    private Thread serverThread;

    public AdminMainMenuController(AdminMainMenuView view, AdminMainMenuModel model, String loggedInUserName) {
        this.view = view;
        this.model = model;
        this.loggedInUserName = loggedInUserName;

        this.view.setLoggedInUserName(loggedInUserName);
        this.view.initializeDateTime();

        this.view.setActionAddNewTerminalButton(event -> handleAddNewTerminal());
        this.view.setActionModifyTerminalButton(event -> handleModifyTerminal());
        this.view.setActionShowStudentReservationButton(event -> handleViewStudentReservation());
        this.view.setActionResApprovalButton(event -> handleReservationApproval());
        this.view.setActionReportsButton(event -> handleReports());
        this.view.setActionToggleButton(event -> handleServerToggleButton());
        this.view.setActionLogoutButton(this::handleLogout);
    }

    /** Handles Server Start/Stop */
    public void handleServerToggleButton() {
        if (view.isServerToggleSelected()) {
            view.setToggleText("STOP");

            if (serverThread == null || !serverThread.isAlive()) {
                serverThread = new Thread(() -> {
                    //  ServerMain.startServer();
                });
                serverThread.setDaemon(true);
                serverThread.start();
                System.out.println("Server Started");
            }
        } else {
            view.setToggleText("START");
            // ServerMain.stopServer();
            System.out.println("Server Stopped");
        }
    }

    private void handleAddNewTerminal() {
        System.out.println("Navigating to Add New Terminal...");
    }

    private void handleViewStudentReservation() {
        System.out.println("Navigating to View Student Reservations...");
    }

    private void handleModifyTerminal() {
        System.out.println("Navigating to Modify Terminal Status...");
    }

    private void handleReports() {
        System.out.println("Navigating to Reports...");
    }

    private void handleReservationApproval() {
        System.out.println("Navigating to Reservation Approval");
    }

    /** Handles Logout and Log to `logs.xml` */
    private void handleLogout(ActionEvent event) {
        if (loggedInUserName != null) {
            LogsXMLHandler.logLogout(loggedInUserName, "Admin");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            new LoginController(loginView, new LoginModel(), new AdminMainMenuView());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading login page: " + e.getMessage());
        }
    }
}
