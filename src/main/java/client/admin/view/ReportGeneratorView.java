package client.admin.view;

import client.admin.controller.ReportGeneratorController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import server.utility.LogReport;
import server.utility.ReservationReport;

public class ReportGeneratorView {
    @FXML private VBox centerPane;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private TextField searchReportTextField;

    @FXML private TableView<LogReport> logReportTableView;
    @FXML private TableColumn<LogReport, String> resIDColumn1;
    @FXML private TableColumn<LogReport, String> terminalColumn1;
    @FXML private TableColumn<LogReport, String> roomNumberColumn1;
    @FXML private TableColumn<LogReport, String> statusColumn1;
    @FXML private TableColumn<LogReport, String> statusColumn12;

    @FXML private TableView<ReservationReport> reservationReportTableView;
    @FXML private TableColumn<ReservationReport, String> resIDColumn11;
    @FXML private TableColumn<ReservationReport, String> terminalColumn11;
    @FXML private TableColumn<ReservationReport, String> roomNumberColumn11;
    @FXML private TableColumn<ReservationReport, String> statusColumn11;
    @FXML private TableColumn<ReservationReport, String> statusColumn111;

    private final ReportGeneratorController controller;
    private final ObservableList<LogReport> logReports = FXCollections.observableArrayList();
    private final ObservableList<ReservationReport> reservationReports = FXCollections.observableArrayList();

    public ReportGeneratorView() {
        this.controller = new ReportGeneratorController(this);
    }

    @FXML
    public void initialize() {
        // Bind columns to model properties
        resIDColumn1.setCellValueFactory(cellData -> cellData.getValue().userIDProperty());
        terminalColumn1.setCellValueFactory(cellData -> cellData.getValue().terminalIDProperty());
        roomNumberColumn1.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty());
        statusColumn1.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusColumn12.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        resIDColumn11.setCellValueFactory(cellData -> cellData.getValue().reservationIDProperty());
        terminalColumn11.setCellValueFactory(cellData -> cellData.getValue().terminalIDProperty());
        roomNumberColumn11.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty());
        statusColumn11.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusColumn111.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Bind tables to observable lists
        logReportTableView.setItems(logReports);
        reservationReportTableView.setItems(reservationReports);

        // Button actions
        searchButton.setOnAction(event -> performSearch());
        refreshButton.setOnAction(event -> {
            controller.loadLogsData();
            controller.loadReservationReports();
        });

        // Load initial data
        controller.loadLogsData();
        controller.loadReservationReports();
    }

    private void performSearch() {
        String query = searchReportTextField.getText().trim();
        controller.searchReports(query);
    }

    public void setReservationReports(ObservableList<ReservationReport> data) {
        System.out.println("Setting reservations in TableView: " + data.size());
        reservationReports.setAll(data);
        reservationReportTableView.setItems(null); // Clear first
        reservationReportTableView.setItems(reservationReports);
        reservationReportTableView.refresh();
    }

    public void setLogsData(ObservableList<LogReport> data) {
        logReports.setAll(data);
    }
}