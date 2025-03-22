package client.utility;

import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.Broadcast;
import shared.callback.UpdateTable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientCallBack extends UnicastRemoteObject implements Broadcast {
    private final UpdateTable controller;

    public ClientCallBack(UpdateTable controller) throws RemoteException {
        super();
        this.controller = controller;
    }

    @Override
    public void updateTerminal(List<Terminal> terminals) throws RemoteException {
        System.out.println("[UPDATE] Terminal list updated from server.");
        controller.updateTerminals(terminals);
    }

    @Override
    public void updateReservationApproval(List<Reservation> reservations) throws RemoteException {
        System.out.println("[UPDATE] Reservation approvals updated from server.");
        controller.updateReservations(reservations);
    }

    @Override
    public void updateLogs(List<Log> logs) throws RemoteException {
        System.out.println("[UPDATE] Logs updated from server.");
        controller.updateLogs(logs);
    }

    @Override
    public void registerCallback(Broadcast callback) throws RemoteException {
        System.out.println("[CALLBACK] Registering client callback (handled by server).");
    }

    @Override
    public void unregisterCallback(Broadcast callback) throws RemoteException {
        System.out.println("[CALLBACK] Unregistering client callback (handled by server).");
    }
}
