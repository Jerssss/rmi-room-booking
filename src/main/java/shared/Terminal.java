package shared;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Represents a terminal in a specific room, including its operating system, status,
 * and reservation details. Implements {@link Serializable} for object serialization.
 */
public class Terminal implements Serializable {

    @Expose
    @SerializedName("terminal_id")
    private String terminalID;

    @Expose
    @SerializedName("terminal_room")
    private String room;

    @Expose
    @SerializedName("terminal_os")
    private String os;

    @Expose
    @SerializedName("terminal_status")
    private String status;

    @Expose
    @SerializedName("reservation_date")
    private String reservationDate;

    @Expose
    @SerializedName("start_time")
    private String startTime;

    @Expose
    @SerializedName("end_time")
    private String endTime;

    /**
     * Constructs a new Terminal object with the specified details.
     *
     * @param terminalID       Unique identifier for the terminal.
     * @param room             The room where the terminal is located.
     * @param os               The operating system installed on the terminal.
     * @param status           The status of the terminal (e.g., available, in use, under maintenance).
     * @param reservationDate  The date of the reservation for this terminal.
     * @param startTime        The start time of the reservation.
     * @param endTime          The end time of the reservation.
     */
    public Terminal(String terminalID, String room, String os, String status, String reservationDate, String startTime, String endTime) {
        this.terminalID = terminalID;
        this.room = room;
        this.os = os;
        this.status = status;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * @return The unique terminal ID.
     */
    public String getTerminalID() {
        return terminalID;
    }

    /**
     * @return The room where the terminal is located.
     */
    public String getRoom() {
        return room;
    }

    /**
     * @return The operating system of the terminal.
     */
    public String getOs() {
        return os;
    }

    /**
     * @return The status of the terminal.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the terminal.
     * @param status The new status of the terminal.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The reservation date for this terminal.
     */
    public String getReservationDate() {
        return reservationDate;
    }

    /**
     * @return The start time of the reservation.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @return The end time of the reservation.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Returns a string representation of the terminal object.
     * @return A formatted string containing terminal details.
     */
    @Override
    public String toString() {
        return "Terminal{" +
                "terminalID='" + terminalID + '\'' +
                ", room='" + room + '\'' +
                ", os='" + os + '\'' +
                ", status='" + status + '\'' +
                ", reservationDate='" + reservationDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}