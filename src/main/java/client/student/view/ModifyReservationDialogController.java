package client.student.view;

import client.student.controller.ModifyReservationController;
import client.student.model.ModifyReservationModel;
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

/**
 * Controller for the Modify Reservation dialog.
 * This class handles the user interface for modifying an existing reservation,
 * including input validation and interaction with the main controller.
 */
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
    private ModifyReservationModel model;
    private boolean changesMade = false;

    /**
     * Initializes the dialog by setting up the room number combo box
     * and configuring the date picker to restrict date selection.
     */
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
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Listeners for real-time overlap validation
        startTimeTextField.textProperty().addListener((obs, oldVal, newVal) -> validateTimeOverlap());
        endTimeTextField.textProperty().addListener((obs, oldVal, newVal) -> validateTimeOverlap());
    }

    /**
     * Sets the dialog stage for this controller.
     *
     * @param dialogStage the stage to be set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the reservation to be modified and updates the input fields
     * with the reservation's current data.
     *
     * @param reservation the reservation to be modified
     */
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

    /**
     * Checks if the user has permission to edit the reservation based on its status
     * and the time remaining before the reservation starts.
     *
     * @param reservation the reservation to check
     */
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

    /**
     * Sets the main controller for this dialog.
     *
     * @param mainController the main controller to be set
     */
    public void setMainController(ModifyReservationController mainController) {
        this.mainController = mainController;
    }

    /**
     * Handles the action of sending the modification request.
     * Validates input, updates the reservation, and notifies the main controller.
     */
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

        String startTime = startTimeTextField.getText();
        String endTime = endTimeTextField.getText();
        String terminalID = terminalNumberTextField.getText();
        String reservationDate = datePicker.getValue().toString();

        // Validate maximum reservation duration (2 hours)
        int startMinutes = convertTimeToMinutes(startTime);
        int endMinutes = convertTimeToMinutes(endTime);
        int duration = endMinutes - startMinutes;
        if (duration > 120) {
            showErrorDialog(List.of("Reservations cannot exceed 2 hours."));
            return;
        }

        // Validate time overlap
        List<Reservation> existingReservations = model.fetchReservationsForTerminal(terminalID, reservationDate);
        if (existingReservations != null && hasOverlap(existingReservations, startTime, endTime)) {
            startTimeTextField.setStyle("-fx-text-fill: red;");
            endTimeTextField.setStyle("-fx-text-fill: red;");
            showErrorDialog(List.of("Time slot overlaps with an existing reservation!"));
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

    /**
     * Handles the action of canceling the modification and closing the dialog.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Handles the action of deleting the reservation.
     * Prompts the user for confirmation before proceeding with the cancellation.
     */
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

    /**
     * Validates the input fields for the reservation modification.
     *
     * @return a list of error messages if validation fails, otherwise an empty list
     */
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

        // Validate maximum reservation duration (2 hours)
        if (startTime != null && endTime != null) {
            int startMinutes = convertTimeToMinutes(startTimeTextField.getText());
            int endMinutes = convertTimeToMinutes(endTimeTextField.getText());
            int duration = endMinutes - startMinutes;
            if (duration > 120) {
                errors.add("Reservations cannot exceed 2 hours.");
            }
        }

        return errors;
    }

    /**
     * Parses a time string into a LocalTime object.
     *
     * @param time the time string to parse
     * @param errors a list to collect error messages
     * @param fieldName the name of the field being validated
     * @return the parsed LocalTime, or null if parsing fails
     */
    private LocalTime parseTime(String time, List<String> errors, String fieldName) {
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            errors.add("Invalid " + fieldName + " format (use HH:mm).");
            return null;
        }
    }

    /**
     * Updates the reservation object with the current input field values.
     */
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

    /**
     * Displays an error dialog with the provided validation error messages.
     *
     * @param errors the list of error messages to display
     */
    private void showErrorDialog(List<String> errors) {
        StringBuilder message = new StringBuilder("Validation errors:\n");
        for (String error : errors) {
            message.append("• ").append(error).append("\n");
        }
        new Alert(Alert.AlertType.ERROR, message.toString()).showAndWait();
    }

    /**
     * Checks if the current reservation input fields have been modified
     * compared to the original reservation data.
     *
     * @return true if the reservation has been modified, false otherwise
     */
    private boolean isReservationModified() {
        // Compare the current input fields with the original reservation data
        return !datePicker.getValue().toString().equals(reservation.getReservationDate()) ||
                !startTimeTextField.getText().equals(reservation.getStartTime()) ||
                !endTimeTextField.getText().equals(reservation.getEndTime()) ||
                !roomNumberComboBox.getValue().equals(reservation.getRoomID()) ||
                !terminalNumberTextField.getText().equals(reservation.getTerminalID());
    }

    /**
     * Checks if any changes have been made to the reservation.
     *
     * @return true if changes were made, false otherwise
     */
    public boolean isChangesMade() {
        return changesMade;
    }

    /**
     * Sets the model for this dialog.
     *
     * @param model the model to be set
     */
    public void setModel(ModifyReservationModel model) {
        this.model = model;
    }

    /**
     * Validates if the provided time string is in the correct format (HH:mm).
     *
     * @param time the time string to validate
     * @return true if the time format is valid, false otherwise
     */
    private boolean isValidTimeFormat(String time) {
        return time.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    }

    /**
     * Converts a time string in the format HH:mm to the total number of minutes since midnight.
     *
     * @param time the time string to convert
     * @return the total number of minutes since midnight
     */
    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * Checks if a new time slot overlaps with any existing reservations.
     *
     * @param existingReservations the list of existing reservations
     * @param newStart the start time of the new reservation in HH:mm format
     * @param newEnd the end time of the new reservation in HH:mm format
     * @return true if there is an overlap, false otherwise
     */
    private boolean hasOverlap(List<Reservation> existingReservations, String newStart, String newEnd) {
        int newStartTime = convertTimeToMinutes(newStart);
        int newEndTime = convertTimeToMinutes(newEnd);
        for (Reservation res : existingReservations) {
            // Skip the current reservation being edited
            if (res.getReservationID().equals(reservation.getReservationID())) {
                continue;
            }
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

    /**
     * Validates if the new time slot overlaps with any existing reservations.
     * If an overlap is detected, the start and end time text fields are highlighted in red.
     */
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

        String terminalID = terminalNumberTextField.getText();
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
}