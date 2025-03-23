package client.landingpage.pickserver;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * The SetIPView class is responsible for managing the UI for setting and connecting to a server IP address.
 * It provides functionality for selecting a predefined IP from a ComboBox or entering a custom IP in a TextField.
 * The class also includes validation to ensure only valid IP addresses (numbers and periods) are entered.
 */
public class SetIPView {

    @FXML
    private ComboBox<String> serversComboBox; // ComboBox for selecting predefined IP addresses

    @FXML
    private TextField ipTextField; // TextField for entering a custom IP address

    @FXML
    private Button connectButton; // Button to initiate the connection

    @FXML
    private Button clearButton; // Button to clear the input fields

    private Consumer<String> connectHandler; // Handler to process the provided IP address

    private Stage stage; // Reference to the stage (window)

    /**
     * Initializes the UI components and sets up event listeners.
     * This method is automatically called by JavaFX after the FXML file is loaded.
     */
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

        // Set the clear button action
        clearButton.setOnAction(event -> handleClearButtonAction());

        // Delay the close request handler setup until the scene is fully initialized
        Platform.runLater(this::setupCloseRequestHandler);
    }

    /**
     * Sets up the close request handler for the stage.
     * This ensures the program exits when the user clicks the X button on the window.
     */
    private void setupCloseRequestHandler() {
        // Get the stage from the connectButton's scene
        stage = (Stage) connectButton.getScene().getWindow();

        // Set the close request handler
        stage.setOnCloseRequest(event -> {
            System.out.println("Closing the application...");
            System.exit(0); // Exit the program
        });
    }

    /**
     * Handles the action when the Connect button is clicked.
     * Retrieves the IP address from either the TextField or ComboBox,
     * validates it, and passes it to the connect handler.
     */
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
        stage.close();
    }

    /**
     * Handles the action when the Clear button is clicked.
     * Clears the TextField and ComboBox selection, and re-enables both fields for new input.
     */
    @FXML
    private void handleClearButtonAction() {
        // Clear the TextField
        ipTextField.clear();

        // Clear the ComboBox selection
        serversComboBox.getSelectionModel().clearSelection();

        // Enable both TextField and ComboBox
        ipTextField.setDisable(false);
        serversComboBox.setDisable(false);
    }

    /**
     * Sets the connect handler to be called when the user provides an IP address.
     *
     * @param connectHandler The handler to call with the provided IP address.
     */
    public void setConnectHandler(Consumer<String> connectHandler) {
        this.connectHandler = connectHandler;
    }

    /**
     * Animates a button by scaling it to the specified size.
     *
     * @param button The button to animate.
     * @param scale  The scale factor to apply (e.g., 0.9 for a slight shrink).
     */
    private void animateButton(Button button, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Handles the event when the mouse exits the Connect button.
     * Resets the button to its original size.
     */
    @FXML
    private void connectButtonExited() {
        animateButton(connectButton, 1.0);
    }

    /**
     * Handles the event when the mouse hovers over the Connect button.
     * Scales the button down slightly to provide visual feedback.
     */
    @FXML
    private void connectButtonHovered() {
        animateButton(connectButton, 0.9);
    }
}