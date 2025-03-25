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
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import shared.Log;
import shared.Reservation;
import client.admin.controller.ReportGeneratorController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGeneratorView implements Initializable {

    @FXML
    public Label reportsLabel;
    @FXML
    public TextField searchReportTextField;
    @FXML
    public Button exportButton;
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
        initializeController();
        exportButton.setOnAction(this::handleExportButtonAction);
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
        new ReportGeneratorController(this);
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
            System.out.println("[CLIENT] No logs to display.");
            return;
        }
        allLogs.setAll(logs);
        logReportTableView.setItems(allLogs);
        System.out.println("[CLIENT] Log table updated with " + logs.size() + " entries.");
    }

    /** Updates the reservations table with new data */
    public void updateReservationsTable(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("[CLIENT] No reservations to display.");
            return;
        }
        allReservations.setAll(reservations);
        reservationReportTableView.setItems(allReservations);
        System.out.println("[CLIENT] Reservation table updated with " + reservations.size() + " entries.");
    }

    @FXML
    public void handleExportButtonAction(ActionEvent event) {
        Tab selectedTab = reportsTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == logReportTab) {
            exportLogsToCSV();
        } else if (selectedTab == reservationReportTab) {
            exportReservationsToCSV();
        } else {
            showAlert("No data to export from the selected tab.");
        }
    }

    private void exportLogsToCSV() {
        ObservableList<Log> logs = logReportTableView.getItems();
        if (logs.isEmpty()) {
            showAlert("No logs to export.");
            return;
        }
        String csvContent = generateLogsCSV(logs);
        String fileName = "logs_" + System.currentTimeMillis() + ".csv";
        saveCSVToFile(csvContent, fileName);
    }

    private void exportReservationsToCSV() {
        ObservableList<Reservation> reservations = reservationReportTableView.getItems();
        if (reservations.isEmpty()) {
            showAlert("No reservations to export.");
            return;
        }
        String csvContent = generateReservationsCSV(reservations);
        String fileName = "reservations_" + System.currentTimeMillis() + ".csv";
        saveCSVToFile(csvContent, fileName);
    }

    private String generateLogsCSV(ObservableList<Log> logs) {
        StringBuilder csv = new StringBuilder();
        // Add export timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        csv.append("Exported on: ").append(timestamp).append("\n\n");

        csv.append("User ID,User Type,Actions,Date,Time\n");
        for (Log log : logs) {
            String line = String.format("%s,%s,%s,%s,%s\n",
                    log.getUserID().toString(),
                    log.getUserType(),
                    log.getAction(),
                    log.getDate(),
                    log.getTime());
            csv.append(line);
        }
        return csv.toString();
    }

    private String generateReservationsCSV(ObservableList<Reservation> reservations) {
        StringBuilder csv = new StringBuilder();
        // Add export timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        csv.append("Exported on: ").append(timestamp).append("\n\n");

        csv.append("Reservation ID,Terminal ID,Room Number,Reservation Status,Terminal Status\n");
        for (Reservation res : reservations) {
            String line = String.format("%s,%s,%s,%s,%s\n",
                    res.getReservationID(),
                    res.getTerminalID(),
                    res.getRoomID(),
                    res.getStatus(),
                    res.getStatus());
            csv.append(line);
        }
        return csv.toString();
    }

    private void saveCSVToFile(String csvContent, String fileName) {
        // Default directory setup
        Path defaultPath = Path.of("src/main/resources/reports").toAbsolutePath();
        File defaultDirectory = defaultPath.toFile();

        // Ensure default directory exists
        if (!defaultDirectory.exists()) {
            try {
                Files.createDirectories(defaultPath);
            } catch (IOException e) {
                showAlert("Failed to create default directory: " + defaultPath);
                return;
            }
        }

        // Configure directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Location");
        directoryChooser.setInitialDirectory(defaultDirectory);

        // Get current window context
        Window window = exportButton.getScene().getWindow();

        // Show dialog and get selected directory
        File selectedDirectory = directoryChooser.showDialog(window);

        // Fall back to default if user cancels
        if (selectedDirectory == null) {
            selectedDirectory = defaultDirectory;
        }

        // Create target directory if it doesn't exist
        if (!selectedDirectory.exists()) {
            boolean created = selectedDirectory.mkdirs();
            if (!created) {
                showAlert("Failed to create directory: " + selectedDirectory.getAbsolutePath());
                return;
            }
        }

        // Create and save file
        File outputFile = new File(selectedDirectory, fileName);
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(csvContent);
            showAlert("File saved successfully to:\n" + outputFile.getAbsolutePath());
        } catch (IOException e) {
            showAlert("Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Handles the refresh button's hover exit animation */
    public void exportButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), exportButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /** Handles the refresh button's hover animation */
    public void exportButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), exportButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}
