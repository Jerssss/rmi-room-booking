package client.admin.view;

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


    @FXML
    public void initialize() {
        System.out.println("AddAdminView initialized"); // Debug
        createButton.setOnAction(event -> {
            System.out.println("Create button clicked"); // Debug
        });
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
