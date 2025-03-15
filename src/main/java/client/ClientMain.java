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
import shared.interfaces.student.StudentProcessors;
import shared.interfaces.admin.AdminProcessors;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

/**
 * ClientMain initializes the client application and connects to the RMI server.
 */
public class ClientMain extends Application {
    private static final String SERVER_IP = "172.27.102.14";
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

        new Thread(ClientMain::connectToRMIServer).start();
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
            stage.setResizable(false);
            stage.show();

            System.out.println("[Client] WELCOME TO LENDIFY");
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

                authService.logClientConnection(InetAddress.getLocalHost().getHostAddress());

                System.out.println("[Client] Connected to Authentication, Student, and Admin Processors.");
                return;
            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                showServerDownMessage("Server unreachable. Retrying in 5 seconds...");
                sleep(5000);
            }
        }
    }


    /**
     * Displays a popup message when the server is down, ensuring it appears above the main GUI.
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