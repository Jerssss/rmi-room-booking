package client.signup;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * View class for the sign-up page of the application.
 * Handles UI elements, animations, and event bindings for the sign-up and sign-in buttons.
 */
public class SignUpView {

    @FXML
    private Button signInPageSignInButton; // Button for signing in
    @FXML
    private Button signInPageSignUpButton; // Button for signing up
    @FXML
    private TextField signUpUserID; // Text field for entering user ID
    @FXML
    private TextField courseYearField; // Text field for entering course year
    @FXML
    private TextField nameField; // Text field for entering name
    @FXML
    private PasswordField signUpUserPass; // Password field for entering password
    @FXML
    private Label signUpPromptLabel; // Label for displaying prompts or error messages
    @FXML
    private StackPane courseYearStackPane; // Stack pane for course year field

    /**
     * Initializes the view and sets up input validation for the user ID field.
     * Ensures the course year field is always visible.
     */
    @FXML
    public void initialize() {
        // ID validation remains the same
        signUpUserID.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,7}")) return change;
            return null;
        }));

        // Force student fields to be always visible
        courseYearStackPane.setVisible(true);
        courseYearStackPane.setManaged(true);
    }

    /**
     * Attaches an event handler to the "Sign In" button.
     *
     * @param event the event handler to be triggered when the button is clicked
     */
    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        signInPageSignInButton.setOnAction(event);
    }

    /**
     * Attaches an event handler to the "Sign Up" button.
     *
     * @param event the event handler to be triggered when the button is clicked
     */
    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        signInPageSignUpButton.setOnAction(event);
    }

    /**
     * Returns the text field for entering the user ID.
     *
     * @return the ID text field
     */
    public TextField getIDField() {
        return signUpUserID;
    }

    /**
     * Returns the text field for entering the name.
     *
     * @return the name text field
     */
    public TextField getNameField() {
        return nameField;
    }

    /**
     * Returns the text field for entering the course year.
     *
     * @return the course year text field
     */
    public TextField getCourseYearField() {
        return courseYearField;
    }

    /**
     * Returns the password field for entering the password.
     *
     * @return the password field
     */
    public PasswordField getPassField() {
        return signUpUserPass;
    }

    /**
     * Returns the label for displaying prompts or error messages.
     *
     * @return the prompt label
     */
    public Label getPromptLabel() {
        return signUpPromptLabel;
    }

    /**
     * Resets the "Sign Up" button to its original size when the hover ends.
     */
    public void signUpButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignUpButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Applies a hover effect to the "Sign Up" button by scaling it down.
     */
    public void signUpButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignUpButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Resets the "Log In" button to its original size when the hover ends.
     */
    public void logInButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignInButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Applies a hover effect to the "Log In" button by scaling it down.
     */
    public void logInButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignInButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}