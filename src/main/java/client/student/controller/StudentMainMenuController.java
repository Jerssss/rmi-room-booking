package client.student.controller;

import client.ClientMain;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
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
import shared.Log;
import util.JSONUtility;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for the student main menu of the application.
 * Handles navigation to create, view, and modify reservations, as well as logout functionality.
 */
public class StudentMainMenuController {
    private final StudentMainMenuView view; // The associated view for the student main menu
    private final StudentMainMenuModel model; // The associated model for the student main menu
    private final String loggedInUserName; // The name of the logged-in user
    private String studentID; // The ID of the logged-in student

    /**
     * Constructs a StudentMainMenuController and initializes button handlers.
     *
     * @param view the StudentMainMenuView instance to associate with this controller
     * @param model the StudentMainMenuModel instance to associate with this controller
     * @param loggedInUserName the name of the logged-in user
     * @param studentID the ID of the logged-in student
     */
    public StudentMainMenuController(StudentMainMenuView view, StudentMainMenuModel model, String loggedInUserName, String studentID) {
        this.view = view;
        this.model = model;
        this.loggedInUserName = loggedInUserName;
        this.studentID = studentID;

        this.view.setActionLogoutButton(this::handleLogout);
        this.view.setActionCreateReservationButton(event -> handleCreateReservation());
        this.view.setActionViewReservationButton(this::handleViewReservation);
        this.view.setActionModifyReservationButton(event -> handleModifyReservation());
        this.view.setLoggedInUserName(loggedInUserName);
        this.view.initializeDateTime();
    }

    /**
     * Handles the "Create Reservation" button click event.
     * (Currently a placeholder for future implementation.)
     */
    private void handleCreateReservation() {
        System.out.println("Navigating to Create Reservations...");
    }

    /**
     * Handles the "View Reservation" button click event.
     * Loads the view reservation page and switches the scene to it.
     *
     * @param event the ActionEvent triggered by the button click
     */
    private void handleViewReservation(ActionEvent event) {
        System.out.println("[CLIENT] Navigating to View Student Reservations...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/view_reservation_pane.fxml"));
            Parent root = loader.load();

            // Ensure View is loaded
            ViewReservationView reservationView = loader.getController();
            if (reservationView == null) {
                System.err.println("[ERROR] ViewReservationView is NULL after FXML load!");
                return;
            }
            System.out.println("[CLIENT] ViewReservationView successfully loaded.");

            // Create MVC Components
            ViewReservationModel reservationModel = new ViewReservationModel(studentID);
            ViewReservationController reservationController = new ViewReservationController(reservationView, reservationModel, studentID);

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

    /**
     * Handles the "Modify Reservation" button click event.
     * (Currently a placeholder for future implementation.)
     */
    private void handleModifyReservation() {
        System.out.println("Navigating to Modify Reservations");
    }

    /**
     * Handles the "Logout" button click event.
     * Logs out the user and redirects to the login page.
     *
     * @param event the ActionEvent triggered by the button click
     */
    private void handleLogout(ActionEvent event) {
        try {
            if (ClientMain.getAuthService() != null) {
                ClientMain.getAuthService().logout(loggedInUserName); // Logs out on the server
            }

            System.out.println("[CLIENT] Logging out and loading Login Page...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            if (loginView == null) {
                System.err.println("[ERROR] LoginView is NULL! Check FXML file.");
                return;
            }

            // Get authentication service
            LoginModel loginModel = new LoginModel(ClientMain.getAuthService());
            new LoginController(loginView, loginModel);

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
            });

        } catch (RemoteException e) {
            System.err.println("[ERROR] Failed to communicate with AuthenticationService: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}