package client.admin.view;

import client.admin.controller.AddNewTerminalController;
import client.admin.model.AddNewTerminalModel;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.Terminal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller class for managing terminal administration view.
 * Handles displaying, searching, and managing computer terminals in the system.
 * Implements real-time search functionality and provides UI animations.
 */
public class AddNewTerminalView implements Initializable {

    @FXML private TableColumn<Terminal, String> terminalColumn;
    @FXML private TableColumn<Terminal, String> roomNumberColumn;
    @FXML private TableColumn<Terminal, String> terminalOSColumn;
    @FXML private TableColumn<Terminal, String> startTimeColumn;
    @FXML private TableColumn<Terminal, String> endTimeColumn;
    @FXML private TableColumn<Terminal, String> statusColumn;
    @FXML private TableColumn<Terminal, String> reservationDateColumn;
    @FXML private TableView<Terminal> addTerminalTableView;
    @FXML private TextField searchTerminalTextField;
    @FXML private Button redirectAddTerminalWindowButton;

    private final ObservableList<Terminal> allTerminals = FXCollections.observableArrayList();
    private AddNewTerminalController controller;
    private BorderPane rootPane;

    /**
     * Initializes the controller after FXML loading.
     * Sets up table columns, controller, terminal data loading, and search functionality.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        controller = new AddNewTerminalController(this, new AddNewTerminalModel());
        controller.loadTerminals();  // Fetch & update table on startup
        initializeSearchListener();
        redirectAddTerminalWindowButton.setOnAction(event -> openAddTerminalWindow());
    }

    /**
     * Configures table column value factories to display terminal properties.
     */
    private void initializeTableColumns() {
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        terminalOSColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOs()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        reservationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
    }

    /**
     * Gets the root border pane of this view.
     *
     * @return The root BorderPane container
     */
    public BorderPane getRootPane() {
        return rootPane;
    }

    /**
     * Sets the controller for this view.
     *
     * @param controller The AddNewTerminalController instance to set
     */
    public void setController(AddNewTerminalController controller) {
        this.controller = controller;
        System.out.println("=====================================================");
        System.out.println("[CLIENT] Controller has been set in AddNewTerminalView.");
    }

    /**
     * Updates the table with new terminal data.
     * Logs errors if the input list is empty or null.
     *
     * @param terminals List of Terminal objects to display in the table
     */
    public void updateTable(List<Terminal> terminals) {
        System.out.println("=====================================================");
        if (terminals == null || terminals.isEmpty()) {
            System.err.println("[ERROR] No terminals found. updateTable() was called with an empty list.");
            return;
        }
        System.out.println("[CLIENT] Received " + terminals.size() + " terminals in updateTable().");
        allTerminals.setAll(terminals);
        addTerminalTableView.setItems(allTerminals);
        System.out.println("[CLIENT] allTerminals now contains " + allTerminals.size() + " items.");
    }

    /**
     * Initializes the real-time search listener on the search text field.
     * Filters terminals as the user types.
     */
    public void initializeSearchListener() {
        searchTerminalTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchTerminals(newValue.toLowerCase().trim());
        });
    }

    /**
     * Filters terminals based on search query.
     * Searches across all terminal properties and updates the table view.
     *
     * @param query The search string to filter terminals by
     */
    public void searchTerminals(String query) {
        if (allTerminals.isEmpty()) {
            return;
        }
        if (query == null || query.isEmpty()) {
            addTerminalTableView.setItems(allTerminals);
            return;
        }
        List<Terminal> filteredList = allTerminals.stream()
                .filter(terminal -> terminal.getTerminalID().toLowerCase().contains(query) ||
                        terminal.getRoom().toLowerCase().contains(query) ||
                        terminal.getOs().toLowerCase().contains(query) ||
                        terminal.getStatus().toLowerCase().contains(query) ||
                        terminal.getReservationDate().toLowerCase().contains(query) ||
                        terminal.getStartTime().toLowerCase().contains(query) ||
                        terminal.getEndTime().toLowerCase().contains(query))
                .collect(Collectors.toList());
        addTerminalTableView.setItems(FXCollections.observableArrayList(filteredList));
    }

    /**
     * Opens a new window for adding terminals.
     */
    private void openAddTerminalWindow() {
        AddNewTerminalWindowView windowView = new AddNewTerminalWindowView();
        windowView.showWindow();
    }

    // Button animation methods
    public void addTerminalButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), redirectAddTerminalWindowButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void addTerminalButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), redirectAddTerminalWindowButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
