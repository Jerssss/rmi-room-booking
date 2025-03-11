package client.admin.model;

import shared.Reservation;
import shared.interfaces.AdminProcessors;
import client.ClientMain;

import java.rmi.RemoteException;
import java.util.List;

public class ViewStudentReservationsModel {

    private AdminProcessors adminProcessors;

    public ViewStudentReservationsModel() {
        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
    }

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
}
