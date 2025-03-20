package client.student.model;

import client.ClientMain;
import shared.Reservation;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;

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
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates a specific reservation.
     *
     * @param reservation the reservation to be updated
     * @return true if the update was successful, false otherwise
     */
    public boolean updateReservation(Reservation reservation) {
        try {
            return studentProcessors.updateReservation(reservation);
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancels a reservation identified by its ID.
     *
     * @param reservationID the ID of the reservation to be cancelled
     * @return true if the cancellation was successful, false otherwise
     */
    public boolean cancelReservation(String reservationID) {
        try {
            return studentProcessors.cancelReservation(reservationID);
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return false;
        }
    }
}