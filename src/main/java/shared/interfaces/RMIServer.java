package shared.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {

    String sendMessage(String message) throws RemoteException;
    String getServerIP() throws RemoteException;
}
