package client.landingpage.pickserver;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;


import java.util.function.Consumer;

public class SetIPView {

    @FXML
    private ComboBox<String> serversComboBox;

    @FXML
    private TextField ipTextField;

    @FXML
    private Button connectButton;

    @FXML
    private Button clearButton;

    private Consumer<String> connectHandler;

    @FXML
    public void initialize() {
        // Initialize the ComboBox with predefined IPs if any
        serversComboBox.getItems().addAll("192.168.100.13", "192.168.1.2", "192.168.1.3", "192.168.1.100", null);

        // Disable TextField when ComboBox is used
        serversComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ipTextField.setDisable(true); // Disable TextField
                ipTextField.clear(); // Clear TextField
            } else {
                ipTextField.setDisable(false); // Enable TextField
            }
        });

        // Disable ComboBox when TextField is used
        ipTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                serversComboBox.setDisable(true); // Disable ComboBox
                serversComboBox.getSelectionModel().clearSelection(); // Clear ComboBox selection
            } else {
                serversComboBox.setDisable(false); // Enable ComboBox
            }
        });

        // Add a TextFormatter to restrict input to numbers and periods
        ipTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9.]*")) {
                return change; // Allow the change if it matches the pattern
            } else {
                return null; // Reject the change if it doesn't match the pattern
            }
        }));
    }

    @FXML
    private void handleConnectButtonAction() {
        String serverIP;

        // Get the IP from the active field
        if (serversComboBox.isDisabled()) {
            serverIP = ipTextField.getText().trim(); // Use TextField value
        } else {
            serverIP = serversComboBox.getSelectionModel().getSelectedItem(); // Use ComboBox value
        }

        if (serverIP == null || serverIP.isEmpty()) {
            System.out.println("Please enter a valid IP address.");
            return;
        }

        // Notify the connect handler (ClientMain) with the provided IP
        if (connectHandler != null) {
            connectHandler.accept(serverIP); // Pass the IP to the handler
        }

        // Close the IP input window
        Stage stage = (Stage) connectButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleUseSelectedButtonAction() {
        String selectedIP = serversComboBox.getSelectionModel().getSelectedItem();
        if (selectedIP != null && !selectedIP.isEmpty()) {
            ipTextField.setText(selectedIP); // Populate the TextField with the selected IP
        } else {
            System.out.println("No IP selected from the ComboBox.");
        }
    }

    /**
     * Sets the connect handler to be called when the user provides an IP address.
     *
     * @param connectHandler The handler to call with the provided IP address.
     */
    public void setConnectHandler(Consumer<String> connectHandler) {
        this.connectHandler = connectHandler;
    }


    /** Button Animations */
    private void animateButton(Button button, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    @FXML
    private void connectButtonExited() {
        animateButton(connectButton, 1.0);
    }

    @FXML
    private void connectButtonHovered() {
        animateButton(connectButton, 0.9);
    }
}