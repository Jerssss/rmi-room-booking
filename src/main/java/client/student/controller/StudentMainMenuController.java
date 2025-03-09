package client.student.controller;

import client.ClientMain;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
import client.student.model.StudentMainMenuModel;
import client.student.view.StudentMainMenuView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class StudentMainMenuController {
    private final StudentMainMenuView view;
    private final StudentMainMenuModel model;
    private final String loggedInUserName;

    public StudentMainMenuController(StudentMainMenuView view, StudentMainMenuModel model, String loggedInUserName) {




        this.view = view;
        this.model = model;
        this.loggedInUserName = loggedInUserName;
        this.view.setActionLogoutButton(this::handleLogout);
        this.view.setActionCreateReservationButton(event -> handleCreateReservation());
        this.view.setActionViewReservationButton(event -> handleViewReservation());
        this.view.setActionModifyReservationButton(event -> handleModifyReservation());
        this.view.setLoggedInUserName(loggedInUserName);
        this.view.initializeDateTime();
    }




    private void handleCreateReservation() {
        System.out.println("Navigating to Create Reservations...");
    }

    private void handleViewReservation() {
        System.out.println("Navigating to View Reservations...");
    }

    private void handleModifyReservation() {
        System.out.println("Navigating to Modify Reservations");
    }

    /** Handles Logout and logs the action. */
    private void handleLogout(ActionEvent event) {
        if (loggedInUserName != null) {
            try {
                System.out.println("=====================================================");
                System.out.println("[CLIENT] Requesting logout for student: " + loggedInUserName);
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
