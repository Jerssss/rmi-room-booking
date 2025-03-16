package client.utility;

import shared.callback.ClientCallbackInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Client-side callback implementation for handling server notifications.
 */
public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallbackInterface {

    public ClientCallbackImpl() throws RemoteException {
        super(); // Ensure proper export
    }

    /**
     * Handles server notifications by printing messages to the console.
     * @param message The notification message from the server.
     * @throws RemoteException If an RMI communication error occurs.
     */
    @Override
    public void notify(String message) throws RemoteException {
        System.out.println("[SERVER MESSAGE]: " + message);
    }
}
