package server.rmiservices;

import shared.Reservation;
import shared.Terminal;
import shared.callback.Broadcast;
import shared.interfaces.student.StudentProcessors;
import shared.interfaces.admin.AdminProcessors;
import util.JSONUtility;
import util.exception.ModifyReservationException;
import util.exception.ReservationException;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class StudentProcessorService extends UnicastRemoteObject implements StudentProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");
    private static final File TERMINALS_FILE = new File("src/main/resources/data/terminal.json");

    // Thread-safe list to store registered callbacks
    private final List<Broadcast> callbacks = new CopyOnWriteArrayList<>();

    // ExecutorService for asynchronous callback notifications
    private final ExecutorService callbackExecutor = Executors.newCachedThreadPool();

    public StudentProcessorService() throws RemoteException {
        super();
        System.out.println("[SERVER] StudentProcessorService instantiated.");
    }

    @Override
    public List<Reservation> getReservations(String studentID) throws RemoteException {
        try {
            System.out.println("[SERVER] StudentProcessorService: Fetching reservations for student: " + studentID);
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            List<Reservation> userReservations = allReservations.stream()
                    .filter(reservation -> studentID.equals(reservation.getUserID()))
                    .collect(Collectors.toList());

            System.out.println("[SERVER] StudentProcessorService: Found " + userReservations.size() + " reservations for student " + studentID);
            return userReservations;
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: getReservations: " + e.getMessage());
            throw new RemoteException("Error fetching reservations", e);
        }
    }

    @Override
    public List<Reservation> getAllReservations() throws RemoteException {
        try {
            System.out.println("[SERVER] StudentProcessorService: Fetching all reservations.");
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);
            System.out.println("[SERVER] StudentProcessorService: Total reservations loaded: " + allReservations.size());
            return allReservations;
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: getAllReservations: " + e.getMessage());
            throw new RemoteException("Error fetching all reservations", e);
        }
    }

    @Override
    public void setReservations(Reservation newReservation) throws RemoteException, ReservationException {
        try {
            System.out.println("[SERVER] StudentProcessorService: Adding reservation for user: " + newReservation.getUserID());
            List<Reservation> allReservations = JSONUtility.loadReservations(RESERVATIONS_FILE);

            allReservations.add(newReservation);
            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[SERVER] StudentProcessorService: Reservation added successfully.");

            // Notify local callbacks asynchronously
            notifyReservationUpdate(allReservations);

            // Notify admin clients about reservation update via RMI lookup
            try {
                Registry registry = LocateRegistry.getRegistry(1099); // Ensure correct RMI port
                AdminProcessors adminProc = (AdminProcessors) registry.lookup("admin_processors");
                adminProc.updateReservations(allReservations);
                System.out.println("[SERVER] StudentProcessorService: Pushed reservation update to admin clients.");
            } catch (Exception e) {
                System.err.println("[SERVER] StudentProcessorService: Failed to push reservation update to admin clients: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: setReservations: " + e.getMessage());
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
                    System.out.println("[SERVER] StudentProcessorService: Updated reservation: " + updatedReservation.getReservationID());
                    break;
                }
            }

            if (!updated) {
                throw new ReservationException("Reservation ID " + updatedReservation.getReservationID() + " not found.");
            }

            JSONUtility.saveReservations(allReservations, RESERVATIONS_FILE);
            System.out.println("[SERVER] StudentProcessorService: Reservation updated successfully.");

            // Notify local callbacks asynchronously
            notifyReservationUpdate(allReservations);

            // Notify admin clients about reservation update via RMI lookup
            try {
                Registry registry = LocateRegistry.getRegistry(1099); // Ensure correct RMI port
                AdminProcessors adminProc = (AdminProcessors) registry.lookup("admin_processors");
                adminProc.updateReservations(allReservations);
                System.out.println("[SERVER] StudentProcessorService: Pushed reservation update to admin clients.");
            } catch (Exception e) {
                System.err.println("[SERVER] StudentProcessorService: Failed to push reservation update to admin clients: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: updateReservation: " + e.getMessage());
            throw new ModifyReservationException("Failed to update reservation", e);
        }
    }

    /**
     * New method for handling batch reservation updates.
     */
    @Override
    public void updateReservations(List<Reservation> reservations) throws RemoteException {
        System.out.println("[SERVER] StudentProcessorService: Received batch reservation update.");
        try {
            // Save the whole list to file
            JSONUtility.saveReservations(reservations, RESERVATIONS_FILE);
            System.out.println("[SERVER] StudentProcessorService: Reservations saved successfully via batch update.");

            // Notify local callbacks asynchronously
            notifyReservationUpdate(reservations);
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: updateReservations: " + e.getMessage());
            throw new RemoteException("Batch update failed", e);
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
            System.out.println("[SERVER] StudentProcessorService: Reservation cancelled successfully.");

            // Notify local callbacks asynchronously
            notifyReservationUpdate(allReservations);

            // Notify admin clients about reservation update via RMI lookup
            try {
                Registry registry = LocateRegistry.getRegistry(1099); // Ensure correct RMI port
                AdminProcessors adminProc = (AdminProcessors) registry.lookup("admin_processors");
                adminProc.updateReservations(allReservations);
                System.out.println("[SERVER] StudentProcessorService: Pushed reservation update to admin clients.");
            } catch (Exception e) {
                System.err.println("[SERVER] StudentProcessorService: Failed to push reservation update to admin clients: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: cancelReservation: " + e.getMessage());
            throw new ReservationException("Failed to cancel reservation", e);
        }
    }

    @Override
    public List<Terminal> getActiveTerminals() throws RemoteException {
        try {
            System.out.println("[SERVER] StudentProcessorService: Fetching active terminals...");
            List<Terminal> terminals = JSONUtility.loadTerminals(TERMINALS_FILE);

            List<Terminal> activeTerminals = terminals.stream()
                    .filter(ter -> "Active".equalsIgnoreCase(ter.getStatus()))
                    .collect(Collectors.toList());

            System.out.println("[SERVER] StudentProcessorService: Loaded " + activeTerminals.size() + " active terminals.");
            return activeTerminals;
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: getActiveTerminals: " + e.getMessage());
            throw new RemoteException("Error fetching active terminals", e);
        }
    }

    // --- Callback Management Methods ---

    @Override
    public void registerCallback(Broadcast callback) throws RemoteException {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            System.out.println("[SERVER] StudentProcessorService: Client callback registered. Total clients: " + callbacks.size());
        }
    }

    @Override
    public void unregisterCallback(Broadcast callback) throws RemoteException {
        callbacks.remove(callback);
        System.out.println("[SERVER] StudentProcessorService: Client callback unregistered. Remaining clients: " + callbacks.size());
    }

    private void notifyReservationUpdate(List<Reservation> reservations) {
        System.out.println("[SERVER] StudentProcessorService: Notifying reservation update to " + callbacks.size() + " callbacks.");
        for (Broadcast callback : callbacks) {
            callbackExecutor.submit(() -> {
                try {
                    System.out.println("[SERVER] StudentProcessorService: Sending updateReservationApproval() to callback: " + callback.getClass().getName());
                    callback.updateReservationApproval(reservations);
                } catch (RemoteException e) {
                    System.err.println("[SERVER] StudentProcessorService: Removing unreachable callback ("
                            + callback.getClass().getName() + "): " + e.getMessage());
                    callbacks.remove(callback);
                }
            });
        }
    }

    private void notifyTerminalUpdate(List<Terminal> terminals) {
        System.out.println("[SERVER] StudentProcessorService: Notifying terminal update to " + callbacks.size() + " callbacks.");
        for (Broadcast callback : callbacks) {
            callbackExecutor.submit(() -> {
                try {
                    System.out.println("[SERVER] StudentProcessorService: Sending updateTerminal() to callback: " + callback.getClass().getName());
                    callback.updateTerminal(terminals);
                } catch (RemoteException e) {
                    System.err.println("[SERVER] StudentProcessorService: Removing unreachable callback ("
                            + callback.getClass().getName() + "): " + e.getMessage());
                    callbacks.remove(callback);
                }
            });
        }
    }

    @Override
    public void updateTerminals(List<Terminal> terminals) throws RemoteException {
        System.out.println("[SERVER] StudentProcessorService: Received terminal update. Notifying clients...");
        notifyTerminalUpdate(terminals);
    }
}
