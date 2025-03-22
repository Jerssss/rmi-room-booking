package server.rmiservices;

import shared.Reservation;
import shared.Terminal;
import shared.callback.Broadcast;
import shared.interfaces.student.StudentProcessors;
import util.JSONUtility;
import util.exception.ModifyReservationException;
import util.exception.ReservationException;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * StudentProcessorService handles student-related actions with callback support.
 */
public class StudentProcessorService extends UnicastRemoteObject implements StudentProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");
    private static final File TERMINALS_FILE = new File("src/main/resources/data/terminal.json");

    // Thread-safe list to store registered callbacks
    private final List<Broadcast> callbacks = new CopyOnWriteArrayList<>();

    public StudentProcessorService() throws RemoteException {
        super();
    }

    @Override
    public List<Reservation> getReservations(String studentID) throws RemoteException {
        try {
            System.out.println("[StudentProcessorService] Fetching reservations for student: " + studentID);
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            List<Reservation> userReservations = allReservations.stream()
                    .filter(reservation -> studentID.equals(reservation.getUserID()))
                    .collect(Collectors.toList());

            System.out.println("[StudentProcessorService] Found " + userReservations.size() + " reservations for student " + studentID);
            return userReservations;
        } catch (Exception e) {
            System.err.println("[ERROR] getReservations: " + e.getMessage());
            throw new RemoteException("Error fetching reservations", e);
        }
    }

    @Override
    public List<Reservation> getAllReservations() throws RemoteException {
        try {
            System.out.println("[StudentProcessorService] Fetching all reservations.");
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);
            System.out.println("[StudentProcessorService] Total reservations loaded: " + allReservations.size());
            return allReservations;
        } catch (Exception e) {
            System.err.println("[ERROR] getAllReservations: " + e.getMessage());
            throw new RemoteException("Error fetching all reservations", e);
        }
    }

    @Override
    public void setReservations(Reservation newReservation) throws RemoteException, ReservationException {
        try {
            System.out.println("[StudentProcessorService] Adding reservation for user: " + newReservation.getUserID());
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            allReservations.add(newReservation);
            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[StudentProcessorService] Reservation added successfully.");

            notifyReservationUpdate(allReservations);
        } catch (Exception e) {
            System.err.println("[ERROR] setReservations: " + e.getMessage());
            throw new ReservationException("Failed to add reservation", e);
        }
    }

    @Override
    public void updateReservation(Reservation updatedReservation) throws RemoteException, ModifyReservationException {
        try {
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);
            boolean updated = false;

            for (int i = 0; i < allReservations.size(); i++) {
                if (allReservations.get(i).getReservationID().equals(updatedReservation.getReservationID())) {
                    allReservations.set(i, updatedReservation);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                throw new ReservationException("Reservation ID " + updatedReservation.getReservationID() + " not found.");
            }

            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[StudentProcessorService] Reservation updated successfully.");

            notifyReservationUpdate(allReservations);
        } catch (Exception e) {
            System.err.println("[ERROR] updateReservation: " + e.getMessage());
            throw new ModifyReservationException("Failed to update reservation", e);
        }
    }

    @Override
    public void cancelReservation(String reservationID) throws RemoteException, ReservationException {
        try {
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);
            boolean removed = allReservations.removeIf(res -> res.getReservationID().equals(reservationID));

            if (!removed) {
                throw new ReservationException("Reservation ID " + reservationID + " not found.");
            }

            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[StudentProcessorService] Reservation cancelled successfully.");

            notifyReservationUpdate(allReservations);
        } catch (Exception e) {
            System.err.println("[ERROR] cancelReservation: " + e.getMessage());
            throw new ReservationException("Failed to cancel reservation", e);
        }
    }

    @Override
    public List<Terminal> getActiveTerminals() throws RemoteException {
        try {
            System.out.println("[StudentProcessorService] Fetching active terminals...");
            List<Terminal> terminals = JSONUtility.loadTerminals(TERMINALS_FILE);

            List<Terminal> activeTerminals = terminals.stream()
                    .filter(ter -> "Active".equalsIgnoreCase(ter.getStatus()))
                    .collect(Collectors.toList());

            System.out.println("[StudentProcessorService] Loaded " + activeTerminals.size() + " active terminals.");
            return activeTerminals;
        } catch (Exception e) {
            System.err.println("[ERROR] getActiveTerminals: " + e.getMessage());
            throw new RemoteException("Error fetching active terminals", e);
        }
    }

    // --- Callback Management Methods ---

    @Override
    public void registerCallback(Broadcast callback) throws RemoteException {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            System.out.println("[StudentProcessorService] Client callback registered. Total clients: " + callbacks.size());
        }
    }

    @Override
    public void unregisterCallback(Broadcast callback) throws RemoteException {
        callbacks.remove(callback);
        System.out.println("[StudentProcessorService] Client callback unregistered. Remaining clients: " + callbacks.size());
    }

    private void notifyReservationUpdate(List<Reservation> reservations) {
        callbacks.removeIf(callback -> {
            try {
                callback.updateReservationApproval(reservations);
                return false; // Keep callback
            } catch (RemoteException e) {
                System.err.println("[StudentProcessorService] Removing unreachable callback: " + e.getMessage());
                return true; // Remove callback
            }
        });
    }

    private void notifyTerminalUpdate(List<Terminal> terminals) {
        callbacks.removeIf(callback -> {
            try {
                callback.updateTerminal(terminals);
                return false;
            } catch (RemoteException e) {
                System.err.println("[StudentProcessorService] Removing unreachable callback: " + e.getMessage());
                return true;
            }
        });
    }

    @Override
    public void updateTerminals(List<Terminal> terminals) throws RemoteException {
        System.out.println("[StudentProcessorService] Received terminal update. Notifying clients...");
        notifyTerminalUpdate(terminals);
    }
}
