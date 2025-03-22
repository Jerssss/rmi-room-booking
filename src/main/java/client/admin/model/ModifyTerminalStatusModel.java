package client.admin.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Terminal;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;
import java.rmi.RemoteException;
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

    /**
     * Registers the client callback so that the server can push terminal updates.
     *
     * @param updateTable The callback implementation that handles terminal updates.
     */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            adminService.registerCallback(clientCallBack);
            System.out.println("[CLIENT] Callback registered for terminal status updates.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
