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
     * @return true if successful, false otherwise.
     */
    public boolean register(String userID, String name, String password, String userType, String courseYear, String facultyType) throws RemoteException {
        if (authService == null) {
            System.err.println("[ERROR] Authentication service is NULL. Server might be down.");
            return false;
        }
        return authService.signUp(userID, name, password, userType, courseYear, facultyType);
    }
}
