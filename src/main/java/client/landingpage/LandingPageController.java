package client.landingpage;

import client.admin.view.AdminMainMenuView;
import client.login.LoginController;
import client.login.LoginModel;
import client.login.LoginView;
import client.utility.ServerConnection;
import client.utility.ServerConnectionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import javafx.application.Platform;
import java.io.IOException;

public class LandingPageController {
    private final ServerConnection serverConnection;

    public LandingPageController(LandingPageView view) {
        serverConnection = ServerConnectionManager.getConnection();

        if (view == null) {
            System.err.println("[ERROR] LandingPageView is NULL! Controller is not receiving the view.");
            return;
        }

        view.setActionSignInButton(this::handleSignIn);
        view.setActionSignUpButton(this::handleSignUp);
    }

    private void handleSignIn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/login_page.fxml"));
            Parent root = loader.load();

            LoginView loginView = loader.getController();
            System.out.println("[DEBUG] LoginView loaded: " + (loginView != null));

            new LoginController(loginView, new LoginModel(), new AdminMainMenuView());

            switchScene(event, root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            showErrorDialog("Error loading the login page. Please try again.");
        }
    }

    private void handleSignUp(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/sign_up_page.fxml"));
            Parent root = loader.load();

            SignUpView signUpView = loader.getController();
            new SignUpController(signUpView, new SignUpModel());

            switchScene(event, root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            showErrorDialog("Error loading the sign-up page. Please try again.");
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
