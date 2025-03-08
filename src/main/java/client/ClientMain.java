package client;

import client.landingpage.LandingPageController;
import client.landingpage.LandingPageView;
import shared.interfaces.Authentication;
import shared.interfaces.StudentProcessors;
import shared.interfaces.AdminProcessors;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    public static final String SERVER_IP = "192.168.5.38"; // Change this IP when switching PCs
    private static final int PORT = 1099;

    private static Authentication authService;
    private static StudentProcessors studentProcessors;
    private static AdminProcessors adminProcessors;

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("[Client] Starting client at " + new Date());
        System.out.println("[Client] Connecting to RMI server at " + SERVER_IP + " on port " + PORT);
        System.out.println("=====================================================");

        connectToRMIServer();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
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
     * Connects to the RMI server.
     */
    private static void connectToRMIServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(SERVER_IP, PORT);

            authService = (Authentication) registry.lookup("authentication");
            studentProcessors = (StudentProcessors) registry.lookup("student_processors");
            adminProcessors = (AdminProcessors) registry.lookup("admin_processors");

            // Get Client IP Address
            String clientIP = InetAddress.getLocalHost().getHostAddress();

            // Log client connection immediately after connection
            System.out.println("[Client] Connected to RMI Server. IP Address: " + clientIP);
            authService.logClientConnection(clientIP); // Make sure this method exists in Authentication interface

            System.out.println("[Client] Connected to Authentication, Student, and Admin Processors.");
        } catch (NotBoundException | IOException e) {
            System.err.println("[ERROR] Could not connect to RMI services: " + e.getMessage());
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