package client.student.controller;
import client.student.model.ViewReservationModel;
import client.student.view.ViewReservationView;
import javafx.application.Platform;
import shared.Reservation;

import java.util.List;

public class ViewReservationController {
    private final ViewReservationView view;
    private final ViewReservationModel model;

    public ViewReservationController(ViewReservationView view, ViewReservationModel model) {
        this.view = view;
        this.model = model;

        // Load reservations when the page opens
        loadReservations();

        this.view.setRefreshButtonAction(event -> loadReservations());
        this.view.setSearchButtonAction(event -> view.searchReservations()); // Add search functionality
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[DEBUG] loadReservations() method called."); // Add this

        List<Reservation> reservations = model.fetchReservations();

        if (reservations != null) {
            Platform.runLater(() -> {
                view.updateTable(reservations);
                System.out.println("[DEBUG] Table updated with " + reservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }
}
