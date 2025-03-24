package client.admin.view;

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
    void createButtonExited(MouseEvent event) {

    }

    @FXML
    void createButtonHovered(MouseEvent event) {

    }

}
