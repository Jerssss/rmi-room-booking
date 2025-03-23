package client.admin.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Terminal;
import shared.callback.Broadcast;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;
import java.util.List;

/**
 * The `AddNewTerminalModel` class is responsible for handling the logic related to
 * adding new terminals and fetching terminal data from the server via RMI.
 */
public class AddNewTerminalModel {

    private AdminProcessors adminService;

    /**
     * Constructs an `AddNewTerminalModel` and initializes the RMI service.
     */
    public AddNewTerminalModel() {
        this.adminService = ClientMain.getAdminProcessors(); // Get RMI instance
    }

    /**
     * Fetches a list of all terminals from the server.
     *
     * @return A list of `Terminal` objects, or `null` if the operation fails.
     */
    public List<Terminal> fetchTerminals() {
        try {
            return adminService.getAllTerminals();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a new terminal to the server.
     *
     * @param terminal The `Terminal` object to be added.
     * @return `true` if the terminal was successfully added, `false` otherwise.
     */
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
     *
     * @param updateTable The callback implementation that handles terminal updates.
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
