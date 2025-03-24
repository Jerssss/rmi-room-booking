package client.admin.controller;

import client.admin.model.ReservationApprovalModel;
import client.admin.view.ConfirmModificationsView;
import client.admin.view.ReservationApprovalView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Reservation;
import shared.Log;
import shared.Terminal;
import shared.callback.UpdateTable;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The `ReservationApprovalController` class is responsible for managing the logic
 * and interactions for approving reservations. It handles loading reservation data,
 * updating the view, saving changes, and searching for reservations.
 */
public class ReservationApprovalController {

    private final ReservationApprovalModel model;
    private final ReservationApprovalView view;
    private final ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    /**
     * Constructs a `ReservationApprovalController` with the specified view.
     * Initializes the model, sets up callbacks for reservation updates,
     * and loads the initial reservation data.
     *
     * @param view The `ReservationApprovalView` instance associated with this controller.
     */
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
        this.view.setActionSaveChangesButton(event -> handleSaveChanges());
        this.view.getSearchStudResTextField().textProperty().addListener(
                (observable, oldValue, newValue) -> searchReservations(newValue)
        );
    }

    /**
     * Loads reservation data from the model and updates the view.
     */
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

    /**
     * Saves the modified reservation data to the model.
     */
    public void saveChanges() {
        List<Reservation> reservationsToSave = List.copyOf(view.getApproveResTableView().getItems());

        System.out.println("[CLIENT] Saving " + reservationsToSave.size() + " reservations.");
        model.saveReservationData(reservationsToSave);

        JOptionPane.showMessageDialog(null, "Changes have been successfully saved!",
                "Save Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    public void handleSaveChanges() {
        try {
            // Load the modification confirmation FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/confirm_modifications_window.fxml"));
            Parent root = loader.load();

            // Get the controller and pass reference
            ConfirmModificationsView confirmController = loader.getController();
            confirmController.setReservationApprovalController(this); // Pass the controller reference

            // Create a new modal stage for confirmation
            Stage confirmationStage = new Stage();
            confirmationStage.setTitle("Confirm Modifications");
            confirmationStage.setScene(new Scene(root));
            confirmationStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            confirmationStage.setResizable(false);
            confirmationStage.showAndWait(); // Wait for user action

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error loading the modification confirmation window.",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void applyChanges() {
        List<Reservation> reservationsToSave = List.copyOf(view.getApproveResTableView().getItems());

        System.out.println("[CLIENT] Saving " + reservationsToSave.size() + " reservations.");
        boolean success = model.saveReservationData(reservationsToSave);

        if (!success) {
            JOptionPane.showMessageDialog(null,
                    "Failed to modify terminal data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            try {
                // Load the "Saved Notifier" FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/saved_notifier_window.fxml"));
                Parent root = loader.load();

                // Create a new Stage (pop-up window)
                Stage notifierStage = new Stage();
                notifierStage.setTitle("Saved Successfully");
                notifierStage.setScene(new Scene(root));
                notifierStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
                notifierStage.setResizable(false);
                notifierStage.showAndWait(); // Wait until the user closes it

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error loading the saved notification window.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Filters reservations based on the provided search text and updates the view.
     *
     * @param searchText The text to filter reservations by.
     */
    public void searchReservations(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            view.setReservationData(reservationData);
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
    }
}
