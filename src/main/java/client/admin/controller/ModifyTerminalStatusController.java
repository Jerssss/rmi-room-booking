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
import shared.Terminal;

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

        // Load reservations when the page opens
        loadTerminals();
    }

    /** Loads reservation data and updates the view */
    public void loadTerminals() {
        System.out.println("[CLIENT] loadTerminals() method called."); // Add this

        List<Terminal> terminals = model.fetchTerminals();

        if (terminals != null) {
            Platform.runLater(() -> {
                terminalData.setAll(terminals); // Update observable list
                view.updateTable(terminals);
                System.out.println("[CLIENT] Table updated with " + terminals.size() + " reservations.");
            });
        } else {
            System.err.println("[ERROR] Failed to load reservations.");
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



    public void searchTerminals(ObservableList<Terminal> terminalData, String searchText) {

        if (searchText == null || searchText.trim().isEmpty()) {
            System.out.println("[DEBUG] Search text is empty. Resetting to full terminal list.");
        }

        String lowerCaseSearchText = searchText.toLowerCase();
        ObservableList<Terminal> filteredList = terminalData.stream()
                .filter(terminal ->
                        terminal.getRoom().toLowerCase().contains(lowerCaseSearchText) ||
                                terminal.getTerminalID().toLowerCase().contains(lowerCaseSearchText) ||
                                terminal.getOs().toLowerCase().contains(lowerCaseSearchText) ||
                                terminal.getStartTime().toLowerCase().contains(lowerCaseSearchText) ||
                                terminal.getEndTime().toLowerCase().contains(lowerCaseSearchText) ||
                                terminal.getReservationDate().toLowerCase().contains(lowerCaseSearchText) ||
                                terminal.getStatus().toLowerCase().contains(lowerCaseSearchText))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        System.out.println("[DEBUG] Search completed. Matching results: " + filteredList.size());
        view.updateTable(filteredList);
    }
}
