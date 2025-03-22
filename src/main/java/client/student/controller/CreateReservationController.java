package client.student.controller;

import client.student.model.CreateReservationModel;
import client.student.view.CreateReservationDialogController;
import client.student.view.CreateReservationView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Reservation;
import shared.Terminal;

import java.io.IOException;
import java.util.List;

public class CreateReservationController {
    private final CreateReservationView view;
    private final CreateReservationModel model;

    public CreateReservationController(CreateReservationView view, CreateReservationModel model) {
        this.view = view;
        this.model = model;
        initialize();
    }

    private void initialize() {
        loadTerminals();
        // Set action for the Add Reservation button
        this.view.setAddReservationButtonAction(event -> {
            Terminal selectedTerminal = view.getSelectedTerminal();
            if (selectedTerminal != null) {
                openCreateReservationDialog(selectedTerminal);
            } else {
                System.out.println("[ERROR] No terminal selected!");
            }
        });
    }

    private void loadTerminals() {
        List<Terminal> terminals = model.fetchTerminals();
        if (terminals != null && !terminals.isEmpty()) {
            view.updateTable(terminals);
            System.out.println("[DEBUG] Terminals loaded successfully.");
        } else {
            System.out.println("[DEBUG] No terminals available.");
        }
    }

    /**
     * Opens the reservation dialog with the selected terminal's details.
     * After the dialog closes, retrieves the created reservation and passes it to the model.
     * If the reservation is successfully added, a popup will confirm the success.
     */
    private void openCreateReservationDialog(Terminal terminal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/add_reservation_window.fxml"));
            BorderPane reservationPane = loader.load();
            CreateReservationDialogController dialogController = loader.getController();
            dialogController.setTerminalDetails(terminal);
            // Pass the model instance for validations and fetching reservations
            dialogController.setModel(model);
            Stage dialogStage = new Stage();
            dialogController.setDialogStage(dialogStage);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(reservationPane));
            dialogStage.showAndWait();

            System.out.println("[DEBUG] Reservation dialog opened successfully.");

            // After the dialog is closed, retrieve the created reservation
            Reservation newRes = dialogController.getNewReservation();
            if (newRes != null) {
                try {
                    model.addReservation(newRes);
                    System.out.println("[DEBUG] Reservation added successfully.");
                    // Show success popup
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Reservation Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Reservation created successfully!");
                    alert.showAndWait();
                    // Optionally refresh the table view here if needed.
                } catch (RuntimeException ex) {
                    System.err.println("[ERROR] Failed to add the reservation: " + ex.getMessage());
                    // Show error popup
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Reservation Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to create reservation: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open reservation dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
