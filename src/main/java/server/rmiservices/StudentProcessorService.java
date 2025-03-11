package server.rmiservices;

import shared.Reservation;
import shared.Terminal;
import shared.interfaces.StudentProcessors;
import util.JSONUtility;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StudentProcessorService handles student-related actions.
 */
public class StudentProcessorService extends UnicastRemoteObject implements StudentProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");
    private static final File TERMINALS_FILE = new File("src/main/resources/data/terminal.json");

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
    public List<Reservation> getReservations(String studentID) throws RemoteException {
        try {
            System.out.println("[StudentProcessorService] Fetching reservations for student: " + studentID);

            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            // Filter the reservations based on the student ID.
            List<Reservation> userReservations = allReservations.stream()
                    .filter(reservation -> studentID.equals(reservation.getUserID()))
                    .collect(Collectors.toList());

            if (userReservations == null || userReservations.isEmpty()) {
                System.out.println("[StudentProcessorService] No reservations found for student " + studentID);
            } else {
                System.out.println("[StudentProcessorService] Loaded " + userReservations.size() + " reservations.");
            }

            return userReservations;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch reservations: " + e.getMessage());
            return null;
        }
    }
    public List<Terminal> getTerminals() throws RemoteException {
        try {
            System.out.println("[StudentProcessorService] Fetching Terminals...");

            List<Terminal> terminals = JSONUtility.loadTerminals(TERMINALS_FILE);

            if (terminals == null || terminals.isEmpty()) {
                System.out.println("[StudentProcessorService] No terminals found in JSON.");
                return terminals; // Return empty list
            }

            // Filter only active terminals
            List<Terminal> activeTerminals = terminals.stream()
                    .filter(ter -> "Active".equalsIgnoreCase(ter.getStatus()))
                    .collect(Collectors.toList());

            System.out.println("[StudentProcessorService] Loaded " + activeTerminals.size() + " active terminals.");
            activeTerminals.forEach(ter -> System.out.println("[StudentProcessorService] " + ter));

            return activeTerminals;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }


}
