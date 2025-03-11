package client.admin.model;

import shared.Terminal;
import shared.interfaces.AdminProcessors;
import client.ClientMain;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class AddNewTerminalModel {
    private AdminProcessors adminService;

    public AddNewTerminalModel() {
        connectToServer();
    }

    private void connectToServer() {
        try {
            String serverIP = ClientMain.getServerIP();
            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);
            adminService = (AdminProcessors) registry.lookup("admin_processors");

            System.out.println("[RMI] Connected to admin_processors service.");
        } catch (Exception e) {
            System.err.println("[ERROR] Could not connect to admin_processors: " + e.getMessage());
            adminService = null;
        }
    }

    public List<Terminal> fetchTerminals() {
        if (adminService == null) {
            System.err.println("[ERROR] RMI service is NULL. Cannot fetch terminals.");
            return null;
        }

        try {
            List<Terminal> terminals = adminService.getAllTerminals();
            System.out.println("[CLIENT] Fetched " + terminals.size() + " terminals via RMI.");
            for (Terminal terminal : terminals) {
                System.out.println("[SERVER] " + terminal);
            }
            return terminals;
        } catch (RemoteException e) {
            System.err.println("[ERROR] Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }

}
