package client.admin.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Terminal;
import shared.callback.Broadcast;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;
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

    /**
     * Registers the client callback so that the server can push terminal updates.
     */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            adminService.registerCallback(clientCallBack);
            System.out.println("[CLIENT] Callback registered for terminal updates.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
