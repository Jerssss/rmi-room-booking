package client.admin.view;


import client.admin.controller.AddNewTerminalWindowController;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.Terminal;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    @FXML private Button saveChangesButton;


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

                if (date.isBefore(tomorrow) || date.isAfter(maxDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
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
        saveChangesButton.setOnAction(event -> handleSave());
    }

    private void handleSave() {
        String terminalID = terminalNoTextField.getText().replace("PC", "");
        String os = terminalOSComboBox.getValue();
        String room = roomNumberComboBox.getValue();
        String status = statusComboBox.getValue();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : "";

        // Input Validations
        if (!validateTerminalNumber(terminalID) || !validateDate(date)) {
            return; // Stops the saving process if validation fails
        }

        // Pass data to the controller to save to JSON
        boolean isSuccess = controller.addNewTerminal("PC" + terminalID, os, room, status, startTime, endTime, date);

        if (isSuccess) {
            JOptionPane.showMessageDialog(null, "Terminal Successfully Added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("[CLIENT] Terminal Successfully Added!");
            Stage stage = (Stage) saveChangesButton.getScene().getWindow();
            stage.close(); // Close the window only if successful
        } else {
            JOptionPane.showMessageDialog(null, "Failed to add terminal! Duplicate date or time detected.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("[CLIENT] Terminal was not Saved!");
        }
    }

    private boolean validateTerminalNumber(String terminalNumber) {
        try {
            int terminalNum = Integer.parseInt(terminalNumber);

            if (terminalNum < 1 || terminalNum > 50) {
                JOptionPane.showMessageDialog(null, "Terminal number must be between 1 to 50 only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                System.out.println("[CLIENT] Terminal was not Saved!");
                return false;
            }

            // Check if terminal number already exists
            List<Terminal> terminals = controller.getAllTerminals();
            for (Terminal terminal : terminals) {
                if (terminal.getTerminalID().equals("PC-" + terminalNumber)) {
                    JOptionPane.showMessageDialog(null, "Terminal number already exists.", "Duplicate Terminal", JOptionPane.ERROR_MESSAGE);
                    System.out.println("[CLIENT] Terminal was not Saved!");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Terminal number must be numbers only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            System.out.println("[CLIENT] Terminal was not Saved!");
            return false;
        }
        return true;
    }

    private boolean validateDate(String date) {
        if (date == null || date.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a date.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            System.out.println("[CLIENT] Terminal was not Saved!");
            return false;
        }
        // Check for duplicate date and time for the same terminal
        List<Terminal> terminals = controller.getAllTerminals();
        for (Terminal terminal : terminals) {
            if (terminal.getReservationDate().equals(date) && terminal.getStartTime().equals(startTime) && terminal.getTerminalID().equals(terminalNoTextField.getText())) {
                JOptionPane.showMessageDialog(null, "This terminal already has a reservation at this time and date.", "Duplicate Reservation", JOptionPane.ERROR_MESSAGE);
                System.out.println("[CLIENT] Terminal was not Saved!");
                return false;
            }
        }
        return true;
    }
    public void saveChangesButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveChangesButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void saveChangesButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveChangesButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
