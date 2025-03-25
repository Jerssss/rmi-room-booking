package client.landingpage;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * View class for the landing page of the application.
 * Handles UI elements and animations for the login and sign-up buttons.
 */
public class LandingPageView {

    @FXML
    private Button landingPageLogInButton; // Button for logging in
    @FXML
    private Button landingPageSignUpButton; // Button for signing up

    /**
     * Initializes the view and checks if the buttons are properly loaded.
     * Logs an error if any button is null.
     */
    public void initialize() {
        if (landingPageLogInButton == null) {
            System.err.println("[ERROR] landingPageLogInButton is NULL! Check FXML fx:id.");
        }
        if (landingPageSignUpButton == null) {
            System.err.println("[ERROR] landingPageSignUpButton is NULL! Check FXML fx:id.");
        }
    }

    /**
     * Sets the action handler for the "Sign In" button.
     *
     * @param event the event handler to be triggered when the button is clicked
     */
    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        if (landingPageLogInButton != null) {
            landingPageLogInButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] landingPageLogInButton is NULL! Cannot set action.");
        }
    }

    /**
     * Sets the action handler for the "Sign Up" button.
     *
     * @param event the event handler to be triggered when the button is clicked
     */
    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        if (landingPageSignUpButton != null) {
            landingPageSignUpButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] landingPageSignUpButton is NULL! Cannot set action.");
        }
    }

    /**
     * Applies a hover effect to the "Log In" button by scaling it down.
     */
    @FXML
    public void logInButtonHovered() {
        if (landingPageLogInButton != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageLogInButton);
            st.setToX(0.9);
            st.setToY(0.9);
            st.setCycleCount(1);
            st.setAutoReverse(false);
            st.play();
        } else {
            System.err.println("[ERROR] landingPageLogInButton is NULL!");
        }
    }

    /**
     * Resets the "Log In" button to its original size when the hover ends.
     */
    @FXML
    public void logInButtonExited() {
        if (landingPageLogInButton != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageLogInButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setCycleCount(1);
            st.setAutoReverse(false);
            st.play();
        } else {
            System.err.println("[ERROR] landingPageLogInButton is NULL!");
        }
    }

    /**
     * Applies a hover effect to the "Sign Up" button by scaling it down.
     */
    @FXML
    public void signUpButtonHovered() {
        if (landingPageSignUpButton != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageSignUpButton);
            st.setToX(0.9);
            st.setToY(0.9);
            st.setCycleCount(1);
            st.setAutoReverse(false);
            st.play();
        } else {
            System.err.println("[ERROR] landingPageSignUpButton is NULL!");
        }
    }

    /**
     * Resets the "Sign Up" button to its original size when the hover ends.
     */
    @FXML
    public void signUpButtonExited() {
        if (landingPageSignUpButton != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageSignUpButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setCycleCount(1);
            st.setAutoReverse(false);
            st.play();
        } else {
            System.err.println("[ERROR] landingPageSignUpButton is NULL!");
        }
    }
}