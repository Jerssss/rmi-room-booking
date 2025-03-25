package client.admin.view;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * View controller class for the admin addition success notification window.
 * Provides functionality to display and close a notification window
 * that confirms successful admin registration.
 */
public class AddedNotifierView {

    @FXML
    private Button closeButton;

    /**
     * Initializes the controller class.
     * Sets up the event handler for the close button.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        closeButton.setOnAction(event -> closeWindow());
    }

    /**
     * Closes the notification window.
     * Retrieves the current stage from the close button's scene
     * and closes it.
     */
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close(); // Close the notifier window
    }

    /**
     * Handles the mouse exit event for the close button.
     * Animates the button to return to its original size (scale 1.0)
     * when the mouse pointer exits the button area.
     */
    public void closeButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Handles the mouse hover event for the close button.
     * Animates the button to slightly shrink (scale 0.9)
     * when the mouse pointer hovers over the button.
     */
    public void closeButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
