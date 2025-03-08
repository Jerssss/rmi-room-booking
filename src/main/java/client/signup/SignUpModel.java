package client.signup;

import client.utility.ServerConnectionManager;
import client.utility.ServerConnection;
import javax.swing.JOptionPane;
import javafx.application.Platform;

public class SignUpModel {
    private ServerConnection serverConnection;

    public SignUpModel() {
        serverConnection = ServerConnectionManager.getConnection();
    }

    public boolean register(String userID, String name, String password, String userType, String courseYear, String facultyType) {
        if (serverConnection == null) {
            showErrorDialog("Server is not available. Please try again later.");
            return false;
        }

        try {
            // Call RMI signUp method
            boolean success = serverConnection.signUp(userID, name, password, userType, courseYear, facultyType);

            if (success) {
                System.out.println("DEBUG: Registration successful for user -> " + userID);
            } else {
                System.out.println("DEBUG: Registration failed for user -> " + userID);
            }

            return success;
        } catch (Exception e) {
            showErrorDialog("Lost connection to the server.");
            return false;
        }
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE));
    }
}
