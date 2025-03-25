package client.utility;

import client.landingpage.LandingPageController;
import client.landingpage.LandingPageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientController {
    public ClientController(ClientView view, Stage stage) {
        System.out.println("Loading client's landing page controller...");

        LandingPageView landingPageView = view.getFxmlLoader().getController();

        if (landingPageView == null) {
            System.err.println("[ERROR] LandingPageView is NULL! Cannot initialize LandingPageController.");
            return;
        }

        // Pass only the LandingPageView, removing 'stage'
        new LandingPageController(landingPageView);

        // Set the close request handler for the stage
        stage.setOnCloseRequest(this::handleCloseRequest);
    }

    private void handleCloseRequest(WindowEvent event) {
        System.out.println("[INFO] Close request received. Terminating the application...");

        // Exit the JavaFX application
        javafx.application.Platform.exit();

        // Ensure the JVM exits
        System.exit(0);
    }
}

