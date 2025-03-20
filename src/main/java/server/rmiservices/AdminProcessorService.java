package server.rmiservices;

import shared.Reservation;
import shared.Terminal;
import shared.interfaces.admin.AdminProcessors;
import util.JSONUtility;

import java.io.File;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
    public List<Reservation> getAllStudentReservations() throws RemoteException {
        try {
            System.out.println("[AdminProcessorService] Fetching student reservations from: " + RESERVATIONS_FILE.getAbsolutePath());

            // Check if file exists
            if (!RESERVATIONS_FILE.exists()) {
                System.err.println("[ERROR] JSON file does not exist: " + RESERVATIONS_FILE.getAbsolutePath());
                return new ArrayList<>();
            }

            // Load reservations from JSON
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

    @Override
    public boolean updateReservations(List<Reservation> updatedReservations) throws RemoteException {
        try {
            System.out.println("[AdminProcessorService] Updating reservations...");

            // Load existing reservations
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            if (allReservations == null) {
                System.err.println("[ERROR] Failed to load existing reservations.");
                return false;
            }

            // Update the reservations
            for (Reservation updatedReservation : updatedReservations) {
                boolean found = false;
                for (int i = 0; i < allReservations.size(); i++) {
                    if (allReservations.get(i).getReservationID().equals(updatedReservation.getReservationID())) {
                        allReservations.set(i, updatedReservation);
                        found = true;
                        System.out.println("[AdminProcessorService] Updated reservation: " + updatedReservation.getReservationID());
                        break;
                    }
                }
                if (!found) {
                    System.err.println("[ERROR] Reservation not found: " + updatedReservation.getReservationID());
                }
            }

            // Save the updated reservations to the JSON file
            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[AdminProcessorService] Reservations saved successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update reservations: " + e.getMessage());
            return false;
        }
    }
    @Override
    public void addNewTerminal(Terminal terminal) throws RemoteException {
        try {
            List<Terminal> terminals = JSONUtility.loadTerminals(TERMINALS_FILE);
            terminals.add(terminal);
            JSONUtility.saveTerminals(terminals, TERMINALS_FILE);
            System.out.println("[SERVER] Terminal added successfully.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to save terminal: " + e.getMessage());
        }
    }

    @Override
    public boolean modifyTerminalStatus(List<Terminal> updatedTerminals) throws RemoteException {
        try {
            System.out.println("[AdminProcessorService] Modifying terminal status...");

            List<Terminal> existingTerminals = JSONUtility.loadTerminals(TERMINALS_FILE);

            if (existingTerminals == null) {
                existingTerminals = new ArrayList<>();
            }

            for (Terminal updated : updatedTerminals) {
                boolean found = false;
                for (int i = 0; i < existingTerminals.size(); i++) {
                    if (existingTerminals.get(i).getTerminalID().equals(updated.getTerminalID())) {
                        existingTerminals.set(i, updated);
                        found = true;
                        System.out.println("[AdminProcessorService] Updated terminal: " + updated.getTerminalID());
                        break;
                    }
                }
                if (!found) {
                    System.err.println("[ERROR] Terminal not found, adding as new: " + updated.getTerminalID());
                    existingTerminals.add(updated);
                }
            }

            JSONUtility.saveTerminals(existingTerminals, TERMINALS_FILE);
            System.out.println("[AdminProcessorService] Terminals updated successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update terminals: " + e.getMessage());
            return false;
        }
    }

}
