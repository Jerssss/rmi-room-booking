package client.admin.controller;

import client.admin.model.AddNewTerminalModel;
import client.admin.view.AddNewTerminalView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.Terminal;
import java.util.List;

public class AddNewTerminalController {

    private final AddNewTerminalView view;
    private final AddNewTerminalModel model;

    public AddNewTerminalController(AddNewTerminalView view, AddNewTerminalModel addNewTerminalModel) {
        this.view = view;
        this.model = new AddNewTerminalModel();
    }

    /** Loads terminal data and updates the TableView */
    public void loadTerminals() {
        System.out.println("[CLIENT] Fetching terminals from model...");
        List<Terminal> terminals = model.fetchTerminals();

        if (terminals == null || terminals.isEmpty()) {
            System.out.println("[CLIENT] No terminal data found.");
            return;
        }

        // Update TableView on JavaFX thread
        Platform.runLater(() -> {
            ObservableList<Terminal> terminalData = FXCollections.observableArrayList(terminals);
            view.getTableView().setItems(terminalData);
            System.out.println("[CLIENT] Table updated with " + terminals.size() + " terminals.");
        });
    }
}
