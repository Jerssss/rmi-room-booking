package server.rmiservices;

import server.ServerMain;
import shared.Admin;
import shared.Student;
import shared.interfaces.Authentication;
import util.JSONUtility;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Implements Authentication interface, handles login and registration using JSON.
 */
public class AuthenticationService extends UnicastRemoteObject implements Authentication {
    private static final File ADMIN_JSON_FILE = new File("src/main/resources/data/admin.json");
    private static final File STUDENT_JSON_FILE = new File("src/main/resources/data/student.json");

    public AuthenticationService() throws RemoteException {
        super();
    }

    @Override
    public boolean signUp(String userID, String name, String password, String userType, String courseYear, String facultyType)
            throws RemoteException {

        if ("Admin".equalsIgnoreCase(userType)) {
            return registerAdmin(userID, name, password, facultyType);
        } else if ("Student".equalsIgnoreCase(userType)) {
            return registerStudent(userID, name, password, courseYear);
        }
        return false;
    }

    private boolean registerAdmin(String id, String name, String password, String facultyType) {
        // Use LinkedHashMap instead of HashMap to maintain order
        LinkedHashMap<String, Admin> admins = JSONUtility.loadAdmins(ADMIN_JSON_FILE);

        if (admins.containsKey(id)) {
            System.err.println("[SIGNUP] Admin already exists: " + id);
            return false;
        }

        // Debug: Print the new admin details
        System.out.println("[DEBUG] Creating new admin: " + id + ", " + name + ", " + password + ", " + facultyType);

        Admin newAdmin = new Admin(id, name, "Admin", password, facultyType);

        // Insert the new admin at the end
        admins.put(id, newAdmin);

        // Save back to JSON file
        JSONUtility.saveAdmins(admins, ADMIN_JSON_FILE);

        System.out.println("[SIGNUP] Admin registered: " + id);
        return true;
    }

    private boolean registerStudent(String id, String name, String password, String courseYear) {
        // Use LinkedHashMap instead of HashMap to maintain order
        LinkedHashMap<String, Student> students = JSONUtility.loadStudents(STUDENT_JSON_FILE);

        if (students.containsKey(id)) {
            System.err.println("[SIGNUP] Student already exists: " + id);
            return false;
        }

        Student newStudent = new Student(id, name, password, courseYear);

        // Insert the new student at the end
        students.put(id, newStudent);

        // Save back to JSON file
        JSONUtility.saveStudents(students, STUDENT_JSON_FILE);

        System.out.println("[SIGNUP] Student registered: " + id);
        return true;
    }


    /**
     * Handles user authentication (login).
     */
    @Override
    public Object[] login(String userID, String password, String userType, String clientIP)
            throws RemoteException, InvalidCredentialsException, AccountAlreadyLoggedIn {

        if ("Admin".equalsIgnoreCase(userType)) {
            return authenticateAdmin(userID, password, clientIP);
        } else if ("Student".equalsIgnoreCase(userType)) {
            return authenticateStudent(userID, password, clientIP);
        } else {
            throw new InvalidCredentialsException("Invalid user type.");
        }
    }

    /**
     * Authenticates an Admin user.
     */
    private Object[] authenticateAdmin(String userID, String password, String clientIP)
            throws InvalidCredentialsException, AccountAlreadyLoggedIn {

        HashMap<String, Admin> admins = JSONUtility.loadAdmins(ADMIN_JSON_FILE);

        if (!admins.containsKey(userID)) {
            throw new InvalidCredentialsException("Invalid admin credentials.");
        }

        Admin admin = admins.get(userID);

        if (!admin.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Incorrect password.");
        }

        // Log the client connection
        ServerMain.logClientConnection(clientIP);

        System.out.println("[LOGIN] Admin logged in: " + admin.getName() + " from IP: " + clientIP);

        return new Object[]{"SUCCESS", generateSessionToken(), admin.getName()};
    }

    /**
     * Authenticates a Student user.
     */
    private Object[] authenticateStudent(String userID, String password, String clientIP)
            throws InvalidCredentialsException, AccountAlreadyLoggedIn {

        HashMap<String, Student> students = JSONUtility.loadStudents(STUDENT_JSON_FILE);

        if (!students.containsKey(userID)) {
            throw new InvalidCredentialsException("Invalid student credentials.");
        }

        Student student = students.get(userID);

        if (!student.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Incorrect password.");
        }

        // Log the client connection
        ServerMain.logClientConnection(clientIP);

        System.out.println("[LOGIN] Student logged in: " + student.getName() + " from IP: " + clientIP);

        return new Object[]{"SUCCESS", generateSessionToken(), student.getName()};
    }

    @Override
    public void logClientConnection(String clientIP) throws RemoteException {
        ServerMain.logClientConnection(clientIP);
    }

    /**
     * Generates a session token for authentication.
     */
    private String generateSessionToken() {
        return "SESSION-" + UUID.randomUUID();
    }

    /**
     * Handles user logout.
     */
    @Override
    public void logout(String sessionToken) throws RemoteException {
        System.out.println("[LOGOUT] Session ended for token: " + sessionToken);
    }
}
