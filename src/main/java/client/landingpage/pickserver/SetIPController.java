package client.landingpage.pickserver;

import client.landingpage.pickserver.SetIPModel;
import client.ClientMain;
import shared.interfaces.IPInputHandler;

public class SetIPController {
    private SetIPModel model;
    private IPInputHandler ipInputHandler;

    public SetIPController(SetIPModel model, IPInputHandler ipInputHandler) {
        this.model = model;
        this.ipInputHandler = ipInputHandler;
    }

}