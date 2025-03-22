package client.admin.controller;

import client.admin.model.ReservationApprovalModel;
import client.admin.view.ReservationApprovalView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Reservation;
import shared.callback.UpdateTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationApprovalController implements UpdateTable {

    private final ReservationApprovalView view;
    private final ReservationApprovalModel model;
    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    public ReservationApprovalController(ReservationApprovalView view, ReservationApprovalModel model) {
        this.view = view;
        this.model = model;

        // Let the model initialize and register the callback with this controller as the UpdateTable listener.
        model.initCallback(this);

        loadReservations();
        this.view.setActionRefreshButton(event -> loadReservations());
        this.view.setActionSaveChangesButton(event -> saveChanges());
        this.view.setActionSearchButton(event -> searchTerminals(view.getSearchStudResTextField().getText()));
    }

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

    public void saveChanges() {
        List<Reservation> reservationsToSave = new ArrayList<>(view.getApproveResTableView().getItems());
        System.out.println("[CLIENT] Reservations to save:");
        for (Reservation reservation : reservationsToSave) {
            System.out.println(reservation);
        }
        model.saveReservationData(reservationsToSave);
        JOptionPane.showMessageDialog(null, "Changes have been successfully saved!",
                "Save Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    public void searchTerminals(String searchText) {
        System.out.println("[DEBUG] Searching for terminals with keyword: " + searchText);

        if (searchText == null || searchText.trim().isEmpty()) {
            view.setReservationData(reservationData);
            System.out.println("[DEBUG] Search text is empty. Resetting to full terminal list.");
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

    // This method is defined by the UpdateTable interface.
    @Override
    public void updateReservations(List<Reservation> reservations) {
        Platform.runLater(() -> {
            reservationData.setAll(reservations);
            view.updateTable(reservations);
            System.out.println("[CLIENT] Reservations updated via callback.");
        });
    }

    @Override
    public void updateTerminals(List<shared.Terminal> terminals) {
        System.out.println("[CLIENT] Terminal update received (not used in this view).");
    }

    @Override
    public void updateLogs(List<shared.Log> logs) {
        System.out.println("[CLIENT] Log update received (not used in this view).");
    }
}
