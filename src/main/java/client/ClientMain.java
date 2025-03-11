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
import shared.interfaces.RMIServer;
import shared.interfaces.StudentProcessors;
import shared.interfaces.AdminProcessors;

import javax.swing.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

/**
 * ClientMain initializes the client application and connects to the RMI server.
 */
public class ClientMain extends Application {
    public static final String SERVER_IP = "127.0.0.1"; // Change this IP when switching PCs
    private static final int PORT = 1099;

    private static Authentication authService;
    private static StudentProcessors studentProcessors;
    private static AdminProcessors adminProcessors;
    private static RMIServer rmiServer;
    private static Stage primaryStage; // Reference to the main window
    private static boolean connected = false;

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("[Client] Starting client at " + new Date());

        new Thread(() -> {
            try {
                Registry tempRegistry = LocateRegistry.getRegistry("localhost", PORT);
                RMIServer rmiServer = (RMIServer) tempRegistry.lookup("RMIServer");

                // Step 2: Fetch the actual IP address of the server
                String actualServerIP = rmiServer.getServerIP();
                System.out.println("[Client] Connecting to RMI server at " + actualServerIP + " on port " + PORT);
            } catch (Exception e) {
                System.err.println("[ERROR] Could not retrieve server IP.");
            }
        }).start();

        System.out.println("=====================================================");

        // Ensure the server is available before launching the GUI
        waitForServerConnection();

        connectToRMIServer();

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

            System.out.println("[Client] WELCOME TO LENDIFY");

            // Start monitoring server status in a separate thread
            new Thread(ClientMain::monitorServerStatus).start();
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
                // Step 1: Connect to a known address (localhost or any preset IP) to fetch the real server IP
                Registry tempRegistry = LocateRegistry.getRegistry("localhost", PORT);
                rmiServer = (RMIServer) tempRegistry.lookup("RMIServer");

                // Step 2: Fetch the actual IP address of the server
                String actualServerIP = rmiServer.getServerIP();
                System.out.println("[Client] Discovered Server IP: " + actualServerIP);

                // Step 3: Now connect to the real server using the retrieved IP
                Registry registry = LocateRegistry.getRegistry(actualServerIP, PORT);

                authService = (Authentication) registry.lookup("authentication");
                studentProcessors = (StudentProcessors) registry.lookup("student_processors");
                adminProcessors = (AdminProcessors) registry.lookup("admin_processors");

                System.out.println("[Client] Successfully connected to the RMI Server.");
                connected = true;
                return; // Exit loop when connection succeeds
            } catch (NotBoundException e) {
                System.err.println("[ERROR] Unable to reach the server. Retrying in 5 seconds...");
                showServerDownMessage("The server is currently down. Retrying...");
                sleep(5000);
            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                sleep(5000);
            }
        }
    }


    private static void waitForServerConnection() {
        while (true) {
            try {
                Registry tempRegistry = LocateRegistry.getRegistry("localhost", PORT);
                RMIServer testServer = (RMIServer) tempRegistry.lookup("RMIServer");

                // If the lookup succeeds, the server is up
                System.out.println("[Client] Server detected, proceeding with application launch.");
                return; // Exit loop and continue launching GUI
            } catch (Exception e) {
                // Show JOptionPane only once when connection fails
                if (!connected) {
                    showServerDownMessage("The server is down. Trying to reconnect...");
                    connected = false;
                }

                System.err.println("[ERROR] Unable to reach the server. Retrying in 5 seconds...");
                sleep(5000); // Retry after 5 seconds
            }
        }
    }

    /**
     * Monitors the server connection and notifies the user if it goes down.
     */
    private static void monitorServerStatus() {
        while (true) {
            if (connected) {
                try {
                    rmiServer.getServerIP();
                } catch (RemoteException e) {
                    System.err.println("[ERROR] Lost connection to the server.");
                    showServerDownMessage("The server is down. Attempting to reconnect...");
                    connected = false;

                    // Keep retrying until reconnected
                    while (!connected) {
                        System.err.println("[ERROR] Trying to reconnect...");
                        connectToRMIServer();
                        sleep(5000);
                    }

                    // Once reconnected, notify the user
                    Platform.runLater(() -> JOptionPane.showMessageDialog(null,
                            "Reconnected to the server!",
                            "Connection Restored",
                            JOptionPane.INFORMATION_MESSAGE));

                    System.out.println("[Client] Reconnected to the RMI Server.");
                }
            }
            sleep(5000);
        }
    }



    /**
     * Displays a popup message when the server is down.
     */
    private static void showServerDownMessage(String message) {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.toFront();
            }
            JOptionPane.showMessageDialog(null,
                    message,
                    "Connection Error",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    /**
     * Logs out the client and closes the application when the server is down.
     */
    private static void disconnectClient() {
        Platform.runLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "You have been logged out due to server disconnection.",
                    "Disconnected",
                    JOptionPane.ERROR_MESSAGE);

            // Simulate logout process
            System.out.println("[LOGOUT] Successfully logged out due to server shutdown.");
            System.out.println("=====================================================");

            // Close the application
            System.exit(0);
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