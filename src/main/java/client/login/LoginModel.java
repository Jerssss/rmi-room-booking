package client.login;

import client.utility.ServerConnectionManager;
import client.utility.ServerConnection;
import javafx.application.Platform;

import javax.swing.JOptionPane;

public class LoginModel {
    private ServerConnection serverConnection;
    private String sessionToken;
    private String loggedInUserName;

    public LoginModel() {
        serverConnection = ServerConnectionManager.getConnection();
    }

    /**
     * Authenticates the user and retrieves their name if successful.
     * @param userID   The user ID
     * @param password The password
     * @param userType The user type ("Admin" or "Student")
     * @return The user's name if authentication is successful, otherwise null.
     */
    public String authenticateAndGetName(String userID, String password, String userType) {
        if (serverConnection == null) {
            showErrorDialog("Server is not available. Please try again later.");
            return null;
        }

        try {
            // Call the RMI login method
            String response = serverConnection.login(userID, password, userType);
            System.out.println("[LOGIN] Server response: " + response); // Debugging output

            // Extract session token and user name
            this.sessionToken = extractField(response, "<SessionToken>", "</SessionToken>");
            this.loggedInUserName = extractField(response, "<Name>", "</Name>");

            // Validate login status
            String status = extractField(response, "<Status>", "</Status>");
            if ("SUCCESS".equalsIgnoreCase(status)) {
                serverConnection.setLoggedInUserId(userID); // Store user ID in active session
                return loggedInUserName;
            } else if ("ALREADY_LOGGED_IN".equalsIgnoreCase(status)) {
                showErrorDialog("Account is already logged in.");
            } else {
                showErrorDialog("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            showErrorDialog("Lost connection to the server.");
            e.printStackTrace();
        }
        return null;
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    private String extractField(String xml, String startTag, String endTag) {
        int start = xml.indexOf(startTag);
        int end = xml.indexOf(endTag, start);
        return (start >= 0 && end > start) ? xml.substring(start + startTag.length(), end).trim() : null;
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE));
    }
}
