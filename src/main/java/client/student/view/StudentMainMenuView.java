package client.student.view;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class StudentMainMenuView {

    @FXML
    private Button createReservationButton;
    @FXML
    private Button viewReservationButton;
    @FXML
    private Button modifyReservationButton;
    @FXML
    private Label placeholderLabel;
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

    private Button currentlyHighlightedButton;

    /**
     * Load a new view inside the main menu
     */
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


    /**
     * Set the name of the logged-in user
     */
    public void setLoggedInUserName(String name) {
        headerNameLabel.setText(name);
    }

    /**
     * Initialize the date and time labels
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
     * Helper method to update the date and time labels
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
     * Show Error Dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public void setActionCreateReservationButton(EventHandler<ActionEvent> event) {
        createReservationButton.setOnAction(event1 -> {
            highlightButton(createReservationButton); //highlight the button when clicked
            loadView("/fxml/client/create_reservation_pane.fxml"); //load the view
        });
    }

    /** Event handler for View Student Reservations Button */
    public void setActionViewReservationButton(EventHandler<ActionEvent> event) {
        viewReservationButton.setOnAction(event1 -> {
            highlightButton(viewReservationButton); //highlight the button when clicked
            loadView("/fxml/client/view_reservation_pane.fxml"); //load the view
        });
    }

    /** Event handler for Modify Terminal Button */
    public void setActionModifyReservationButton(EventHandler<ActionEvent> event) {
        modifyReservationButton.setOnAction(event1 -> {
            highlightButton(modifyReservationButton); //highlight the button when clicked
            loadView("/fxml/client/modify_reservation_pane.fxml"); //load the view
        });
    }


    public void setActionLogoutButton(EventHandler<ActionEvent> event) {
        logOutButton.setOnAction(event);
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


