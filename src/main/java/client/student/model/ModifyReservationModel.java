package client.student.model;

import client.ClientMain;
import shared.Reservation;
import shared.interfaces.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;

public class ModifyReservationModel {
    private final StudentProcessors studentProcessors;
    private final String studentID;

    public ModifyReservationModel(String studentID) {
        this.studentProcessors = ClientMain.getStudentProcessors();
        this.studentID = studentID;
    }

    public List<Reservation> fetchReservations() {
        try {
            return studentProcessors.getReservations(studentID);
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    public boolean updateReservation(Reservation reservation) {
        try {
            return studentProcessors.updateReservation(reservation);
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelReservation(String reservationID) {
        try {
            return studentProcessors.cancelReservation(reservationID);
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return false;
        }
    }
}