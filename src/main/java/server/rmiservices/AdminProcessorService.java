package server.rmiservices;

import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.Broadcast;
import shared.interfaces.admin.AdminProcessors;
import shared.interfaces.student.StudentProcessors;
import util.JSONUtility;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdminProcessorService extends UnicastRemoteObject implements AdminProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");
    private static final File TERMINALS_FILE = new File("src/main/resources/data/terminal.json");
    private static final File LOGS_FILE = new File("src/main/resources/data/logs.json");

    // Thread-safe list to store registered callbacks
    private final List<Broadcast> callbacks = new CopyOnWriteArrayList<>();

    public AdminProcessorService() throws RemoteException {
        super();
        System.out.println("[DEBUG] AdminProcessorService instantiated.");
    }

    @Override
    public List<Log> getAllLogs() throws RemoteException {
        try {
            System.out.println("[DEBUG] AdminProcessorService: Fetching logs...");
            return JSONUtility.loadLogs(LOGS_FILE);
        } catch (Exception e) {
            System.err.println("[ERROR] AdminProcessorService: Failed to fetch logs: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Reservation> getAllStudentReservations() throws RemoteException {
        try {
            System.out.println("[DEBUG] AdminProcessorService: Fetching student reservations from: "
                    + RESERVATIONS_FILE.getAbsolutePath());

            if (!RESERVATIONS_FILE.exists()) {
                System.err.println("[ERROR] JSON file does not exist: " + RESERVATIONS_FILE.getAbsolutePath());
                return new ArrayList<>();
            }

            List<Reservation> reservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[DEBUG] AdminProcessorService: No reservations found in JSON.");
            } else {
                System.out.println("[DEBUG] AdminProcessorService: Loaded " + reservations.size() + " reservations.");
            }

            return reservations;
        } catch (Exception e) {
            System.err.println("[ERROR] AdminProcessorService: Failed to fetch reservations: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Terminal> getAllTerminals() throws RemoteException {
        try {
            System.out.println("[DEBUG] AdminProcessorService: Fetching terminal data...");
            return JSONUtility.loadTerminals(TERMINALS_FILE);
        } catch (Exception e) {
            System.err.println("[ERROR] AdminProcessorService: Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void updateReservations(List<Reservation> updatedReservations) throws RemoteException {
        try {
            System.out.println("[DEBUG] AdminProcessorService: Updating reservations...");

            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            if (allReservations == null) {
                System.err.println("[ERROR] AdminProcessorService: Failed to load existing reservations.");
                return;
            }

            // Update the reservations based on the provided list
            for (Reservation updatedReservation : updatedReservations) {
                boolean found = false;
                for (int i = 0; i < allReservations.size(); i++) {
                    if (allReservations.get(i).getReservationID().equals(updatedReservation.getReservationID())) {
                        allReservations.set(i, updatedReservation);
                        found = true;
                        System.out.println("[DEBUG] AdminProcessorService: Updated reservation: " + updatedReservation.getReservationID());
                        break;
                    }
                }
                if (!found) {
                    System.err.println("[ERROR] AdminProcessorService: Reservation not found: " + updatedReservation.getReservationID());
                }
            }

            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[DEBUG] AdminProcessorService: Reservations saved successfully.");

            // Notify local callbacks
            notifyReservationUpdate(allReservations);

            // Notify student clients about reservation update via RMI lookup
            try {
                Registry registry = LocateRegistry.getRegistry(1099); // Ensure correct RMI port
                StudentProcessors studentProc = (StudentProcessors) registry.lookup("student_processors");
                // Use the new batch update method
                studentProc.updateReservations(allReservations);
                System.out.println("[DEBUG] AdminProcessorService: Pushed reservation update to student clients.");
            } catch (Exception e) {
                System.err.println("[ERROR] AdminProcessorService: Failed to push reservation update to student clients: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[ERROR] AdminProcessorService: Failed to update reservations: " + e.getMessage());
        }
    }

    @Override
    public void addNewTerminal(Terminal terminal) throws RemoteException {
        try {
            List<Terminal> terminals = JSONUtility.loadTerminals(TERMINALS_FILE);
            terminals.add(terminal);
            JSONUtility.saveTerminals(terminals, TERMINALS_FILE);
            System.out.println("[DEBUG] AdminProcessorService: Terminal added successfully.");

            // Notify local callbacks
            notifyTerminalUpdate(terminals);

            // Notify student clients about terminal update via RMI lookup
            try {
                Registry registry = LocateRegistry.getRegistry(1099); // Ensure correct RMI port
                StudentProcessors studentProc = (StudentProcessors) registry.lookup("student_processors");
                studentProc.updateTerminals(terminals);
                System.out.println("[DEBUG] AdminProcessorService: Pushed terminal update to student clients.");
            } catch (Exception e) {
                System.err.println("[ERROR] AdminProcessorService: Failed to push terminal update to student clients: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[ERROR] AdminProcessorService: Failed to save terminal: " + e.getMessage());
        }
    }

    @Override
    public boolean modifyTerminalStatus(List<Terminal> updatedTerminals) throws RemoteException {
        try {
            System.out.println("[DEBUG] AdminProcessorService: Modifying terminal details...");
            List<Terminal> existingTerminals = JSONUtility.loadTerminals(TERMINALS_FILE);

            if (existingTerminals == null) {
                existingTerminals = new ArrayList<>();
            }

            for (int i = 0; i < existingTerminals.size(); i++) {
                Terminal existing = existingTerminals.get(i);
                boolean found = false;

                for (Terminal updated : updatedTerminals) {
                    if (existing.getTerminalID().equals(updated.getTerminalID())) {
                        existingTerminals.set(i, updated);
                        System.out.println("[DEBUG] AdminProcessorService: Updated terminal: " + updated.getTerminalID());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.err.println("[DEBUG] AdminProcessorService: Removing terminal: " + existing.getTerminalID());
                    existingTerminals.remove(i);
                    i--;
                }
            }

            JSONUtility.saveTerminals(existingTerminals, TERMINALS_FILE);
            System.out.println("[DEBUG] AdminProcessorService: Terminals updated successfully.");

            // Notify local callbacks
            notifyTerminalUpdate(existingTerminals);

            // Notify student clients about terminal update via RMI lookup
            try {
                Registry registry = LocateRegistry.getRegistry(1099); // Ensure correct RMI port
                StudentProcessors studentProc = (StudentProcessors) registry.lookup("student_processors");
                studentProc.updateTerminals(existingTerminals);
                System.out.println("[DEBUG] AdminProcessorService: Pushed terminal update to student clients.");
            } catch (Exception e) {
                System.err.println("[ERROR] AdminProcessorService: Failed to push terminal update to student clients: " + e.getMessage());
            }

            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] AdminProcessorService: Failed to update terminals: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void registerCallback(Broadcast callback) throws RemoteException {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            System.out.println("[DEBUG] AdminProcessorService: Client callback registered. Total callbacks: " + callbacks.size());
        }
    }

    @Override
    public void unregisterCallback(Broadcast callback) throws RemoteException {
        callbacks.remove(callback);
        System.out.println("[DEBUG] AdminProcessorService: Client callback unregistered. Total callbacks: " + callbacks.size());
    }

    /**
     * Notifies all registered callbacks about a reservation update.
     */
    private void notifyReservationUpdate(List<Reservation> reservations) {
        System.out.println("[DEBUG] AdminProcessorService: Notifying reservation update to " + callbacks.size() + " callbacks.");
        for (Broadcast callback : callbacks) {
            try {
                System.out.println("[DEBUG] AdminProcessorService: Sending updateReservationApproval() to callback: " + callback.getClass().getName());
                callback.updateReservationApproval(reservations);
            } catch (RemoteException e) {
                System.err.println("[ERROR] AdminProcessorService: Failed to notify client (" + callback.getClass().getName() + "): " + e.getMessage());
                callbacks.remove(callback);
            }
        }
    }

    /**
     * Notifies all registered callbacks about a terminal update.
     */
    private void notifyTerminalUpdate(List<Terminal> terminals) {
        System.out.println("[DEBUG] AdminProcessorService: Notifying terminal update to " + callbacks.size() + " callbacks.");
        for (Broadcast callback : callbacks) {
            try {
                System.out.println("[DEBUG] AdminProcessorService: Sending updateTerminal() to callback: " + callback.getClass().getName());
                callback.updateTerminal(terminals);
            } catch (RemoteException e) {
                System.err.println("[ERROR] AdminProcessorService: Failed to notify client (" + callback.getClass().getName() + "): " + e.getMessage());
                callbacks.remove(callback);
            }
        }
    }
}
