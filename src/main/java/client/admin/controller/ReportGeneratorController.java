package client.admin.controller;

import client.admin.model.ReportGeneratorModel;
import client.admin.view.ReportGeneratorView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;

/**
 * The `ReportGeneratorController` class is responsible for managing the logic
 * and interactions for generating reports. It handles loading reservation and log data,
 * updating the view, and responding to updates via callbacks.
 */
public class ReportGeneratorController {

    private final ReportGeneratorModel model;
    private final ReportGeneratorView view;

    /**
     * Constructs a `ReportGeneratorController` with the specified view.
     * Initializes the model, sets up callbacks for reservation and log updates,
     * and loads the initial data.
     *
     * @param view The `ReportGeneratorView` instance associated with this controller.
     */
    public ReportGeneratorController(ReportGeneratorView view) {
        this.model = new ReportGeneratorModel();
        this.view = view;

        // Register callback to receive log and reservation updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {

            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                Platform.runLater(() -> {
                    view.updateReservationsTable(reservations);
//                    System.out.println("[DEBUG] Reservation update received via callback. Table updated with "
//                            + reservations.size() + " entries.");
                });
            }

            @Override
            public void updateLogs(List<Log> logs) {
                Platform.runLater(() -> {
                    view.updateLogsTable(logs);
//                    System.out.println("[DEBUG] Log update received via callback. Table updated with "
//                            + logs.size() + " entries.");
                });
            }
        });

        // Initial load of data
        loadReservations();
        loadLogs();
    }

    /**
     * Loads reservation data from the model and updates the view.
     */
    public void loadReservations() {

        List<Reservation> reservations = model.fetchReservations();

        if (reservations != null) {
            Platform.runLater(() -> {
                view.updateReservationsTable(reservations);
                System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }

    /**
     * Loads log data from the model and updates the view.
     */
    public void loadLogs() {
        List<Log> logs = model.fetchLogs();

        if (logs != null) {
            Platform.runLater(() -> {
                view.updateLogsTable(logs);
                System.out.println("[CLIENT] Table updated with " + logs.size() + " log entries.");
            });
        } else {
            System.err.println("[ERROR] Failed to load logs.");
        }
    }
}
