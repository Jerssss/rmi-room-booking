package client.utility;

import shared.*;

import java.util.HashMap;
import java.util.List;

public class ClientModel {
    private HashMap<String, Admin> adminList;
    private HashMap<String, Student> studentList;
    private List<Log> logs;
    private List<Terminal> terminals;
    private List<Reservation> reservations;

    public ClientModel() {
        adminList = null;
        studentList = null;
        logs = null;
        terminals = null;
        reservations = null;
    }

    public ClientModel(HashMap<String, Admin> adminList, HashMap<String, Student> studentList,
                       List<Log> logs, List<Terminal> terminals, List<Reservation> reservations) {
        this.adminList = adminList;
        this.studentList = studentList;
        this.logs = logs;
        this.terminals = terminals;
        this.reservations = reservations;
    }

    // ** GETTERS ** //
    public HashMap<String, Admin> getAdminList() {
        return adminList;
    }

    public HashMap<String, Student> getStudentList() {
        return studentList;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    // ** SETTERS ** //
    public void setAdminList(HashMap<String, Admin> adminList) {
        this.adminList = adminList;
    }

    public void setStudentList(HashMap<String, Student> studentList) {
        this.studentList = studentList;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void setTerminals(List<Terminal> terminals) {
        this.terminals = terminals;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}

