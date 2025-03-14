package client.admin.model;

import client.ClientMain;
import shared.Terminal;
import shared.interfaces.AdminProcessors;

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
        }
    }

    public List<Terminal> fetchTerminals() {
        try {
            return adminService.getAllTerminals();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }

    public boolean addNewTerminal(Terminal terminal) {
        try {
            System.out.println("=====================================================");
            adminService.addNewTerminal(terminal);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add terminal via RMI: " + e.getMessage());
            return false;
        }
    }
}
