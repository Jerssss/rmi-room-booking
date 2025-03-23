package client.student.model;

import client.ClientMain;
import shared.Reservation;
import shared.interfaces.student.StudentProcessors;
import util.exception.ModifyReservationException;
import util.exception.ReservationException;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model class for managing reservation modifications.
 * This class interacts with the remote student processors to fetch,
 * update, and cancel reservations for a specific student.
 */
public class ModifyReservationModel {
    private final StudentProcessors studentProcessors;
    private final String studentID;

    /**
     * Constructs a ModifyReservationModel for the specified student.
     *
     * @param studentID the ID of the student whose reservations are managed
     */
    public ModifyReservationModel(String studentID) {
        this.studentProcessors = ClientMain.getStudentProcessors();
        this.studentID = studentID;
    }

    /**
     * Fetches the list of reservations for the student.
     *
     * @return a list of reservations, or null if the fetch fails
     */
    public List<Reservation> fetchReservations() {
        try {
            return studentProcessors.getReservations(studentID);
        } catch (RemoteException e) {
            System.err.println("[CLIENT] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fetches all reservations for a specific terminal on a specific date.
     *
     * @param terminalId the ID of the terminal
     * @param date the date of the reservations
     * @return a list of reservations for the specified terminal and date, or null if the fetch fails
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
            System.err.println("[CLIENT] Failed to fetch reservations: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates a specific reservation.
     *
     * @param reservation the reservation to be updated
     * @return true if the update was successful, false otherwise
     */
    public void updateReservation(Reservation reservation) {
        try {
            studentProcessors.updateReservation(reservation);
        } catch (RemoteException e) {
            System.err.println("[CLIENT] RMI call failed: " + e.getMessage());
            throw new RuntimeException("Remote exception during update", e);
        } catch ( ModifyReservationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cancels a reservation identified by its ID.
     *
     * @param reservationID the ID of the reservation to be cancelled
     * @return true if the cancellation was successful, false otherwise
     */
    public void cancelReservation(String reservationID) {
        try {
            studentProcessors.cancelReservation(reservationID);
        } catch (RemoteException e) {
            System.err.println("[CLIENT] RMI call failed: " + e.getMessage());
            throw new RuntimeException("Remote exception during cancellation", e);
        } catch (ReservationException e) {
            System.err.println("[CLIENT] Reservation cancellation failed: " + e.getMessage());
            throw new RuntimeException("Reservation cancellation exception", e);
        }
    }
}