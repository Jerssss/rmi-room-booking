package client.admin.model;

import client.ClientMain;
import shared.interfaces.Authentication;

import java.rmi.RemoteException;

public class AddAdminModel {
    private final Authentication authService;

    public AddAdminModel() {
        this.authService = ClientMain.getAuthService();
    }

    public void registerAdmin(String adminID, String name, String password, String facultyType)
            throws RemoteException {
        if (authService == null) {
            System.err.println("[ERROR] AuthService is NULL!"); // Debug
            throw new RemoteException("Server error");
        }
        System.out.println("[DEBUG] Calling authService.signUp()..."); // Debug
        authService.signUp(adminID, name, password, "Admin", "", facultyType);
    }
}
