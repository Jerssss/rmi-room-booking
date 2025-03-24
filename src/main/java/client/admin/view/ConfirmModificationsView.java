package client.admin.view;

import client.admin.controller.ModifyTerminalStatusController;
import client.admin.controller.ReservationApprovalController;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ConfirmModificationsView {
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

    private ModifyTerminalStatusController modifyController; // Reference to main controller
    private ReservationApprovalController reservationController;

    public void setModifyTerminalStatusController(ModifyTerminalStatusController controller) {
        this.modifyController = controller;
    }

    public void setReservationApprovalController(ReservationApprovalController controller) {
        this.reservationController = controller;
    }

    @FXML
    private void initialize() {
        cancelButton.setOnAction(event -> closeWindow());
        confirmButton.setOnAction(event -> confirmChanges());
    }

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
