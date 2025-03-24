package client.admin.controller;

import client.ClientMain;
import client.admin.model.AddAdminModel;
import client.admin.model.AddNewTerminalModel;
import client.admin.model.AdminMainMenuModel;
import client.admin.model.ViewStudentReservationsModel;
import client.admin.view.AddAdminView;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The `AdminMainMenuController` class is responsible for handling the actions and logic
 * of the Admin Main Menu. It manages navigation to different admin functionalities,
 * such as adding terminals, viewing student reservations, and handling logout.
 */
public class AdminMainMenuController {
    private final AdminMainMenuView view;
    private final AdminMainMenuModel model;
    private final String loggedInUserName;
    private Thread serverThread;

    private static final File LOGS_JSON_FILE = new File("src/main/resources/data/logs.json");

    /**
     * Constructs an `AdminMainMenuController` with the specified view, model, and logged-in username.
     * Initializes the view with the logged-in user's name, sets up date and time display,
     * and configures button actions.
     *
     * @param view             The `AdminMainMenuView` instance associated with this controller.
     * @param model            The `AdminMainMenuModel` instance associated with this controller.
     * @param loggedInUserName The username of the currently logged-in admin.
     */
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
//        this.view.setActionCreateAdminButton(event -> handleCreateAdmin());

    }

//    private void handleCreateAdmin() {
//        System.out.println("=====================================================");
//
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/add_new_admin.fxml"));
//            VBox createAdminView = loader.load();
//
//            AddAdminView view = loader.getController();
//            if (view == null) {
//                System.err.println("[ERROR] AddNewTerminalView is NULL after FXML load!");
//                return;
//            }
//
//            // Inject Controller
//            new AddAdminController(view, new AddAdminModel());
//
//            // Set new view in the center pane
//            view.getRootPane().setCenter(createAdminView);
//        } catch (IOException e) {
//            System.err.println("[ERROR] Failed to load Add New Terminal: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    /**
     * Handles the action for adding a new terminal. Loads the Add New Terminal view
     * and sets up the corresponding controller.
     *
     * @param event The event triggered by clicking the "Add New Terminal" button.
     */
    private void handleAddNewTerminal(ActionEvent event) {
        System.out.println("=====================================================");

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
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Add New Terminal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action for viewing student reservations. Loads the Student Reservations view
     * and sets up the corresponding controller.
     *
     * @param event The event triggered by clicking the "View Student Reservations" button.
     */
    private void handleViewStudentReservation(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/student_reservations_pane.fxml"));
            Parent root = loader.load();

            //  Ensure View is loaded
            ViewStudentReservationsView reservationsView = loader.getController();
            if (reservationsView == null) {
                System.err.println("[ERROR] ViewStudentReservationsView is NULL after FXML load!");
                return;
            }
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

    /**
     * Handles the action for modifying terminal status. Currently prints a placeholder message.
     */
    private void handleModifyTerminal() {
        System.out.println("Navigating to Modify Terminal Status...");
    }

    /**
     * Handles the action for generating reports. Currently prints a placeholder message.
     */
    private void handleReports() {
        System.out.println("Navigating to Reports...");
    }

    /**
     * Handles the action for reservation approval. Currently prints a placeholder message.
     */
    private void handleReservationApproval() {
        System.out.println("Navigating to Reservation Approval");
    }

    /**
     * Handles the logout action. Logs the logout event and navigates back to the login page.
     *
     * @param event The event triggered by clicking the "Logout" button.
     */
    private void handleLogout(ActionEvent event) {
        if (loggedInUserName != null) {
            logLogoutToJson(loggedInUserName, "Admin");
        }

        try {
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
     * Logs a logout action into the logs.json file.
     *
     * @param userID   The ID of the user logging out.
     * @param userType The type of the user (e.g., "Admin").
     */
    private void logLogoutToJson(String userID, String userType) {
        List<Log> logs = JSONUtility.loadLogs(LOGS_JSON_FILE);

        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        logs.add(new Log(userID, userType, "Logout", date, time));

        JSONUtility.saveLogs(logs, LOGS_JSON_FILE);
        System.out.println("=====================================================");
        System.out.println("[LOGOUT] Successfully logged out: " + userID);
        System.out.println("=====================================================");
    }

}
