package client.admin.controller;

import client.ClientMain;
import client.admin.model.AddNewTerminalModel;
import client.admin.model.AdminMainMenuModel;
import client.admin.model.ViewStudentReservationsModel;
import client.admin.view.AddNewTerminalView;
import client.admin.view.AdminMainMenuView;
import client.admin.view.ViewStudentReservationsView;

import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.Log;
import util.JSONUtility;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


/**
 * Controls the Admin Main Menu actions.
 */
public class AdminMainMenuController {
    private final AdminMainMenuView view;
    private final AdminMainMenuModel model;
    private final String loggedInUserName;
    private Thread serverThread;

    private static final File LOGS_JSON_FILE = new File("src/main/resources/data/logs.json");

    public AdminMainMenuController(AdminMainMenuView view, AdminMainMenuModel model, String loggedInUserName) {
        this.view = view;
        this.model = model;
        this.loggedInUserName = loggedInUserName;

        this.view.setLoggedInUserName(loggedInUserName);
        this.view.initializeDateTime();
        this.view.setActionLogoutButton(this::handleLogout);

        this.view.setActionAddNewTerminalButton(this::handleAddNewTerminal);
        this.view.setActionModifyTerminalButton(event -> handleModifyTerminal());
        this.view.setActionShowStudentReservationButton(this::handleViewStudentReservation);
        this.view.setActionResApprovalButton(event -> handleReservationApproval());
        this.view.setActionReportsButton(event -> handleReports());

    }


    private void handleAddNewTerminal(ActionEvent event) {
        System.out.println("[DEBUG] Navigating to Add New Terminal...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/add_terminal_pane.fxml"));
            VBox addTerminalView = loader.load();

            AddNewTerminalView view = loader.getController();
            if (view == null) {
                System.err.println("[ERROR] AddNewTerminalView is NULL after FXML load!");
                return;
            }

            // Inject Controller
            new AddNewTerminalController(view, new AddNewTerminalModel());

            // Set new view in the center pane
            view.getRootPane().setCenter(addTerminalView);
            System.out.println("[DEBUG] Successfully loaded Add New Terminal view.");

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Add New Terminal: " + e.getMessage());
            e.printStackTrace();
        }
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
            logLogoutToJson(loggedInUserName, "Admin");
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

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Logs a logout action into logs.json.
     */
    private void logLogoutToJson(String userID, String userType) {
        List<Log> logs = JSONUtility.loadLogs(LOGS_JSON_FILE);

        String date = LocalDate.now().toString();
        String time = LocalTime.now().toString();

        logs.add(new Log(userID, userType, "Logout", date, time));

        JSONUtility.saveLogs(logs, LOGS_JSON_FILE);
        System.out.println("=====================================================");
        System.out.println("[LOGOUT] Successfully logged out: " + userID);
        System.out.println("=====================================================");
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
