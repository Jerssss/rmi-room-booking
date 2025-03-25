package client;

import client.landingpage.LandingPageController;
import client.landingpage.LandingPageView;
import client.landingpage.pickserver.SetIPController;
import client.landingpage.pickserver.SetIPModel;
import client.landingpage.pickserver.SetIPView;
import client.student.view.ServerErrorWindowView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.interfaces.Authentication;
import shared.interfaces.student.StudentProcessors;
import shared.interfaces.admin.AdminProcessors;
import shared.interfaces.IPInputHandler;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.Objects;

/**
 * ClientMain initializes the client application and connects to the RMI server.
 */
public class ClientMain extends Application implements IPInputHandler {
    private static String serverIP; // Server IP will be set by the user
    private static final int PORT = 1099;

    private static Authentication authService;
    private static StudentProcessors studentProcessors;
    private static AdminProcessors adminProcessors;
    private static Stage primaryStage; // Reference to the main window
    private static Stage currentPopupStage; // Reference to the current popup window

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("[Client] Starting client at " + new Date());
        System.out.println("=====================================================");

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(false);
        primaryStage = stage;

        // Set application icon
        try {
            URL iconUrl = ClientMain.class.getResource("/images/client/app_icon.png");
            if (iconUrl == null) {
                throw new RuntimeException("Icon file not found!");
            }
            primaryStage.getIcons().add(new Image(iconUrl.openStream()));
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load icon: " + e.getMessage());
            e.printStackTrace();
        }

