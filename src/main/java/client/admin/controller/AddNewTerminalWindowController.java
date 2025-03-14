package client.admin.controller;


import client.admin.model.AddNewTerminalModel;
import shared.Terminal;


import java.util.List;


public class AddNewTerminalWindowController {


    private final AddNewTerminalModel model;


    public AddNewTerminalWindowController() {
        this.model = new AddNewTerminalModel();
    }


    public boolean addNewTerminal(String terminalID, String os, String room, String status, String startTime, String endTime, String date) {
        Terminal newTerminal = new Terminal(terminalID, room, os, status, date, startTime, endTime);
        return model.addNewTerminal(newTerminal);
    }


    public List<Terminal> getAllTerminals() {
        return model.fetchTerminals();
    }
}
