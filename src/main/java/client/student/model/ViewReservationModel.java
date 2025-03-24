package client.student.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Reservation;
import shared.callback.UpdateTable;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

public class ViewReservationModel {
    private StudentProcessors studentProcessors;
    private String studentID; // The logged-in student's ID

    public ViewReservationModel(String studentID) {
        this.studentProcessors = ClientMain.getStudentProcessors(); // Get RMI instance
        this.studentID = studentID; // Store the student ID after login
    }

    /** Fetch reservations for the logged-in student */
    public List<Reservation> fetchReservations() {
        System.out.println("[CLIENT] fetchReservations() method called.");

        try {
            if (studentProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = studentProcessors.getAllReservations();

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


    /** Registers the callback for real-time reservation updates */
    public void initCallback(UpdateTable updateTable) {
        try {
            ClientCallBack clientCallBack = new ClientCallBack(updateTable);
            studentProcessors.registerCallback(clientCallBack);
            System.out.println("[CLIENT] Callback registered for reservation updates.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
