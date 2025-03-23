package client.student.controller;

import client.student.model.ViewReservationModel;
import client.student.view.ViewReservationView;
import javafx.application.Platform;
import shared.Reservation;
import shared.callback.UpdateTable;
import shared.Log; // Assuming Log is defined in shared, even if not used here
import shared.Terminal; // For completeness; not used in reservations callback here

import java.util.List;

public class ViewReservationController {
    private final ViewReservationView view;
    private final ViewReservationModel model;

    public ViewReservationController(ViewReservationView view, ViewReservationModel model) {
        this.view = view;
        this.model = model;

        // Load reservations when the page opens
        loadReservations();

        // Register the callback to receive reservation updates
        model.initReservationCallback(new UpdateTable() {
            @Override
            public void updateReservations(List<Reservation> reservations) {
                // Run on the JavaFX Application Thread
                Platform.runLater(() -> {
                    view.updateTable(reservations);
                    System.out.println("[CLIENT] Reservation callback updated table with "
                            + (reservations != null ? reservations.size() : 0) + " reservations.");
                });
            }

            @Override
            public void updateTerminals(List<Terminal> terminals) {
                // Not used in this context
            }

            @Override
            public void updateLogs(List<Log> logs) {
                // Not used in this context
            }
        });

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
