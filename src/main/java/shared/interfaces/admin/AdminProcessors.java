package shared.interfaces.admin;

import shared.Admin;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.Broadcast;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for admin-related processes.
 */
public interface AdminProcessors extends Remote {
    /**
     * Retrieves all logs.
     *
     * @return A list of all logs.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Log> getAllLogs() throws RemoteException;
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
     * @throws RemoteException If an RMI communication error occurs.
     */
    void updateReservations(List<Reservation> reservations) throws RemoteException;

    /**
     * Adds a new terminal to the system.
     *
     * @param terminal The terminal to be added.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void addNewTerminal(Terminal terminal) throws RemoteException;

    /**
     * Modifies the status of existing terminals in the system.
     *
     * @param terminal A list of terminals with updated status information.
     * @return {@code true} if the modification was successful, {@code false} otherwise.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void modifyTerminalStatus(List<Terminal> terminal) throws RemoteException;

    /**
     * Registers a new admin in the system and saves it to JSON.
     *
     * @param admin The admin object to be added.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void registerAdmin(Admin admin) throws RemoteException;

    /**
     * Registers a client callback to receive updates from the server.
     *
     * @param callback The client's callback instance to register.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void registerCallback(Broadcast callback) throws RemoteException;

    /**
     * Unregisters a client callback to stop receiving updates from the server.
     *
     * @param callback The client's callback instance to unregister.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void unregisterCallback(Broadcast callback) throws RemoteException;
}