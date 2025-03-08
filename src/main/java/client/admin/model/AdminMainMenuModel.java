package client.admin.model;

import shared.interfaces.RMIServer;
import client.ClientMain;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

/**
 * Model for Admin Main Menu, handling server interactions.
 */
public class AdminMainMenuModel {
    private RMIServer server;

    /**
     * Constructor that initializes the connection to the RMI server.
     */
    public AdminMainMenuModel() {
        connectToServer(); // Attempt to establish the connection
    }

    /**
     * Tries to connect to the RMI server once.
     */
    private void connectToServer() {
        try {
            String serverIP = ClientMain.getServerIP();
            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);

            // Debug: Print all registered RMI services
            System.out.println("[DEBUG] Available RMI services: " + Arrays.toString(registry.list()));

            // Attempt to lookup RMIServer
            server = (RMIServer) registry.lookup("RMIServer");

            System.out.println("[RMI] Connected to RMIServer at " + serverIP);
        } catch (Exception e) {
            System.err.println("[ERROR] Could not connect to RMIServer: " + e.getMessage());
            server = null;
        }
    }

    /**
     * Sends a message to the RMI server.
     */
    public String sendMessageToServer(String message) {
        if (server == null) {
            System.err.println("[ERROR] Cannot send message, RMI server is NULL. Reconnecting...");
            connectToServer();
        }

        try {
            return server != null ? server.sendMessage(message)
                    : "<Response><Status>ERROR</Status></Response>";
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI sendMessage error: " + e.getMessage());
            return "<Response><Status>ERROR</Status></Response>";
        }
    }
}
