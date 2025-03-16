package server;

import server.rmiservices.AuthenticationService;
import server.rmiservices.StudentProcessorService;
import server.rmiservices.AdminProcessorService;
import server.rmiservices.RMIServerService;
import shared.interfaces.Authentication;
import shared.interfaces.student.StudentProcessors;
import shared.interfaces.admin.AdminProcessors;
import shared.interfaces.RMIServer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ServerMain initializes the RMI server and handles remote services.
 */
public class ServerMain {
    private static final int PORT = 1099;
    private static final CopyOnWriteArrayList<String> connectedClients = new CopyOnWriteArrayList<>();
    private static Registry registry;
    private static boolean running = false;

    private static Authentication authService;
    private static StudentProcessors studentProcessors;
    private static AdminProcessors adminProcessors;
    private static RMIServer rmiServer;

    public static void main(String[] args) {
        //shutdown hook to ensure that the server is entirely dead on 'exit'
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (running) {
                stopServer();
            }
        }));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Server Commands: [start | stop | exit]");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "start":
                    if (!running) {
                        startServer();
                    } else {
                        System.out.println("[Server] Already running.");
                    }
                    break;

                case "stop":
                    if (running) {
                        stopServer();
                    } else {
                        System.out.println("[Server] Not running.");
                    }
                    break;

                case "exit":
                    stopServer();
                    System.out.println("[Server] Shutting down.");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid command. Use: [start | stop | exit]");
            }
        }
    }

    /**
     * Starts the RMI server and binds services.
     */
    private static void startServer() {
        new Thread(() -> {
            try {
                registry = LocateRegistry.createRegistry(PORT);

                authService = new AuthenticationService();
                studentProcessors = new StudentProcessorService();
                adminProcessors = new AdminProcessorService();
                rmiServer = new RMIServerService();

                registry.bind("authentication", authService);
                registry.bind("student_processors", studentProcessors);
                registry.bind("admin_processors", adminProcessors);
                registry.bind("RMIServer", rmiServer);

                running = true;
                String serverIP = getServerIP(); // Display the IP address
                System.out.println("=====================================================");
                System.out.println("[Server] RMI Server started successfully on port " + PORT);
                System.out.println("[Server] Server IP Address: " + serverIP);
                System.out.println("[Server] Available RMI Services: " + String.join(", ", registry.list()));
                System.out.println("=====================================================");
            } catch (RemoteException | AlreadyBoundException e) {
                System.err.println("[Server ERROR] " + e.getMessage());
            }
        }).start();
    }


    /**
     * Stops the RMI server.
     */
    private static void stopServer() {
        if (registry != null) {
            try {
                System.out.println("[Server] Stopping server...");

                // Unbind all services
                for (String name : registry.list()) {
                    registry.unbind(name);
                    System.out.println("[Server] Unbound service: " + name);
                }

                // Unexport RMI objects
                if (authService != null) {
                    java.rmi.server.UnicastRemoteObject.unexportObject(authService, true);
                    System.out.println("[Server] Unexported authService.");
                }
                if (studentProcessors != null) {
                    java.rmi.server.UnicastRemoteObject.unexportObject(studentProcessors, true);
                    System.out.println("[Server] Unexported studentProcessors.");
                }
                if (adminProcessors != null) {
                    java.rmi.server.UnicastRemoteObject.unexportObject(adminProcessors, true);
                    System.out.println("[Server] Unexported adminProcessors.");
                }
                if (rmiServer != null) {
                    java.rmi.server.UnicastRemoteObject.unexportObject(rmiServer, true);
                    System.out.println("[Server] Unexported RMIServer.");
                }

                // Unexport the registry
                java.rmi.server.UnicastRemoteObject.unexportObject(registry, true);
                System.out.println("[Server] Unexported RMI registry.");

                // Nullify references
                registry = null;
                authService = null;
                studentProcessors = null;
                adminProcessors = null;
                rmiServer = null;
                running = false;

                // Force garbage collection to clean up RMI resources
                System.gc();
                System.out.println("[Server] Server stopped.");
            } catch (Exception e) {
                System.err.println("[Server ERROR] Could not stop: " + e.getMessage());
            }
        } else {
            System.out.println("[Server] Server is already stopped.");
        }
    }


    /**
     * Gets the server's actual IP address.
     * @return The server's real IP address, or "Unknown" if unable to determine.
     */
    private static String getServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    /**
     * Logs client connections.
     * @param clientIP The IP address of the connected client.
     */
    public static void logClientConnection(String clientIP) {
        if (!connectedClients.contains(clientIP)) {
            connectedClients.add(clientIP);
            System.out.println("[Server] New client connected: " + clientIP);
        }
    }
}
