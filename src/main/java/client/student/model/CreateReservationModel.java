package client.student.model;

import client.ClientMain;
import shared.Reservation;
import shared.Terminal;
import shared.interfaces.StudentProcessors;

import java.rmi.RemoteException;
import java.util.List;

public class CreateReservationModel {
    private StudentProcessors studentProcessors;

    public CreateReservationModel() {
        this.studentProcessors = ClientMain.getStudentProcessors(); // Get RMI instance
    }

    public List<Terminal> fetchTerminals() {
        System.out.println("[DEBUG] fetchTerminals() method called.");

        try {
            if (studentProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Terminal> terminals = studentProcessors.getActiveTerminals();

            if (terminals == null || terminals.isEmpty()) {
                System.out.println("[DEBUG] No terminals found via RMI.");
            } else {
                System.out.println("[DEBUG] Loaded " + terminals.size() + " terminals via RMI.");
                for (Terminal ter : terminals) {
                    System.out.println("[DEBUG] " + ter);
                }
            }

            return terminals;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a new reservation by calling the RMI service.
     * @param reservation The reservation object to add.
     * @return true if the reservation was added successfully, false otherwise.
     */
    public boolean addReservation(Reservation reservation) {
        try {
            return studentProcessors.setReservations(reservation);
        } catch (RemoteException e) {
            System.err.println("[ERROR] Remote exception while adding reservation: " + e.getMessage());
            return false;
        }
    }
}
