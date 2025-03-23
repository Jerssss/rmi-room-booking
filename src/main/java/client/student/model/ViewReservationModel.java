package client.student.model;

import client.ClientMain;
import client.utility.ClientCallBack;
import shared.Reservation;
import shared.callback.UpdateTable;
import shared.interfaces.student.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;

public class ViewReservationModel {
    private StudentProcessors studentProcessors;
    private String studentID; // The logged-in student's ID

    public ViewReservationModel(String studentID) {
        this.studentProcessors = ClientMain.getStudentProcessors(); // Get RMI instance
        this.studentID = studentID; // Store the student ID after login
    }

    /** Fetch reservations for the logged-in student */
    public List<Reservation> fetchReservations() {
        try {
            if (studentProcessors == null) {
                System.err.println("[ERROR] StudentProcessors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = studentProcessors.getReservations(studentID);

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
