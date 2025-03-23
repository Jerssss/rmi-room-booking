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
     *
     * @param retryButtonAction The action to execute when the retry button is clicked.
     */
    public void setRetryButtonAction(Runnable retryButtonAction) {
        this.retryButtonAction = retryButtonAction;
    }

    /**
     * Sets the action for the close button.
     *
     * @param closeButtonAction The action to execute when the close button is clicked.
     */
    public void setCloseButtonAction(Runnable closeButtonAction) {
        this.closeButtonAction = closeButtonAction;
    }

    /**
     * Initializes the UI components and sets up event handlers.
     */
    @FXML
    public void initialize() {
        // Enable the retry button by default
        retryButton.setDisable(false);

        // Handle the "Close" button action
        closeButton.setOnAction(event -> {
            if (closeButtonAction != null) {
                closeButtonAction.run(); // Execute the close button action
            }
            // Close the pop-up window
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
            System.out.println("[CLIENT] Closing the client program...");
            System.exit(0);

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
                    Thread.sleep(2000); // 2 seconds delay
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

    /**
     * Resets the hover effect for the Close button.
     */
    public void closeButtonExited() {
        applyScaleTransition(closeButton, 1.0, 1.0);
    }

    /**
     * Applies a hover effect to the Close button.
     */
    public void closeButtonHovered() {
        applyScaleTransition(closeButton, 0.9, 0.9);
    }

    /**
     * Resets the hover effect for the Retry button.
     */
    public void retryButtonExited() {
        applyScaleTransition(retryButton, 1.0, 1.0);
    }

    /**
     * Applies a hover effect to the Retry button.
     */
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
     * Resets the retry button to its default state (enabled).
     */
    public void resetRetryButton() {
        retryButton.setDisable(false);
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