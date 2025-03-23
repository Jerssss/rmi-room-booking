package client.admin.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Reservation;
import shared.callback.Broadcast;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;
import java.util.List;

/**
 * The `ReservationApprovalModel` class is responsible for handling the logic related to
 * approving reservations. It fetches and updates reservation data via RMI.
 */
public class ReservationApprovalModel {

    private AdminProcessors adminProcessors;

    /**
     * Constructs a `ReservationApprovalModel` and initializes the RMI service.
     */
    public ReservationApprovalModel() {
        this.adminProcessors = ClientMain.getAdminProcessors();
    }

    /**
     * Initializes and registers the callback with the RMI service.
     * The model creates the callback using the provided UpdateTable listener.
     *
     * @param updateTable The callback implementation that handles reservation updates.
     */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            registerCallback(clientCallBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the updated reservation data to the server.
     *
     * @param reservations A list of `Reservation` objects with updated data.
     */
    public void saveReservationData(List<Reservation> reservations) {
        if (adminProcessors == null) {
            System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
            return;
        }

        try {
            adminProcessors.updateReservations(reservations);
            System.out.println("[CLIENT] All reservations updated successfully.");
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
        }
    }

    /**
     * Fetches a list of all reservations from the server.
     *
     * @return A list of `Reservation` objects, or `null` if the operation fails.
     */
    public List<Reservation> fetchReservations() {
        System.out.println("[CLIENT] fetchReservations() method called.");

        try {
            if (adminProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = adminProcessors.getAllStudentReservations();

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[SERVER] No reservations found via RMI.");
            } else {
                System.out.println("[CLIENT] Loaded " + reservations.size() + " reservations via RMI.");
                for (Reservation res : reservations) {
                    System.out.println("[SERVER] " + res);
                }
            }

            return reservations;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers the provided callback with the AdminProcessors RMI service.
     *
     * @param callback The callback implementation to be registered.
     * @throws RemoteException If the RMI call fails.
     */
    public void registerCallback(Broadcast callback) throws RemoteException {
        if (adminProcessors == null) {
            System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
            return;
        }
        adminProcessors.registerCallback(callback);
    }
}
