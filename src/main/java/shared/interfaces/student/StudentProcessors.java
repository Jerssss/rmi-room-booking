package shared.interfaces.student;

import shared.Reservation;
import shared.Terminal;
import util.exception.ModifyReservationException;
import util.exception.ReservationException;

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
     * @throws RemoteException If an RMI communication error occurs.
     * @throws ReservationException If the reservation could not be added.
     */
    void setReservations(Reservation newReservation) throws RemoteException, ReservationException;

    /**
     * Retrieves all active terminals.
     * @return A list of active terminals.
     * @throws RemoteException If an RMI communication error occurs.
     */
    List<Terminal> getActiveTerminals() throws RemoteException;

    /**
     * Updates an existing reservation.
     * @param reservation The updated reservation details.
     * @throws RemoteException If an RMI communication error occurs.
     */
    void updateReservation(Reservation reservation) throws RemoteException, ModifyReservationException;

    /**
     * Cancels a reservation based on its ID.
     * @param reservationID The ID of the reservation to cancel.
     * @throws RemoteException If an RMI communication error occurs.
     * @throws ReservationException If the reservation could not be canceled.
     */
    void cancelReservation(String reservationID) throws RemoteException, ReservationException;
}
