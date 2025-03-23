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
import shared.Reservation;
import client.admin.controller.ViewStudentReservationsController;
import client.admin.model.ViewStudentReservationsModel;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class ViewStudentReservationsView implements Initializable {


    @FXML
    private TableView<Reservation> studResTableView;
    @FXML
    private TableColumn<Reservation, String> reservationIdColumn;
    @FXML
    private TableColumn<Reservation, String> userIdColumn;
    @FXML
    private TableColumn<Reservation, String> terminalNumberColumn;
    @FXML
    private TableColumn<Reservation, String> roomNumberColumn;
    @FXML
    private TableColumn<Reservation, String> dateColumn;
    @FXML
    private TableColumn<Reservation, String> startTimeColumn;
    @FXML
    private TableColumn<Reservation, String> endTimeColumn;
    @FXML
    private TableColumn<Reservation, String> statusColumn;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private Button refreshButton;

    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    // Store controller instance
    private ViewStudentReservationsController controller;

    /** JavaFX calls this method automatically after loading FXML */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        System.out.println("[CLIENT] Table columns initialized successfully.");

        // Manually initialize the controller
        initializeController();
        initializeSearchListener();
    }

    /** Forcefully create and initialize the controller */
    public void initializeController() {
        System.out.println("=====================================================");
        ViewStudentReservationsModel model = new ViewStudentReservationsModel();
        this.controller = new ViewStudentReservationsController(this, model);
    }

    /** Properly initializes TableView columns */
    private void initializeTableColumns() {
        reservationIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationID()));
        userIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID()));
        terminalNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomID()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    /** Updates TableView with new reservations */
    public void updateTable(List<Reservation> reservations) {
        System.out.println("=====================================================");
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("[CLIENT] No data to display in TableView.");
            return;
        }
        allReservations.setAll(reservations);
        studResTableView.setItems(allReservations);
        System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
    }

    /** Real-Time Search for Reservations */
    public void initializeSearchListener() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterReservations(newValue.toLowerCase().trim());
        });
    }

    /** Filter Reservations Based on Search Text */
    private void filterReservations(String searchText) {
        if (searchText.isEmpty()) {
            studResTableView.setItems(allReservations); // Show all if search is empty
            return;
        }

        List<Reservation> filteredList = allReservations.stream()
                .filter(res -> res.getReservationID().toLowerCase().contains(searchText) ||
                        res.getUserID().toLowerCase().contains(searchText) ||
                        res.getRoomID().toLowerCase().contains(searchText) ||
                        res.getStatus().toLowerCase().contains(searchText) ||
                        res.getStartTime().toLowerCase().contains(searchText) ||
                        res.getEndTime().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        studResTableView.setItems(FXCollections.observableArrayList(filteredList));
    }
}
