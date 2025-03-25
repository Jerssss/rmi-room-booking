package client.landingpage;

import client.ClientMain;
import client.admin.view.AdminMainMenuView;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
import client.signup.SignUpController;
import client.signup.SignUpModel;
import client.signup.SignUpView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import javafx.application.Platform;
import java.io.IOException;

/**
 * Controller for the landing page of the application.
 * Handles navigation to the login and sign-up pages.
 */
public class LandingPageController {

    /**
     * Constructs a LandingPageController and initializes button handlers.
     *
     * @param view the LandingPageView instance to associate with this controller
     */
    public LandingPageController(LandingPageView view) {
        if (view == null) {
            System.err.println("[CLIENT] LandingPageView is NULL! Button handlers will not be assigned.");
            return;
        }

        view.setActionSignInButton(this::handleSignIn);
        view.setActionSignUpButton(this::handleSignUp);
    }

    /**
     * Handles the "Sign In" button click event.
     * Loads the login page and switches the scene to it.
     *
     * @param event the ActionEvent triggered by the button click
     */
    private void handleSignIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            if (loginView == null) {
                System.err.println("[CLIENT] LoginView is NULL after loading FXML!");
                return;
            }

            new LoginController(loginView, new LoginModel(ClientMain.getAuthService())); // Removed extra argument

            switchScene(event, root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            showErrorDialog("Error loading the login page. Please try again.");
        }
    }

    /**
     * Handles the "Sign Up" button click event.
     * Loads the sign-up page and switches the scene to it.
     *
     * @param event the ActionEvent triggered by the button click
     */
    private void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/sign_up_page.fxml"));
            Parent root = loader.load();

            SignUpView signUpView = loader.getController();
            if (signUpView == null) {
                System.err.println("[CLIENT] SignUpView is NULL after loading FXML!");
                return;
            }

            new SignUpController(signUpView, new SignUpModel());

            switchScene(event, root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            showErrorDialog("Error loading the sign-up page. Please try again.");
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
        stage.setResizable(false);
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