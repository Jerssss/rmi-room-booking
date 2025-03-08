package client.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientView {
    private FXMLLoader fxmlLoader;
    private final Stage stage;

    public ClientView(Stage stage) {
        this.stage = stage;
    }

    public void runInterface() {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client/landing_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Lendify");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public Stage getStage() {
        return stage;
    }
}
