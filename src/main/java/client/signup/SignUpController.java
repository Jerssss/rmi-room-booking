package client.signup;

import client.admin.view.AdminMainMenuView;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class SignUpController {
    private final SignUpView signUpView;
    private final SignUpModel signUpModel;
    private final AdminMainMenuView adminMainMenuView = new AdminMainMenuView();

    public SignUpController(SignUpView signUpView, SignUpModel signUpModel) {
        this.signUpView = signUpView;
        this.signUpModel = signUpModel;

        // Handle Sign In button click
        this.signUpView.setActionSignInButton(this::redirectToLogin);

        // Handle Sign Up button clickS
        this.signUpView.setActionSignUpButton(event -> {
            try {
                handleSignUp(event);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void redirectToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            new LoginController(loginView, new LoginModel(), new AdminMainMenuView());

            switchScene(event, root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            showErrorDialog("Error loading the login page. Please try again.");
        }
    }

    private void handleSignUp(ActionEvent event) throws ParserConfigurationException {

        // Store field and dropdown contents
        String userID = signUpView.getIDField().getText();
        String name = signUpView.getNameField().getText();
        String pass = signUpView.getPassField().getText();
        String userType = signUpView.getUserTypeBox().getValue();
        String courseYear = signUpView.getCourseYearField().getText();
        String facultyType = signUpView.getFacultyTypeField().getText();

        // Validate input fields
        if (userID.isEmpty() || name.isEmpty() || pass.isEmpty() || userType == null) {
            signUpView.getPromptLabel().setText("Please accomplish all fields.");
            signUpView.getPromptLabel().setVisible(true);
        } else if (!userID.matches("\\d{1,7}")) { // Validate ID is up to 7 digits
            signUpView.getPromptLabel().setText("ID must be a numeric value with up to 7 digits.");
            signUpView.getPromptLabel().setVisible(true);
        } else {
            signUpView.getPromptLabel().setVisible(false); // Hide error prompt if all is good

            // Call the register method in SignUpModel
            boolean isRegistered = signUpModel.register(userID, name, pass, userType, courseYear, facultyType);

            if (isRegistered) {
                signUpView.getPromptLabel().setText("Registration successful!");
                signUpView.getPromptLabel().setVisible(true);
                redirectToLogin(event);
            } else {
                signUpView.getPromptLabel().setText("Registration failed. Please try again.");
                signUpView.getPromptLabel().setVisible(true);
            }
        }
    }
    private void switchScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE));
    }
}


