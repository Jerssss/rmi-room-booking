package client.admin.view;

import client.admin.controller.AddNewTerminalController;
import client.admin.model.AddNewTerminalModel;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.Terminal;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddNewTerminalView implements Initializable {

    @FXML private TableColumn<Terminal, String> terminalColumn;
    @FXML private TableColumn<Terminal, String> roomNumberColumn;
    @FXML private TableColumn<Terminal, String> terminalOSColumn;
    @FXML private TableColumn<Terminal, String> startTimeColumn;
    @FXML private TableColumn<Terminal, String> endTimeColumn;
    @FXML private TableColumn<Terminal, String> statusColumn;
    @FXML private TableColumn<Terminal, String> reservationDateColumn;

    @FXML private TableView<Terminal> addTerminalTableView;

    @FXML
    private TextField searchTerminalTextField;
    @FXML
    private Button searchButton;
    @FXML
    private Button redirectAddTerminalWindowButton;
    @FXML
    private Button refreshButton;  // Added refresh button reference

    private final ObservableList<Terminal> allTerminals = FXCollections.observableArrayList();
    private AddNewTerminalController controller;
    private BorderPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        controller = new AddNewTerminalController(this, new AddNewTerminalModel());
        controller.loadTerminals();  // Fetch & update table on startup

        // Connect search button
        searchButton.setOnAction(event -> handleSearch());

        // Refresh table when refresh button is clicked
        refreshButton.setOnAction(event -> controller.loadTerminals());
        redirectAddTerminalWindowButton.setOnAction(event -> openAddTerminalWindow());
    }

    private void initializeTableColumns() {
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        terminalOSColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOs()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        reservationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
    }

    public BorderPane getRootPane() {
        return rootPane;
    }

    public void setController(AddNewTerminalController controller) {
        this.controller = controller;
        System.out.println("=====================================================");
        System.out.println("[CLIENT] Controller has been set in AddNewTerminalView.");
    }

    /** Updates Table */
    public void updateTable(List<Terminal> terminals) {
        System.out.println("=====================================================");
        if (terminals == null || terminals.isEmpty()) {
            System.err.println("[ERROR] No terminals found. updateTable() was called with an empty list.");
            return;
        }

        // Print how many terminals we received
        System.out.println("[CLIENT] Received " + terminals.size() + " terminals in updateTable().");

        allTerminals.setAll(terminals);
        addTerminalTableView.setItems(allTerminals);

        // Print after updating
        System.out.println("[CLIENT] allTerminals now contains " + allTerminals.size() + " items.");
    }

    /** Handles search input */
    private void handleSearch() {
        String query = searchTerminalTextField.getText().trim().toLowerCase();
        System.out.println("[CLIENT] Searching for: " + query);
        searchTerminals(query);
    }

    /** Searches terminals without modifying the full list. */
    public void searchTerminals(String query) {
        System.out.println("=====================================================");
        System.out.println("[CLIENT] Searching for: " + query);

        // Prevent searching if allTerminals is empty
        if (allTerminals.isEmpty()) {
            System.err.println("[ERROR] allTerminals is still empty. Did loadTerminals() run?");
            return;
        }

        // If query is empty, reset table
        if (query == null || query.isEmpty()) {
            System.out.println("[CLIENT] Search query empty, showing all terminals.");
            updateTable(allTerminals);
            return;
        }

        // Filter terminals based on query
        List<Terminal> filteredList = allTerminals.stream()
                .filter(terminal -> terminal.getTerminalID().toLowerCase().contains(query) ||
                        terminal.getRoom().toLowerCase().contains(query) ||
                        terminal.getOs().toLowerCase().contains(query) ||
                        terminal.getStatus().toLowerCase().contains(query) ||
                        terminal.getReservationDate().toLowerCase().contains(query) ||
                        terminal.getStartTime().toLowerCase().contains(query) ||
                        terminal.getEndTime().toLowerCase().contains(query))
                .collect(Collectors.toList());

        System.out.println("[CLIENT] Found " + filteredList.size() + " matching terminals.");
        updateTable(filteredList);
    }
    private void openAddTerminalWindow() {
        AddNewTerminalWindowView windowView = new AddNewTerminalWindowView();
        windowView.showWindow();
    }


    public void searchButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), searchButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void searchButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), searchButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
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
    public void refreshButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
    public void refreshButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

}
