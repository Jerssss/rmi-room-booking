package client.admin.controller;

import client.admin.model.ReservationApprovalModel;
import client.admin.view.ReservationApprovalView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Reservation;
import shared.Log;
import shared.Terminal;
import shared.callback.UpdateTable;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationApprovalController {

    private final ReservationApprovalModel model;
    private final ReservationApprovalView view;
    private final ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    public ReservationApprovalController(ReservationApprovalView view) {
        this.view = view;
        this.model = new ReservationApprovalModel();

        // Register callback to receive reservation updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                System.out.println("[CLIENT] Terminal update received (not used in this view).");
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                Platform.runLater(() -> {
                    reservationData.setAll(reservations);
                    view.updateTable(reservations);
                    System.out.println("[CLIENT] Reservation update received via callback. Table updated with "
                            + reservations.size() + " entries.");
                });
            }

            @Override
            public void updateLogs(List<Log> logs) {
                System.out.println("[CLIENT] Log update received (not used in this view).");
            }
        });

        // Initial load of data
        loadReservations();

        // Set button actions
        this.view.setActionSaveChangesButton(event -> saveChanges());
        this.view.getSearchStudResTextField().textProperty().addListener(
                (observable, oldValue, newValue) -> searchReservations(newValue)
        );
    }

    /** Loads reservation data and updates the view */
    public void loadReservations() {
        System.out.println("[CLIENT] loadReservations() method called.");

        List<Reservation> reservations = model.fetchReservations();

        if (reservations != null) {
            Platform.runLater(() -> {
                reservationData.setAll(reservations);
                view.updateTable(reservations);
                System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }

    /** Saves reservation changes to the model */
    public void saveChanges() {
        List<Reservation> reservationsToSave = List.copyOf(view.getApproveResTableView().getItems());

        System.out.println("[CLIENT] Saving " + reservationsToSave.size() + " reservations.");
        model.saveReservationData(reservationsToSave);

        JOptionPane.showMessageDialog(null, "Changes have been successfully saved!",
                "Save Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Filters reservations based on search text */
    public void searchReservations(String searchText) {
        System.out.println("[DEBUG] Searching reservations with keyword: " + searchText);

        if (searchText == null || searchText.trim().isEmpty()) {
            view.setReservationData(reservationData);
            System.out.println("[DEBUG] Search text is empty. Resetting to full reservation list.");
            return;
        }

        String lowerCaseQuery = searchText.toLowerCase();
        ObservableList<Reservation> filteredList = reservationData.stream()
                .filter(reservation -> reservation.getReservationID().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getUserID().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getTerminalID().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getRoomID().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getReservationDate().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getStartTime().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getEndTime().toLowerCase().contains(lowerCaseQuery) ||
                        reservation.getStatus().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        view.setReservationData(filteredList);
        System.out.println("[DEBUG] Search completed. Matching results: " + filteredList.size());
    }
}
