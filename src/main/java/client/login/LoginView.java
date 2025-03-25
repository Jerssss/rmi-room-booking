package client.login;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * View class for the login page of the application.
 * Handles UI elements, animations, and event bindings for the login and sign-up buttons.
 */
public class LoginView {

    @FXML
    private Button logInPageLogInButton; // Button for logging in
    @FXML
    private Button logInPageSignUpButton; // Button for signing up
    @FXML
    private TextField idField; // Text field for entering user ID
    @FXML
    private PasswordField passField; // Password field for entering password
    @FXML
    private ComboBox<String> userTypeBox; // Combo box for selecting user type
    @FXML
    private Label promptLabel; // Label for displaying prompts or error messages

    /**
     * Initializes the view and checks if the UI components are properly loaded.
     * Logs an error if any component is null.
     *
     * @param url the location used to resolve relative paths for the root object
     * @param resourceBundle the resources used to localize the root object
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("[DEBUG] Initializing Login View...");

        if (logInPageLogInButton == null) {
            System.err.println("[ERROR] logInPageLogInButton is NULL! Check FXML.");
        }
        if (logInPageSignUpButton == null) {
            System.err.println("[ERROR] logInPageSignUpButton is NULL! Check FXML.");
        }
    }

    /**
     * Returns the text field for entering the user ID.
     *
     * @return the ID text field
     */
    public TextField getIDField() {
        return idField;
    }

    /**
     * Returns the password field for entering the password.
     *
     * @return the password field
     */
    public PasswordField getPassField() {
        return passField;
    }

    /**
     * Returns the combo box for selecting the user type.
     *
     * @return the user type combo box
     */
    public ComboBox<String> getUserTypeBox() {
        return userTypeBox;
    }

    /**
     * Returns the label for displaying prompts or error messages.
     *
     * @return the prompt label
     */
    public Label getPromptLabel() {
        return promptLabel;
    }

    /**
     * Sets the text of the prompt label.
     *
     * @param text the text to display in the prompt label
     */
    public void setPromptLabel(String text) {
        promptLabel.setText(text);
    }

    /**
     * Sets the visibility of the prompt label.
     *
     * @param visible whether the prompt label should be visible
     */
    public void setPromptLabelVisible(boolean visible) {
        promptLabel.setVisible(visible);
    }

    /**
     * Attaches an event handler to the "Sign In" button.
     *
     * @param event the event handler to be triggered when the button is clicked
     */
    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        if (logInPageLogInButton != null) {
            logInPageLogInButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] logInPageLogInButton is NULL! Check FXML.");
        }
    }

    /**
     * Attaches an event handler to the "Sign Up" button.
     *
     * @param event the event handler to be triggered when the button is clicked
     */
    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        if (logInPageSignUpButton != null) {
            logInPageSignUpButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] logInPageSignUpButton is NULL! Check FXML.");
        }
    }

    /**
     * Resets the "Sign Up" button to its original size when the hover ends.
     */
    public void signUpButtonExited() {
        animateButton(logInPageSignUpButton, 1.0);
    }

    /**
     * Applies a hover effect to the "Sign Up" button by scaling it down.
     */
    public void signUpButtonHovered() {
        animateButton(logInPageSignUpButton, 0.9);
    }

    /**
     * Resets the "Log In" button to its original size when the hover ends.
     */
    public void logInButtonExited() {
        animateButton(logInPageLogInButton, 1.0);
    }

    /**
     * Applies a hover effect to the "Log In" button by scaling it down.
     */
    public void logInButtonHovered() {
        animateButton(logInPageLogInButton, 0.9);
    }

    /**
     * Applies a scaling animation to a button.
     *
     * @param button the button to animate
     * @param scale the target scale for the button
     */
    private void animateButton(Button button, double scale) {
        if (button != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(scale);
            st.setToY(scale);
            st.setCycleCount(1);
            st.setAutoReverse(false);
            st.play();
        }
    }
}