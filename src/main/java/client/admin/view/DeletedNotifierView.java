package client.admin.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


//only viewing and exiting the notification window
public class DeletedNotifierView {

    @FXML
    private Button closeButton;

    //button action setter
    public void setActionCloseButton(EventHandler<ActionEvent> event) {
        this.closeButton.setOnAction(event);
    }
}
