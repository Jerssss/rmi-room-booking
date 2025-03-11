package client.student.view;

import client.student.controller.ModifyReservationController;
import client.student.model.ModifyReservationModel;
import client.utility.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;
import shared.Reservation;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ModifyReservationView implements Initializable {
    @FXML private TextField searchStudResTextField;
    @FXML private Button searchButton;
    @FXML private Button saveChangesButton;
    @FXML private Button refreshButton;
    @FXML private TableView<Reservation> modResTableView;
    @FXML private TableColumn<Reservation, String> reservationIDColumn;
    @FXML private TableColumn<Reservation, String> roomNumberColumn;
    @FXML private TableColumn<Reservation, String> terminalIDColumn;
    @FXML private TableColumn<Reservation, String> reservationDateColumn;
    @FXML private TableColumn<Reservation, String> startTimeColumn;
    @FXML private TableColumn<Reservation, String> endTimeColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> editColumn;

    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();
    private ModifyReservationController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        System.out.println("[DEBUG] Table columns initialized successfully.");
        initializeController();
    }

    private void initializeController() {
        System.out.println("[DEBUG] Initializing ModifyReservationController...");
        String studentID = SessionManager.getStudentID();
        if (studentID == null || studentID.isEmpty()) {
            System.err.println("[ERROR] Student ID is missing from the session.");
            return;
        }
        ModifyReservationModel model = new ModifyReservationModel(studentID);
        this.controller = new ModifyReservationController(this, model);
        System.out.println("[DEBUG] ModifyReservationController successfully created with student ID: " + studentID);
    }

    private void initializeTableColumns() {
        reservationIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomID()));
        terminalIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        reservationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        editColumn.setCellValueFactory(param -> new SimpleStringProperty(""));
        editColumn.setCellFactory(createEditButtonCellFactory());
    }

    public void updateTable(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("[DEBUG] No data to display in TableView.");
            return;
        }
        allReservations.setAll(reservations);
        modResTableView.setItems(allReservations);
        System.out.println("[DEBUG] Table updated with " + reservations.size() + " reservations.");
    }

    public void searchReservations() {
        String searchText = searchStudResTextField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            modResTableView.setItems(allReservations);
            return;
        }
        List<Reservation> filteredList = allReservations.stream()
                .filter(res -> res.getReservationID().toLowerCase().contains(searchText) ||
                        res.getRoomID().toLowerCase().contains(searchText) ||
                        res.getTerminalID().toLowerCase().contains(searchText) ||
                        res.getReservationDate().toLowerCase().contains(searchText) ||
                        res.getStartTime().toLowerCase().contains(searchText) ||
                        res.getEndTime().toLowerCase().contains(searchText) ||
                        res.getStatus().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        modResTableView.setItems(FXCollections.observableArrayList(filteredList));
    }

    private Callback<TableColumn<Reservation, String>, TableCell<Reservation, String>> createEditButtonCellFactory() {
        return column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #0d3073; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        controller.showEditDialog(reservation);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        };
    }

    public void setSearchButtonAction(EventHandler<ActionEvent> event) {
        searchButton.setOnAction(event);
    }

    public void setRefreshButtonAction(EventHandler<javafx.event.ActionEvent> event) {
        refreshButton.setOnAction(event);
    }

    public void saveChangesButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveChangesButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    public void saveChangesButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveChangesButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
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