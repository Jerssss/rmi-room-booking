package client.student.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.Reservation;
import shared.Terminal;

import java.util.UUID;

public class CreateReservationDialogController {

    @FXML
    private Button saveChangesButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField terminalNoTextField;
    @FXML
    private TextField roomNoTextField;

    private Stage dialogStage;
    private Reservation newReservation;

    /**
     * Initializes the controller class.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Wire the saveChangesButton to call the handleSaveReservation() method when clicked.
        saveChangesButton.setOnAction(event -> handleSaveReservation());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Pre-fills the form with the selected terminal details.
     * Assumes Terminal provides getTerminalID() and getRoom() methods.
     */
    public void setTerminalDetails(Terminal terminal) {
        if (terminal != null) {
            terminalNoTextField.setText(String.valueOf(terminal.getTerminalID()));
            roomNoTextField.setText(String.valueOf(terminal.getRoom()));
        }
    }

    /**
     * Handler for the save button action.
     * Gathers data from the form, creates a Reservation object, and closes the dialog.
     */
    @FXML
    private void handleSaveReservation() {
        // Extract the input values from the form
        String reservationDate = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";
        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        String terminalID = terminalNoTextField.getText();
        String roomID = roomNoTextField.getText();

        // Generate a unique reservation ID
        String reservationID = UUID.randomUUID().toString();
        // Assume a method or a constant provides the current student/user ID; using a placeholder here.
        String userID = "currentStudentID";  // Replace with actual user identification logic.

        // Create a new Reservation object with an initial status, e.g., "Pending"
        newReservation = new Reservation(reservationID, userID, terminalID, roomID,
                reservationDate, startTime, endTime, "Pending");

        System.out.println("[DEBUG] Created reservation: " + newReservation);

        // Close the dialog window
        dialogStage.close();
    }

    /**
     * Returns the newly created Reservation object.
     *
     * @return the Reservation if created, or null otherwise.
     */
    public Reservation getNewReservation() {
        return newReservation;
    }
}
