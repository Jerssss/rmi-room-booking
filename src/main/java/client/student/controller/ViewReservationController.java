package client.student.controller;

import client.student.model.ViewReservationModel;
import client.student.view.ViewReservationView;
import javafx.application.Platform;
import shared.Reservation;
import shared.Terminal;
import shared.Log;
import shared.callback.UpdateTable;

import java.util.List;

public class ViewReservationController {
    private final ViewReservationView view;
    private final ViewReservationModel model;

    public ViewReservationController(ViewReservationView view, ViewReservationModel model) {
        this.view = view;
        this.model = model;

        // Register the callback for reservation updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                // No need to handle terminal updates here
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                Platform.runLater(() -> {
                    view.updateTable(reservations);
                    System.out.println("[CLIENT] Reservation update received via callback.");
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
