package client.student.view;

import client.admin.controller.ViewStudentReservationsController;
import client.admin.model.ViewStudentReservationsModel;
import client.student.controller.CreateReservationController;
import client.student.model.CreateReservationModel;
import client.utility.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import shared.Reservation;
import shared.Terminal;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateReservationView implements Initializable {

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
    @FXML
    public TableColumn <Terminal, String> reserveColumn;



    public TextField terminalNoTextField;
    public TextField roomNoTextField;
    private Stage confirmationStage;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private Button saveChangesButton;

    private final ObservableList<Terminal> allTerminals = FXCollections.observableArrayList();

    // Store controller instance
    private CreateReservationController controller;

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
        String studentID = SessionManager.getStudentID();
        if (studentID == null || studentID.isEmpty()) {
            System.err.println("[ERROR] Student ID is missing from the session.");
            return;
        }
        CreateReservationModel model = new CreateReservationModel(studentID);
        this.controller = new CreateReservationController(this, model);
    }


    /** Properly initializes TableView columns */
    private void initializeTableColumns() {
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        terminalOSColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOs()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        reserveColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addReservationButton = new Button("Add Reservation");

            {
                addReservationButton.setStyle("-fx-background-color: #0d3073; -fx-text-fill: white;");
                addReservationButton.setOnAction(event -> {
                    Terminal terminal = getTableView().getItems().get(getIndex());
                    if (terminal != null) {
                        showReservationForm(terminal);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(addReservationButton);
                }
            }
        });

    }

    public void updateTable(List<Terminal> terminals) {
        if (terminals == null || terminals.isEmpty()) {
            System.out.println("[CLIENT] No data to display in TableView.");
            return;
        }
        allTerminals.setAll(terminals);
        createReservationTableView.setItems(allTerminals);
        System.out.println("[CLIENT] Table updated with " + terminals.size() + " Terminals.");
    }

    public void initializeSearchListener() {
        searchStudResTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterReservations(newValue.toLowerCase().trim());
        });
    }

    /** Filter Reservations Based on Search Text */
    private void filterReservations(String searchText) {
        if (searchText.isEmpty()) {
            createReservationTableView.setItems(allTerminals); // Show all if search is empty
            return;
        }

        List<Terminal> filteredList = allTerminals.stream()
                .filter(ter -> ter.getTerminalID().toLowerCase().contains(searchText) ||
                        ter.getRoom().toLowerCase().contains(searchText) ||
                        ter.getOs().toLowerCase().contains(searchText) ||
                        ter.getStatus().toLowerCase().contains(searchText) ||
                        ter.getReservationDate().toLowerCase().contains(searchText) ||
                        ter.getStartTime().toLowerCase().contains(searchText) ||
                        ter.getEndTime().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        createReservationTableView.setItems(FXCollections.observableArrayList(filteredList));
    }


    private void showReservationForm(Terminal terminal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/add_reservation_window.fxml"));
            BorderPane reservationPane = loader.load();

            CreateReservationDialogController controller = loader.getController();


            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(reservationPane));
            dialogStage.showAndWait();

        //    if (controller.isReservationCreated()) {
       //         JOptionPane.showMessageDialog(null, "Reservation successfully created!");
       //     }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
