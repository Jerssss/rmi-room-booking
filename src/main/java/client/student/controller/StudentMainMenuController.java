package client.student.controller;

import client.ClientMain;
import client.student.model.StudentMainMenuModel;
import client.student.model.ViewReservationModel;
import client.student.view.StudentMainMenuView;
import client.student.view.ViewReservationView;
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
    private String studentID;

    public StudentMainMenuController(StudentMainMenuView view, StudentMainMenuModel model, String loggedInUserName, String studentID) {




        this.view = view;
        this.model = model;
        this.loggedInUserName = loggedInUserName;
        this.view.setActionLogoutButton(this::handleLogout);
        this.view.setActionCreateReservationButton(event -> handleCreateReservation());
        this.view.setActionViewReservationButton(this::handleViewReservation);
        this.view.setActionModifyReservationButton(event -> handleModifyReservation());
        this.view.setLoggedInUserName(loggedInUserName);
        this.view.initializeDateTime();
    }




    private void handleCreateReservation() {
        System.out.println("Navigating to Create Reservations...");
    }

    private void handleViewReservation(ActionEvent event) {
        System.out.println("[DEBUG] Navigating to View Student Reservations...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/view_reservation_pane.fxml"));
            Parent root = loader.load();

            //  Ensure View is loaded
            ViewReservationView reservationView = loader.getController();
            if (reservationView == null) {
                System.err.println("[ERROR] ViewReservationView is NULL after FXML load!");
                return;
            }
            System.out.println("[DEBUG] ViewReservationView successfully loaded.");

            // Create MVC Components
            ViewReservationModel reservationModel = new ViewReservationModel(studentID);
            ViewReservationController reservationController = new ViewReservationController(reservationView, reservationModel);

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
