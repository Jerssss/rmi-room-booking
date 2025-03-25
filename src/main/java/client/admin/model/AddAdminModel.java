package client.admin.model;

import client.ClientMain;
import shared.Admin;
import shared.interfaces.Authentication;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;

public class AddAdminModel {
    private final Authentication authService;
    private final AdminProcessors adminService;

    public AddAdminModel() {
        this.authService = ClientMain.getAuthService();
        this.adminService = ClientMain.getAdminService();
    }

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
