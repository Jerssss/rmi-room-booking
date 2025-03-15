package client.login;

import client.ClientMain;
import client.admin.controller.AdminMainMenuController;
import client.admin.view.AdminMainMenuView;
import client.admin.model.AdminMainMenuModel;
import client.signup.SignUpController;
import client.signup.SignUpModel;
import client.signup.SignUpView;
import client.student.controller.StudentMainMenuController;
import client.student.model.StudentMainMenuModel;
import client.student.view.StudentMainMenuView;
import client.utility.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;

import javax.swing.*;
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

        // Check if authService is initialized before attempting authentication
        if (ClientMain.getAuthService() == null) {
            loginView.setPromptLabel("Server connection error. Please restart the client.");
            loginView.setPromptLabelVisible(true);
            JOptionPane.showMessageDialog(null,
                    "The server is unreachable. Please try again later.",
                    "Server Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Retrieve client IP
            String clientIP = java.net.InetAddress.getLocalHost().getHostAddress();

            // Call the login method with the correct arguments
            Object[] loginResponse = ClientMain.getAuthService().login(userID, password, userType, clientIP);

            String loginStatus = (String) loginResponse[0];
            String sessionToken = (String) loginResponse[1];
            String userName = (String) loginResponse[2];

            if ("SUCCESS".equals(loginStatus)) {
                loginView.setPromptLabel("Login successful!");
                loginView.setPromptLabelVisible(true);
                SessionManager.createSession(sessionToken, userID);

                if ("Student".equalsIgnoreCase(userType)) {
                    redirectToStudentMainMenu(event, userName,userID);
                } else {
                    redirectToAdminMainMenu(event, userName);
                }
            } else if ("INVALID_CREDENTIALS".equals(loginStatus)) {
                loginView.setPromptLabel("Invalid credentials. Please try again.");
                loginView.setPromptLabelVisible(true);
            } else if ("ALREADY_LOGGED_IN".equals(loginStatus)) {
                loginView.setPromptLabel("Account already logged in.");
                loginView.setPromptLabelVisible(true);
            }
        } catch (InvalidCredentialsException e) {
            loginView.setPromptLabel("Invalid credentials. Please try again.");
            loginView.setPromptLabelVisible(true);
        } catch (AccountAlreadyLoggedIn e) {
            loginView.setPromptLabel("Account already logged in.");
            loginView.setPromptLabelVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "A network error occurred while logging in. Please try again.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
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

    private void redirectToStudentMainMenu(ActionEvent event, String loggedInUserName, String userID) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client/student_main_menu.fxml"));
            Parent root = fxmlLoader.load();
            StudentMainMenuView studentMainMenuView = fxmlLoader.getController();
            if (studentMainMenuView == null) {
                System.err.println("[ERROR] StudentMainMenuView is NULL after loading FXML!");
                return;
            }
            System.out.println("[DEBUG] StudentMainMenuView successfully loaded.");
            new StudentMainMenuController(studentMainMenuView, new StudentMainMenuModel(), loggedInUserName, userID);
            changeScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Failed to load Student Main Menu!");
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
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}