package client.signup;

import shared.interfaces.Authentication;
import client.ClientMain;
import java.rmi.RemoteException;

/**
 * Handles sign-up logic and communicates with the RMI Authentication Service.
 */
public class SignUpModel {
    private final Authentication authService;

    public SignUpModel() {
        // Access authentication service via the instance of ClientMain
        this.authService = ClientMain.getAuthService();
    }

    /**
     * Registers a new user via the RMI Authentication Service.
     * @throws RemoteException If a communication error occurs.
     * @throws IllegalArgumentException If input data is invalid.
     * @throws RuntimeException If the user already exists.
     */
    public void register(String userID, String name, String password, String courseYear)
            throws RemoteException {
        // Hardcode userType to "Student" and facultyType to empty
        if (authService == null) throw new RemoteException("Server error");
        authService.signUp(userID, name, password, "Student", courseYear, "");
    }
}
