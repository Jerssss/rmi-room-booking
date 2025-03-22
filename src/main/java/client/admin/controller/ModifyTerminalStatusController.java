package client.admin.controller;

import client.admin.model.ModifyTerminalStatusModel;
import client.admin.view.ConfirmModificationsView;
import client.admin.view.ModifyTerminalStatusView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import shared.callback.UpdateTable;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyTerminalStatusController {
    private final ModifyTerminalStatusView view;
    private final ModifyTerminalStatusModel model;
    private ObservableList<Terminal> terminalData = FXCollections.observableArrayList();

    public ModifyTerminalStatusController(ModifyTerminalStatusView view) {
        this.view = view;
        this.model = new ModifyTerminalStatusModel();

        // Register the callback for terminal updates
        model.initCallback(new UpdateTable() {
            @Override
            public void updateTerminals(List<Terminal> terminals) {
                Platform.runLater(() -> {
                    terminalData.setAll(terminals);
                    view.updateTable(terminals);
                    System.out.println("[CLIENT] Terminal update received via callback in ModifyTerminalStatusController.");
                });
            }

            @Override
            public void updateReservations(List<Reservation> reservations) {

            }

            @Override
            public void updateLogs(List<Log> logs) {

            }
        });

        // Initial load of terminal data
        loadTerminals();
    }

    /** Loads terminal data and updates the view */
    public void loadTerminals() {
        System.out.println("[CLIENT] loadTerminals() method called.");

        List<Terminal> terminals = model.fetchTerminals();

        if (terminals != null) {
            Platform.runLater(() -> {
                terminalData.setAll(terminals); // Update observable list
                view.updateTable(terminals);
                System.out.println("[CLIENT] Table updated with " + terminals.size() + " terminals.");
            });
        } else {
            System.err.println("[ERROR] Failed to load terminals.");
        }
    }

    public void removeTerminal(Terminal terminal) {
        terminalData.remove(terminal);
        view.updateTable(FXCollections.observableArrayList(terminalData));
        System.out.println("[DEBUG] Terminal removed: " + terminal.getTerminalID());
    }

    public void saveChanges() {
        try {
            // Load the modification confirmation FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/confirm_modifications_window.fxml"));
            Parent root = loader.load();

            // Get the controller and pass reference
            ConfirmModificationsView confirmController = loader.getController();
            confirmController.setModifyTerminalStatusController(this); // Pass the controller reference

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
        List<Terminal> modifiedTerminals = terminalData.stream().collect(Collectors.toList());

        boolean success = model.saveModifiedTerminals(modifiedTerminals);
        view.updateTable(modifiedTerminals);

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

    public void searchTerminals(String query) {
        if (terminalData.isEmpty()) {
            return;
        }

        if (query == null || query.trim().isEmpty()) {
            System.out.println("[DEBUG] Search text is empty. Resetting to full terminal list.");
            view.updateTable(terminalData); // Reset table to original data
            return;
        }

        String lowerCaseQuery = query.toLowerCase();
        List<Terminal> filteredList = terminalData.stream()
                .filter(terminal ->
                        terminal.getTerminalID().toLowerCase().contains(lowerCaseQuery) ||
                                terminal.getRoom().toLowerCase().contains(lowerCaseQuery) ||
                                terminal.getOs().toLowerCase().contains(lowerCaseQuery) ||
                                terminal.getStatus().toLowerCase().contains(lowerCaseQuery) ||
                                terminal.getReservationDate().toLowerCase().contains(lowerCaseQuery) ||
                                terminal.getStartTime().toLowerCase().contains(lowerCaseQuery) ||
                                terminal.getEndTime().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());

        System.out.println("[DEBUG] Search completed. Matching results: " + filteredList.size());
        view.updateTable(FXCollections.observableArrayList(filteredList));
    }
}
