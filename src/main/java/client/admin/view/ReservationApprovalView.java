package client.admin.view;

import client.admin.controller.ReservationApprovalController;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import shared.Reservation;

public class ReservationApprovalView {

    // FXML components
    @FXML
    private Button searchButton, refreshButton;
    @FXML
    private Button saveChangesButton;
    @FXML
    private TextField searchStudResTextField;
    @FXML
    private TableView<Reservation> approveResTableView;
    @FXML
    private TableColumn<Reservation, String> reservationIdColumn, userIdColumn, terminalNumberColumn,
            roomNumberColumn, dateColumn, startTimeColumn, endTimeColumn;
    @FXML
    private TableColumn<Reservation, String> statusColumn;

    // Controller and data
    private ReservationApprovalController controller = new ReservationApprovalController(this);
    private final ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    // Initialize the view
    @FXML
    public void initialize() {
        System.out.println("Initializing ReservationApprovalView...");

        // Bind table columns to Reservation properties
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        terminalNumberColumn.setCellValueFactory(new PropertyValueFactory<>("terminalID"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Bind to status property
        statusColumn.setCellFactory(createStyledStatusCellFactory()); // Apply custom cell factory

        // Set table data
        approveResTableView.setItems(reservationData);

        // Load initial data
        if (controller != null) {
            controller.loadReservationData();
        }

        // Set button actions
        setActionSearchButton(event -> controller.searchTerminals(searchStudResTextField.getText()));
        setActionRefreshButton(event -> controller.loadReservationData());
        setActionSaveChangesButton(event -> controller.saveChanges());
    }

    // Custom cell factory for the status column
    private Callback<TableColumn<Reservation, String>, TableCell<Reservation, String>> createStyledStatusCellFactory() {
        return column -> new TableCell<>() {
            private final ComboBox<String> statusComboBox = new ComboBox<>(
                    FXCollections.observableArrayList("Pending", "Approved", "Rejected")
            );

            {
                // Style the ComboBox
                statusComboBox.setStyle("-fx-border-color: transparent; " +
                        "-fx-padding: 5px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-family: 'System';");

                // Handle ComboBox value changes
                statusComboBox.setOnAction(e -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        reservation.setStatus(statusComboBox.getValue()); // Update the status property
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableRow().getItem();
                    statusComboBox.setValue(reservation.getStatus()); // Set the current status

                    // Alternate row colors for better readability
                    int rowIndex = getIndex();
                    Color rowColor = (rowIndex % 2 == 1) ? Color.web("#f8f8f8") : Color.WHITE;
                    setBackground(new Background(new BackgroundFill(rowColor, new CornerRadii(5), null)));

                    // Style the ComboBox to match the row color
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

            // Convert Color to RGB hex code
            private String toRGBCode(Color color) {
                return String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
            }
        };
    }

    // Getters and setters for UI components
    public TableView<Reservation> getApproveResTableView() {
        return approveResTableView;
    }

    public void setActionSearchButton(EventHandler<ActionEvent> event) {
        searchButton.setOnAction(event);
    }

    public void setActionRefreshButton(EventHandler<ActionEvent> event) {
        refreshButton.setOnAction(event);
    }

    public void setActionSaveChangesButton(EventHandler<ActionEvent> event) {
        saveChangesButton.setOnAction(event);
    }

    public TextField getSearchStudResTextField() {
        return searchStudResTextField;
    }

    // Update the table with new reservation data
    public void setReservationData(ObservableList<Reservation> data) {
        reservationData.setAll(data); // Update dataset
        approveResTableView.setItems(null); // Force reset
        approveResTableView.setItems(reservationData); // Reload table data
        approveResTableView.refresh(); // Force UI refresh
        System.out.println("[DEBUG] Reservation data updated. New table size: " + reservationData.size());
    }

    // Hover and exit animations for buttons
    public void saveChangesButtonExited() {
        applyExitAnimation(saveChangesButton);
    }

    public void saveChangesButtonHovered() {
        applyHoverAnimation(saveChangesButton);
    }

    public void searchButtonExited() {
        applyExitAnimation(searchButton);
    }

    public void searchButtonHovered() {
        applyHoverAnimation(searchButton);
    }

    public void refreshButtonExited() {
        applyExitAnimation(refreshButton);
    }

    public void refreshButtonHovered() {
        applyHoverAnimation(refreshButton);
    }

    // Utility method for hover animation
    private void applyHoverAnimation(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    // Utility method for exit animation
    private void applyExitAnimation(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}