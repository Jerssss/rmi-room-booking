package server.rmiservices;

import shared.interfaces.AdminProcessors;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * AdminProcessorService handles admin-related actions.
 */
public class AdminProcessorService extends UnicastRemoteObject implements AdminProcessors {

    public AdminProcessorService() throws RemoteException {
        super();
    }

    @Override
    public void processAdminRequest(String adminID) throws RemoteException {
        System.out.println("Processing request for admin: " + adminID);
        // Add logic to handle admin requests
    }

    @Override
    public String getAdminDetails(String adminID) throws RemoteException {
        return "";
    }
}
