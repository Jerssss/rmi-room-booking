package server;

import server.landingpage.LoginProcessor;
import server.landingpage.SignUpProcessor;
import shared.RMIServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {
    private final Map<String, String> activeUsers;

    public RMIServerImpl() throws RemoteException {
        super();
        activeUsers = new HashMap<>();
    }

    @Override
    public String login(String userID, String password, String userType) throws RemoteException {
        String userName = LoginProcessor.getUserName(userID, password, userType);
        if (userName != null) {
            activeUsers.put(userID, userName);
            return "<Response><Status>SUCCESS</Status><Name>" + userName + "</Name><SessionToken>12345</SessionToken></Response>";
        }
        return "<Response><Status>INVALID_CREDENTIALS</Status></Response>";
    }

    @Override
    public boolean signUp(String userID, String name, String password, String userType, String courseYear, String facultyType) throws RemoteException {
        return SignUpProcessor.registerUser(userID, name, password, userType, courseYear, facultyType);
    }

    @Override
    public String sendMessage(String message) throws RemoteException {
        System.out.println("Received message: " + message);
        return "<Response><Status>MESSAGE_SENT</Status></Response>";
    }

    @Override
    public void logout(String userID) throws RemoteException {
        activeUsers.remove(userID);
        System.out.println("User " + userID + " logged out.");
    }
}
