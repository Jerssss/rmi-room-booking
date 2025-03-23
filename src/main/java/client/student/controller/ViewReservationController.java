package client.student.controller;

import client.student.model.ViewReservationModel;
import client.student.view.ViewReservationView;
import javafx.application.Platform;
import shared.Reservation;
import shared.Terminal;
import shared.Log;
import shared.callback.UpdateTable;

import java.util.List;
import java.util.stream.Collectors;

public class ViewReservationController {
    private final ViewReservationView view;
    private final ViewReservationModel model;
    private final String studentID; // Store the student's ID

    public ViewReservationController(ViewReservationView view, ViewReservationModel model, String studentID) {
        this.view = view;
        this.model = model;
        this.studentID = studentID; // Store the logged-in student's ID

        // Register the callback for reservation updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                // No need to handle terminal updates here
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                // Filter reservations only for the logged-in student
                List<Reservation> studentReservations = reservations.stream()
                        .filter(reservation -> reservation.getUserID().equals(studentID))
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    view.updateTable(studentReservations);
                    System.out.println("[CLIENT] Callback received. Updating table with "
                            + studentReservations.size() + " reservations.");
                });
            }

            @Override
            public void updateLogs(List<Log> logs) {
                // No need to handle log updates here
            }
        });

        // Load reservations initially
        loadReservations();

        // Set refresh button action
        this.view.setRefreshButtonAction(event -> loadReservations());
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        List<Reservation> reservations = model.fetchReservations();

        if (reservations != null) {
            Platform.runLater(() -> {
                view.updateTable(reservations);
                System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }
}
