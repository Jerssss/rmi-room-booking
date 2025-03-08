package client.admin.model;

import client.utility.ServerConnection;
import client.utility.ServerConnectionManager;

public class AdminMainMenuModel {
    private ServerConnection serverConnection;

    public AdminMainMenuModel() {
        serverConnection = ServerConnectionManager.getConnection();
        if (serverConnection == null) {
            System.err.println("ERROR: Server connection is NULL! Check RMI registry.");
        }
    }

    public String sendMessageToServer(String name, String message) {
        if (serverConnection == null) {
            System.err.println("ERROR: Cannot send message, server connection is NULL.");
            return "<Response><Status>ERROR</Status></Response>";
        }
        return serverConnection.sendMessage(name, message);
    }
}

