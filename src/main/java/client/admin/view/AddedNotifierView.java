package client.admin.view;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class AddedNotifierView {

    @FXML
    private Button closeButton;

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
