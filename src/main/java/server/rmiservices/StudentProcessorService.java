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

/**
 * The StudentProcessorService class provides remote methods for managing student reservations
 * and terminals. It implements the StudentProcessors interface and allows students to create,
 * update, retrieve, and cancel reservations, as well as manage callbacks for reservation updates.
 */
public class StudentProcessorService extends UnicastRemoteObject implements StudentProcessors {

    private static final File RESERVATIONS_FILE = new File("src/main/resources/data/reservation_approval.json");
    private static final File TERMINALS_FILE = new File("src/main/resources/data/terminal.json");

    // Thread-safe list to store registered callbacks
    private final List<Broadcast> callbacks = new CopyOnWriteArrayList<>();

    // ExecutorService for asynchronous callback notifications
    private final ExecutorService callbackExecutor = Executors.newCachedThreadPool();

    /**
     * Constructs a new StudentProcessorService instance.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public StudentProcessorService() throws RemoteException {
        super();
        System.out.println("[SERVER] StudentProcessorService instantiated.");
    }

    /**
     * Retrieves a list of reservations for a specific student.
     *
     * @param studentID the ID of the student whose reservations are to be fetched
     * @return a list of reservations associated with the specified student
     * @throws RemoteException if a remote communication error occurs
     */
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

    /**
     * Retrieves all reservations.
     *
     * @return a list of all reservations
     * @throws RemoteException if a remote communication error occurs
     */
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

    /**
     * Adds a new reservation.
     *
     * @param newReservation the reservation to be added
     * @throws RemoteException if a remote communication error occurs
     * @throws ReservationException if there is an error adding the reservation
     */
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
                System.err.println("[ SERVER] StudentProcessorService: Failed to push reservation update to admin clients: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[SERVER] StudentProcessorService: setReservations: " + e.getMessage());
            throw new ReservationException("Failed to add reservation", e);
        }
    }

    /**
     * Updates an existing reservation.
     *
     * @param updatedReservation the reservation with updated details
     * @throws RemoteException if a remote communication error occurs
     * @throws ModifyReservationException if there is an error modifying the reservation
     */
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
     * Updates multiple reservations in a batch.
     *
     * @param reservations the list of reservations to be updated
     * @throws RemoteException if a remote communication error occurs
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

    /**
     * Cancels a reservation by its ID.
     *
     * @param reservationID the ID of the reservation to be canceled
     * @throws RemoteException if a remote communication error occurs
     * @throws ReservationException if there is an error canceling the reservation
     */
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

    /**
     * Retrieves a list of active terminals.
     *
     * @return a list of active terminals
     * @throws RemoteException if a remote communication error occurs
     */
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

    /**
     * Registers a callback for reservation updates.
     *
     * @param callback the callback to be registered
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void registerCallback(Broadcast callback) throws RemoteException {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            System.out.println("[SERVER] StudentProcessorService: Client callback registered. Total clients: " + callbacks.size());
        }
    }

    /**
     * Unregisters a callback for reservation updates.
     *
     * @param callback the callback to be unregistered
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void unregisterCallback(Broadcast callback) throws RemoteException {
        callbacks.remove(callback);
        System.out.println("[SERVER] StudentProcessorService: Client callback unregistered. Remaining clients: " + callbacks.size());
    }

    /**
     * Notifies all registered callbacks about reservation updates.
     *
     * @param reservations the list of updated reservations
     */
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

    /**
     * Notifies all registered callbacks about terminal updates.
     *
     * @param terminals the list of updated terminals
     */
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

    /**
     * Updates the list of terminals and notifies clients.
     *
     * @param terminals the list of terminals to be updated
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void updateTerminals(List<Terminal> terminals) throws RemoteException {
        System.out.println("[SERVER] StudentProcessorService: Received terminal update. Notifying clients...");
        notifyTerminalUpdate(terminals);
    }
}