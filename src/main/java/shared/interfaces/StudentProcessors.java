package shared.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for student-related processes.
 */
public interface StudentProcessors extends Remote {

    /**
     * Handles student-specific requests.
     * @param studentID The student's ID.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void processStudentRequest(String studentID) throws RemoteException;

    /**
     * Retrieves student information.
     * @param studentID The student's ID.
     * @return JSON or XML string of student details.
     * @throws RemoteException If an RMI communication error occurs.
     */
    String getStudentDetails(String studentID) throws RemoteException;
}
