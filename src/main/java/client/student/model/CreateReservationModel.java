package client.student.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

public class CreateReservationModel {
    private final StudentProcessors studentService;
    private final String studentID;

    public CreateReservationModel(String studentID) {
        this.studentService = ClientMain.getStudentProcessors(); // Get RMI instance
        this.studentID = studentID;
    }

    public List<Terminal> fetchTerminals() {
        try {
            return studentService.getActiveTerminals();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch terminals: " + e.getMessage());
            return null;
        }
    }

    public boolean addReservation(Reservation reservation) {
        try {
            // You might choose to use updateReservation() if you are updating an existing reservation,
            // or a dedicated method like setReservations() if adding a new one.
            studentService.updateReservation(reservation);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add reservation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the next available reservation ID based on the highest numeric ID so far.
     * If no reservations exist or parsing fails, returns "1".
     */
    public String getNextReservationId() {
        try {
            List<Reservation> allReservations = studentService.getAllReservations();
            int maxId = 0;
            if (allReservations != null) {
                for (Reservation res : allReservations) {
                    try {
                        int id = Integer.parseInt(res.getReservationID());
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore non-numeric IDs
                    }
                }
            }
            return String.valueOf(maxId + 1);
        } catch (RemoteException e) {
            System.err.println("[ERROR] Failed to fetch all reservations: " + e.getMessage());
            return "1";
        }
    }

    /**
     * Registers the client callback so that the server can push reservation updates.
     */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            studentService.registerCallback(clientCallBack);
            System.out.println("[CLIENT] Callback registered for reservation updates.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Getter for studentID if needed elsewhere
    public String getStudentID() {
        return studentID;
    }

    public List<Reservation> fetchReservationsForTerminal(String terminalId, String date) {
        try {
            List<Reservation> allReservations = studentService.getAllReservations();
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
}
