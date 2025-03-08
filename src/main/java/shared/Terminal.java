package shared;

public class Terminal {
    private String terminalID;
    private String room;
    private String os;
    private String status;
    private String reservationDate;
    private String startTime;
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

