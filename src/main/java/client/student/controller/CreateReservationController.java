package client.student.controller;

import client.student.model.CreateReservationModel;
import client.student.view.CreateReservationView;
import javafx.application.Platform;
import shared.Reservation;
import shared.Terminal;

import java.util.List;

public class CreateReservationController {
    private final CreateReservationView view;
    private final CreateReservationModel model;

    public CreateReservationController(CreateReservationView view, CreateReservationModel model) {
        this.view = view;
        this.model = model;

        // Load reservations when the page opens
        loadTerminals();

        // Set button actions
        this.view.setRefreshButtonAction(event -> loadTerminals());
        this.view.setSearchButtonAction(event -> view.searchTerminals()); // Add search functionality
    }

    /** Loads reservation data and updates the view */
    public void loadTerminals() {
        System.out.println("[DEBUG] loadTerminals() method called."); // Add this

        List<Terminal> terminals = model.fetchTerminals();

        if (terminals != null) {
            Platform.runLater(() -> {
                view.updateTable(terminals);
                System.out.println("[DEBUG] Table updated with " + terminals.size() + " Terminals.");
            });
        } else {
            System.err.println("[ERROR] Failed to load Terminals.");
        }
    }
}
