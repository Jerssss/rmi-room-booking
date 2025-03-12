package client.student.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.Terminal;

public class CreateReservationDialogController {

    @FXML
    private Button saveChangesButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField terminalNoTextField;
    @FXML
    private TextField roomNoTextField;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /** Pre-fills the form with the selected terminal details */
    public void setTerminalDetails(Terminal terminal) {
        if (terminal != null) {
            terminalNoTextField.setText(String.valueOf(terminal.getTerminalID())); // Assuming getTerminalNo() exists
            roomNoTextField.setText(String.valueOf(terminal.getRoom())); // Assuming getRoomNo() exists
        }
    }
}
