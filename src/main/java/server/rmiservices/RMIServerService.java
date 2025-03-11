package server.rmiservices;

import shared.interfaces.RMIServer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implements RMIServer to handle admin-related RMI operations and provide server information.
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

    /**
     * Returns the server's IP address so clients can dynamically connect.
     * @return The server's actual IP address.
     * @throws RemoteException if an RMI error occurs.
     */
    @Override
    public String getServerIP() throws RemoteException {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
