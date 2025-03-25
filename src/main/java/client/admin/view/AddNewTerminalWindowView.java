package client.admin.view;

import client.admin.controller.AddNewTerminalWindowController;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.Terminal;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddNewTerminalWindowView implements Initializable {
    private AddNewTerminalWindowController controller;
    @FXML private TextField terminalNoTextField;
    @FXML private ComboBox<String> terminalOSComboBox;
    @FXML private ComboBox<String> roomNumberComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Button saveTerminalButton;
    private String startTime;
    private String endTime;
    public void showWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/add_terminal_window.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Terminal");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Add Terminal window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=====================================================");
        controller = new AddNewTerminalWindowController();

        // Disable dates before tomorrow and after 3 months in the DatePicker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate tomorrow = LocalDate.now().plusDays(1);
                LocalDate maxDate = LocalDate.now().plusMonths(3);

                // Disable if before tomorrow, after 3 months, or a Sunday
                if (date.isBefore(tomorrow) || date.isAfter(maxDate) || date.getDayOfWeek().getValue() == 7) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ff9999;"); // Light red for disabled
                    setTooltip(new Tooltip("No school on Sundays!"));
                }
            }
        });

        datePicker.setValue(LocalDate.now().plusDays(1));

        // Set ComboBox Options
        terminalOSComboBox.getItems().addAll("Windows", "macOS");
        roomNumberComboBox.getItems().addAll("D524", "D526", "D426");
        statusComboBox.getItems().addAll("Active", "Down", "Maintenance");
        timeComboBox.getItems().addAll("09:30 - 11:30", "11:30 - 16:30", "07:30 - 15:30");

        // Force "PC" in Terminal No.
        terminalNoTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith("PC")) {
                terminalNoTextField.setText("PC" + newValue.replace("PC", ""));
            }
        });

        // Extract Start and End Time
        timeComboBox.setOnAction(event -> {
            String[] timeParts = timeComboBox.getValue().split(" - ");
            startTime = timeParts[0]; // Start time
            endTime = timeParts[1];   // End time
        });

        // Save button action
        saveTerminalButton.setOnAction(event -> handleSave());
    }

    private void handleSave() {
        String terminalID = terminalNoTextField.getText().replace("PC", "");
        String os = terminalOSComboBox.getValue();
        String room = roomNumberComboBox.getValue();
        String status = statusComboBox.getValue();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";

        StringBuilder errors = new StringBuilder();
        List<Control> invalidFields = new ArrayList<>();

        validateTerminalNumber(terminalID, errors, invalidFields);
        validateOS(os, errors, invalidFields);
        validateRoom(room, errors, invalidFields);
        validateStatus(status, errors, invalidFields);
        validateDateTime(date, timeComboBox.getValue(), errors, invalidFields);

        // If there are errors, show the alert and highlight fields
        if (!errors.isEmpty()) {
            String errorMessage = errors.toString().trim(); // Remove trailing newline
            showAlert("Validation Error", errors.toString(), invalidFields);
            return;
        }

        // Check for duplicate terminal reservation
        if (isDuplicateTerminal(terminalID, room, invalidFields)) {
            showAlert("Duplicate Terminal", "• A terminal with the same number already exists in this room.", invalidFields);
            return;
        }


        // Pass data to the controller to save to JSON
        boolean success = controller.addNewTerminal("PC" + terminalID, os, room, status, startTime, endTime, date);

        if (!success) {
            JOptionPane.showMessageDialog(null,
                    "Failed to add terminal                                 .",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            try {
                // Load the "Saved Notifier" FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/added_notifier_window.fxml"));
                Parent root = loader.load();

                // Create a new Stage (pop-up window)
                Stage notifierStage = new Stage();
                notifierStage.setTitle("Terminal Saved Successfully");
                System.out.println("[CLIENT] Terminal Successfully Added!");
                notifierStage.setScene(new Scene(root));
                notifierStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
                notifierStage.setResizable(false);
                notifierStage.showAndWait(); // Wait until the user closes it

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error loading the saved notification window.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        closeWindow();
    }

    private void validateTerminalNumber(String terminalNumber, StringBuilder errors, List<Control> invalidFields) {
        try {
            if (terminalNumber == null || terminalNumber.isEmpty()) {
                errors.append("• Please input a terminal number.\n");
                invalidFields.add(terminalNoTextField);
            }
            int terminalNum = Integer.parseInt(terminalNumber);
            if (terminalNum < 1 || terminalNum > 50) {
                errors.append("• Terminal number must be between 1 to 50.\n");
                invalidFields.add(terminalNoTextField);
            }
        } catch (NumberFormatException e) {
            errors.append("• Terminal number must be a number.\n");
            invalidFields.add(terminalNoTextField);
        }
    }

    private void validateOS(String os, StringBuilder errors, List<Control> invalidFields) {
        if (os == null || os.isEmpty()) {
            errors.append("• Please select an Operating System.\n");
            invalidFields.add(terminalOSComboBox);
        }
    }

    private void validateRoom(String room, StringBuilder errors, List<Control> invalidFields) {
        if (room == null || room.isEmpty()) {
            errors.append("• Please select a Room Number.\n");
            invalidFields.add(roomNumberComboBox);
        }
    }

    private void validateStatus(String status, StringBuilder errors, List<Control> invalidFields) {
        if (status == null || status.isEmpty()) {
            errors.append("• Please select a Terminal Status.\n");
            invalidFields.add(statusComboBox);
        }
    }

    private void validateDateTime(String date, String startTime, StringBuilder errors, List<Control> invalidFields) {
        if (date == null || date.isEmpty()) {
            errors.append("• Please select a Date.\n");
            invalidFields.add(datePicker);
        }

        if (startTime == null || startTime.isEmpty()) {
            errors.append("• Please select a Time Slot.\n");
            invalidFields.add(timeComboBox);
        }
    }

    private boolean isDuplicateTerminal(String terminalID, String room, List<Control> invalidFields) {
        List<Terminal> terminals = controller.getAllTerminals();

        for (Terminal terminal : terminals) {
            // Check if terminal ID and room match
            if (terminal.getTerminalID().trim().equals("PC" + terminalID.trim()) &&
                    terminal.getRoom().trim().equals(room.trim())) {
                invalidFields.add(terminalNoTextField);
                invalidFields.add(roomNumberComboBox);
                return true;  // Duplicate found
            }
        }
        return false;  // No duplicate
    }

    private void showAlert(String title, String message, List<Control> invalidFields) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Keep the window on top
        Stage stage = (Stage) saveTerminalButton.getScene().getWindow();
        alert.initOwner(stage);
        alert.showAndWait();

        // Highlight all invalid fields
        for (Control field : invalidFields) {
            if (field instanceof TextField textField) {
                textField.setStyle("-fx-background-color: red;");
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.trim().isEmpty()) {
                        textField.setStyle("");
                    }
                });
            } else if (field instanceof ComboBox<?> comboBox) {
                comboBox.setStyle("-fx-background-color: #EBC7C7;");
                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        comboBox.setStyle("");
                    }
                });
            } else if (field instanceof DatePicker datePicker) {
                datePicker.setStyle("-fx-background-color: red;");
                datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        datePicker.setStyle("");
                    }
                });
            }
        }
    }


    private void closeWindow() {
        Stage stage = (Stage) saveTerminalButton.getScene().getWindow();
        stage.close();
    }

    public void saveTerminalButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveTerminalButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void saveTerminalButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveTerminalButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
