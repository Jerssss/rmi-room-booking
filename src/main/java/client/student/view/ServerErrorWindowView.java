package client.student.view;

import javafx.animation.ScaleTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ServerErrorWindowView {

    @FXML
    private Button closeButton;

    @FXML
    private Button retryButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private AnchorPane rootPane;

    private Runnable retryButtonAction; // Stores the retry button action
    private Runnable closeButtonAction; // Stores the close button action

    /**
     * Sets the action for the retry button.
     */
    public void setRetryButtonAction(Runnable retryButtonAction) {
        this.retryButtonAction = retryButtonAction;
    }

    /**
     * Sets the action for the close button.
     */
    public void setCloseButtonAction(Runnable closeButtonAction) {
        this.closeButtonAction = closeButtonAction;
    }

    @FXML
    public void initialize() {
        // Handle the "Close" button action
        closeButton.setOnAction(event -> {
            if (closeButtonAction != null) {
                closeButtonAction.run(); // Execute the close button action
            }
            // Close the pop-up window
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Handle the "Retry" button action
        retryButton.setOnAction(event -> {
            // Disable the retry button and show the loading indicator
            retryButton.setDisable(true);
            loadingIndicator.setVisible(true);

            // Run the retry logic in a background thread
            Task<Void> retryTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (retryButtonAction != null) {
                        retryButtonAction.run(); // Execute the retry button action
                    }
                    // Simulate a delay to ensure the loading indicator is visible
                    Thread.sleep(20000); // 10 seconds delay
                    return null;
                }
            };

            // When the task is done, hide the loading indicator and re-enable the retry button
            retryTask.setOnSucceeded(e -> {
                loadingIndicator.setVisible(false);
                retryButton.setDisable(false);
            });

            // Start the background thread
            new Thread(retryTask).start();
        });
    }

    // Hover effects for the Close button
    public void closeButtonExited() {
        applyScaleTransition(closeButton, 1.0, 1.0);
    }

    public void closeButtonHovered() {
        applyScaleTransition(closeButton, 0.9, 0.9);
    }

    // Hover effects for the Retry button
    public void retryButtonExited() {
        applyScaleTransition(retryButton, 1.0, 1.0);
    }

    public void retryButtonHovered() {
        applyScaleTransition(retryButton, 0.9, 0.9);
    }

    /**
     * Disables the retry button.
     */
    public void disableRetryButton() {
        retryButton.setDisable(true);
    }

    /**
     * Helper method to apply scale transition to a button
     */
    private void applyScaleTransition(Button button, double scaleX, double scaleY) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(scaleX);
        st.setToY(scaleY);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}