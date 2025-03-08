package client.utility;

import client.landingpage.LandingPageController;
import client.utility.ClientView;

public class ClientController {
    public ClientController(ClientView view) {
        System.out.println("Loading client's landing page controller...");
        new LandingPageController(view.getFxmlLoader().getController());
    }
}

