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

import java.io.IOException;

public class LoginController {
    private final LoginView loginView;
    private final LoginModel loginModel;

    public LoginController(LoginView loginView, LoginModel loginModel, AdminMainMenuView adminMainMenuView) {
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

        String userName = loginModel.authenticateAndGetName(userID, password, userType);
        if (userName != null) {
            loginView.setPromptLabel("Login successful!");
            loginView.setPromptLabelVisible(true);

            String sessionToken = loginModel.getSessionToken();
            SessionManager.createSession(sessionToken, userID);

            if ("Student".equalsIgnoreCase(userType)) {
                // redirectToStudentMainMenu(event, userName, sessionToken);
            } else {
                redirectToAdminMainMenu(event, userName);
            }
        } else {
            loginView.setPromptLabel("Invalid credentials. Please try again.");
            loginView.setPromptLabelVisible(true);
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
            System.out.println("Error loading Admin Main Menu GUI: " + e.getMessage());
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
            System.out.println("Error loading Sign-Up GUI: " + e.getMessage());
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
