    package client.student.view;

    import client.student.controller.ViewReservationController;
    import client.student.model.ViewReservationModel;
    import client.utility.SessionManager;
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

        private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

        // Store controller instance
        private ViewReservationController controller;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            initializeTableColumns();
            System.out.println("[CLIENT] Table columns initialized successfully.");
            initializeController();
        }

        /**
         * Initializes the controller.
         * Note: We retrieve the student ID from SessionManager (or another central location)
         * so that it can be passed into the model.
         */
        public void initializeController() {
            // Retrieve the logged-in student ID (ensure SessionManager has a getter for it)
            String studentID = SessionManager.getStudentID();
            if (studentID == null || studentID.isEmpty()) {
                System.err.println("[ERROR] Student ID is missing from the session.");
                return;
            }
            ViewReservationModel model = new ViewReservationModel(studentID);
            this.controller = new ViewReservationController(this, model, studentID);
            initializeSearchListener();
        }

        /** Initializes TableView columns */
        private void initializeTableColumns() {
            terminalNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
            roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomID()));
            dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
            startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
            endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
            statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        }

        /**
         * Updates TableView with new reservations, prioritizing "Approved" reservations
         * and sorting them by the latest reservation date.
         *
         * @param reservations The list of reservations to display in the table.
         */
        public void updateTable(List<Reservation> reservations) {
            if (reservations == null || reservations.isEmpty()) {
                System.out.println("[CLIENT] No data to display in TableView.");
                return;
            }

            // Sort reservations: "Approved" first, then by reservation date (latest first)
            reservations = reservations.stream()
                    .sorted((r1, r2) -> {
                        if (r1.getStatus().equals(r2.getStatus())) {
                            return r2.getReservationDate().compareTo(r1.getReservationDate());
                        } else {
                            return "Approved".equals(r1.getStatus()) ? -1 : 1;
                        }
                    })
                    .collect(Collectors.toList());

            allReservations.setAll(reservations);
            viewResTableView.setItems(allReservations);
            System.out.println("[CLIENT] Table updated with " + reservations.size() + " reservations.");
        }



        public void initializeSearchListener() {
            searchResTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterReservations(newValue.toLowerCase().trim());
            });
        }

        /** Filter Reservations Based on Search Text */
        private void filterReservations(String searchText) {
            if (searchText.isEmpty()) {
                viewResTableView.setItems(allReservations); // Show all if search is empty
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


        /** Sets refresh button action */
        public void setRefreshButtonAction(EventHandler<ActionEvent> event) {
            refreshButton.setOnAction(event);
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
