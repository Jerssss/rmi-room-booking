package shared.interfaces;

import shared.callback.ClientCallbackInterface;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Authentication extends Remote {
    boolean signUp(String userID, String name, String password, String userType, String courseYear, String facultyType) throws RemoteException;

    /**
     * Authenticates a user based on credentials.
     *
     * @param userID   The user's ID.
     * @param password The user's password.
     * @param userType The type of user ("Admin" or "Student").
     * @param clientIP
     * @return Object[] containing:
     * - [0]: Login status ("SUCCESS", "INVALID_CREDENTIALS", "ALREADY_LOGGED_IN").
     * - [1]: Session token (String) if successful, otherwise null.
     * - [2]: Logged-in username (String) if successful, otherwise null.
     * @throws RemoteException             If an RMI error occurs.
     * @throws InvalidCredentialsException If credentials are incorrect.
     * @throws AccountAlreadyLoggedIn      If the account is already logged in.
     */
    Object[] login(String userID, String password, String userType, String clientIP, ClientCallbackInterface clientCallback)
            throws RemoteException, InvalidCredentialsException, AccountAlreadyLoggedIn;

    /**
     * Logs out a user and invalidates the session.
     *
     * @param sessionToken The session token of the user to be logged out.
     * @throws RemoteException If an RMI error occurs.
     */
    void logout(String sessionToken) throws RemoteException;

    void logClientConnection(String clientIP) throws RemoteException;
}
