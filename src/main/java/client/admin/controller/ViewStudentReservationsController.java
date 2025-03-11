package client.admin.controller;

import client.admin.model.ViewStudentReservationsModel;
import client.admin.view.ViewStudentReservationsView;
import javafx.application.Platform;
import shared.Reservation;

import java.util.List;

public class ViewStudentReservationsController {
    private final ViewStudentReservationsView view;
    private final ViewStudentReservationsModel model;

    public ViewStudentReservationsController(ViewStudentReservationsView view, ViewStudentReservationsModel model) {
        this.view = view;
        this.model = model;

        // Load reservations when the page opens
        loadReservations();

        // Set button actions
        this.view.setRefreshButtonAction(event -> loadReservations());
        this.view.setSearchButtonAction(event -> view.searchReservations()); // Add search functionality
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[CLIENT] loadReservations() method called."); // Add this

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
