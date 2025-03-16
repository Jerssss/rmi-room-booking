package server.rmiservices;

import server.ServerMain;
import shared.Admin;
import shared.Log;
import shared.Student;
import shared.interfaces.Authentication;
import shared.callback.ClientCallbackInterface;
import util.JSONUtility;
import util.exception.AccountAlreadyLoggedIn;
import util.exception.InvalidCredentialsException;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements Authentication interface, handles login and registration using JSON.
 */
public class AuthenticationService extends UnicastRemoteObject implements Authentication {
    private static final File ADMIN_JSON_FILE = new File("src/main/resources/data/admin.json");
    private static final File STUDENT_JSON_FILE = new File("src/main/resources/data/student.json");
    private static final File LOGS_JSON_FILE = new File("src/main/resources/data/logs.json");

    // Stores active user sessions along with their callbacks
    private final ConcurrentHashMap<String, ClientCallbackInterface> activeClients = new ConcurrentHashMap<>();

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
    public Object[] login(String userID, String password, String userType, String clientIP, ClientCallbackInterface clientCallback)
            throws RemoteException, InvalidCredentialsException, AccountAlreadyLoggedIn {

        if (activeClients.containsKey(userID)) {
            System.out.println("[DEBUG] User already logged in: " + userID);
            throw new AccountAlreadyLoggedIn("User is already logged in.");
        }

        Object[] response;
        if ("Admin".equalsIgnoreCase(userType)) {
            response = authenticateAdmin(userID, password, clientIP, clientCallback);
        } else if ("Student".equalsIgnoreCase(userType)) {
            response = authenticateStudent(userID, password, clientIP, clientCallback);
        } else {
            throw new InvalidCredentialsException("Invalid user type.");
        }

        System.out.println("[DEBUG] Returning login response: " + java.util.Arrays.toString(response));
        return response;
    }


    /**
     * Authenticates an Admin user.
     */
    private Object[] authenticateAdmin(String userID, String password, String clientIP, ClientCallbackInterface clientCallback)
            throws InvalidCredentialsException {

        HashMap<String, Admin> admins = JSONUtility.loadAdmins(ADMIN_JSON_FILE);

        if (!admins.containsKey(userID)) {
            throw new InvalidCredentialsException("Invalid admin credentials.");
        }

        Admin admin = admins.get(userID);

        if (!admin.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Incorrect password.");
        }

        String sessionToken = generateSessionToken();
        activeClients.put(userID, clientCallback);

        sendNotification(userID, "Admin " + admin.getName() + " has logged in!");

        // Print login success details on the SERVER console
        System.out.println("=====================================================");
        System.out.println("[Server] Login successful!");
        System.out.println("[Server] User: " + admin.getName() + " (ID: " + userID + ")");
        System.out.println("[Server] User Type: Admin");
        System.out.println("[Server] IP Address: " + clientIP);
        System.out.println("[Server] Session Token: " + sessionToken);
        System.out.println("=====================================================");

        return new Object[]{"SUCCESS", sessionToken, admin.getName()};
    }


    /**
     * Authenticates a Student user.
     */
    private Object[] authenticateStudent(String userID, String password, String clientIP, ClientCallbackInterface clientCallback)
            throws InvalidCredentialsException {

        HashMap<String, Student> students = JSONUtility.loadStudents(STUDENT_JSON_FILE);

        if (!students.containsKey(userID)) {
            throw new InvalidCredentialsException("Invalid student credentials.");
        }

        Student student = students.get(userID);

        if (!student.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Incorrect password.");
        }

        String sessionToken = generateSessionToken();
        activeClients.put(userID, clientCallback);

        sendNotification(userID, "Student " + student.getName() + " has logged in!");

        // Print login success details on the SERVER console
        System.out.println("=====================================================");
        System.out.println("[Server] Login successful!");
        System.out.println("[Server] User: " + student.getName() + " (ID: " + userID + ")");
        System.out.println("[Server] User Type: Student");
        System.out.println("[Server] IP Address: " + clientIP);
        System.out.println("[Server] Session Token: " + sessionToken);
        System.out.println("=====================================================");

        return new Object[]{"SUCCESS", sessionToken, student.getName()};
    }



    private void logAction(String userID, String userType, String action) {
        List<Log> logs = JSONUtility.loadLogs(LOGS_JSON_FILE);

        String date = java.time.LocalDate.now().toString();
        String time = java.time.LocalTime.now().toString();

        logs.add(new Log(userID, userType, action, date, time));

        JSONUtility.saveLogs(logs, LOGS_JSON_FILE);
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
     * Sends a notification to all logged-in clients.
     */
    private void sendNotification(String userID, String message) {
        for (String client : activeClients.keySet()) {
            try {
                if (!client.equals(userID)) { // Don't notify the user logging in
                    activeClients.get(client).notify(message);
                }
            } catch (RemoteException e) {
                System.err.println("Failed to send notification to " + client);
                activeClients.remove(client); // Remove inactive client
            }
        }
    }

    /**
     * Handles user logout.
     */
    @Override
    public void logout(String sessionToken) throws RemoteException {
        activeClients.remove(sessionToken);
    }
}
