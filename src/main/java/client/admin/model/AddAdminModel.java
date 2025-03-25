package client.admin.model;

import client.ClientMain;
import shared.Admin;
import shared.interfaces.Authentication;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;

/**
 * Model class for handling admin registration operations.
 * This class interacts with remote services to register new admin users in the system.
 */
public class AddAdminModel {
    private final Authentication authService;
    private final AdminProcessors adminService;

    /**
     * Constructs a new AddAdminModel instance.
     * Initializes the required RMI services for admin operations.
     */
    public AddAdminModel() {
        this.authService = ClientMain.getAuthService();
        this.adminService = ClientMain.getAdminService();
    }


    /**
     * Registers a new admin with the system.
     *
     * @param adminID The unique identifier for the new admin
     * @param name The full name of the admin
     * @param password The password for the admin account
     * @param facultyType The faculty/department the admin belongs to
     * @throws RemoteException If there's a communication failure with the server
     *                        or if the admin service is not available
     * @throws IllegalArgumentException If any of the parameters are invalid
     */
    public void registerAdmin(String adminID, String name, String password, String facultyType) throws RemoteException {
        AdminProcessors adminService = ClientMain.getAdminService();

        if (adminService == null) {
            System.err.println("[ERROR] AdminProcessorService is NULL! Check RMI connection.");
            throw new RemoteException("Server error");
        }

        System.out.println("[CLIENT] Sending new admin data to server...");
        Admin newAdmin = new Admin(adminID, name, "Admin", password, facultyType);
        adminService.registerAdmin(newAdmin);
        System.out.println("[CLIENT] Admin successfully registered!");
    }
}
