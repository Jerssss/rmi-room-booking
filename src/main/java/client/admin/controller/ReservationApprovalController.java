package client.admin.controller;

import client.admin.model.ReservationApprovalModel;
import client.admin.view.ReservationApprovalView;
import client.utility.ClientCallBack;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Reservation;
import shared.callback.Broadcast;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationApprovalController {

    private final ReservationApprovalView view;
    private final ReservationApprovalModel model;
    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();
    private ClientCallBack clientCallBack;

    public ReservationApprovalController(ReservationApprovalView view, ReservationApprovalModel model) {
        this.view = view;
        this.model = model;
        try {
            this.clientCallBack = new ClientCallBack(this);
            registerCallback();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        loadReservations();
        this.view.setActionRefreshButton(event -> loadReservations());
        this.view.setActionSaveChangesButton(event -> saveChanges());
        this.view.setActionSearchButton(event -> searchTerminals(view.getSearchStudResTextField().getText()));
    }

    private void registerCallback() {
        try {
            model.registerCallback(clientCallBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void loadReservations() {
        System.out.println("[CLIENT] loadReservations() method called.");

        List<Reservation> reservations = model.fetchReservations();

        if (reservations != null) {
            Platform.runLater(() -> {
                reservationData.setAll(reservations); // Update reservationData
                view.updateTable(reservations);
                System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
        }
    }

    public void saveChanges() {
        // Convert ObservableList to a regular List
        List<Reservation> reservationsToSave = new ArrayList<>(view.getApproveResTableView().getItems());

        // Debug: Print the reservations being saved
        System.out.println("[CLIENT] Reservations to save:");
        for (Reservation reservation : reservationsToSave) {
            System.out.println(reservation);
        }

        // Save the reservations
        model.saveReservationData(reservationsToSave);

        // Show a success message
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

    public void updateReservations(List<Reservation> reservations) {
        Platform.runLater(() -> {
            reservationData.setAll(reservations);
            view.updateTable(reservations);
            System.out.println("[CLIENT] Reservations updated via callback.");
        });
    }
}