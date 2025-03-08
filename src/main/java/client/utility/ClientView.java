package client.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ClientView {
    private final Stage stage;
    private FXMLLoader fxmlLoader; // Store FXMLLoader instance

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

    // method to return the FXMLLoader
    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }
}
