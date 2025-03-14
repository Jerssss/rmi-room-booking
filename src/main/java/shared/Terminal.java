package shared;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

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

    public Terminal(String terminalID, String room, String os, String status, String reservationDate, String startTime, String endTime) {
        this.terminalID = terminalID;
        this.room = room;
        this.os = os;
        this.status = status;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public String getRoom() {
        return room;
    }

    public String getOs() {
        return os;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String stat) {
        this.status = stat;
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
