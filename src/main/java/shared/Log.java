package shared;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Log {
    @Expose
    @SerializedName("UserID")
    private Object userID; // Can be Integer or String

    @Expose
    @SerializedName("UserType")
    private String userType;

    @Expose
    @SerializedName("Action")
    private String action;

    @Expose
    @SerializedName("Date")
    private String date;

    @Expose
    @SerializedName("Time")
    private String time;

    public Log(Object userID, String userType, String action, String date, String time) {
        this.userID = userID;
        this.userType = userType;
        this.action = action;
        this.date = date;
        this.time = time;
    }

    public Object getUserID() {
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
                "UserID=" + userID +
                ", UserType='" + userType + '\'' +
                ", Action='" + action + '\'' +
                ", Date='" + date + '\'' +
                ", Time='" + time + '\'' +
                '}';
    }
}
