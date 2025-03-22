package client.admin.controller;

import client.admin.model.AddNewTerminalModel;
import client.admin.view.AddNewTerminalView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;

public class AddNewTerminalController {

    private final AddNewTerminalView view;
    private final AddNewTerminalModel model;

    public AddNewTerminalController(AddNewTerminalView view, AddNewTerminalModel model) {
        this.view = view;
        // Use the provided model instance
        this.model = model;

        // Register the callback so that when the server sends terminal updates,
        // the view is refreshed.
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                Platform.runLater(() -> view.updateTable(terminals));
                System.out.println("[CLIENT] Terminal update received via callback.");
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {

            }

            @Override
            public void updateLogs(List<Log> logs) {

            }
        });

        // Load the initial terminal data
        loadTerminals();
    }

    /**
     * Loads terminal data and updates the TableView.
     */
    public void loadTerminals() {
        System.out.println("[CLIENT] Calling fetchTerminals() to get terminal data...");
        List<Terminal> terminals = model.fetchTerminals();
        if (terminals == null || terminals.isEmpty()) {
            System.err.println("[ERROR] No terminals received from fetchTerminals().");
        } else {
            System.out.println("[CLIENT] Fetched " + terminals.size() + " terminals from RMI.");
        }
        view.updateTable(terminals);
    }
}
