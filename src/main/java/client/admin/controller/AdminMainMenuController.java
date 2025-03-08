package client.admin.controller;

import client.ClientMain;
import client.admin.model.AdminMainMenuModel;
import client.admin.view.AdminMainMenuView;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.utility.LogsXMLHandler;

import java.io.IOException;

/**
 * Controls the Admin Main Menu actions.
 */
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

        this.view.setActionAddNewTerminalButton(event -> handleAddNewTerminal(event));
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

    private void handleAddNewTerminal(ActionEvent event) {
        System.out.println("[DEBUG] Navigating to Add New Terminal...");

        switchScene(event, "/fxml/admin/add_terminal.fxml", "Add New Terminal");
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

    /** Handles Logout and logs the action. */
    private void handleLogout(ActionEvent event) {
        if (loggedInUserName != null) {
            LogsXMLHandler.logLogout(loggedInUserName, "Admin");
        }

        try {
            System.out.println("[DEBUG] Logging out and loading Login Page...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            if (loginView == null) {
                System.err.println("[ERROR] LoginView is NULL! Check FXML file.");
                return;
            }

            // Get authentication service
            LoginModel loginModel = new LoginModel(ClientMain.getAuthService());
            new LoginController(loginView, loginModel); // Removed AdminMainMenuView

            Platform.runLater(() -> {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (stage == null) {
                    System.err.println("[ERROR] Stage is NULL! Cannot change scene.");
                    return;
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
                System.out.println("[DEBUG] Successfully switched to Login Page!");
            });

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            System.out.println("[DEBUG] Loading Scene: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("[ERROR] FXML file path is incorrect: " + fxmlPath);
                return;
            }

            Parent root = loader.load();
            System.out.println("[DEBUG] Successfully loaded FXML: " + fxmlPath);

            Platform.runLater(() -> {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (stage == null) {
                    System.err.println("[ERROR] Stage is NULL! Cannot change scene.");
                    return;
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(title);
                stage.centerOnScreen();
                stage.show();
                System.out.println("[DEBUG] Scene changed successfully to " + title);
            });

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load " + fxmlPath);
            e.printStackTrace();
        }
    }


}
