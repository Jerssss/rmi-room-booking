package client.landingpage;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LandingPageView implements Initializable {
    @FXML
    private Button landingPageLogInButton;
    @FXML
    private Button landingPageSignUpButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (landingPageLogInButton == null) {
            System.err.println("[ERROR] landingPageLogInButton is NULL! Check FXML.");
        }
        if (landingPageSignUpButton == null) {
            System.err.println("[ERROR] landingPageSignUpButton is NULL! Check FXML.");
        }
    }

    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        if (landingPageLogInButton != null) {
            landingPageLogInButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] landingPageLogInButton is NULL! Check FXML file.");
        }
    }

    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        if (landingPageSignUpButton != null) {
            landingPageSignUpButton.setOnAction(event);
        } else {
            System.err.println("[ERROR] landingPageSignUpButton is NULL! Check FXML file.");
        }
    }

    public void signUpButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageSignUpButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void signUpButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageSignUpButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void logInButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageLogInButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void logInButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), landingPageLogInButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
