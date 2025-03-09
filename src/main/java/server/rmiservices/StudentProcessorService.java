package server.rmiservices;

import shared.Reservation;
import shared.interfaces.StudentProcessors;
import util.JSONUtility;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * StudentProcessorService handles student-related actions.
 */
public class StudentProcessorService extends UnicastRemoteObject implements StudentProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");

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

    @Override
    public List<Reservation> getReservation() throws RemoteException {
        try {
            System.out.println("[AdminProcessorService] Fetching student reservations...");

            List<Reservation> reservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[AdminProcessorService] No reservations found in JSON.");
            } else {
                System.out.println("[AdminProcessorService] Loaded " + reservations.size() + " reservations.");
                for (Reservation res : reservations) {
                    System.out.println("[AdminProcessorService] " + res);
                }
            }

            return reservations;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch reservations: " + e.getMessage());
            return null;
        }
    }


}
