package shared.callback;

import shared.Log;
import shared.Reservation;
import shared.Terminal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Broadcast interface for server-to-client callbacks.
 */
public interface Broadcast extends Remote {

    /**
     * Sends updated terminal information to all registered clients.
     * @param terminals The updated list of terminals.
     * @throws RemoteException If an RMI error occurs.
     */
    void updateTerminal(List<Terminal> terminals) throws RemoteException;

    /**
     * Sends updated reservation approvals to all registered clients.
     * @param reservations The updated list of reservations.
     * @throws RemoteException If an RMI error occurs.
     */
    void updateReservationApproval(List<Reservation> reservations) throws RemoteException;

    /**
     * Sends updated logs to all registered clients.
     * @param logs The updated list of logs.
     * @throws RemoteException If an RMI error occurs.
     */
    void updateLogs(List<Log> logs) throws RemoteException;

    /**
     * Registers a client callback for receiving updates.
     * @param callback The client's callback implementation.
     * @throws RemoteException If an RMI error occurs.
     */
    void registerCallback(Broadcast callback) throws RemoteException;

    /**
     * Unregisters a client callback from receiving updates.
     * @param callback The client's callback implementation.
     * @throws RemoteException If an RMI error occurs.
     */
    void unregisterCallback(Broadcast callback) throws RemoteException;
}
