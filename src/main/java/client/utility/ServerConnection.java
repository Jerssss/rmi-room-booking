package client.utility;

import shared.interfaces.RMIServer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerConnection {
    private RMIServer server;
    private String loggedInUserId;

    public ServerConnection(String serverIP) {
        try {
            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);
            server = (RMIServer) registry.lookup("RMIServer");
            this.loggedInUserId = null;
            System.out.println("Connected to RMI Server at " + serverIP);
        } catch (Exception e) {
            System.err.println("Error connecting to RMI server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calls the RMI login method.
     * @param userID   The user ID
     * @param password The password
     * @param userType The user type
     * @return The login response XML.
     */
    public String login(String userID, String password, String userType) {
        try {
            return server.login(userID, password, userType);
        } catch (Exception e) {
            System.err.println("RMI login error: " + e.getMessage());
            return "<Response><Status>ERROR</Status></Response>";
        }
    }
    public boolean signUp(String userID, String name, String password, String userType, String courseYear, String facultyType) {
        try {
            return server.signUp(userID, name, password, userType, courseYear, facultyType);
        } catch (Exception e) {
            System.err.println("RMI sign-up error: " + e.getMessage());
            return false;
        }
    }


    public void setLoggedInUserId(String userId) {
        this.loggedInUserId = userId;
    }

    public String sendMessage(String name, String message) {
        try {
            return server.sendMessage(message);
        } catch (Exception e) {
            System.err.println("RMI message sending error: " + e.getMessage());
            return "<Response><Status>ERROR</Status></Response>";
        }
    }


    public String getLoggedInUserId() {
        return this.loggedInUserId;
    }

    public void logout() {
        try {
            if (loggedInUserId != null) {
                server.logout(loggedInUserId);
            }
        } catch (Exception e) {
            System.err.println("Logout failed: " + e.getMessage());
        }
    }
}
