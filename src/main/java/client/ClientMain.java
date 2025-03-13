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

public class ClientMain extends Application {
    private static final int PORT = 1099;
    private static Authentication authService;
    private static StudentProcessors studentProcessors;
    private static AdminProcessors adminProcessors;
    private static RMIServer rmiServer;
    private static Stage primaryStage;
    private static boolean connected = false;
    private static String serverIP;

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("[Client] Starting client at " + new Date());

        retrieveServerIP();
        waitForServerConnection();
        connectToRMIServer();

        launch(args);
    }

    private static void retrieveServerIP() {
        try {
            Registry tempRegistry = LocateRegistry.getRegistry("localhost", PORT);
            RMIServer rmiServer = (RMIServer) tempRegistry.lookup("RMIServer");
            serverIP = rmiServer.getServerIP();
            System.out.println("[Client] Retrieved Server IP: " + serverIP);
        } catch (Exception e) {
            System.err.println("[ERROR] Could not retrieve server IP.");
            serverIP = "localhost"; // Fallback
        }
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

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

            new Thread(ClientMain::monitorServerStatus).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not load landing_page.fxml");
        }
    }

    private static void connectToRMIServer() {
        while (true) {
            try {
                Registry registry = LocateRegistry.getRegistry(serverIP, PORT);
                rmiServer = (RMIServer) registry.lookup("RMIServer");
                authService = (Authentication) registry.lookup("authentication");
                studentProcessors = (StudentProcessors) registry.lookup("student_processors");
                adminProcessors = (AdminProcessors) registry.lookup("admin_processors");

                System.out.println("[Client] Successfully connected to the RMI Server.");
                connected = true;
                return;
            } catch (NotBoundException | RemoteException e) {
                System.err.println("[ERROR] Unable to reach the server. Retrying in 5 seconds...");
                showServerDownMessage("The server is currently down. Retrying...");
                sleep(5000);
            }
        }
    }

    private static void waitForServerConnection() {
        while (true) {
            try {
                Registry tempRegistry = LocateRegistry.getRegistry(serverIP, PORT);
                RMIServer testServer = (RMIServer) tempRegistry.lookup("RMIServer");
                System.out.println("[Client] Server detected, proceeding with application launch.");
                return;
            } catch (Exception e) {
                if (!connected) {
                    showServerDownMessage("The server is down. Trying to reconnect...");
                    connected = false;
                }
                System.err.println("[ERROR] Unable to reach the server. Retrying in 5 seconds...");
                sleep(5000);
            }
        }
    }

    private static void monitorServerStatus() {
        while (true) {
            if (connected) {
                try {
                    rmiServer.getServerIP();
                } catch (RemoteException e) {
                    System.err.println("[ERROR] Lost connection to the server.");
                    showServerDownMessage("The server is down. Attempting to reconnect...");
                    connected = false;

                    while (!connected) {
                        System.err.println("[ERROR] Trying to reconnect...");
                        connectToRMIServer();
                        sleep(5000);
                    }

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

    private static void showServerDownMessage(String message) {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.toFront();
            }
            JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.WARNING_MESSAGE);
        });
    }

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


    public static String getServerIP() {
        return serverIP;
    }
}