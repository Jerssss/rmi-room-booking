package client.admin.model;

import client.ClientMain;
import shared.interfaces.admin.AdminProcessors;

/**
 * The `AdminMainMenuModel` class is responsible for handling server interactions
 * for the Admin Main Menu. It initializes the connection to the RMI server.
 */
public class AdminMainMenuModel {

    private AdminProcessors adminProcessors;

    /**
     * Constructs an `AdminMainMenuModel` and initializes the RMI service.
     */
    public AdminMainMenuModel() {

        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
    }

}
