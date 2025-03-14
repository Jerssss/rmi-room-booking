package client.student.view;

import client.student.controller.ModifyReservationController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shared.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModifyReservationDialogController {
    @FXML private DatePicker datePicker;
    @FXML private TextField startTimeTextField;
    @FXML private TextField endTimeTextField;
    @FXML private ComboBox<String> roomNumberComboBox;
    @FXML private TextField terminalNumberTextField;
    @FXML private Label reservedDateLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label reservationRoomNoLabel;
    @FXML private Label reservationTerminalNoLabel;

    private Stage dialogStage;
    private Reservation reservation;
    private ModifyReservationController mainController;
    private boolean changesMade = false;

    @FXML
    public void initialize() {
        roomNumberComboBox.getItems().addAll("D524", "D526", "D426");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        // Update input fields
        datePicker.setValue(LocalDate.parse(reservation.getReservationDate()));
        startTimeTextField.setText(reservation.getStartTime());
        endTimeTextField.setText(reservation.getEndTime());
        roomNumberComboBox.setValue(reservation.getRoomID());
        terminalNumberTextField.setText(reservation.getTerminalID());

        // Update labels with original reservation data
        reservedDateLabel.setText(reservation.getReservationDate());
        startTimeLabel.setText(reservation.getStartTime());
        endTimeLabel.setText(reservation.getEndTime());
        reservationRoomNoLabel.setText(reservation.getRoomID());
        reservationTerminalNoLabel.setText(reservation.getTerminalID());
    }

    public void setMainController(ModifyReservationController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
        List<String> errors = validateInput();
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        updateReservation();
        changesMade = true;
        dialogStage.close();

        if (mainController != null && changesMade) {
            mainController.updateReservation(reservation);
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Cancel Reservation");
        alert.setContentText("Are you sure you want to cancel this reservation?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            mainController.cancelReservation(reservation.getReservationID());
            dialogStage.close();
        }
    }

    private List<String> validateInput() {
        List<String> errors = new ArrayList<>();
        LocalDate date = datePicker.getValue();
        LocalTime startTime = parseTime(startTimeTextField.getText(), errors, "start time");
        LocalTime endTime = parseTime(endTimeTextField.getText(), errors, "end time");

        if (date == null) errors.add("Invalid date.");
        if (startTime == null || endTime == null) errors.add("Invalid time format (use HH:mm).");
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            errors.add("End time must be after start time.");
        }
        return errors;
    }

    private LocalTime parseTime(String time, List<String> errors, String fieldName) {
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            errors.add("Invalid " + fieldName + " format (use HH:mm).");
            return null;
        }
    }

    private void updateReservation() {
        reservation.setReservationDate(datePicker.getValue().toString());
        reservation.setStartTime(startTimeTextField.getText());
        reservation.setEndTime(endTimeTextField.getText());
        reservation.setRoomID(roomNumberComboBox.getValue());
        reservation.setTerminalID(terminalNumberTextField.getText());
    }

    private void showErrorDialog(List<String> errors) {
        StringBuilder message = new StringBuilder("Validation errors:\n");
        for (String error : errors) {
            message.append("• ").append(error).append("\n");
        }
        new Alert(Alert.AlertType.ERROR, message.toString()).showAndWait();
    }

    public boolean isChangesMade() {
        return changesMade;
    }
}