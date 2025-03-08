package shared.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for admin-related processes.
 */
public interface AdminProcessors extends Remote {

    /**
     * Handles admin-specific requests.
     * @param adminID The admin's ID.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void processAdminRequest(String adminID) throws RemoteException;

    /**
     * Retrieves admin information.
     * @param adminID The admin's ID.
     * @return JSON or XML string of admin details.
     * @throws RemoteException If an RMI communication error occurs.
     */
    String getAdminDetails(String adminID) throws RemoteException;
}
