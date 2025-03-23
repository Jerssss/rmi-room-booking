package client.admin.model;

import shared.Reservation;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;
import client.ClientMain;
import client.utility.ClientCallBack;

import java.rmi.RemoteException;
import java.util.List;

/**
 * The `ViewStudentReservationsModel` class is responsible for handling the logic related to
 * viewing student reservations. It fetches reservation data from the server via RMI.
 */
public class ViewStudentReservationsModel {

    private AdminProcessors adminProcessors;
     /**
     * Constructs a `ViewStudentReservationsModel` and initializes the RMI service.
     */
    public ViewStudentReservationsModel() {
        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
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
                System.out.println("=====================================================");
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
     * Registers the client callback so that the server can push reservation updates.
     *
     * @param updateTable The callback implementation that handles reservation updates.
     */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            adminProcessors.registerCallback(clientCallBack);
            System.out.println("[CLIENT] Callback registered for reservation updates.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
