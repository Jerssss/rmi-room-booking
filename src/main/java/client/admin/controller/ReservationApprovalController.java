// File: client/admin/controller/ReservationApprovalController.java
package client.admin.controller;

import client.admin.model.ReservationApprovalModel;
import client.admin.view.ReservationApprovalView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Reservation;
import shared.Terminal;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationApprovalController {

    private final ReservationApprovalView view;
    private final ReservationApprovalModel model;
    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    public ReservationApprovalController(ReservationApprovalView view) {
        this.view = view;
        this.model = new ReservationApprovalModel();
    }

    public void loadReservationData() {
        reservationData = model.loadReservationData();
        view.setReservationData(reservationData);
    }

    public void saveChanges() {
        List<Reservation> reservationsToSend = reservationData.stream().collect(Collectors.toList());

        boolean success = ReservationApprovalModel.sendReservationApprovalData(reservationsToSend);

        if (!success) {
            JOptionPane.showMessageDialog(null,
                    "Failed to send reservation data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return; // Stop saving if any reservation fails to send
        }

        model.saveReservationData(reservationData); // Save only after successful transmission
        JOptionPane.showMessageDialog(null,
                "Changes have been successfully saved!",
                "Save Successful",
                JOptionPane.INFORMATION_MESSAGE);
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
}