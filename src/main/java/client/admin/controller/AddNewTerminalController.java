package client.admin.controller;

import client.admin.model.AddNewTerminalModel;
import client.admin.view.AddNewTerminalView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;

/**
 * The `AddNewTerminalController` class is responsible for managing the interaction
 * between the `AddNewTerminalView` and the `AddNewTerminalModel`. It handles
 * updating the view with terminal data received from the model and ensures
 * the UI is refreshed when new data is available.
 */
public class AddNewTerminalController {

    private final AddNewTerminalView view;
    private final AddNewTerminalModel model;

    /**
     * Constructs an `AddNewTerminalController` with the specified view and model.
     * Initializes the callback for terminal updates and loads the initial terminal data.
     *
     * @param view  The `AddNewTerminalView` instance associated with this controller.
     * @param model The `AddNewTerminalModel` instance associated with this controller.
     */
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
                // Not implemented
            }

            @Override
            public void updateLogs(List<Log> logs) {
                // Not implemented
            }
        });

        // Load the initial terminal data
        loadTerminals();
    }

    /**
     * Loads terminal data from the model and updates the TableView in the view.
     * If no terminals are received, an error message is logged.
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
