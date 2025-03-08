package shared;

public class Reservation {
    private String reservationID;
    private String userID;
    private String terminalID;
    private String roomID;
    private String reservationDate;
    private String startTime;
    private String endTime;
    private String status;

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

    public String getStatus() {
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
                ", status='" + status + '\'' +
                '}';
    }
}

