package client.admin.view;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * View controller class for the Saved Notification popup window.
 * Provides visual confirmation when an operation completes successfully,
 * with interactive close button and hover animations.
 */
public class SavedNotifierView {

    @FXML
    private Button closeButton;

    /**
     * Initializes the controller after FXML loading.
     * Sets up the close button action handler.
     */
    @FXML
    public void initialize() {
        closeButton.setOnAction(event -> closeWindow());
    }

    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close(); // Close the notifier window
    }

    public void closeButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void closeButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

}
