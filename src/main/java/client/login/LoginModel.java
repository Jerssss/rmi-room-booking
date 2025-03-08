package client.login;

import shared.interfaces.Authentication;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;

/**
 * Handles login authentication via RMI.
 */
public class LoginModel {
    private final Authentication authService;
    private String sessionToken;
    private String loggedInUserName;

    /**
     * Constructor accepts an Authentication service instance.
     * @param authService RMI Authentication service.
     */
    public LoginModel(Authentication authService) {
        this.authService = authService;
    }

    /**
     * Authenticates the user and retrieves their name if successful.
     *
     * @param userID   The user ID
     * @param password The password
     * @param userType The user type ("Admin" or "Student")
     * @return The user's name if authentication is successful, otherwise null.
     * @throws IOException              If communication with the server fails.
     * @throws NotBoundException        If the authentication service is not found in the registry.
     * @throws InvalidCredentialsException If the credentials are incorrect.
     * @throws AccountAlreadyLoggedIn   If the account is already logged in.
     */
    public String authenticate(String userID, String password, String userType)
            throws IOException, NotBoundException, InvalidCredentialsException, AccountAlreadyLoggedIn {

        String clientIP = InetAddress.getLocalHost().getHostAddress();  // Get client IP
        Object[] serverResponse = authService.login(userID, password, userType, clientIP);

        // Parse the response
        String status = (String) serverResponse[0];
        sessionToken = (String) serverResponse[1];
        loggedInUserName = (String) serverResponse[2];

        switch (status) {
            case "SUCCESS":
                return loggedInUserName;
            case "ALREADY_LOGGED_IN":
                throw new AccountAlreadyLoggedIn("Account is already logged in.");
            default:
                throw new InvalidCredentialsException("Invalid username or password.");
        }
    }

    public String getSessionToken() {
        return this.sessionToken;
    }
}