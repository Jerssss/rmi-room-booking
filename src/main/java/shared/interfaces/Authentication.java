package shared.interfaces;

import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Authentication extends Remote {

    /**
     * Registers a new user (sign-up).
     *
     * @param userID      The unique ID of the user.
     * @param name        The name of the user.
     * @param password    The password of the user.
     * @param userType    The type of user (e.g., "Admin" or "Student").
     * @param courseYear  The course year of the student (if applicable).
     * @param facultyType The faculty type of the admin (if applicable).
     * @return True if registration is successful, false otherwise.
     * @throws RemoteException If a communication-related exception occurs during the remote method call.
     */
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
    Object[] login(String userID, String password, String userType, String clientIP)
            throws RemoteException, InvalidCredentialsException, AccountAlreadyLoggedIn;

    /**
     * Logs out a user and invalidates the session.
     *
     * @param sessionToken The session token of the user to be logged out.
     * @throws RemoteException If an RMI error occurs.
     */
    void logout(String sessionToken) throws RemoteException;

    /**
     * Logs a client connection for tracking purposes.
     *
     * @param clientIP The IP address of the client.
     * @throws RemoteException If an RMI error occurs.
     */
    void logClientConnection(String clientIP) throws RemoteException;

    /**
     * Implements the heartbeat mechanism to check the server's availability.
     * This method is called periodically to ensure the server is responsive.
     *
     * @throws RemoteException If a communication-related exception occurs during the remote method call.
     */
    void heartbeat() throws RemoteException; // Add this method
}
