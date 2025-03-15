package client.student.model;

import client.ClientMain;
import shared.Reservation;
import shared.Terminal;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

public class CreateReservationModel {
    private StudentProcessors studentProcessors;
    private String studentID;

    public CreateReservationModel(String studentID) {
        this.studentProcessors = ClientMain.getStudentProcessors(); // Get RMI instance
        this.studentID = studentID;  // Save the student ID from session
    }

    // Getter for the studentID
    public String getStudentID() {
        return studentID;
    }

    public List<Terminal> fetchTerminals() {
        System.out.println("[DEBUG] fetchTerminals() method called.");
        try {
            if (studentProcessors == null) {
                System.err.println("[ERROR] StudentProcessors RMI service is NULL!");
                return null;
            }
            List<Terminal> terminals = studentProcessors.getActiveTerminals();
            if (terminals == null || terminals.isEmpty()) {
                System.out.println("[DEBUG] No terminals found via RMI.");
            } else {
                System.out.println("[DEBUG] Loaded " + terminals.size() + " terminals via RMI.");
                terminals.forEach(ter -> System.out.println("[DEBUG] " + ter));
            }
            return terminals;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a new reservation by calling the RMI service.
     */
    public boolean addReservation(Reservation reservation) {
        try {
            return studentProcessors.setReservations(reservation);
        } catch (RemoteException e) {
            System.err.println("[ERROR] Remote exception while adding reservation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches reservations for a specific terminal and date.
     * Since the interface only provides getAllReservations(), we filter the results.
     */
    public List<Reservation> fetchReservationsForTerminal(String terminalId, String date) {
        try {
            List<Reservation> allReservations = studentProcessors.getAllReservations();
            if (allReservations == null) return null;
            return allReservations.stream()
                    .filter(res -> res.getTerminalID().equals(terminalId)
                            && res.getReservationDate().equals(date))
                    .collect(Collectors.toList());
        } catch (RemoteException e) {
            System.err.println("[ERROR] Failed to fetch reservations: " + e.getMessage());
            return null;
        }
    }

    /**
     * Returns the next available reservation ID based on the highest numeric ID so far.
     * If no reservations exist or if parsing fails, returns "1".
     */
    public String getNextReservationId() {
        try {
            List<Reservation> allReservations = studentProcessors.getAllReservations();
            int maxId = 0;
            if (allReservations != null) {
                for (Reservation res : allReservations) {
                    try {
                        int id = Integer.parseInt(res.getReservationID());
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore IDs that are not numeric
                    }
                }
            }
            return String.valueOf(maxId + 1);
        } catch (RemoteException e) {
            System.err.println("[ERROR] Failed to fetch all reservations: " + e.getMessage());
            return "1";
        }
    }
}
