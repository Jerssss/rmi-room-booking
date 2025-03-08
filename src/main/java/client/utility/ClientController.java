package client.utility;

import client.landingpage.LandingPageController;
import client.landingpage.LandingPageView;
import javafx.stage.Stage;

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
    }
}

