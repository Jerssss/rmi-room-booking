package client.admin.view;

import client.admin.model.AddAdminModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.rmi.RemoteException;

public class AddAdminView {

    @FXML
    private TextField adminIDField;

    @FXML
    private PasswordField adminPassField;

    @FXML
    private VBox centerPane;

    @FXML
    private Button createButton;

    @FXML
    private TextField facultyTypeField;

    @FXML
    private StackPane facultyTypeStackPane;

    @FXML
    private TextField nameField;

    @FXML
    private StackPane nameStackPane;

    @FXML
    private Label reportsLabel;

    @FXML
    private Label signUpPromptLabel;

    private final AddAdminModel addAdminModel = new AddAdminModel();



    @FXML
    public void initialize() {
        System.out.println("AddAdminView initialized"); // Debug
        createButton.setOnAction(event -> {
            System.out.println("Create button clicked"); // Debug
        });
        createButton.setOnAction(this::handleCreateButton);
    }

    @FXML
    private void handleCreateButton(ActionEvent event) {
        String adminID = adminIDField.getText();
        String name = nameField.getText();
        String facultyType = facultyTypeField.getText();
        String password = adminPassField.getText();

        // Validate inputs before proceeding
        if (adminID.isEmpty() || name.isEmpty() || facultyType.isEmpty() || password.isEmpty()) {
            signUpPromptLabel.setText("All fields must be filled!");
            signUpPromptLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        System.out.println("Create button clicked");

        // Show confirmation dialog
        int choice = JOptionPane.showConfirmDialog(
                null,
                "Do you want to save this admin?",
                "Save Admin",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Call the RMI service through AddAdminModel
                addAdminModel.registerAdmin(adminID, name, password, facultyType);

                // Show the red success message
                Platform.runLater(() -> {
                    signUpPromptLabel.setText("Admin Created & Saved Successfully!");
                    signUpPromptLabel.setStyle("-fx-text-fill: red;");
                });

                // Show the success popup
                JOptionPane.showMessageDialog(null, "Admin account successfully saved!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                adminIDField.clear();
                nameField.clear();
                facultyTypeField.clear();
                adminPassField.clear();
            } catch (RemoteException e) {
                System.err.println("[CLIENT] Error registering admin: " + e.getMessage());
                signUpPromptLabel.setText("Failed to register admin. Check connection.");
                signUpPromptLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            // If user selects "No"
            Platform.runLater(() -> {
                signUpPromptLabel.setText("Admin creation was canceled.");
                signUpPromptLabel.setStyle("-fx-text-fill: red;");
            });

            JOptionPane.showMessageDialog(null, "Admin creation canceled.", "Canceled", JOptionPane.WARNING_MESSAGE);
        }
    }

    @FXML
    void createButtonExited(MouseEvent event) {

    }

    @FXML
    void createButtonHovered(MouseEvent event) {

    }

    public void setActionCreateButton(EventHandler<ActionEvent> event) {
        System.out.println("Setting action for createButton"); // Debug
        createButton.setOnAction(event);
    }

    // Getters
    public TextField getAdminIDField() {
        return adminIDField;
    }

    public TextField getNameField() {
        return nameField;
    }

    public PasswordField getAdminPassField() {
        return adminPassField;
    }

    public TextField getFacultyTypeField() {
        return facultyTypeField;
    }

    public Label getPromptLabel() {
        return signUpPromptLabel;
    }

    public VBox getCenterPane() {
        return centerPane;
    }

}
