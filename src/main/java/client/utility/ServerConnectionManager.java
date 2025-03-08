package client.utility;

import client.ClientMain;
import shared.interfaces.RMIServer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerConnectionManager {
    private static ServerConnection serverConnection;

    public static void initializeConnection() {
        if (serverConnection == null) {
            String serverIP = ClientMain.getServerIP(); // Ensure the IP is retrieved
            serverConnection = new ServerConnection(serverIP); // Create RMI connection
        }
    }

    public static ServerConnection getConnection() {
        if (serverConnection == null) {
            initializeConnection(); // Ensure connection is initialized
        }
        return serverConnection;
    }

    public static void closeConnection() {
        if (serverConnection != null) {
            //serverConnection.close();
            serverConnection = null;
        }
    }
}
