package shared;

public class Log {
    private String userID;
    private String userType;
    private String action;
    private String date;
    private String time;

    public Log(String userID, String userType, String action, String date, String time) {
        this.userID = userID;
        this.userType = userType;
        this.action = action;
        this.date = date;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserType() {
        return userType;
    }

    public String getAction() {
        return action;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Log{" +
                "userID='" + userID + '\'' +
                ", userType='" + userType + '\'' +
                ", action='" + action + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
