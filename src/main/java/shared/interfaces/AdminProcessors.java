package shared.interfaces;

import shared.Reservation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

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

    /**
     * Fetches all student reservations from the system.
     * @return List of student reservations.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Reservation> getAllStudentReservations() throws RemoteException;
}
