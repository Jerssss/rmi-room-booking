package server.rmiservices;

import shared.Reservation;
import shared.Terminal;
import shared.interfaces.AdminProcessors;
import util.JSONUtility;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * AdminProcessorService handles admin-related actions.
 */
public class AdminProcessorService extends UnicastRemoteObject implements AdminProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");
    private static final File TERMINALS_FILE = new File("src/main/resources/data/terminal.json");

    public AdminProcessorService() throws RemoteException {
        super();
    }

    @Override
    public void processAdminRequest(String adminID) throws RemoteException {
        System.out.println("Processing request for admin: " + adminID);
        // Add logic to handle admin requests
    }

    @Override
    public String getAdminDetails(String adminID) throws RemoteException {
        return "";
    }

    @Override
    public List<Reservation> getAllStudentReservations() throws RemoteException {
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
    @Override
    public List<Terminal> getAllTerminals() throws RemoteException {
        try {
            System.out.println("[AdminProcessorService] Fetching terminal data...");
            return JSONUtility.loadTerminals(TERMINALS_FILE);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }

}
