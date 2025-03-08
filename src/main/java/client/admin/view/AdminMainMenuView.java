// File: client/admin/view/AdminMainMenuView.java
package client.admin.view;

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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class AdminMainMenuView {

    @FXML
    private Button addNewTerminalButton;
    @FXML
    private Button showStudentReservationButton;
    @FXML
    private Button modifyTerminalButton;
    @FXML
    private Button resApprovalButton;
    @FXML
    private Button reportsButton;
    @FXML
    private Button logOutButton;
    @FXML
    private Label headerNameLabel;
    @FXML
    private Label headerDateLabel;
    @FXML
    private Label headerTimeLabel;
    @FXML
    private BorderPane rootPane;

    // 🟡 Add this (fixing the error)
    @FXML
    private ToggleButton serverToggleButton;

    private Button currentlyHighlightedButton;

    /** Load a new view inside the main menu */
    private void loadView(String fxmlFile) {
        try {
            System.out.println("Loading FXML: " + fxmlFile);
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


    /** Set the name of the logged-in user */
    public void setLoggedInUserName(String name) {
        headerNameLabel.setText(name);
    }

    /** Initialize the date and time labels */
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

    /** Helper method to update the date and time labels */
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

    /** Event handler for Add Terminal Button */
    public void setActionAddNewTerminalButton(EventHandler<ActionEvent> event) {
        addNewTerminalButton.setOnAction(event1 -> {
            highlightButton(addNewTerminalButton); //highlight the button when clicked
            loadView("/fxml/admin/add_terminal_pane.fxml"); //load the view
        });
    }

    /** Event handler for View Student Reservations Button */
    public void setActionShowStudentReservationButton(EventHandler<ActionEvent> event) {
        showStudentReservationButton.setOnAction(event1 -> {
            highlightButton(showStudentReservationButton); //highlight the button when clicked
            loadView("/fxml/admin/student_reservations_pane.fxml"); //load the view
        });
    }

    /** Event handler for Modify Terminal Button */
    public void setActionModifyTerminalButton(EventHandler<ActionEvent> event) {
        modifyTerminalButton.setOnAction(event1 -> {
            highlightButton(modifyTerminalButton); //highlight the button when clicked
            loadView("/fxml/admin/modify_terminal_pane.fxml"); //load the view
        });
    }



    /** Event handler for Reservation Approval Button */
    public void setActionResApprovalButton(EventHandler<ActionEvent> event) {
        resApprovalButton.setOnAction(event1 -> {
            highlightButton(resApprovalButton); //highlight the button when clicked
            loadView("/fxml/admin/reservation_approval_pane.fxml"); //load the view
        });
    }

    /** Event handler for Reports Button */
    public void setActionReportsButton(EventHandler<ActionEvent> event) {
        reportsButton.setOnAction(event1 -> {
            highlightButton(reportsButton); //highlight the button when clicked
            loadView("/fxml/admin/reports_pane.fxml"); //load the view
        });
    }

    /** Event handler for Logout Button */
    public void setActionLogoutButton(EventHandler<ActionEvent> event) {
        logOutButton.setOnAction(event);
    }

    /** Show Reservation Approval View */
    public void showReservationApprovalView() {
        loadView("/fxml/admin/reservation_approval_pane.fxml");
    }

    /** Event handler for Toggle Button */
    public void setActionToggleButton(EventHandler<ActionEvent> event) {
        serverToggleButton.setOnAction(event);
    }

    /** Check if Toggle Button is Selected */
    public boolean isServerToggleSelected() {
        return serverToggleButton.isSelected();
    }

    /** Set Toggle Button Text */
    public void setToggleText(String text) {
        serverToggleButton.setText(text);
    }

    /** Show Error Dialog */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
