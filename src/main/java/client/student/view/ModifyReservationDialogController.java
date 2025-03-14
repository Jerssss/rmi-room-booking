package client.student.view;

import client.student.controller.ModifyReservationController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shared.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // Set the DatePicker restrictions
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && (item.isBefore(today) || item.isAfter(today.plusDays(30)))) {
                    setDisable(true); // Disable past dates and dates beyond 30 days
                }
            }
        });
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

        // Check if the reservation can be edited
        checkEditPermission(reservation);
    }

    private void checkEditPermission(Reservation reservation) {
        LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
        LocalTime startTime = LocalTime.parse(reservation.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, startTime);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursFromNow = now.plusHours(24);

        boolean canEdit = "Pending".equals(reservation.getStatus()) && reservationDateTime.isAfter(twentyFourHoursFromNow);

        if (!canEdit) {
            // Show a confirmation dialog to inform the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Edit Not Allowed");
            alert.setHeaderText(null);
            alert.setContentText("You can only edit reservations that are Pending and have at least 24 hours before the start time.");

            // Add an OK button to close the dialog
            alert.getButtonTypes().setAll(ButtonType.OK);

            // Show the dialog and wait for the user to close it
            alert.showAndWait();

            // Close the dialog stage if editing is not allowed
            if (dialogStage != null) {
                dialogStage.close();
            }
        }
    }

    public void setMainController(ModifyReservationController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSendRequest() {
        // Check if any changes were made
        if (!isReservationModified()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Changes");
            alert.setHeaderText(null);
            alert.setContentText("No modifications detected. Nothing to save.");
            alert.showAndWait();
            return;
        }

        List<String> errors = validateInput();
        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return;
        }

        updateReservation();

        // Update the table
        if (mainController != null) {
            mainController.setChangesMade(true); // Notify main controller of changes
            mainController.updateTableWithPendingReservation(reservation);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Action Required");
        alert.setHeaderText(null);
        alert.setContentText("Click 'Save Changes' in main window to persist modifications");
        alert.showAndWait();

        dialogStage.close(); // Close dialog without saving to database
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
        if (!isReservationModified()) {
            changesMade = false;
            return;
        }

        reservation.setReservationDate(datePicker.getValue().toString());
        reservation.setStartTime(startTimeTextField.getText());
        reservation.setEndTime(endTimeTextField.getText());
        reservation.setRoomID(roomNumberComboBox.getValue());
        reservation.setTerminalID(terminalNumberTextField.getText());
        changesMade = true; // Notify the main controller that changes were made
    }

    private void showErrorDialog(List<String> errors) {
        StringBuilder message = new StringBuilder("Validation errors:\n");
        for (String error : errors) {
            message.append("• ").append(error).append("\n");
        }
        new Alert(Alert.AlertType.ERROR, message.toString()).showAndWait();
    }

    private boolean isReservationModified() {
        // Compare the current input fields with the original reservation data
        return !datePicker.getValue().toString().equals(reservation.getReservationDate()) ||
                !startTimeTextField.getText().equals(reservation.getStartTime()) ||
                !endTimeTextField.getText().equals(reservation.getEndTime()) ||
                !roomNumberComboBox.getValue().equals(reservation.getRoomID()) ||
                !terminalNumberTextField.getText().equals(reservation.getTerminalID());
    }

    public boolean isChangesMade() {
        return changesMade;
    }
}