package client.admin.controller;

import client.admin.model.ReportGeneratorModel;
import client.admin.view.ReportGeneratorView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import shared.Log;
import shared.Reservation;

import java.util.List;

public class ReportGeneratorController {

    private final ReportGeneratorModel model;
    private final ReportGeneratorView view;

    public ReportGeneratorController(ReportGeneratorView view) {
        this.model = new ReportGeneratorModel();
        this.view = view;
        loadReservations();
        loadLogs();
    }
    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[DEBUG] loadReservations() method called."); // Add this

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
        System.out.println("[DEBUG] loadReservations() method called."); // Add this

        List<Log> logs = model.fetchLogs();

        if (logs != null) {
            Platform.runLater(() -> {
                view.updateLogsTable(logs);
                System.out.println("[DEBUG] Table updated with " + logs.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }
}