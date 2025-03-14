package client.admin.controller;

import client.admin.model.ModifyTerminalStatusModel;
import client.admin.view.ModifyTerminalStatusView;
import javafx.application.Platform;
import shared.Terminal;

import java.util.List;

public class ModifyTerminalStatusController {
    private final ModifyTerminalStatusView view;
    private final ModifyTerminalStatusModel model;

    public ModifyTerminalStatusController(ModifyTerminalStatusView view, ModifyTerminalStatusModel model) {
        this.view = view;
        this.model = model;

        // Load reservations when the page opens
        loadReservations();

        // Set button actions
        //this.view.setRefreshButtonAction(event -> loadReservations());
        //this.view.setSearchButtonAction(event -> view.searchReservations()); // Add search functionality
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[CLIENT] loadReservations() method called."); // Add this

        List<Terminal> reservations = model.fetchReservations();

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
