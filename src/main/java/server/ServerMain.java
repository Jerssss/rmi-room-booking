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

                Authentication authentication = new AuthenticationService();
                StudentProcessors studentProcessors = new StudentProcessorService();
                AdminProcessors adminProcessors = new AdminProcessorService();
                RMIServer rmiServer = new RMIServerService();

                registry.bind("authentication", authentication);
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

                //unexport the registry to forcefully release the resources
                java.rmi.server.UnicastRemoteObject.unexportObject(registry, true);
                System.out.println("[Server] Unexported RMI registry.");

                // Nullify registry to stop accepting new connections
                registry = null;
                running = false;

                // Force garbage collection to clean up RMI resources
                System.gc();
                System.out.println("[Server] Server stopped.");
            } catch (Exception e) {
                System.err.println("[Server ERROR] Could not stop: " + e.getMessage());
            }
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
