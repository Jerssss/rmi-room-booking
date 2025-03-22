package client.admin.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Log;
import shared.Reservation;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;
import java.rmi.RemoteException;
import java.util.List;

public class ReportGeneratorModel {
    private AdminProcessors adminProcessors;

    public ReportGeneratorModel() {
        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
    }

    public List<Log> fetchLogs() {
        System.out.println("[DEBUG] fetchLogs() method called.");

        try {
            if (adminProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Log> logs = adminProcessors.getAllLogs();

            if (logs == null || logs.isEmpty()) {
                System.out.println("[DEBUG] No logs found via RMI.");
            } else {
                System.out.println("[DEBUG] Loaded " + logs.size() + " logs via RMI.");
                for (Log log : logs) {
                    System.out.println("[DEBUG] " + log);
                }
            }

            return logs;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    public List<Reservation> fetchReservations() {
        System.out.println("[DEBUG] fetchReservations() method called.");

        try {
            if (adminProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = adminProcessors.getAllStudentReservations();

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[DEBUG] No reservations found via RMI.");
            } else {
                System.out.println("[DEBUG] Loaded " + reservations.size() + " reservations via RMI.");
                for (Reservation res : reservations) {
                    System.out.println("[DEBUG] " + res);
                }
            }

            return reservations;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers the client callback so that the server can push log and reservation updates.
     *
     * @param updateTable The callback implementation handling log and reservation updates.
     */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            adminProcessors.registerCallback(clientCallBack);
            System.out.println("[CLIENT] Callback registered for report updates.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
