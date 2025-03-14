package client.admin.controller;

import client.admin.model.AddNewTerminalModel;
import client.admin.view.AddNewTerminalView;
import shared.Terminal;
import java.util.List;

public class AddNewTerminalController {

    private final AddNewTerminalView view;
    private final AddNewTerminalModel model;

    public AddNewTerminalController(AddNewTerminalView view, AddNewTerminalModel addNewTerminalModel) {
        this.view = view;
        this.model = new AddNewTerminalModel();
    }

    /**
     * Loads terminal data and updates the TableView
     */
    public void loadTerminals() {
        System.out.println("[CLIENT] Calling fetchTerminals() to get terminal data...");
        List<Terminal> terminals = model.fetchTerminals();

        if (terminals == null || terminals.isEmpty()) {
            System.err.println("[ERROR] No terminals received from fetchTerminals().");
        } else {
            System.out.println("[CLIENT] Fetched " + terminals.size() + " terminals from RMI.");
        }

        view.updateTable(terminals);
    }
}
