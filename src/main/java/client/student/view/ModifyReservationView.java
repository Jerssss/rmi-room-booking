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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Duration;
import shared.Reservation;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The ModifyReservationView class is responsible for displaying and managing the user interface
 * for modifying reservations. It allows users to search, edit, and cancel their reservations.
 * This class implements the Initializable interface to perform initialization tasks.
 */
public class ModifyReservationView implements Initializable {
    @FXML private TextField searchStudResTextField;
    @FXML private Button searchButton;
    @FXML private Button saveChangesButton;
    @FXML private Button refreshButton;
    @FXML private TableView<Reservation> modResTableView;
    @FXML private TableColumn<Reservation, String> roomNumberColumn;
    @FXML private TableColumn<Reservation, String> terminalIDColumn;
    @FXML private TableColumn<Reservation, String> reservationDateColumn;
    @FXML private TableColumn<Reservation, String> startTimeColumn;
    @FXML private TableColumn<Reservation, String> endTimeColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> editColumn;
    @FXML private TableColumn<Reservation, String> cancelColumn;

    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();
    private ModifyReservationController controller;

    /**
     * Initializes the view by setting up the table columns, row factory, controller, and search listener.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        initializeRowFactory();
        System.out.println("[CLIENT] Table columns initialized successfully.");
        initializeController();
        initializeSearchListener();
    }

    /**
     * Initializes the row factory for the reservation table to apply custom styles based on reservation status.
     */
    private void initializeRowFactory() {
        modResTableView.setRowFactory(tv -> new TableRow<Reservation>() {
            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                getStyleClass().remove("cancelled-row");
                getStyleClass().remove("past-date-row");

                if (!empty && reservation != null) {
                    if ("Cancelled".equals(reservation.getStatus())) {
                        getStyleClass().add("cancelled-row");
                    }

                    LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
                    if (reservationDate.isBefore(LocalDate.now())) {
                        getStyleClass().add("past-date-row");
                    }
                }
            }
        });
    }

    /**
     * Handles the action of saving changes made to reservations. It removes cancelled reservations
     * from the list and commits the changes to the server.
     */
    @FXML
    private void handleSaveChanges() {
        if (controller != null) {
            // Remove cancelled rows from the table
            List<Reservation> reservationsToRemove = new ArrayList<>();
            for (Reservation reservation : allReservations) {
                if ("Cancelled".equals(reservation.getStatus())) {
                    reservationsToRemove.add(reservation);
                }
            }
            allReservations.removeAll(reservationsToRemove); // Remove cancelled reservations from the list

            // Commit changes to the server
            controller.commitChanges();
        }
    }

    /**
     * Initializes the ModifyReservationController with the current student's ID.
     */
    private void initializeController() {
        String studentID = SessionManager.getStudentID();
        if (studentID == null || studentID.isEmpty()) {
            return;
        }
        ModifyReservationModel model = new ModifyReservationModel(studentID);
        this.controller = new ModifyReservationController(this, model);
    }

    /**
     * Initializes the table columns with the appropriate cell value factories.
     */
    private void initializeTableColumns() {
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomID()));
        terminalIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        reservationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        editColumn.setCellValueFactory(param -> new SimpleStringProperty(""));
        editColumn.setCellFactory(createEditButtonCellFactory());
        cancelColumn.setCellValueFactory(param -> new SimpleStringProperty(""));
        cancelColumn.setCellFactory(createCancelButtonCellFactory());
    }

    /**
     * Updates the reservation table with a new list of reservations.
     *
     * @param reservations The list of reservations to display in the table.
     */
    public void updateTable(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            System.out.println("[CLIENT] No data to display in TableView.");
            return;
        }

        // Sort reservations by status and date
        reservations = reservations.stream()
                .sorted((r1, r2) -> {
                    if (r1.getStatus().equals(r2.getStatus())) {
                        return r2.getReservationDate().compareTo(r1.getReservationDate());
                    } else {
                        return "Pending".equals(r1.getStatus()) ? -1 : 1;
                    }
                })
                .collect(Collectors.toList());

        allReservations.setAll(reservations);
        modResTableView.setItems(allReservations);
        modResTableView.refresh();
        System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
        modResTableView.requestLayout();
    }

    /**
     * Initializes a listener for the search text field to filter reservations based on user input.
     */
    public void initializeSearchListener() {
        searchStudResTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchReservations(newValue.toLowerCase().trim());
        });
    }

    /**
     * Searches for reservations based on the provided query and updates the table view accordingly.
     *
     * @param query The search query to filter reservations.
     */
    public void searchReservations(String query) {
        String searchText = searchStudResTextField.getText().trim().toLowerCase();
        if (allReservations.isEmpty()) {
            return;
        }

        // If query is empty, show all terminals again
        if (query == null || query.isEmpty()) {
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

    /**
     * Creates a cell factory for the edit button in the reservation table.
     *
     * @return A Callback that creates TableCell instances with edit buttons.
     */
    private Callback<TableColumn<Reservation, String>, TableCell<Reservation, String>> createEditButtonCellFactory() {
        return column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle("-fx-background-color: #0d3073; -fx-text-fill: white;");
                editButton.setOnAction(event -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        // Check if the reservation can be edited
                        LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
                        LocalDateTime startDateTime = LocalDateTime.of(reservationDate, LocalTime.parse(reservation.getStartTime(), DateTimeFormatter.ofPattern("HH:mm")));
                        LocalDateTime now = LocalDateTime.now();

                        // Check if the reservation is in the past or has been rejected
                        if (reservationDate.isBefore(LocalDate.now()) || "Rejected".equals(reservation.getStatus())) {
                            return; // Do not allow editing for past dates or rejected reservations
                        }

                        // Check if the reservation can be edited based on status and time
                        boolean canEdit = "Pending".equals(reservation.getStatus()) && startDateTime.isAfter(now.plusHours(24));

                        if (canEdit) {
                            controller.showEditDialog(reservation);
                        } else {
                            // Show an alert for reservations that are not allowed to be edited
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Edit Not Allowed");
                            alert.setHeaderText(null);
                            alert.setContentText("You can only edit reservations that are Pending and have at least 24 hours before the start time.");
                            alert.showAndWait();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
                        // Disable the edit button for cancelled or rejected reservations or past dates
                        if ("Cancelled".equals(reservation.getStatus()) || "Rejected".equals(reservation.getStatus()) || reservationDate.isBefore(LocalDate.now())) {
                            editButton.setDisable(true); // Disable the edit button
                        } else {
                            editButton.setDisable(false); // Enable the edit button for active reservations
                        }
                    }
                    setGraphic(editButton );
                }
            }
        };
    }

    /**
     * Creates a cell factory for the cancel button in the reservation table.
     *
     * @return A Callback that creates TableCell instances with cancel buttons.
     */
    private Callback<TableColumn<Reservation, String>, TableCell<Reservation, String>> createCancelButtonCellFactory() {
        return column -> new TableCell<>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setStyle("-fx-background-color: #ab1313; -fx-text-fill: white;");
                cancelButton.setOnAction(event -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        LocalDate reservationDate = LocalDate.parse(reservation.getReservationDate());
                        LocalTime startTime = LocalTime.parse(reservation.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, startTime);
                        LocalDateTime now = LocalDateTime.now();

                        // Check if the reservation is within 24 hours or has been rejected
                        if (reservationDateTime.isBefore(now.plusHours(24)) || "Rejected".equals(reservation.getStatus())) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Cancellation Not Allowed");
                            alert.setHeaderText(null);
                            alert.setContentText("You cannot cancel a reservation that has been rejected or less than 24 hours before the start time.");
                            alert.showAndWait();
                            return; // Exit the method if cancellation is not allowed
                        }

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm Cancellation");
                        alert.setHeaderText("Cancel Reservation");
                        alert.setContentText("Are you sure you want to cancel this reservation?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            // Mark the reservation as cancelled
                            reservation.setStatus("Cancelled");
                            controller.cancelReservation(reservation.getReservationID());

                            // Refresh the table to apply CSS changes
                            modResTableView.refresh(); // This will now reflect the local change

                            // Disable buttons
                            cancelButton.setDisable(true);
                            TableRow<Reservation> row = getTableRow();
                            if (row != null) {
                                for (Node node : row.getChildrenUnmodifiable()) {
                                    if (node instanceof TableCell) {
                                        TableCell<?, ?> cell = (TableCell<?, ?>) node;
                                        if (cell.getGraphic() instanceof Button && "Edit".equals(((Button) cell.getGraphic()).getText())) {
                                            cell.getGraphic().setDisable(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        // Disable the cancel button for rejected reservations
                        cancelButton.setDisable("Rejected".equals(reservation.getStatus()));
                    }
                    setGraphic(cancelButton);
                }
            }
        };
    }

    /**
     * Sets the action for the search button.
     *
     * @param event The event handler to be set for the search button.
     */
    public void setSearchButtonAction(EventHandler<ActionEvent> event) {
        searchButton.setOnAction(event);
    }

    /**
     * Sets the action for the refresh button.
     *
     * @param event The event handler to be set for the refresh button.
     */
    public void setRefreshButtonAction(EventHandler<javafx.event.ActionEvent> event) {
        refreshButton.setOnAction(event);
    }

    /**
     * Animates the save changes button when the mouse exits.
     */
    public void saveChangesButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveChangesButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Animates the save changes button when the mouse hovers over it.
     */
    public void saveChangesButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), saveChangesButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Animates the search button when the mouse exits.
     */
    public void searchButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), searchButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Animates the search button when the mouse hovers over it.
     */
    public void searchButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), searchButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Animates the refresh button when the mouse exits.
     */
    public void refreshButtonExited() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }

    /**
     * Animates the refresh button when the mouse hovers over it.
     */
    public void refreshButtonHovered() {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), refreshButton);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setCycleCount(1);
        st.setAutoReverse(false);
        st.play();
    }
}