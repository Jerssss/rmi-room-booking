// File: client/admin/view/AdminMainMenuView.java
package client.admin.view;

import client.ClientMain;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller class for the Admin Main Menu view.
 * Handles navigation between different admin functionalities,
 * server status monitoring, and UI interactions.
 */
public class AdminMainMenuView {

    @FXML
    private Button addNewTerminalButton;

    @FXML
    private VBox centerPane;

    @FXML
    private Button createAdminButton;

    @FXML
    private Label headerDateLabel;

    @FXML
    private Label headerNameLabel;

    @FXML
    private Label headerTimeLabel;

    @FXML
    private Button logOutButton;

    @FXML
    private Button modifyTerminalButton;

    @FXML
    private Label offlineLabel;

    @FXML
    private Label onlineLabel;

    @FXML
    private Button reportsButton;

    @FXML
    private Button resApprovalButton;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button showStudentReservationButton;
    private Button currentlyHighlightedButton;


    // Timer to periodically check server status
    private Timer serverStatusTimer;

    /**
     * Initializes the view controller.
     * Sets up date/time display and starts server status monitoring.
     */
    public void initialize() {
        initializeDateTime();
        startServerStatusChecker();
    }

    /**
     * Starts a timer to periodically check server connection status.
     * Updates UI labels based on server availability.
     */
    private void startServerStatusChecker() {
        serverStatusTimer = new Timer(true);
        serverStatusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean isServerOnline = checkServerStatus();
                updateServerStatusLabels(isServerOnline);
            }
        }, 0, 5000); // Check every 5 seconds
    }

    /**
     * Checks if the server is reachable via RMI.
     * @return true if server is online, false otherwise
     */
    private boolean checkServerStatus() {
        try {
            Registry registry = LocateRegistry.getRegistry(ClientMain.getServerIP(), 1099);
            registry.lookup("authentication"); // Try to look-up a service
            return true; // Server is online
        } catch (RemoteException | NotBoundException e) {
            return false; // Server is offline
        }
    }

    /**
     * Updates the server status indicators in the UI.
     * @param isServerOnline Current server connection status
     */
    private void updateServerStatusLabels(boolean isServerOnline) {
        javafx.application.Platform.runLater(() -> {
            if (isServerOnline) {
                onlineLabel.setVisible(true);
                onlineLabel.setManaged(true); // Include in layout
                offlineLabel.setVisible(false);
                offlineLabel.setManaged(false); // Exclude from layout
            } else {
                onlineLabel.setVisible(false);
                onlineLabel.setManaged(false); // Exclude from layout
                offlineLabel.setVisible(true);
                offlineLabel.setManaged(true); // Include in layout
            }
        });
    }

    /**
     * Loads a new view into the main content area.
     * @param fxmlFile Path to the FXML file to load
     */
    private void loadView(String fxmlFile) {
        try {
            System.out.println("[SERVER] Loading FXML: " + fxmlFile);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            if (fxmlLoader.getLocation() == null) {
                throw new IllegalStateException("FXML file not found: " + fxmlFile);
            }
            VBox view = fxmlLoader.load();
            rootPane.setCenter(view);
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            showError("Failed to load view: " + fxmlFile);
        }
    }


    /**
     * Sets the name of the logged-in admin in the header.
     * @param name The admin's name to display
     */
    public void setLoggedInUserName(String name) {
        headerNameLabel.setText(name);
    }

    /**
     * Initializes and starts updating the date/time display.
     */
    public void initializeDateTime() {
        updateDateTime();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDateTime();
            }
        }, 0, 1000);
    }

    /**
     * Updates the date and time display labels.
     */
    private void updateDateTime() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        javafx.application.Platform.runLater(() -> {
            headerDateLabel.setText(currentDate.format(dateFormatter));
            headerTimeLabel.setText(currentTime.format(timeFormatter));
        });
    }

    /**
     * Sets action handler for Add Terminal button.
     * @param event The event handler to set
     */
    public void setActionAddNewTerminalButton(EventHandler<ActionEvent> event) {
        addNewTerminalButton.setOnAction(event1 -> {
            highlightButton(addNewTerminalButton); //highlight the button when clicked
            loadView("/fxml/admin/add_terminal_pane.fxml"); //load the view
        });
    }

    /**
     * Sets action handler for View Student Reservations button.
     * @param event The event handler to set
     */
    public void setActionShowStudentReservationButton(EventHandler<ActionEvent> event) {
        showStudentReservationButton.setOnAction(event1 -> {
            highlightButton(showStudentReservationButton); //highlight the button when clicked
            loadView("/fxml/admin/student_reservations_pane.fxml"); //load the view
        });
    }

    /**
     * Sets action handler for Modify Terminal button.
     * @param event The event handler to set
     */
    public void setActionModifyTerminalButton(EventHandler<ActionEvent> event) {
        modifyTerminalButton.setOnAction(event1 -> {
            highlightButton(modifyTerminalButton); //highlight the button when clicked
            loadView("/fxml/admin/modify_terminal_pane.fxml"); //load the view
        });
    }

    /**
     * Sets action handler for Reservation Approval button.
     * @param event The event handler to set
     */
    public void setActionResApprovalButton(EventHandler<ActionEvent> event) {
        resApprovalButton.setOnAction(event1 -> {
            highlightButton(resApprovalButton); //highlight the button when clicked
            loadView("/fxml/admin/reservation_approval_pane.fxml"); //load the view
        });
    }

    /**
     * Sets action handler for Reports button.
     * @param event The event handler to set
     */
    public void setActionReportsButton(EventHandler<ActionEvent> event) {
        reportsButton.setOnAction(event1 -> {
            highlightButton(reportsButton); //highlight the button when clicked
            loadView("/fxml/admin/reports_pane.fxml"); //load the view
        });
    }

    /**
     * Sets action handler for Create Admin button.
     * @param event The event handler to set
     */
    public void setActionCreateAdminButton(EventHandler<ActionEvent> event) {
        createAdminButton.setOnAction(event1 -> {
            highlightButton(createAdminButton);
            loadView("/fxml/admin/add_new_admin.fxml");
        });
    }

    /**
     * Sets action handler for Logout button.
     * @param event The event handler to set
     */
    public void setActionLogoutButton(EventHandler<ActionEvent> event) {
        logOutButton.setOnAction(event);
    }

    /**
     * Shows the Reservation Approval view.
     */
    public void showReservationApprovalView() {
        loadView("/fxml/admin/reservation_approval_pane.fxml");
    }


    /**
     * Displays an error alert dialog.
     * @param message The error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Highlights the currently selected navigation button.
     * @param button The button to highlight
     */
    private void highlightButton(Button button) {
        //remove the highlight from the previously highlighted button

        if (currentlyHighlightedButton != null) {
            currentlyHighlightedButton.getStyleClass().remove("highlighted-button");
        }

        //highlight the new button
        button.getStyleClass().add("highlighted-button");

        //updates the currently highlighted button
        currentlyHighlightedButton = button;
    }
    public void logOutButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), logOutButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void logOutButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), logOutButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
