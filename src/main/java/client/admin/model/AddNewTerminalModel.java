package client.admin.model;

import client.ClientMain;
import shared.Terminal;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class AddNewTerminalModel {

    private AdminProcessors adminService;

    public AddNewTerminalModel() {
        this.adminService = ClientMain.getAdminProcessors(); // Get RMI instance
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
