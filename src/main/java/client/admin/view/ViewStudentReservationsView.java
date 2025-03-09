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
        System.out.println("[DEBUG] Table columns initialized successfully.");

        // Manually initialize the controller
        initializeController();
    }

    /** Forcefully create and initialize the controller */
    public void initializeController() {
        System.out.println("[DEBUG] Initializing ViewStudentReservationsController...");
        ViewStudentReservationsModel model = new ViewStudentReservationsModel();
        this.controller = new ViewStudentReservationsController(this, model);
        System.out.println("[DEBUG] ViewStudentReservationsController successfully created.");
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
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("[DEBUG] No data to display in TableView.");
            return;
        }
        allReservations.setAll(reservations);
        studResTableView.setItems(allReservations);
        System.out.println("[DEBUG] Table updated with " + reservations.size() + " reservations.");
    }

    /** Search for reservations based on input */
    public void searchReservations() {
        String searchText = searchTextField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            studResTableView.setItems(allReservations);
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


    /** Sets search button action */
    public void setSearchButtonAction(EventHandler<ActionEvent> event) {
        searchButton.setOnAction(event1 -> searchReservations());
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
}
