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
import util.JSONUtility;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Controller for the login functionality of the application.
 * Handles user authentication and navigation to the appropriate main menu based on user type.
 */
public class LoginController {
    private final LoginView loginView; // The associated view for the login page
    private final LoginModel loginModel; // The associated model for the login page

    /**
     * Constructs a LoginController and initializes button handlers.
     *
     * @param loginView the LoginView instance to associate with this controller
     * @param loginModel the LoginModel instance to associate with this controller
     */
    public LoginController(LoginView loginView, LoginModel loginModel) {
        this.loginView = loginView;
        this.loginModel = loginModel;

        this.loginView.setActionSignInButton(this::handleSignIn);
        this.loginView.setActionSignUpButton(this::redirectToSignUp);
    }

    /**
     * Handles the "Sign In" button click event.
     * Validates user credentials and redirects to the appropriate main menu upon successful login.
     *
     * @param event the ActionEvent triggered by the button click
     */
    private void handleSignIn(ActionEvent event) {
        String userID = loginView.getIDField().getText();
        String password = loginView.getPassField().getText();
        String userType = loginView.getUserTypeBox().getValue();

        if (userID.isEmpty() || password.isEmpty() || userType == null) {
            loginView.setPromptLabel("Please complete all fields.");
            loginView.setPromptLabelVisible(true);
            return;
        }

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
            String clientIP = java.net.InetAddress.getLocalHost().getHostAddress();
            Object[] loginResponse = ClientMain.getAuthService().login(userID, password, userType, clientIP);

            String loginStatus = (String) loginResponse[0];
            String sessionToken = (String) loginResponse[1];
            String userName = (String) loginResponse[2];

            if ("SUCCESS".equals(loginStatus)) {
                loginView.setPromptLabel("Login successful!");
                loginView.setPromptLabelVisible(true);
                SessionManager.createSession(sessionToken, userID);

                if ("Student".equalsIgnoreCase(userType)) {
                    redirectToStudentMainMenu(event, userName, userID);
                } else {
                    redirectToAdminMainMenu(event, userName);
                }
            } else {
                handleLoginFailure(loginStatus);
            }
        } catch (InvalidCredentialsException e) {
            handleLoginFailure("INVALID_CREDENTIALS");
        } catch (AccountAlreadyLoggedIn e) {
            handleLoginFailure("ALREADY_LOGGED_IN");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "A network error occurred while logging in. Please try again.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles login failures by displaying appropriate error messages.
     *
     * @param status the status of the login attempt
     */
    private void handleLoginFailure(String status) {
        switch (status) {
            case "INVALID_CREDENTIALS":
                loginView.setPromptLabel("Invalid credentials. Please try again.");
                break;
            case "ALREADY_LOGGED_IN":
                loginView.setPromptLabel("Account already logged in.");
                break;
            default:
                loginView.setPromptLabel("Login failed. Please try again.");
                break;
        }
        loginView.setPromptLabelVisible(true);
    }

    /**
     * Redirects the user to the admin main menu upon successful login.
     *
     * @param event the ActionEvent triggered by the button click
     * @param loggedInUserName the name of the logged-in user
     */
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

    /**
     * Redirects the user to the student main menu upon successful login.
     *
     * @param event the ActionEvent triggered by the button click
     * @param loggedInUserName the name of the logged-in user
     * @param userID the ID of the logged-in user
     */
    private void redirectToStudentMainMenu(ActionEvent event, String loggedInUserName, String userID) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client/student_main_menu.fxml"));
            Parent root = fxmlLoader.load();
            StudentMainMenuView studentMainMenuView = fxmlLoader.getController();
            if (studentMainMenuView == null) {
                System.err.println("[CLIENT] StudentMainMenuView is NULL after loading FXML!");
                return;
            }
            new StudentMainMenuController(studentMainMenuView, new StudentMainMenuModel(), loggedInUserName, userID);
            changeScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[CLIENT] Failed to load Student Main Menu!");
        }
    }

    /**
     * Redirects the user to the sign-up page.
     *
     * @param event the ActionEvent triggered by the button click
     */
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

    /**
     * Changes the current scene to the specified root node.
     *
     * @param event the ActionEvent triggered by the button click
     * @param root the root node of the new scene
     */
    private void changeScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}