package client.admin.view;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class ConfirmApprovalsView {

    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

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
