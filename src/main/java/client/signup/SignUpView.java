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

public class SignUpView {

    @FXML
    private Button signInPageSignInButton;
    @FXML
    private Button signInPageSignUpButton;
    @FXML
    private ComboBox<String> signUpUserTypePicker;
    @FXML
    private TextField signUpUserID;
    @FXML
    private TextField facultyTypeField;
    @FXML
    private TextField courseYearField;
    @FXML
    private TextField nameField; // Added Name Field
    @FXML
    private PasswordField signUpUserPass;
    @FXML
    private Label signUpPromptLabel;
    @FXML
    private StackPane facultyTypeStackPane;
    @FXML
    private StackPane courseYearStackPane;

    private Parent root;
    private FXMLLoader fxmlLoader;

    @FXML
    public void initialize() {
        // Limit the ID field to 7 digits
        signUpUserID.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,7}")) {
                return change; // Allow change
            }
            return null; // Reject change
        }));

        signUpUserTypePicker.getItems().addAll("Student", "Admin");
        signUpUserTypePicker.setValue("Student");
        updateFormFields();
        signUpUserTypePicker.setOnAction((event -> updateFormFields()));
    }


    private void updateFormFields() {
        String selectedType = signUpUserTypePicker.getValue();
        boolean isStudent = "Student".equals(selectedType);

        courseYearStackPane.setVisible(isStudent);
        courseYearStackPane.setManaged(isStudent);

        courseYearField.setVisible(isStudent);
        courseYearField.setManaged(isStudent);

        facultyTypeField.setVisible(!isStudent);
        facultyTypeField.setManaged(!isStudent);

        facultyTypeStackPane.setVisible(!isStudent);
        facultyTypeStackPane.setManaged(!isStudent);
    }

    // Adds action receiver to button landingPageSignInButton
    public void setActionSignInButton(EventHandler<ActionEvent> event) {
        signInPageSignInButton.setOnAction(event);
    }

    // Adds action receiver to button landingPageSignUpButton
    public void setActionSignUpButton(EventHandler<ActionEvent> event) {
        signInPageSignUpButton.setOnAction(event);
    }

    // Getters
    public TextField getIDField() {
        return signUpUserID;
    }

    public TextField getNameField() { // Getter for the Name field
        return nameField;
    }

    public TextField getCourseYearField() {
        return courseYearField;
    }

    public TextField getFacultyTypeField() {
        return facultyTypeField;
    }

    public PasswordField getPassField() {
        return signUpUserPass;
    }

    public ComboBox<String> getUserTypeBox() {
        return signUpUserTypePicker;
    }

    public Label getPromptLabel() {
        return signUpPromptLabel;
    }


    public void signUpButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignUpButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void signUpButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignUpButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void logInButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignInButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void logInButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), signInPageSignInButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
