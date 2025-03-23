package client.admin.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Log;
import shared.Reservation;
import shared.callback.UpdateTable;
import shared.interfaces.admin.AdminProcessors;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The `ReportGeneratorModel` class is responsible for handling the logic related to
 * generating reports. It fetches logs and reservations from the server via RMI.
 */
public class ReportGeneratorModel {
    private AdminProcessors adminProcessors;

    /**
     * Constructs a `ReportGeneratorModel` and initializes the RMI service.
     */
    public ReportGeneratorModel() {
        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
    }

    /**
     * Fetches a list of all logs from the server.
     *
     * @return A list of `Log` objects, or `null` if the operation fails.
     */
    public List<Log> fetchLogs() {
        try {
            if (adminProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Log> logs = adminProcessors.getAllLogs();

            if (logs == null || logs.isEmpty()) {
                System.out.println("[CLIENT] No logs found via RMI.");
            } else {
                System.out.println("[CLIENT] Loaded " + logs.size() + " logs via RMI.");
            }

            return logs;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fetches a list of all reservations from the server.
     *
     * @return A list of `Reservation` objects, or `null` if the operation fails.
     */
    public List<Reservation> fetchReservations() {
        try {
            if (adminProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = adminProcessors.getAllStudentReservations();

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[CLIENT] No reservations found via RMI.");
            } else {
                System.out.println("[CLIENT] Loaded " + reservations.size() + " reservations via RMI.");

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
