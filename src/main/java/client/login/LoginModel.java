package client.login;

import shared.interfaces.Authentication;
import shared.callback.ClientCallbackInterface;
import client.utility.ClientCallbackImpl;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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

        String clientIP = InetAddress.getLocalHost().getHostAddress();

        try {
            ClientCallbackInterface clientCallback = new ClientCallbackImpl();
            Object[] serverResponse = authService.login(userID, password, userType, clientIP, clientCallback);

            if (serverResponse == null || serverResponse.length < 3) {
                System.out.println("[ERROR] Invalid response from server.");
                return null;
            }

            String status = (String) serverResponse[0];
            sessionToken = (String) serverResponse[1];
            loggedInUserName = (String) serverResponse[2];

            if ("SUCCESS".equals(status)) {
                System.out.println("=====================================================");
                System.out.println("[Client] Login successful!");
                System.out.println("[Client] User: " + loggedInUserName + " (ID: " + userID + ")");
                System.out.println("[Client] User Type: " + userType);
                System.out.println("[Client] IP Address: " + clientIP);
                System.out.println("[Client] Session Token: " + sessionToken);
                System.out.println("=====================================================");
                return loggedInUserName;
            } else {
                System.out.println("[ERROR] Login failed with status: " + status);
                throw new InvalidCredentialsException("Invalid username or password.");
            }
        } catch (RemoteException e) {
            throw new IOException("Remote error during login: " + e.getMessage());
        }
    }


    public String getSessionToken() {
        return this.sessionToken;
    }
}
