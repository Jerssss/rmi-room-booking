package client.admin.view;

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
import shared.Log;
import shared.Reservation;
import client.admin.controller.ReportGeneratorController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReportGeneratorView implements Initializable {

    @FXML
    public Label reportsLabel;

    @FXML
    public TextField searchReportTextField;

    @FXML
    public Button searchButton;

    @FXML
    public Button refreshButton;

    @FXML
    public TabPane reportsTabPane;

    @FXML
    public Tab logReportTab;

    @FXML
    public TableView<Log> logReportTableView;

    @FXML
    public TableColumn<Log, String> userIDColumn;

    @FXML
    public TableColumn<Log, String> userTypeColumn;

    @FXML
    public TableColumn<Log, String> actionsColumn;

    @FXML
    public TableColumn<Log, String> dateColumn;

    @FXML
    public TableColumn<Log, String> timeColumn;

    @FXML
    public Tab reservationReportTab;

    @FXML
    public TableView<Reservation> reservationReportTableView;

    @FXML
    public TableColumn<Reservation, String> resIDColumn;

    @FXML
    public TableColumn<Reservation, String> terminalColumn;

    @FXML
    public TableColumn<Reservation, String> roomNumberColumn;

    @FXML
    public TableColumn<Reservation, String> resStatusColumn;

    @FXML
    public TableColumn<Reservation, String> TerStatusColumn;

    // Observable lists to store all data entries
    private final ObservableList<Log> allLogs = FXCollections.observableArrayList();
    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLogTableColumns();
        initializeReservationTableColumns();
        initializeSearchListener();
        initializeController(); // Added to instantiate the controller.
    }

    /** Initializes Log TableView columns */
    private void initializeLogTableColumns() {
        userIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID().toString()));
        userTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserType()));
        actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAction()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
    }

    /** Initializes Reservation TableView columns */
    private void initializeReservationTableColumns() {
        resIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationID()));
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomID()));
        resStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        TerStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    /** Sets up a listener on the search text field to filter the active table */
    private void initializeSearchListener() {
        searchReportTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase().trim();
            if (reportsTabPane.getSelectionModel().getSelectedItem() == logReportTab) {
                filterLogs(searchText);
            } else if (reportsTabPane.getSelectionModel().getSelectedItem() == reservationReportTab) {
                filterReservations(searchText);
            }
        });
    }

    /** Initializes the ReportGeneratorController */
    public void initializeController() {
        System.out.println("[DEBUG] Initializing ReportGeneratorController...");
        new ReportGeneratorController(this);
        System.out.println("[DEBUG] ReportGeneratorController successfully created.");
    }

    /** Filters logs based on search text */
    private void filterLogs(String searchText) {
        if (searchText.isEmpty()) {
            logReportTableView.setItems(allLogs);
            return;
        }
        List<Log> filteredLogs = allLogs.stream()
                .filter(log -> log.getUserID().toString().toLowerCase().contains(searchText) ||
                        log.getUserType().toLowerCase().contains(searchText) ||
                        log.getAction().toLowerCase().contains(searchText) ||
                        log.getDate().toLowerCase().contains(searchText) ||
                        log.getTime().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        logReportTableView.setItems(FXCollections.observableArrayList(filteredLogs));
    }

    /** Filters reservations based on search text */
    private void filterReservations(String searchText) {
        if (searchText.isEmpty()) {
            reservationReportTableView.setItems(allReservations);
            return;
        }
        List<Reservation> filteredReservations = allReservations.stream()
                .filter(res -> res.getReservationID().toLowerCase().contains(searchText) ||
                        res.getTerminalID().toLowerCase().contains(searchText) ||
                        res.getRoomID().toLowerCase().contains(searchText) ||
                        res.getStatus().toLowerCase().contains(searchText) ||
                        res.getStatus().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        reservationReportTableView.setItems(FXCollections.observableArrayList(filteredReservations));
    }

    /** Updates the logs table with new data */
    public void updateLogsTable(List<Log> logs) {
        if (logs == null || logs.isEmpty()) {
            System.out.println("[DEBUG] No logs to display.");
            return;
        }
        allLogs.setAll(logs);
        logReportTableView.setItems(allLogs);
        System.out.println("[DEBUG] Log table updated with " + logs.size() + " entries.");
    }

    /** Updates the reservations table with new data */
    public void updateReservationsTable(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("[DEBUG] No reservations to display.");
            return;
        }
        allReservations.setAll(reservations);
        reservationReportTableView.setItems(allReservations);
        System.out.println("[DEBUG] Reservation table updated with " + reservations.size() + " entries.");
    }

    /** Sets the action for the refresh button */
    public void setRefreshButtonAction(EventHandler<ActionEvent> event) {
        refreshButton.setOnAction(event);
    }

    /** Handles the refresh button's hover exit animation */
    public void refreshButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /** Handles the refresh button's hover animation */
    public void refreshButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
