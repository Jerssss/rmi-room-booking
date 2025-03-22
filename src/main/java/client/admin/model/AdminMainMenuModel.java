package client.admin.model;

import shared.interfaces.RMIServer;
import client.ClientMain;
import shared.interfaces.admin.AdminProcessors;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

/**
 * Model for Admin Main Menu, handling server interactions.
 */
public class AdminMainMenuModel {

    private AdminProcessors adminProcessors;

    /**
     * Constructor that initializes the connection to the RMI server.
     */
    public AdminMainMenuModel() {

        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
    }

}
