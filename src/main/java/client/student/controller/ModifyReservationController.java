package client.student.controller;

import client.student.model.ModifyReservationModel;
import client.student.view.ModifyReservationDialogController;
import client.student.view.ModifyReservationView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Reservation;

import java.io.IOException;
import java.util.List;

public class ModifyReservationController {
    private final ModifyReservationView view;
    private final ModifyReservationModel model;

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
            view.updateTable(reservations);
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
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
                updateReservation(reservation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateReservation(Reservation reservation) {
        if (model.updateReservation(reservation)) {
            loadReservations();
            System.out.println("Reservation updated successfully!");
        }
    }

    public void cancelReservation(String reservationID) {
        if (model.cancelReservation(reservationID)) {
            loadReservations();
            System.out.println("Reservation cancelled successfully!");
        }
    }
}