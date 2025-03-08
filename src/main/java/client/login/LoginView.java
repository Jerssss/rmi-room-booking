package client.login;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginView {

    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("[DEBUG] Initializing Login View...");

        if (logInPageLogInButton == null) {
            System.err.println("[ERROR] logInPageLogInButton is NULL! Check FXML.");
        }
        if (logInPageSignUpButton == null) {
            System.err.println("[ERROR] logInPageSignUpButton is NULL! Check FXML.");
        }
    }

    @FXML
    private Button logInPageLogInButton; // Ensure this matches FXML fx:id
    @FXML
    private Button logInPageSignUpButton; // Ensure this matches FXML fx:id
    @FXML
    private TextField idField;
    @FXML
    private PasswordField passField;
    @FXML
    private ComboBox<String> userTypeBox;
    @FXML
    private Label promptLabel;


    /**
     * Getters for UI components
     */
    public TextField getIDField() {
        return idField;
    }

    public PasswordField getPassField() {
        return passField;
    }

    public ComboBox<String> getUserTypeBox() {
        return userTypeBox;
    }

    public Label getPromptLabel() {
        return promptLabel;
    }

    /**
     * Sets the text of the prompt label.
     */
    public void setPromptLabel(String text) {
        promptLabel.setText(text);
    }

    /**
     * Sets the visibility of the prompt label.
     */
    public void setPromptLabelVisible(boolean visible) {
        promptLabel.setVisible(visible);
    }

    /**
     * Attaches an event to the Sign-In button.
     */
    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        if (logInPageLogInButton != null) {
            logInPageLogInButton.setOnAction(event);
            System.out.println("[DEBUG] Sign In button action set.");
        } else {
            System.err.println("[ERROR] logInPageLogInButton is NULL! Check FXML.");
        }
    }

    /**
     * Attaches an event to the Sign-Up button.
     */
    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        if (logInPageSignUpButton != null) {
            logInPageSignUpButton.setOnAction(event);
            System.out.println("[DEBUG] Sign Up button action set.");
        } else {
            System.err.println("[ERROR] logInPageSignUpButton is NULL! Check FXML.");
        }
    }
    /**
     * Button hover and exit animations.
     */
    public void signUpButtonExited() {
        animateButton(logInPageSignUpButton, 1.0);
    }

    public void signUpButtonHovered() {
        animateButton(logInPageSignUpButton, 0.9);
    }

    public void logInButtonExited() {
        animateButton(logInPageLogInButton, 1.0);
    }

    public void logInButtonHovered() {
        animateButton(logInPageLogInButton, 0.9);
    }

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
