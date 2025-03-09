package client.admin.controller;

import client.ClientMain;
import client.admin.model.AdminMainMenuModel;
import client.admin.model.ViewStudentReservationsModel;
import client.admin.view.AdminMainMenuView;
import client.admin.view.ViewStudentReservationsView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;


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
        this.view.setActionLogoutButton(this::handleLogout);

        this.view.setActionAddNewTerminalButton(event -> handleAddNewTerminal(event));
        this.view.setActionModifyTerminalButton(event -> handleModifyTerminal());
        this.view.setActionShowStudentReservationButton(this::handleViewStudentReservation);
        this.view.setActionResApprovalButton(event -> handleReservationApproval());
        this.view.setActionReportsButton(event -> handleReports());
        this.view.setActionToggleButton(event -> handleServerToggleButton());

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


    private void handleViewStudentReservation(ActionEvent event) {
        System.out.println("[DEBUG] Navigating to View Student Reservations...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/student_reservations_pane.fxml"));
            Parent root = loader.load();

            //  Ensure View is loaded
            ViewStudentReservationsView reservationsView = loader.getController();
            if (reservationsView == null) {
                System.err.println("[ERROR] ViewStudentReservationsView is NULL after FXML load!");
                return;
            }
            System.out.println("[DEBUG] ViewStudentReservationsView successfully loaded.");

            // Create MVC Components
            ViewStudentReservationsModel reservationsModel = new ViewStudentReservationsModel();
            ViewStudentReservationsController reservationsController = new ViewStudentReservationsController(reservationsView, reservationsModel);

            // Switch Scene
            Platform.runLater(() -> {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Student Reservations");
                stage.centerOnScreen();
                stage.show();
            });

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Student Reservations Page: " + e.getMessage());
            e.printStackTrace();
        }
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
            try {
                System.out.println("=====================================================");
                System.out.println("[CLIENT] Requesting logout for admin: " + loggedInUserName);
                System.out.println("=====================================================");

                // Call logout on the server
                ClientMain.getAuthService().logout(loggedInUserName);

                System.out.println("[CLIENT] Successfully logged out from server.");
            } catch (RemoteException e) {
                System.err.println("[ERROR] Logout failed: " + e.getMessage());
            }
        } else {
            System.err.println("[ERROR] No logged-in user found!");
        }

        // Switch back to login screen
        switchScene(event, "/fxml/client/login_page.fxml", "Login Page");
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
