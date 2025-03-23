package client.admin.view;

import client.admin.controller.ReservationApprovalController;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import shared.Reservation;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationApprovalView implements Initializable {

    @FXML
    private Button saveChangesButton;
    @FXML
    private TextField searchStudResTextField;
    @FXML
    private TableView<Reservation> approveResTableView;
    @FXML
    private TableColumn<Reservation, String> reservationIdColumn, userIdColumn, terminalNumberColumn,
            roomNumberColumn, dateColumn, startTimeColumn, endTimeColumn, statusColumn;

    private ReservationApprovalController controller;
    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        controller = new ReservationApprovalController(this);  // Controller is initialized here
        System.out.println("[CLIENT] ReservationApprovalView initialized successfully.");
    }

    private void initializeTableColumns() {
        reservationIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationID()));
        userIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID()));
        terminalNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomID()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellFactory(createStyledStatusCellFactory());
    }

    private Callback<TableColumn<Reservation, String>, TableCell<Reservation, String>> createStyledStatusCellFactory() {
        return column -> new TableCell<Reservation, String>() {
            private final ComboBox<String> statusComboBox = new ComboBox<>();

            {
                statusComboBox.getItems().addAll("Pending", "Approved", "Rejected");
                statusComboBox.setStyle("-fx-border-color: transparent; -fx-padding: 5px; -fx-font-size: 13px;");
                statusComboBox.setOnAction(e -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        reservation.setStatus(statusComboBox.getValue());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableRow().getItem();
                    statusComboBox.setValue(getTableRow().getItem().getStatus());

                    int rowIndex = getIndex();
                    Color rowColor = (rowIndex % 2 == 1) ? Color.web("#f8f8f8") : Color.WHITE;
                    setBackground(new Background(new BackgroundFill(rowColor, new CornerRadii(5), null)));

                    statusComboBox.setStyle("-fx-background-color: " +
                            toRGBCode(rowColor) + "; " +
                            "-fx-border-color: transparent; " +
                            "-fx-padding: 5px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-family: 'System';");

                    statusComboBox.setMaxWidth(Double.MAX_VALUE);

                    setGraphic(statusComboBox);
                }
            }

            private String toRGBCode(Color color) {
                return String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
            }
        };
    }

    public TableView<Reservation> getApproveResTableView() {
        return approveResTableView;
    }

    public void setActionSaveChangesButton(EventHandler<ActionEvent> event) {
        saveChangesButton.setOnAction(event);
    }

    public TextField getSearchStudResTextField() {
        return searchStudResTextField;
    }

    public void setReservationData(ObservableList<Reservation> data) {
        allReservations.setAll(data);
        approveResTableView.setItems(allReservations);
        approveResTableView.refresh();
        System.out.println("[CLIENT] Table updated with " + allReservations.size() + " reservations.");
    }

    public void updateTable(List<Reservation> reservations) {
        if (reservations != null && !reservations.isEmpty()) {
            approveResTableView.getItems().setAll(reservations);
            System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
        } else {
            System.out.println("[CLIENT] No data available for update.");
        }
    }

    /** Button Animations */
    private void animateButton(Button button, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    @FXML
    private void saveChangesButtonExited() {
        animateButton(saveChangesButton, 1.0);
    }

    @FXML
    private void saveChangesButtonHovered() {
        animateButton(saveChangesButton, 0.9);
    }

}
