package client.admin.view;

import client.admin.controller.ModifyTerminalStatusController;
import client.admin.model.ModifyTerminalStatusModel;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import shared.Terminal;

import java.io.IOException;
import java.util.List;

public class ModifyTerminalStatusView {

    @FXML private Button searchButton;
    @FXML private Button saveChangesButton;
    @FXML private Button refreshButton;
    @FXML private TextField modTerTextField;
    @FXML private TableView<Terminal> modTerTableView;
    @FXML private TableColumn<Terminal, String> roomNumberColumn;
    @FXML private TableColumn<Terminal, String> terminalColumn;
    @FXML private TableColumn<Terminal, String> terminalOSColumn;
    @FXML public TableColumn<Terminal, String> reservationDateColumn;
    @FXML public TableColumn<Terminal, String> startTimeColumn;
    @FXML public TableColumn<Terminal, String> endTimeColumn;
    @FXML private TableColumn<Terminal, String> terminalStatusColumn;
    @FXML private TableColumn<Terminal, String> editColumn;
    private Stage confirmationStage;
    @FXML private Label roomNoLabel;
    @FXML private Label terminalNoLabel;
    @FXML private Label terminalOSLabel;
    @FXML private Label terminalStatusLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    private ModifyTerminalStatusController controller;
    private ObservableList<Terminal> terminalData = FXCollections.observableArrayList();

    public void initialize() {
        initializeTableColumns();
        System.out.println("[CLIENT] Table columns initialized successfully.");
        initializeController();

        // Attach the buttons functionality
        setActionSearchButton(actionEvent -> controller.searchTerminals(terminalData, modTerTextField.getText()));
        setActionRefreshButton(event -> controller.loadTerminals());
        setActionSaveChangesButton(event -> controller.saveChanges());
    }

    public void initializeTableColumns() {
        terminalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTerminalID()));
        roomNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom()));
        terminalOSColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOs()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
        reservationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReservationDate()));
        terminalStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        terminalStatusColumn.setCellFactory(createStyledStatusCellFactory());
        editColumn.setCellFactory(column -> createDeleteButtonCellFactory());
    }

    public void initializeController() {
        System.out.println("[CLIENT] Initializing ModifyTerminalStatusController...");
        this.controller = new ModifyTerminalStatusController(this);
        System.out.println("[CLIENT] ModifyTerminalStatusController successfully created.");
    }

    public void setActionSearchButton(EventHandler<ActionEvent> event) {
        searchButton.setOnAction(event);
        System.out.println("[DEBUG] Search triggered. Query: " + modTerTextField.getText());
    }

    public void setActionRefreshButton(EventHandler<ActionEvent> event) {
        refreshButton.setOnAction(event);
        System.out.println("[DEBUG] Refresh triggered");
    }

    public void setActionSaveChangesButton(EventHandler<ActionEvent> event) {
        saveChangesButton.setOnAction(event);
    }

    //Refreshed the table
    public void updateTable(List<Terminal> data) {
        terminalData.setAll(data); // Update dataset
        modTerTableView.setItems(null); // Force reset
        modTerTableView.setItems(terminalData); // Reload table data
        modTerTableView.refresh(); // Force UI refresh
        System.out.println("[DEBUG] Terminal data updated. New table size: " + terminalData.size());
    }


    private Callback<TableColumn<Terminal, String>, TableCell<Terminal, String>> createStyledStatusCellFactory() {
        return column -> new TableCell<Terminal, String>() {
            private final ComboBox<String> statusComboBox = new ComboBox<>();

            {
                statusComboBox.getItems().addAll("Active", "Down", "Under Maintenance");
                statusComboBox.setStyle("-fx-border-color: transparent; " +
                        "-fx-padding: 5px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-family: 'System';");
                statusComboBox.setOnAction(e -> {
                    Terminal terminal = getTableRow().getItem();
                    if (terminal != null) {
                        terminal.setStatus(statusComboBox.getValue());
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
                    Terminal terminal = getTableRow().getItem();
                    statusComboBox.setValue(terminal.getStatus());

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

    private TableCell<Terminal, String> createDeleteButtonCellFactory() {
        return new TableCell<>() {
            private final Button deleteButton = new Button("Remove");

            {
                deleteButton.setStyle("-fx-background-color: #0d3073; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Terminal terminal = getTableRow().getItem();
                    if (terminal != null) {
                        showConfirmationPane(terminal);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }

    private void showConfirmationPane(Terminal terminal) {
        if (confirmationStage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/confirm_delete_window.fxml"));
                BorderPane confirmationPane = loader.load();

                // Get elements from FXML
                roomNoLabel = (Label) confirmationPane.lookup("#roomNoLabel");
                terminalNoLabel = (Label) confirmationPane.lookup("#terminalNoLabel");
                terminalOSLabel = (Label) confirmationPane.lookup("#terminalOSLabel");
                terminalStatusLabel = (Label) confirmationPane.lookup("#terminalStatusLabel");
                confirmButton = (Button) confirmationPane.lookup("#confirmButton");
                cancelButton = (Button) confirmationPane.lookup("#cancelButton");

                confirmationStage = new Stage();
                confirmationStage.initModality(Modality.APPLICATION_MODAL);
                confirmationStage.setScene(new Scene(confirmationPane));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Set terminal details in confirmation pane
        roomNoLabel.setText(terminal.getRoom());
        terminalNoLabel.setText(terminal.getTerminalID());
        terminalOSLabel.setText(terminal.getOs());
        terminalStatusLabel.setText(terminal.getStatus());

        // Handle confirm button action
        confirmButton.setOnAction(event -> {
            controller.removeTerminal(terminal); // Remove terminal and refresh table
            confirmationStage.close();
        });

        // Handle cancel button action
        cancelButton.setOnAction(event -> confirmationStage.close());

        confirmationStage.showAndWait();
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
