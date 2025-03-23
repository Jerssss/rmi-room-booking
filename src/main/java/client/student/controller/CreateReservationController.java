package client.student.controller;

import client.student.model.CreateReservationModel;
import client.student.view.CreateReservationView;
import javafx.application.Platform;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import java.util.List;

public class CreateReservationController {
    private final CreateReservationView view;
    private final CreateReservationModel model;

    public CreateReservationController(CreateReservationView view, CreateReservationModel model) {
        this.view = view;
        this.model = model;

        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                Platform.runLater(() -> view.updateTable(terminals));
                System.out.println("[CLIENT] Terminal update received via callback.");
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                // Not used in this view
            }

            @Override
            public void updateLogs(List<Log> logs) {
                // Not used in this view
            }
        });

        loadTerminals();
    }

    public void loadTerminals() {
        System.out.println("[CLIENT] Fetching terminal data...");
        List<Terminal> terminals = model.fetchTerminals();
        if (terminals == null || terminals.isEmpty()) {
            System.err.println("[ERROR] No terminals found.");
        } else {
            System.out.println("[CLIENT] Loaded " + terminals.size() + " terminals.");
        }
        view.updateTable(terminals);
    }

    // Added getter to allow access to the model from the view
    public CreateReservationModel getModel() {
        return model;
    }
}
