package shared.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
    String login(String userID, String password, String userType) throws RemoteException;
    boolean signUp(String userID, String name, String password, String userType, String courseYear, String facultyType) throws RemoteException;
    String sendMessage(String message) throws RemoteException;
    void logout(String userID) throws RemoteException;
    String getServerIP() throws RemoteException;
}
