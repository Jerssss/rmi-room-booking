package server.rmiservices;

import shared.interfaces.StudentProcessors;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * StudentProcessorService handles student-related actions.
 */
public class StudentProcessorService extends UnicastRemoteObject implements StudentProcessors {

    public StudentProcessorService() throws RemoteException {
        super();
    }

    @Override
    public void processStudentRequest(String studentID) throws RemoteException {
        System.out.println("Processing request for student: " + studentID);
        // Add logic to handle student requests
    }

    @Override
    public String getStudentDetails(String studentID) throws RemoteException {
        return "";
    }
}
