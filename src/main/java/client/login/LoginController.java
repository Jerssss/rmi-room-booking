package client.login;

import client.admin.controller.AdminMainMenuController;
import client.admin.view.AdminMainMenuView;
import client.admin.model.AdminMainMenuModel;
import client.signup.SignUpController;
import client.signup.SignUpModel;
import client.signup.SignUpView;
import client.utility.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;
import java.io.IOException;
import java.rmi.NotBoundException;

public class LoginController {
    private final LoginView loginView;
    private final LoginModel loginModel;

    public LoginController(LoginView loginView, LoginModel loginModel) {
        this.loginView = loginView;
        this.loginModel = loginModel;

        this.loginView.setActionSignInButton(this::handleSignIn);
        this.loginView.setActionSignUpButton(this::redirectToSignUp);
    }

    private void handleSignIn(ActionEvent event) {
        String userID = loginView.getIDField().getText();
        String password = loginView.getPassField().getText();
        String userType = loginView.getUserTypeBox().getValue();

        if (userID.isEmpty() || password.isEmpty() || userType == null) {
            loginView.setPromptLabel("Please complete all fields.");
            loginView.setPromptLabelVisible(true);
            return;
        }

        try {
            String userName = loginModel.authenticate(userID, password, userType);

            if (userName != null) {
                loginView.setPromptLabel("Login successful!");
                loginView.setPromptLabelVisible(true);

                String sessionToken = loginModel.getSessionToken();
                SessionManager.createSession(sessionToken, userID);

                if ("Student".equalsIgnoreCase(userType)) {
                    // Redirect to student main menu
                } else {
                    redirectToAdminMainMenu(event, userName);
                }
            }
        } catch (InvalidCredentialsException e) {
            loginView.setPromptLabel("Invalid credentials. Please try again.");
            loginView.setPromptLabelVisible(true);
        } catch (AccountAlreadyLoggedIn e) {
            loginView.setPromptLabel("Account already logged in.");
            loginView.setPromptLabelVisible(true);
        } catch (NotBoundException e) {
            loginView.setPromptLabel("Server error: Authentication service not found.");
            loginView.setPromptLabelVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void redirectToAdminMainMenu(ActionEvent event, String loggedInUserName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_menu_page.fxml"));
            Parent root = fxmlLoader.load();
            AdminMainMenuView adminMainMenuView = fxmlLoader.getController();
            new AdminMainMenuController(adminMainMenuView, new AdminMainMenuModel(), loggedInUserName);

            changeScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void redirectToSignUp(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client/sign_up_page.fxml"));
            Parent root = fxmlLoader.load();
            SignUpView signUpView = fxmlLoader.getController();
            new SignUpController(signUpView, new SignUpModel());

            changeScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}