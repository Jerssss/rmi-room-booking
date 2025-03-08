package server.rmiservices;

import shared.interfaces.RMIServer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implements RMIServer to handle admin-related RMI operations.
 */
public class RMIServerService extends UnicastRemoteObject implements RMIServer {

    public RMIServerService() throws RemoteException {
        super();
    }

    @Override
    public String sendMessage(String message) throws RemoteException {
        System.out.println("[RMIServer] Received message: " + message);
        return "[Server Response] " + message;
    }

    @Override
    public void logout(String sessionToken) throws RemoteException {
        System.out.println("[RMIServer] Session ended for token: " + sessionToken);
    }

    @Override
    public String login(String userID, String password, String userType) throws RemoteException {
        return "";
    }

    @Override
    public boolean signUp(String userID, String name, String password, String userType, String courseYear, String facultyType)
            throws RemoteException {
        System.out.println("[RMIServer] Sign-up attempt for: " + name + " (" + userType + ")");
        return true; // Placeholder logic, replace with actual sign-up process
    }
}
