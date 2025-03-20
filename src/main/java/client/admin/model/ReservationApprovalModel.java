package client.admin.model;

import client.ClientMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Reservation;
import shared.callback.Broadcast;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;
import java.util.List;

public class ReservationApprovalModel {

    private AdminProcessors adminProcessors;

    public ReservationApprovalModel() {
        this.adminProcessors = ClientMain.getAdminProcessors();
    }

    public ObservableList<Reservation> loadReservationData() {
        System.out.println("[CLIENT] loadReservationData() method called");

        try {
            if (adminProcessors == null) {
                System.out.println("[ERROR] Admin Processors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = adminProcessors.getAllStudentReservations();
            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[SERVER] No reservations found via RMI.");
            } else {
                System.out.println("[CLIENT] Loaded " + reservations.size() + " reservations via RMI.");
                for (Reservation reservation : reservations) {
                    System.out.println("[SERVER] " + reservation);
                }
            }
            return FXCollections.observableArrayList(reservations);
        } catch (RemoteException re) {
            System.out.println("[ERROR] RMI call failed: " + re.getMessage());
            return null;
        }
    }

    public void saveReservationData(List<Reservation> reservations) {
        if (adminProcessors == null) {
            System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
            return;
        }

        try {
            boolean success = adminProcessors.updateReservations(reservations);
            if (success) {
                System.out.println("[CLIENT] All reservations updated successfully.");
            } else {
                System.err.println("[ERROR] Failed to update reservations.");
            }
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
        }
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

    public void registerCallback(Broadcast callback) throws RemoteException {
        if (adminProcessors == null) {
            System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
            return;
        }
        adminProcessors.registerCallback(callback);
    }
}