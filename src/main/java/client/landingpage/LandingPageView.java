package client.landingpage;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class LandingPageView {

    @FXML
    private Button landingPageLogInButton;
    @FXML
    private Button landingPageSignUpButton;

    public void initialize() {
        if (landingPageLogInButton == null) {
            System.err.println("[ERROR] landingPageLogInButton is NULL! Check FXML fx:id.");
        }
        if (landingPageSignUpButton == null) {
            System.err.println("[ERROR] landingPageSignUpButton is NULL! Check FXML fx:id.");
        }
    }

    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        if (landingPageLogInButton != null) {
            landingPageLogInButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] landingPageLogInButton is NULL! Cannot set action.");
        }
    }

    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        if (landingPageSignUpButton != null) {
            landingPageSignUpButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] landingPageSignUpButton is NULL! Cannot set action.");
        }
    }

    // Hover effect for Log In Button
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

    // Hover effect for Sign Up Button
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
