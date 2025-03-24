package client.student.controller;

import client.student.model.ViewReservationModel;
import client.student.view.ViewReservationView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The ViewReservationController class is responsible for managing the logic and interactions
 * for viewing student reservations. It handles loading reservation data, updating the view,
 * and responding to reservation updates via callbacks.
 */
public class ViewReservationController {
    private final ViewReservationView view;
    private final ViewReservationModel model;
    private final String studentID; // Logged-in student's ID

    /**
     * Constructs a ViewReservationController with the specified view, model, and student ID.
     * Registers a callback to receive reservation updates and loads initial reservation data.
     *
     * @param view      the ViewReservationView instance
     * @param model     the ViewReservationModel instance
     * @param studentID the ID of the logged-in student
     */
    public ViewReservationController(ViewReservationView view, ViewReservationModel model, String studentID) {
        this.view = view;
        this.model = model;
        this.studentID = studentID;

        // Register the callback to receive reservation updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                // Terminal updates are not handled here.
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                // Filter reservations so only those for the logged-in student are displayed.
                List<Reservation> studentReservations = reservations.stream()
                        .filter(reservation -> reservation.getUserID().equals(studentID))
                        .collect(Collectors.toList());
                Platform.runLater(() -> {
                    view.updateTable(studentReservations);
                    System.out.println("[CLIENT] Callback received. Table updated with "
                            + studentReservations.size() + " reservations.");
                });
            }

            @Override
            public void updateLogs(List<Log> logs) {
                // Log updates are not needed in this view.
            }
        });

        // Load reservations when the view is initialized
        loadReservations();

        // Optionally, you can add a refresh button action similar to the admin version:
        this.view.setRefreshButtonAction(event -> loadReservations());
    }

    /**
     * Loads reservation data from the model, filters it by the student ID,
     * and updates the view.
     */
    public void loadReservations() {
        System.out.println("[CLIENT] loadReservations() method called.");
        List<Reservation> reservations = model.fetchReservations();
        if (reservations != null) {
            // Filter reservations by logged-in student's ID
            List<Reservation> studentReservations = reservations.stream()
                    .filter(reservation -> reservation.getUserID().equals(studentID))
                    .collect(Collectors.toList());
            Platform.runLater(() -> {
                view.updateTable(studentReservations);
                System.out.println("[CLIENT] Table updated with " + studentReservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }
}
