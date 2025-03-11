package client.admin.view;

import client.admin.controller.AddNewTerminalController;
import client.admin.model.AddNewTerminalModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import shared.Terminal;
import java.net.URL;
import java.util.ResourceBundle;

public class AddNewTerminalView implements Initializable {

    @FXML
    private TableView<Terminal> addTerminalTableView;
    @FXML
    private TableColumn<Terminal, String> terminalColumn;
    @FXML
    private TableColumn<Terminal, String> roomNumberColumn;
    @FXML
    private TableColumn<Terminal, String> terminalOSColumn;
    @FXML
    private TableColumn<Terminal, String> startTimeColumn;
    @FXML
    private TableColumn<Terminal, String> endTimeColumn;
    @FXML
    private TableColumn<Terminal, String> statusColumn;

    private AddNewTerminalController controller;
    private BorderPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        System.out.println("[CLIENT] Table columns initialized successfully.");
        this.controller = new AddNewTerminalController(this, new AddNewTerminalModel());
        controller.loadTerminals();  // Fetch & update table on start
    }


    private void initializeTableColumns() {
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        terminalOSColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOs()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }


    /**
     * Get TableView
     */
    public TableView<Terminal> getTableView() {
        return addTerminalTableView;
    }

    public BorderPane getRootPane() {
        return rootPane;
    }
}