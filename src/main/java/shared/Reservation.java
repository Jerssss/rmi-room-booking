package shared;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Represents a reservation for a terminal in a specific room.
 * This class implements {@link Serializable} to allow object serialization.
 */
public class Reservation implements Serializable {

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
    private String status;

    /**
     * Constructs a new Reservation object with the specified details.
     *
     * @param reservationID   Unique identifier for the reservation.
     * @param userID          Unique identifier for the user making the reservation.
     * @param terminalID      Unique identifier for the terminal being reserved.
     * @param roomID          Unique identifier for the room containing the terminal.
     * @param reservationDate Date of the reservation.
     * @param startTime       Start time of the reservation.
     * @param endTime         End time of the reservation.
     * @param status          Status of the reservation (e.g., pending, approved, canceled).
     */
    public Reservation(String reservationID, String userID, String terminalID, String roomID,
                       String reservationDate, String startTime, String endTime, String status) {
        this.reservationID = reservationID;
        this.userID = userID;
        this.terminalID = terminalID;
        this.roomID = roomID;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    /**
     * @return The unique reservation ID.
     */
    public String getReservationID() { return reservationID; }

    /**
     * @return The unique user ID associated with the reservation.
     */
    public String getUserID() { return userID; }

    /**
     * @return The unique terminal ID for the reserved terminal.
     */
    public String getTerminalID() { return terminalID; }

    /**
     * @return The unique room ID where the terminal is located.
     */
    public String getRoomID() { return roomID; }

    /**
     * @return The reservation date.
     */
    public String getReservationDate() { return reservationDate; }

    /**
     * @return The start time of the reservation.
     */
    public String getStartTime() { return startTime; }

    /**
     * @return The end time of the reservation.
     */
    public String getEndTime() { return endTime; }

    /**
     * @return The current status of the reservation.
     */
    public String getStatus() { return status; }

    /**
     * Sets the reservation date.
     * @param reservationDate The new reservation date.
     */
    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    /**
     * Sets the start time of the reservation.
     * @param startTime The new start time.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time of the reservation.
     * @param endTime The new end time.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the room ID where the terminal is located.
     * @param roomID The new room ID.
     */
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    /**
     * Sets the terminal ID for the reservation.
     * @param terminalID The new terminal ID.
     */
    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    /**
     * Sets the status of the reservation.
     * @param status The new reservation status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a string representation of the reservation object.
     * @return A formatted string containing reservation details.
     */
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
                ", status='" + status + '\'' +
                '}';
    }
}
