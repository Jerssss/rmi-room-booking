package client.login;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

public class LoginView {

    @FXML
    private Button logInPageLogInButton;
    @FXML
    private Button logInPageSignUpButton;
    @FXML
    private TextField idField;
    @FXML
    private PasswordField passField;
    @FXML
    private ComboBox<String> userTypeBox;
    @FXML
    private Label promptLabel;

    @FXML
    private Button signInButton;
    @FXML
    private Button signUpButton;

    // Getters for UI components
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

    // New methods for setting promptLabel text and visibility
    public void setPromptLabel(String text) {
        promptLabel.setText(text);
    }

    public void setPromptLabelVisible(boolean visible) {
        promptLabel.setVisible(visible);
    }

    // Event handlers for button actions
    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        logInPageLogInButton.setOnAction(event);
    }

    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        logInPageSignUpButton.setOnAction(event);
    }

    public void initialize() {
        System.out.println("Initializing Login View...");

        // Attach event handlers to buttons
        if (signInButton != null) {
            signInButton.setOnAction(event -> {
                System.out.println("Sign In Button Clicked");
                // Trigger event from controller if needed
            });
        }

        if (signUpButton != null) {
            signUpButton.setOnAction(event -> {
                System.out.println("Sign Up Button Clicked");
                // Trigger event from controller if needed
            });
        }
    }


    public void signUpButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), logInPageSignUpButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void signUpButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), logInPageSignUpButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void logInButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), logInPageLogInButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void logInButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), logInPageLogInButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
