package shared;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;

public class Reservation {
    @Expose
    @SerializedName("reservation_id")
    private String reservationID;

    @Expose
    @SerializedName("user_id")
    private String userID;

    @Expose
    @SerializedName("terminal_id")
    private String terminalID;

    @Expose
    @SerializedName("room_id")
    private String roomID;

    @Expose
    @SerializedName("reservation_date")
    private String reservationDate;

    @Expose
    @SerializedName("start_time")
    private String startTime;

    @Expose
    @SerializedName("end_time")
    private String endTime;

    @Expose
    @SerializedName("status")
    private SimpleStringProperty status; // Use SimpleStringProperty for status

    public Reservation(String reservationID, String userID, String terminalID, String roomID,
                       String reservationDate, String startTime, String endTime, String status) {
        this.reservationID = reservationID;
        this.userID = userID;
        this.terminalID = terminalID;
        this.roomID = roomID;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = new SimpleStringProperty(status); // Initialize the property
    }

    // Getters for all fields
    public String getReservationID() {
        return reservationID;
    }

    public String getUserID() {
        return userID;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    // Getter for status property
    public String getStatus() {
        return status.get();
    }

    // Setter for status property
    public void setStatus(String status) {
        this.status.set(status);
    }

    // Property accessor for status (required for JavaFX bindings)
    public SimpleStringProperty statusProperty() {
        return status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationID='" + reservationID + '\'' +
                ", userID='" + userID + '\'' +
                ", terminalID='" + terminalID + '\'' +
                ", roomID='" + roomID + '\'' +
                ", reservationDate='" + reservationDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status='" + status.get() + '\'' +
                '}';
    }
}