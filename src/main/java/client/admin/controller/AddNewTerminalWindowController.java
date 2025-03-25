package client.admin.controller;


import client.admin.model.AddNewTerminalModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Terminal;


import javax.swing.*;
import java.io.IOException;
import java.util.List;

/**
 * The `AddNewTerminalWindowController` class is responsible for handling the logic
 * related to adding new terminals and retrieving all terminals. It interacts with
 * the `AddNewTerminalModel` to perform these operations.
 */
public class AddNewTerminalWindowController {

    private final AddNewTerminalModel model;

    /**
     * Constructs an `AddNewTerminalWindowController` and initializes the `AddNewTerminalModel`.
     */
    public AddNewTerminalWindowController() {
        this.model = new AddNewTerminalModel();
    }

    /**
     * Adds a new terminal with the specified details.
     *
     * @param terminalID The unique identifier for the terminal.
     * @param os         The operating system of the terminal.
     * @param room       The room where the terminal is located.
     * @param status     The current status of the terminal.
     * @param startTime  The start time of the terminal's availability.
     * @param endTime    The end time of the terminal's availability.
     * @param date       The date of the terminal's availability.
     * @return `true` if the terminal was successfully added, `false` otherwise.
     */
    public boolean addNewTerminal(String terminalID, String os, String room, String status, String startTime, String endTime, String date) {
        Terminal newTerminal = new Terminal(terminalID, room, os, status, date, startTime, endTime);
        return model.addNewTerminal(newTerminal);
    }

    /**
     * Retrieves a list of all terminals from the model.
     *
     * @return A list of `Terminal` objects representing all terminals.
     */
    public List<Terminal> getAllTerminals() {
        return model.fetchTerminals();
    }
}
