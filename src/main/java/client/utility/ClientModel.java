package client.utility;

import shared.Admin;
import shared.Student;
import shared.Reservation;
import shared.Terminal;
import shared.Log;

import java.util.HashMap;
import java.util.List;

public class ClientModel {
    private HashMap<String, Admin> adminList;
    private HashMap<String, Student> studentList;
    private List<Log> logs;
    private List<Terminal> terminals;
    private List<Reservation> reservations;

    public ClientModel() {
        adminList = new HashMap<>();
        studentList = new HashMap<>();
    }

    public HashMap<String, Admin> getAdminList() {
        return adminList;
    }

    public HashMap<String, Student> getStudentList() {
        return studentList;
    }

    public void setAdminList(HashMap<String, Admin> adminList) {
        this.adminList = adminList;
    }

    public void setStudentList(HashMap<String, Student> studentList) {
        this.studentList = studentList;
    }
}
