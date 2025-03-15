package shared.interfaces.student;

import shared.Reservation;
import shared.Terminal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for student-related processes.
 */
public interface StudentProcessors extends Remote {
    /**
     * Retrieves all reservations.
     * @return A list of all reservations.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Reservation> getAllReservations() throws RemoteException;

    /**
     * Retrieves reservations for a specific student.
     * @param studentID The student's ID.
     * @return A list of reservations belonging to the student.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Reservation> getReservations(String studentID) throws RemoteException;

    /**
     * Adds a new reservation for a student.
     * @param newReservation The reservation details.
     * @return True if the reservation was successfully added, false otherwise.
     * @throws RemoteException If an RMI communication error occurs.
     */
    boolean setReservations(Reservation newReservation) throws RemoteException;

    /**
     * Retrieves all active terminals.
     * @return A list of active terminals.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Terminal> getActiveTerminals() throws RemoteException;

    /**
     * Updates an existing reservation.
     * @param reservation The updated reservation details.
     * @return True if the reservation was successfully updated, false otherwise.
     * @throws RemoteException If an RMI communication error occurs.
     */
    boolean updateReservation(Reservation reservation) throws RemoteException;

    /**
     * Cancels a reservation based on its ID.
     * @param reservationID The ID of the reservation to cancel.
     * @return True if the reservation was successfully canceled, false otherwise.
     * @throws RemoteException If an RMI communication error occurs.
     */
    boolean cancelReservation(String reservationID) throws RemoteException;
}
