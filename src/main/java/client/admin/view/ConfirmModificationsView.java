package client.admin.view;

import client.admin.controller.ModifyTerminalStatusController;
import client.admin.controller.ReservationApprovalController;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * View controller class for the modification confirmation dialog.
 * Provides a confirmation interface with visual feedback when
 * modifying terminal statuses or approving reservations.
 * Handles user confirmation/cancellation and delegates actions
 * to the appropriate controllers.
 */
public class ConfirmModificationsView {
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

    private ModifyTerminalStatusController modifyController; // Reference to main controller
    private ReservationApprovalController reservationController;

    /**
     * Sets the ModifyTerminalStatusController reference.
     * @param controller The controller to handle terminal status modifications
     */
    public void setModifyTerminalStatusController(ModifyTerminalStatusController controller) {
        this.modifyController = controller;
    }

    /**
     * Sets the ReservationApprovalController reference.
     * @param controller The controller to handle reservation approvals
     */
    public void setReservationApprovalController(ReservationApprovalController controller) {
        this.reservationController = controller;
    }

    /**
     * Initializes the view controller.
     * Sets up button event handlers for confirmation and cancellation.
     */
    @FXML
    private void initialize() {
        cancelButton.setOnAction(event -> closeWindow());
        confirmButton.setOnAction(event -> confirmChanges());
    }

    /**
     * Handles the confirmation action.
     * Closes the window and delegates to the appropriate controller
     * based on which controller reference is set.
     */
    private void confirmChanges() {
        // Close confirmation window
        closeWindow();
        // Call the save logic in ModifyTerminalStatusController if it's set
        if (modifyController != null) {
            modifyController.applyChanges();
        }

        // Call the approval logic in ReservationApprovalController if it's set
        if (reservationController != null) {
            reservationController.applyChanges();
        }
    }

    /**
     * Closes the confirmation window.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void cancelButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cancelButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void cancelButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), cancelButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void confirmButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), confirmButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void confirmButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), confirmButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
