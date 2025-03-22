package client.student.model;

import client.ClientMain;
import shared.interfaces.RMIServer;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class StudentMainMenuModel {
    private StudentProcessors studentProcessors;
    /**
     * Constructor that initializes the connection to the RMI server.
     */
    public StudentMainMenuModel() {
        this.studentProcessors = ClientMain.getStudentProcessors(); // Get RMI instance
    }


}