        showIPInputView();
    }

    /**
     * Starts a heartbeat mechanism to periodically check the server's availability.
     */
    private static void startHeartbeat() {
        new Thread(() -> {
            while (true) {
                try {
                    // Sleep for a certain period before checking the server status
                    Thread.sleep(5000); // Check every 5 seconds

                    // Check if the server is reachable
                    if (authService != null) {
                        authService.heartbeat(); // Call the heartbeat method on the server
                    } else {
                        throw new RemoteException("Server is down");
                    }
                } catch (RemoteException e) {
                    // Server is down, notify the user
                    System.out.println("[Client] Server is down. Notifying the user...");

                    Platform.runLater(() -> {
                        // Show the popup window with the retry button enabled
                        showServerErrorWindow("Server is down. Please try again later.", true);
                    });

                    // Wait for the server to come back up
                    while (true) {
                        try {
                            Thread.sleep(5000); // Check every 5 seconds

                            // Attempt to reconnect to the server
                            Registry registry = LocateRegistry.getRegistry(serverIP, PORT);
                            authService = (Authentication) registry.lookup("authentication");
                            studentProcessors = (StudentProcessors) registry.lookup("student_processors");
                            adminProcessors = (AdminProcessors) registry.lookup("admin_processors");

                            System.out.println("[Client] Reconnected to the server.");

                            // Close the popup window on successful reconnection
                            closePopupWindow();

                            // Exit the loop and continue normal operation
                            break;
                        } catch (Exception ex) {
                            System.err.println("[ERROR] " + ex.getMessage());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("[ERROR] Heartbeat thread interrupted.");
                    break;
                }
            }
        }).start();
    }

    public static AdminProcessors getAdminService() {
        if (adminProcessors == null) {
            System.err.println("[CLIENT] Warning: AdminProcessorService is NULL! Ensure RMI is initialized.");
        }
        return adminProcessors;
    }

    /**
     * Terminates the application gracefully.
     */
    private void terminateApplication() {
        System.out.println("[INFO] Terminating application...");
        Platform.exit(); // Shut down JavaFX
        System.exit(0); // Ensure JVM exits
    }


    private void showIPInputView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/set_ip_window.fxml"));
            Parent root = loader.load();

            // Get the controller
            SetIPView controller = loader.getController();
            controller.setConnectHandler(this::handleIPInput); // Pass the IP to handleIPInput

            // Create a new stage for the IP input view
            Stage ipInputStage = new Stage();
            ipInputStage.setTitle("Specify Server");
            ipInputStage.setScene(new Scene(root));
            ipInputStage.setResizable(false);
            ipInputStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows

            // Set application icon
            try {
                URL iconUrl = ClientMain.class.getResource("/images/client/app_icon.png");
                if (iconUrl == null) {
                    throw new RuntimeException("Icon file not found!");
                }
                primaryStage.getIcons().add(new Image(iconUrl.openStream()));
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to load icon: " + e.getMessage());
                e.printStackTrace();
            }

            // Show the IP input view and wait for user input
            ipInputStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not load SetIPView.fxml");
        }
    }

    /**
     * Handles the IP input from the user and attempts to connect to the server.
     *
     * @param ip The server IP address entered by the user.
     */
    public void handleIPInput(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            System.out.println("[Client] No IP address provided. Exiting...");
            Platform.exit();
            return;
        }

        serverIP = ip.trim();
        System.out.println("[Client] Connecting to RMI server at " + serverIP + " on port " + PORT);

        // Attempt to connect to the server in a background thread
        new Thread(() -> {
            connectToRMIServer(this); // Pass the current instance
        }).start();
    }

    /**
     * Loads the main landing page after a successful connection.
     */
    private void loadLandingPage() {

        if (Platform.isFxApplicationThread()) {
            // If already on the JavaFX Application Thread, load the landing page directly
            loadLandingPageUI();
        } else {
            Platform.runLater(() -> {
                loadLandingPageUI();
            });
        }
    }

    private void loadLandingPageUI() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/landing_page.fxml"));
            Parent root = loader.load();

            // Get the controller
            LandingPageView landingPageView = loader.getController();
            if (landingPageView == null) {
                System.err.println("[ERROR] LandingPageView is NULL after FXML load!");
            } else {
                System.out.println("[DEBUG] LandingPageView controller loaded successfully."); // Debug log
                new LandingPageController(landingPageView);
            }

            // Set the scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);

            primaryStage.setOnCloseRequest(event -> {
                System.out.println("[INFO] Close request received. Terminating the application...");
                terminateApplication();
            });
            // Show the landing page
            primaryStage.show();

            System.out.println("[Client] WELCOME TO LENDIFY");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not load landing_page.fxml: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERROR] Unexpected error in loadLandingPageUI(): " + e.getMessage());
        }
    }

    /**
     * Connects to the RMI server and retries if the server is down.
     */
    private static void connectToRMIServer(ClientMain clientMain) {
        final int MAX_RETRIES = 5; // Maximum number of retries
        final int RETRY_DELAY = 5000; // Delay between retries in milliseconds

        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                System.out.println("[DEBUG] Attempting to connect to server at " + serverIP + ":" + PORT);

                Registry registry = LocateRegistry.getRegistry(serverIP, PORT);

                System.out.println("[DEBUG] Registry located. Looking up services...");

                authService = (Authentication) registry.lookup("authentication");
                studentProcessors = (StudentProcessors) registry.lookup("student_processors");
                adminProcessors = (AdminProcessors) registry.lookup("admin_processors");

                System.out.println("[DEBUG] Services successfully looked up.");

                authService.logClientConnection(InetAddress.getLocalHost().getHostAddress());

                System.out.println("[Client] Connected to Authentication, Student, and Admin Processors.");

                // Close the popup window on successful reconnection
                closePopupWindow();

                // Start the heartbeat mechanism
                startHeartbeat();

                // Call the method to load the landing page
                clientMain.loadLandingPage();

                return; // Exit the loop on successful connection
            } catch (Exception e) {
                retryCount++;
                System.err.println("[ERROR] " + e.getMessage());

                // Show the error window on the JavaFX Application Thread
                int finalRetryCount = retryCount;
                Platform.runLater(() -> {
                    showServerErrorWindow("Server unreachable. Retry attempt " + finalRetryCount + "/" + MAX_RETRIES, finalRetryCount < MAX_RETRIES);
                });

                if (retryCount >= MAX_RETRIES) {
                    // Show the final error message and exit the application
                    Platform.runLater(() -> {
                        showServerErrorWindow("Failed to connect to the server after " + MAX_RETRIES + " attempts. Exiting...", false);
                        Platform.exit(); // Exit the application
                    });
                    return;
                }

                // Wait for user input before retrying
                try {
                    synchronized (ClientMain.class) {
                        ClientMain.class.wait(); // Pause the thread until notified
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.err.println("[ERROR] Retry thread interrupted.");
                    return;
                }
            }
        }
    }


    /**
     * Displays the ServerErrorWindow as a pop-up.
     *
     * @param message    The error message to display.
     * @param allowRetry Whether to allow the user to retry connecting to the server.
     */
    private static void showServerErrorWindow(String message, boolean allowRetry) {
        Platform.runLater(() -> {
            try {
                System.out.println("[Client] Showing server error window: " + message);

                // Close the existing popup if it is open
                if (currentPopupStage != null) {
                    currentPopupStage.close();
                }

                // Load the FXML file
                FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/fxml/client/server_error_window.fxml"));
                AnchorPane serverErrorWindow = loader.load();

                // Get the controller
                ServerErrorWindowView controller = loader.getController();


                // Set up the retry button action
                if (allowRetry) {
                    controller.setRetryButtonAction(() -> {
                        // Notify the retry thread to continue
                        synchronized (ClientMain.class) {
                            ClientMain.class.notify();
                        }
                        // Close the pop-up window
                        Stage stage = (Stage) serverErrorWindow.getScene().getWindow();
                        stage.close();
                    });

                    // Enable the retry button
                    controller.resetRetryButton();
                } else {
                    // Disable the retry button if no more retries are allowed
                    controller.disableRetryButton();
                }

                // Create a new Stage (pop-up window)
                currentPopupStage = new Stage();
                currentPopupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
                currentPopupStage.setTitle("Server Error");
                currentPopupStage.setResizable(false); // Make the pop-up non-resizable

                // Set the FXML content to the Stage
                Scene scene = new Scene(serverErrorWindow);
                currentPopupStage.setScene(scene);

                // Set application icon
                try {
                    URL iconUrl = ClientMain.class.getResource("/images/client/app_icon.png");
                    if (iconUrl == null) {
                        throw new RuntimeException("Icon file not found!");
                    }
                    primaryStage.getIcons().add(new Image(iconUrl.openStream()));
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to load icon: " + e.getMessage());
                    e.printStackTrace();
                }

                // Handle the popup window's close event
                currentPopupStage.setOnCloseRequest(event -> {
                    // Reissue the popup window if the server is still down
                    if (authService == null) {
                        Platform.runLater(() -> {
                            showServerErrorWindow("Server is down. Please try again later.", true);
                        });
                    }
                });

                // Show the pop-up
                currentPopupStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("[ERROR] Could not load server_error_window.fxml");
            }
        });
    }

    /**
     * Closes the currently open popup window.
     */
    private static void closePopupWindow() {
        Platform.runLater(() -> {
            if (currentPopupStage != null) {
                // Get the controller and reset the retry button
                if (currentPopupStage.getScene() != null && currentPopupStage.getScene().getRoot() != null) {
                    ServerErrorWindowView controller = (ServerErrorWindowView) currentPopupStage.getScene().getRoot().getProperties().get("controller");
                    if (controller != null) {
                        controller.resetRetryButton();
                    }
                }

                // Close the popup window
                currentPopupStage.close();
                currentPopupStage = null; // Clear the reference
            }
        });
    }


    /**
     * Returns the authentication service instance.
     *
     * @return The authentication service.
     */
    public static Authentication getAuthService() {
        return authService;
    }

    /**
     * Returns the student processors instance.
     *
     * @return The student processors.
     */
    public static StudentProcessors getStudentProcessors() {
        return studentProcessors;
    }

    /**
     * Returns the admin processors instance.
     *
     * @return The admin processors.
     */
    public static AdminProcessors getAdminProcessors() {
        return adminProcessors;
    }

    /**
     * Returns the server IP address.
     *
     * @return The server IP address.
     */
    public static String getServerIP() {
        return serverIP;
    }
}