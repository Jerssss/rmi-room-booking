package client.utility;

import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.Broadcast;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * ClientCallBack handles real-time updates from the server, such as terminal status changes,
 * reservation approvals, and log updates.
 */
public class ClientCallBack extends UnicastRemoteObject implements Broadcast {
    private final ClientModel clientModel;

    /**
     * Constructor for ClientCallBack.
     * @param clientModel The ClientModel instance to update when receiving new data.
     * @throws RemoteException If an RMI error occurs.
     */
    public ClientCallBack(ClientModel clientModel) throws RemoteException {
        super();
        this.clientModel = clientModel;
    }

    /**
     * Updates the client's terminal list when the server sends new data.
     * @param terminals The updated list of terminals from the server.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public void updateTerminal(List<Terminal> terminals) throws RemoteException {
        System.out.println("[UPDATE] Terminal list updated from server.");
       // clientModel.setTerminals(terminals);
    }

    /**
     * Updates the client's reservation list when approvals are processed.
     * @param reservations The updated list of reservations from the server.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public void updateReservationApproval(List<Reservation> reservations) throws RemoteException {
        System.out.println("[UPDATE] Reservation approvals updated from server.");
       // clientModel.setReservations(reservations);
    }

    /**
     * Updates the client's logs when a new log entry is received.
     * @param logs The updated list of logs from the server.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public void updateLogs(List<Log> logs) throws RemoteException {
        System.out.println("[UPDATE] Logs updated from server.");
      //  clientModel.setLogs(logs);
    }

    /**
     * Registers a client callback (handled by the server).
     * @param callback The client's callback instance to register.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public void registerCallback(Broadcast callback) throws RemoteException {
        System.out.println("[CALLBACK] Registering client callback (handled by server).");
    }

    /**
     * Unregisters a client callback (handled by the server).
     * @param callback The client's callback instance to unregister.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public void unregisterCallback(Broadcast callback) throws RemoteException {
        System.out.println("[CALLBACK] Unregistering client callback (handled by server).");
    }
}
