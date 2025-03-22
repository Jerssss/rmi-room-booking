package client.admin.controller;

import client.admin.model.ReportGeneratorModel;
import client.admin.view.ReportGeneratorView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;

public class ReportGeneratorController {

    private final ReportGeneratorModel model;
    private final ReportGeneratorView view;

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
                    System.out.println("[DEBUG] Reservation update received via callback. Table updated with "
                            + reservations.size() + " entries.");
                });
            }

            @Override
            public void updateLogs(List<Log> logs) {
                Platform.runLater(() -> {
                    view.updateLogsTable(logs);
                    System.out.println("[DEBUG] Log update received via callback. Table updated with "
                            + logs.size() + " entries.");
                });
            }
        });

        // Initial load of data
        loadReservations();
        loadLogs();
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[DEBUG] loadReservations() method called.");

        List<Reservation> reservations = model.fetchReservations();

        if (reservations != null) {
            Platform.runLater(() -> {
                view.updateReservationsTable(reservations);
                System.out.println("[DEBUG] Table updated with " + reservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }

    public void loadLogs() {
        System.out.println("[DEBUG] loadLogs() method called.");

        List<Log> logs = model.fetchLogs();

        if (logs != null) {
            Platform.runLater(() -> {
                view.updateLogsTable(logs);
                System.out.println("[DEBUG] Table updated with " + logs.size() + " log entries.");
            });
        } else {
            System.err.println("[ERROR] Failed to load logs.");
        }
    }
}
