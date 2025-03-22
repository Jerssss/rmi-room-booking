package client.landingpage.pickserver;

import client.landingpage.pickserver.SetIPModel;
import client.ClientMain;
import shared.interfaces.IPInputHandler;

public class SetIPController {
    private SetIPModel model;
    private ClientMain clientMain;
    private IPInputHandler ipInputHandler;

    public SetIPController(SetIPModel model, IPInputHandler ipInputHandler) {
        this.model = model;
        this.ipInputHandler = ipInputHandler;
    }

    public void handleIPInput(String ip) {
        if (model.isValidIP(ip)) {
            model.setServerIP(ip);
            ipInputHandler.handleIPInput(ip); // Delegate to the handler
        } else {
            System.out.println("Invalid IP address. Please try again.");
        }
    }
}