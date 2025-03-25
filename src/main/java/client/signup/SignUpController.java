package client.signup;

import client.ClientMain;
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
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * Controller for the sign-up functionality of the application.
 * Handles user registration and navigation to the login page upon successful sign-up.
 */
public class SignUpController {
    private final SignUpView signUpView; // The associated view for the sign-up page
    private final SignUpModel signUpModel; // The associated model for the sign-up page
    private final AdminMainMenuView adminMainMenuView = new AdminMainMenuView(); // Admin main menu view (unused in this context)

    /**
     * Constructs a SignUpController and initializes button handlers.
     *
     * @param signUpView the SignUpView instance to associate with this controller
     * @param signUpModel the SignUpModel instance to associate with this controller
     */
    public SignUpController(SignUpView signUpView, SignUpModel signUpModel) {
        this.signUpView = signUpView;
        this.signUpModel = signUpModel;

        // Handle Sign In button click
        this.signUpView.setActionSignInButton(this::redirectToLogin);

        // Handle Sign Up button click
        this.signUpView.setActionSignUpButton(event -> {
            try {
                handleSignUp(event);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Redirects the user to the login page.
     *
     * @param event the ActionEvent triggered by the button click
     */
    private void redirectToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            if (loginView == null) {
                System.err.println("[ERROR] LoginView is NULL! Check FXML file.");
                return;
            }

            // Use static method to get authentication service
            LoginModel loginModel = new LoginModel(ClientMain.getAuthService());
            new LoginController(loginView, loginModel);

            switchScene(event, root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            showErrorDialog("Error loading the login page. Please try again.");
        }
    }

    /**
     * Handles the "Sign Up" button click event.
     * Validates user input and registers the user if all fields are valid.
     *
     * @param event the ActionEvent triggered by the button click
     * @throws ParserConfigurationException if there is an error in the XML parser configuration
     */
    private void handleSignUp(ActionEvent event) throws ParserConfigurationException {
        // Existing field collection
        String userID = signUpView.getIDField().getText();
        String name = signUpView.getNameField().getText();
        String pass = signUpView.getPassField().getText();
        String courseYear = signUpView.getCourseYearField().getText();

        // Update validation (remove userType check)
        if (userID.isEmpty() || name.isEmpty() || pass.isEmpty() || courseYear.isEmpty()) {
            signUpView.getPromptLabel().setText("Please complete all fields.");
            signUpView.getPromptLabel().setVisible(true);
            return;
        }

        if (!userID.matches("\\d{1,7}")) { // Validate ID is up to 7 digits
            signUpView.getPromptLabel().setText("ID must be a numeric value with up to 7 digits.");
            signUpView.getPromptLabel().setVisible(true);
            return;
        }

        signUpView.getPromptLabel().setVisible(false); // Hide error prompt if all is good

        try {
            // Call the register method in SignUpModel
            signUpModel.register(userID, name, pass, courseYear);

            // If no exception, registration was successful
            signUpView.getPromptLabel().setText("Registration successful!");
            signUpView.getPromptLabel().setVisible(true);
            redirectToLogin(event);
        } catch (RemoteException e) {
            showErrorDialog("Registration failed: Server error. Please try again.");
        } catch (IllegalArgumentException e) {
            signUpView.getPromptLabel().setText(e.getMessage());
            signUpView.getPromptLabel().setVisible(true);
        } catch (RuntimeException e) {
            signUpView.getPromptLabel().setText("Registration failed: " + e.getMessage());
            signUpView.getPromptLabel().setVisible(true);
        }
    }

    /**
     * Switches the current scene to the specified root node.
     *
     * @param event the ActionEvent triggered by the button click
     * @param root the root node of the new scene
     */
    private void switchScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Displays an error dialog with the specified message.
     *
     * @param message the error message to display
     */
    private void showErrorDialog(String message) {
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE));
    }
}