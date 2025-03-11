package client.student.view;

import client.admin.controller.ViewStudentReservationsController;
import client.admin.model.ViewStudentReservationsModel;
import client.student.controller.CreateReservationController;
import client.student.model.CreateReservationModel;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import shared.Terminal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateReservationView implements Initializable {

    @FXML
    private Label studResTitleLabel;
    @FXML
    private TextField searchStudResTextField;
    @FXML
    private Button searchButton;
    @FXML
    private Button redirectCreateReservationWindowButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TableView <Terminal> createReservationTableView;
    @FXML
    private TableColumn <Terminal, String> terminalColumn;
    @FXML
    private TableColumn <Terminal, String> roomNumberColumn;
    @FXML
    private TableColumn <Terminal, String> terminalOSColumn;
    @FXML
    private TableColumn <Terminal, String> statusColumn;
    @FXML
    private TableColumn <Terminal, String> startTimeColumn;
    @FXML
    private TableColumn <Terminal, String> endTimeColumn;

    private final ObservableList<Terminal> allTerminals = FXCollections.observableArrayList();

    // Store controller instance
    private CreateReservationController controller;

    /** JavaFX calls this method automatically after loading FXML */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        System.out.println("[DEBUG] Table columns initialized successfully.");

        // Manually initialize the controller
        initializeController();
    }

    /** Forcefully create and initialize the controller */
    public void initializeController() {
        System.out.println("[DEBUG] Initializing CreateReservationsController...");
        CreateReservationModel model = new CreateReservationModel();
        this.controller = new CreateReservationController(this, model);
        System.out.println("[DEBUG] CreateReservationsController successfully created.");
    }


    /** Properly initializes TableView columns */
    private void initializeTableColumns() {
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        terminalOSColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOs()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    public void updateTable(List<Terminal> terminals) {
        if (terminals == null || terminals.isEmpty()) {
            System.out.println("[DEBUG] No data to display in TableView.");
            return;
        }
        allTerminals.setAll(terminals);
        createReservationTableView.setItems(allTerminals);
        System.out.println("[DEBUG] Table updated with " + terminals.size() + " Terminals.");
    }

    public void searchTerminals() {
        String searchText = searchStudResTextField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            createReservationTableView.setItems(allTerminals);
            return;
        }

        List<Terminal> filteredList = allTerminals.stream()
                .filter(res -> res.getTerminalID().toLowerCase().contains(searchText) ||
                        res.getRoom().toLowerCase().contains(searchText) ||
                        res.getOs().toLowerCase().contains(searchText) ||
                        res.getStartTime().toLowerCase().contains(searchText) ||
                        res.getEndTime().toLowerCase().contains(searchText) ||
                        res.getStatus().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        createReservationTableView.setItems(FXCollections.observableArrayList(filteredList));
    }



    /** Sets search button action */
    public void setSearchButtonAction(EventHandler<ActionEvent> event) {
        searchButton.setOnAction(e -> searchTerminals());
    }

    /** Sets refresh button action */
    public void setRefreshButtonAction(EventHandler<ActionEvent> event) {
        refreshButton.setOnAction(event);
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

    public void createButtonHovered() {
        // Assuming you have a button for create reservations, e.g., createButton
        ScaleTransition st = new ScaleTransition(Duration.millis(200), redirectCreateReservationWindowButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void createButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), redirectCreateReservationWindowButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

}
