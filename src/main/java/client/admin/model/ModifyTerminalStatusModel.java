package client.admin.model;

import client.ClientMain;
import shared.Terminal;
import shared.interfaces.admin.AdminProcessors;
import java.util.List;

public class ModifyTerminalStatusModel {
    private AdminProcessors adminService;

    public ModifyTerminalStatusModel() {
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

    public boolean saveModifiedTerminals(List<Terminal> terminals) {
        try {
            System.out.println("=====================================================");
            System.out.println("[CLIENT] Modified terminal status have been saved");
            return adminService.modifyTerminalStatus(terminals);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to modify terminal via RMI: " + e.getMessage());
            return false;
        }
    }
}
