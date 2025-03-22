package server.rmiservices;

import shared.Reservation;
import shared.Terminal;
import shared.interfaces.student.StudentProcessors;
import util.JSONUtility;
import util.exception.ModifyReservationException;
import util.exception.ReservationException;

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
            throw new RemoteException("Error fetching reservations", e);
        }
    }

    @Override
    public List<Reservation> getAllReservations() throws RemoteException {
        try {
            System.out.println("[StudentProcessorService] Fetching all reservations.");

            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            if (allReservations == null || allReservations.isEmpty()) {
                System.out.println("[StudentProcessorService] No reservations found.");
            } else {
                System.out.println("[StudentProcessorService] Loaded " + allReservations.size() + " reservations.");
            }

            return allReservations;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch reservations: " + e.getMessage());
            throw new RemoteException("Error fetching all reservations", e);
        }
    }

    @Override
    public void setReservations(Reservation newReservation) throws RemoteException, ReservationException {
        try {
            System.out.println("[StudentProcessorService] Attempting to add reservation for user: " + newReservation.getUserID());

            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            // Add new reservation
            allReservations.add(newReservation);
            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);

            System.out.println("[StudentProcessorService] Reservation successfully added.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add reservation: " + e.getMessage());
            throw new ReservationException("Failed to add reservation", e);
        }
    }

    @Override
    public void updateReservation(Reservation updatedReservation) throws RemoteException, ModifyReservationException {
        try {
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            // Find and replace the reservation
            boolean updated = false;
            for (int i = 0; i < allReservations.size(); i++) {
                if (allReservations.get(i).getReservationID().equals(updatedReservation.getReservationID())) {
                    allReservations.set(i, updatedReservation);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                throw new ReservationException("Reservation with ID " + updatedReservation.getReservationID() + " not found.");
            }
            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[StudentProcessorService] Reservation successfully updated.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update reservation: " + e.getMessage());
            throw new ModifyReservationException("Failed to update reservation", e);
        }
    }

    @Override
    public void cancelReservation(String reservationID) throws RemoteException, ReservationException {
        try {
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            // Find and remove the reservation
            boolean removed = allReservations.removeIf(res ->
                    res.getReservationID().equals(reservationID)
            );

            if (!removed) {
                throw new ReservationException("Reservation with ID " + reservationID + " not found.");
            }

            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[StudentProcessorService] Reservation successfully canceled.");
        } catch (ReservationException re) {
            throw re;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to cancel reservation: " + e.getMessage());
            throw new ReservationException("Failed to cancel reservation", e);
        }
    }

    @Override
    public List<Terminal> getActiveTerminals() throws RemoteException {
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
            throw new RemoteException("Error fetching active terminals", e);
        }
    }
}
