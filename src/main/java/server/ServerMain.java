package server;

import server.rmiservices.AuthenticationService;
import server.rmiservices.StudentProcessorService;
import server.rmiservices.AdminProcessorService;
import shared.interfaces.Authentication;
import shared.interfaces.StudentProcessors;
import shared.interfaces.AdminProcessors;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ServerMain initializes the RMI server and handles remote services.
 */
public class ServerMain {
    private static final int PORT = 1099;
    private static final CopyOnWriteArrayList<String> connectedClients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        bindServices();
    }

    /**
     * Binds RMI services to the registry.
     */
    private static void bindServices() {
        new Thread(() -> {
            try {
                Authentication authentication = new AuthenticationService();
                StudentProcessors studentProcessors = new StudentProcessorService();
                AdminProcessors adminProcessors = new AdminProcessorService();

                Registry registry = LocateRegistry.createRegistry(PORT);

                registry.bind("authentication", authentication);
                registry.bind("student_processors", studentProcessors);
                registry.bind("admin_processors", adminProcessors);

                String serverIP = getServerIP();
                System.out.println("=====================================================");
                System.out.println("[Server] RMI Server started successfully on port " + PORT);
                System.out.println("[Server] Server IP Address: " + serverIP);
                System.out.println("=====================================================");

            } catch (RemoteException | AlreadyBoundException e) {
                e.printStackTrace();
            }
        }).start();
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
            System.out.println("[Server] Current connected clients: " + connectedClients);
        } else {
            System.out.println("[Server] Client " + clientIP + " is already connected.");
        }
    }
}
