package shared.interfaces.admin;

import shared.Reservation;
import shared.Terminal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for admin-related processes.
 */
public interface AdminProcessors extends Remote {

    /**
     * Retrieves all student reservations.
     *
     * @return A list of all student reservations.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Reservation> getAllStudentReservations() throws RemoteException;

    /**
     * Fetches all terminals from the system.
     *
     * @return A list of terminals.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Terminal> getAllTerminals() throws RemoteException;

    /**
     * Updates the reservations in the system.
     *
     * @param reservations A list of reservations to be updated.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     * @throws RemoteException If an RMI communication error occurs.
     */
    boolean updateReservations(List<Reservation> reservations) throws RemoteException;

    /**
     * Adds a new terminal to the system.
     *
     * @param terminal The terminal to be added.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void addNewTerminal(Terminal terminal) throws RemoteException;

    /**
     * Modifies the status of existing terminals in the system.
     * @param terminal A list of terminals with updated status information.
     * @throws RemoteException If an RMI communication error occurs.
     */
    boolean modifyTerminalStatus(List<Terminal> terminal) throws RemoteException;

}
