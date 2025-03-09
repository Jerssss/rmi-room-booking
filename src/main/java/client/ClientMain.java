package client;

import client.landingpage.LandingPageController;
import client.landingpage.LandingPageView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.interfaces.Authentication;
import shared.interfaces.StudentProcessors;
import shared.interfaces.AdminProcessors;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

/**
 * ClientMain initializes the client application and connects to the RMI server.
 */
public class ClientMain extends Application {
    public static final String SERVER_IP = "172.27.230.169"; // Change this IP when switching PCs
    private static final int PORT = 1099;

    private static Authentication authService;
    private static StudentProcessors studentProcessors;
    private static AdminProcessors adminProcessors;
    private static Stage primaryStage; // Reference to the main window

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("[Client] Starting client at " + new Date());
        System.out.println("[Client] Connecting to RMI server at " + SERVER_IP + " on port " + PORT);
        System.out.println("=====================================================");

        // Start reconnection thread before launching GUI
        new Thread(ClientMain::connectToRMIServer).start();

        // Launch JavaFX GUI
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage; // Store reference to primary stage

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/landing_page.fxml"));
            Parent root = loader.load();

            LandingPageView landingPageView = loader.getController();
            if (landingPageView == null) {
                System.err.println("[ERROR] LandingPageView is NULL after FXML load!");
            } else {
                new LandingPageController(landingPageView);
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            System.out.println("[Client] GUI successfully loaded.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not load landing_page.fxml");
        }
    }

    /**
     * Connects to the RMI server and retries if the server is down.
     */
    private static void connectToRMIServer() {
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(SERVER_IP, PORT);

                authService = (Authentication) registry.lookup("authentication");
                studentProcessors = (StudentProcessors) registry.lookup("student_processors");
                adminProcessors = (AdminProcessors) registry.lookup("admin_processors");

                String clientIP = InetAddress.getLocalHost().getHostAddress();
                System.out.println("[Client] Connected to RMI Server. IP Address: " + clientIP);
                authService.logClientConnection(clientIP);

                System.out.println("[Client] Connected to Authentication, Student, and Admin Processors.");
                return; // Exit loop when connection succeeds
            } catch (NotBoundException | java.rmi.ConnectException e) {
                System.err.println("[ERROR] Server is down. Retrying in 5 seconds...");
                showServerDownMessage();
                sleep(5000);  // Retry after 5 seconds
            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                sleep(5000);  // Retry after 5 seconds
            }
        }
    }

    /**
     * Displays a popup message when the server is down, ensuring it appears above the main GUI.
     */
    private static void showServerDownMessage() {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.toFront(); // Bring main window to front before showing JOptionPane
            }
            JOptionPane.showMessageDialog(null,
                    "The server is currently down. Reconnecting...",
                    "Server Down",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    /**
     * Pauses execution for a given time.
     * @param millis Duration in milliseconds.
     */
    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static Authentication getAuthService() {
        return authService;
    }

    public static StudentProcessors getStudentProcessors() {
        return studentProcessors;
    }

    public static AdminProcessors getAdminProcessors() {
        return adminProcessors;
    }
    // Getter for SERVER_IP
    public static String getServerIP() {
        return SERVER_IP;
    }
}