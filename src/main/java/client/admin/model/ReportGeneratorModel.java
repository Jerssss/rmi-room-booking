package client.admin.model;

import client.ClientMain;
import shared.Admin;
import shared.Log;
import shared.Reservation;
import shared.interfaces.admin.AdminProcessors;
import shared.interfaces.student.StudentProcessors;
import util.JSONUtility;
import java.io.File;
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
                for (Log Logs : logs) {
                    System.out.println("[DEBUG] " + Logs);
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
}
