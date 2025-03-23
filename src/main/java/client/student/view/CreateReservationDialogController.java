package client.student.view;

import client.student.model.CreateReservationModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.Reservation;
import shared.Terminal;

import java.time.LocalDate;
import java.util.List;

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
    private CreateReservationModel model;

    @FXML
    public void initialize() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        datePicker.setValue(LocalDate.now().plusDays(1));

        startTimeTextField.textProperty().addListener((obs, oldVal, newVal) -> validateTimeOverlap());
        endTimeTextField.textProperty().addListener((obs, oldVal, newVal) -> validateTimeOverlap());

        saveChangesButton.setOnAction(event -> handleSaveReservation());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setModel(CreateReservationModel model) {
        this.model = model;
    }

    public void setTerminalDetails(Terminal terminal) {
        if (terminal != null) {
            terminalNoTextField.setText(String.valueOf(terminal.getTerminalID()));
            roomNoTextField.setText(String.valueOf(terminal.getRoom()));
        }
    }

    @FXML
    private void handleSaveReservation() {
        // Reset any previous error styling
        startTimeTextField.setStyle("");
        endTimeTextField.setStyle("");

        if (datePicker.getValue() == null) {
            showErrorAlert("Please select a date.");
            return;
        }
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate.isBefore(LocalDate.now().plusDays(1))) {
            showErrorAlert("Reservations must be made at least 1 day in advance.");
            return;
        }
        String reservationDate = selectedDate.toString();

        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        if (startTime.isEmpty() || endTime.isEmpty()) {
            showErrorAlert("Please enter both start and end times.");
            return;
        }
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            showErrorAlert("Time format must be in HH:mm (24-hour) format.");
            return;
        }
        int startMinutes = convertTimeToMinutes(startTime);
        int endMinutes = convertTimeToMinutes(endTime);
        if (startMinutes >= endMinutes) {
            showErrorAlert("Start time must be before end time.");
            return;
        }
        if (endMinutes - startMinutes < 30) {
            showErrorAlert("Reservations must be at least 30 minutes long.");
            return;
        }
        if (endMinutes - startMinutes > 120) {
            showErrorAlert("Reservations cannot exceed 2 hours.");
            return;
        }

        String terminalID = terminalNoTextField.getText();
        String roomID = roomNoTextField.getText();

        List<Reservation> existingReservations = model.fetchReservationsForTerminal(terminalID, reservationDate);
        if (existingReservations != null && hasOverlap(existingReservations, startTime, endTime)) {
            startTimeTextField.setStyle("-fx-text-fill: red;");
            endTimeTextField.setStyle("-fx-text-fill: red;");
            showErrorAlert("Time slot overlaps with an existing reservation!");
            return;
        }

        String userID = model.getStudentID();
        String reservationID = model.getNextReservationId();
        newReservation = new Reservation(reservationID, userID, terminalID, roomID,
                reservationDate, startTime, endTime, "Pending");

        try {
            // Call the model's addReservation (void) method.
            model.addReservation(newReservation);
            // Show confirmation popup
            showConfirmationPopup("Reservation successfully added!");
            dialogStage.close();
        } catch (RuntimeException ex) {
            showErrorAlert("Failed to add reservation. Please try again.");
        }
    }

    private void validateTimeOverlap() {
        startTimeTextField.setStyle("");
        endTimeTextField.setStyle("");

        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        if (startTime.isEmpty() || endTime.isEmpty()) {
            return;
        }
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            return;
        }
        int startMinutes = convertTimeToMinutes(startTime);
        int endMinutes = convertTimeToMinutes(endTime);
        if (startMinutes >= endMinutes) {
            return;
        }

        String terminalID = terminalNoTextField.getText();
        if (terminalID == null || terminalID.isEmpty() || datePicker.getValue() == null) {
            return;
        }
        String reservationDate = datePicker.getValue().toString();
        List<Reservation> existingReservations = model.fetchReservationsForTerminal(terminalID, reservationDate);
        if (existingReservations != null && hasOverlap(existingReservations, startTime, endTime)) {
            startTimeTextField.setStyle("-fx-text-fill: red;");
            endTimeTextField.setStyle("-fx-text-fill: red;");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmationPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    }

    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private boolean hasOverlap(List<Reservation> existingReservations, String newStart, String newEnd) {
        int newStartTime = convertTimeToMinutes(newStart);
        int newEndTime = convertTimeToMinutes(newEnd);
        for (Reservation res : existingReservations) {
            int existingStart = convertTimeToMinutes(res.getStartTime());
            int existingEnd = convertTimeToMinutes(res.getEndTime());
            if ((newStartTime >= existingStart && newStartTime < existingEnd) ||
                    (newEndTime > existingStart && newEndTime <= existingEnd) ||
                    (newStartTime <= existingStart && newEndTime >= existingEnd)) {
                return true;
            }
        }
        return false;
    }

    public Reservation getNewReservation() {
        return newReservation;
    }
}
