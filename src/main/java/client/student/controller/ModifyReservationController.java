package client.student.controller;

import client.student.model.ModifyReservationModel;
import client.student.view.ModifyReservationDialogController;
import client.student.view.ModifyReservationView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Reservation;

import java.io.IOException;
import java.util.List;

public class ModifyReservationController {
    private final ModifyReservationView view;
    private final ModifyReservationModel model;
    private Reservation pendingReservation;
    private boolean changesMade = false;

    public ModifyReservationController(ModifyReservationView view, ModifyReservationModel model) {
        this.view = view;
        this.model = model;
        loadReservations();
        view.setSearchButtonAction(event -> view.searchReservations());
        view.setRefreshButtonAction(event -> loadReservations());
    }

    public void loadReservations() {
        List<Reservation> reservations = model.fetchReservations();
        if (reservations != null) {
            pendingReservation = null;
            changesMade = false;
            view.updateTable(reservations);
        }
    }

    public void showEditDialog(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/modify_reservation_window.fxml"));
            Parent root = loader.load();

            ModifyReservationDialogController dialogController = loader.getController();
            dialogController.setReservation(reservation);
            dialogController.setMainController(this);

            Stage dialogStage = new Stage();
            dialogController.setDialogStage(dialogStage);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (dialogController.isChangesMade()) {
                this.pendingReservation = reservation; // Store modified reservation
                this.changesMade = true; // Set changesMade to true
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commitChanges() {
        if (!changesMade) {
            showNoChangesAlert();
            return;
        }

        if (pendingReservation != null && model.updateReservation(pendingReservation)) {
            loadReservations();
            pendingReservation = null;
            changesMade = false;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("All changes have been sent to the server");
            alert.showAndWait();
        }
    }

    private void showNoChangesAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Changes");
        alert.setHeaderText(null);
        alert.setContentText("No modifications detected. Nothing to save.");
        alert.showAndWait();
    }


    public void setChangesMade(boolean changesMade) {
        this.changesMade = changesMade;
    }

    public void updateTableWithPendingReservation(Reservation reservation) {
        this.pendingReservation = reservation;
        List<Reservation> reservations = model.fetchReservations();
        if (reservations != null) {
            // Replace the old reservation with the updated one
            reservations.removeIf(r -> r.getReservationID().equals(reservation.getReservationID()));
            reservations.add(reservation);
            view.updateTable(reservations);
        }
    }

    public void cancelReservation(String reservationID) {
        if (model.cancelReservation(reservationID)) {
            loadReservations();
            System.out.println("Reservation cancelled successfully!");
        }
    }
}