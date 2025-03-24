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
import shared.callback.UpdateTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing the modification of reservations.
 * This class handles the interaction between the view and the model,
 * allowing users to modify existing reservations and manage changes.
 */
public class ModifyReservationController {
    private final ModifyReservationView view;
    private ModifyReservationModel model;
    private Reservation pendingReservation;
    private boolean changesMade = false;
    private String query;
    private List<String> cancelledReservationIDs = new ArrayList<>();

    /**
     * Constructs a ModifyReservationController with the specified view and model.
     *
     * @param view  the view associated with this controller
     * @param model the model associated with this controller
     */
    public ModifyReservationController(ModifyReservationView view, ModifyReservationModel model) {
        this.view = view;
        this.model = model;

        // Register callback for real-time updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<shared.Terminal> terminals) {
                // Not used in this context.
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {
                // When a callback is received, reload reservations to reflect any changes.
                loadReservations();
                System.out.println("[CLIENT] Callback received in ModifyReservationController. Table refreshed.");
            }

            @Override
            public void updateLogs(List<shared.Log> logs) {
                // Not used in this context.
            }
        });

        // Initial load of reservations
        loadReservations();
        view.setSearchButtonAction(event -> view.searchReservations(query));
        view.setresetButtonAction(event -> loadReservations());
    }

    /**
     * Loads the reservations from the model and updates the view.
     * Resets the pending reservation and changes made status.
     */
    public void loadReservations() {
        List<Reservation> reservations = model.fetchReservations();
        if (reservations != null) {
            pendingReservation = null;
            changesMade = false;
            view.updateTable(reservations);
        }
    }

    /**
     * Displays a dialog for editing a specific reservation.
     *
     * @param reservation the reservation to be edited
     */
    public void showEditDialog(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/modify_reservation_window.fxml"));
            Parent root = loader.load();

            ModifyReservationDialogController dialogController = loader.getController();
            dialogController.setReservation(reservation);
            dialogController.setMainController(this);
            dialogController.setModel(model); // Pass the model to the dialog

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

    /**
     * Commits the changes made to the reservations.
     * This includes processing cancellations and updating the pending reservation.
     * Displays an alert indicating whether the changes were saved successfully.
     */
    public void commitChanges() {
        if (cancelledReservationIDs.isEmpty() && !changesMade) {
            showNoChangesAlert();
            return;
        }

        // Process cancellations
        for (String id : cancelledReservationIDs) {
            model.cancelReservation(id); // Send to server
        }
        cancelledReservationIDs.clear(); // Reset the list

        // Process pending reservation updates (if any)
        if (pendingReservation != null) {
            model.updateReservation(pendingReservation);
            pendingReservation = null;
        }

        // Refresh data from the server
        loadReservations();

        // Show success alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Changes Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your changes have been successfully saved.");
        alert.showAndWait();
    }

    /**
     * Displays an alert indicating that no changes were detected.
     */
    private void showNoChangesAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Changes");
        alert.setHeaderText(null);
        alert.setContentText("No modifications detected. Nothing to save.");
        alert.showAndWait();
    }

    /**
     * Sets the changesMade status.
     *
     * @param changesMade the new status of changesMade
     */
    public void setChangesMade(boolean changesMade) {
        this.changesMade = changesMade;
    }

    /**
     * Updates the table with the pending reservation.
     *
     * @param reservation the updated reservation to be reflected in the table
     */
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

    /**
     * Cancels a reservation by adding its ID to the list of cancelled reservations.
     *
     * @param reservationID the ID of the reservation to be cancelled
     */
    public void cancelReservation(String reservationID) {
        // Add the ID to the list of cancellations
        cancelledReservationIDs.add(reservationID);
        changesMade = true;
    }
}
