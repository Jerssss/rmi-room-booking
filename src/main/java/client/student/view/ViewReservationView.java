package client.student.view;

import client.student.controller.ViewReservationController;
import client.student.model.ViewReservationModel;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import shared.Reservation;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;



public class ViewReservationView implements Initializable {
    @FXML
    private TextField searchResTextField;

    @FXML
    private Button refreshButton;

    @FXML
    private TableView<Reservation> viewResTableView;
    @FXML
    private TableColumn<Reservation, String> reservationIDColumn;
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


    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    // Store controller instance
    private ViewReservationController controller;

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
        System.out.println("[DEBUG] Initializing ViewReservationController...");
        ViewReservationModel model = new ViewReservationModel();
        this.controller = new ViewReservationController(this, model);
        System.out.println("[DEBUG] ViewStudentReservationsController successfully created.");
    }


    /** Properly initializes TableView columns */
    private void initializeTableColumns() {
        reservationIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationID()));
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
        viewResTableView.setItems(allReservations);
        System.out.println("[DEBUG] Table updated with " + reservations.size() + " reservations.");
    }

    /** Search for reservations based on input */
    public void searchReservations() {
        String searchText = searchTextField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            viewResTableView.setItems(allReservations);
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

        viewResTableView.setItems(FXCollections.observableArrayList(filteredList));
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
