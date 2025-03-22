package client.admin.controller;

import client.admin.model.ViewStudentReservationsModel;
import client.admin.view.ViewStudentReservationsView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;

public class ViewStudentReservationsController {
    private final ViewStudentReservationsView view;
    private final ViewStudentReservationsModel model;

    public ViewStudentReservationsController(ViewStudentReservationsView view, ViewStudentReservationsModel model) {
        this.view = view;
        this.model = model;

        // Register the callback to receive reservation updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {

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

            }

        });

        // Load reservations when the page opens
        loadReservations();

        // Set button actions
        this.view.setRefreshButtonAction(event -> loadReservations());
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[CLIENT] loadReservations() method called.");

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
