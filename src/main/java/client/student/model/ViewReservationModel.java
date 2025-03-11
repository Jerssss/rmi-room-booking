package client.student.model;

import client.ClientMain;
import shared.Reservation;
import shared.interfaces.AdminProcessors;
import shared.interfaces.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;

public class ViewReservationModel {
    private StudentProcessors studentProcessors;
    private String studentID; // The logged-in student's ID

    public ViewReservationModel(String studentID) {
        this.studentProcessors = ClientMain.getStudentProcessors(); // Get RMI instance
        this.studentID = studentID; // Store the student ID after login
    }


    public List<Reservation> fetchReservations() {
        System.out.println("[DEBUG] fetchReservations() method called.");

        try {
            if (studentProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Reservation> reservations = studentProcessors.getReservation(studentID);

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
