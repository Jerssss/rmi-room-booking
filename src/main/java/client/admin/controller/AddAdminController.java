package client.admin.controller;

import client.ClientMain;
import client.admin.model.AddAdminModel;
import client.admin.view.AddAdminView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.rmi.RemoteException;

public class AddAdminController {
    private final AddAdminView addAdminView;
    private final AddAdminModel addAdminModel;

    public AddAdminController(AddAdminView addAdminView, AddAdminModel addAdminModel) {
        System.out.println("AddAdminController initialized"); // Debug
        this.addAdminView = addAdminView;
        this.addAdminModel = addAdminModel;
        this.addAdminView.setActionCreateButton(this::handleAddAdmin);
    }

    private void handleAddAdmin(ActionEvent event) {
        String adminID = addAdminView.getAdminIDField().getText();
        String name = addAdminView.getNameField().getText();
        String pass = addAdminView.getAdminPassField().getText();
        String facultyType = addAdminView.getFacultyTypeField().getText();

        // Input validation (unchanged)
        if (adminID.isEmpty() || name.isEmpty() || pass.isEmpty() || facultyType.isEmpty()) {
            addAdminView.getPromptLabel().setText("Please complete all fields.");
            addAdminView.getPromptLabel().setVisible(true);
            return;
        }

        try {
            System.out.println("[CLIENT] Attempting to register admin..."); // Debug
            addAdminModel.registerAdmin(adminID, name, pass, facultyType);
            System.out.println("[CLIENT] Admin registered successfully!"); // Debug
            addAdminView.getPromptLabel().setText("Admin successfully registered!");
            addAdminView.getPromptLabel().setVisible(true);
            redirectToAdminMainMenu(event);
        } catch (RemoteException e) {
            System.err.println("[CLIENT] RMI Error: " + e.getMessage()); // Debug
            showErrorDialog("Failed to connect to the server. Check your network.");
        } catch (RuntimeException e) {
            System.err.println("[CLIENT] Registration Error: " + e.getMessage()); // Debug
            addAdminView.getPromptLabel().setText("Error: " + e.getMessage());
            addAdminView.getPromptLabel().setVisible(true);
        }
    }

    private void redirectToAdminMainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin_main_menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ioe) {
            showErrorDialog("Error loading Admin Main Menu. Please try again.");
        }
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() ->
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}
