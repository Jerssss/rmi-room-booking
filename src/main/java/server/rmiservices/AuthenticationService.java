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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * Constructs an AuthenticationService instance.
     *
     * @throws RemoteException If a communication-related exception occurs during the remote method call.
     */
    public AuthenticationService() throws RemoteException {
        super();
    }

    /**
     * Handles user registration (sign-up).
     *
     * @param userID      The unique ID of the user.
     * @param name        The name of the user.
     * @param password    The password of the user.
     * @param userType    The type of user (e.g., "Admin" or "Student").
     * @param courseYear  The course year of the student (if applicable).
     * @param facultyType The faculty type of the admin (if applicable).
     * @return True if registration is successful, false otherwise.
     * @throws RemoteException If a communication-related exception occurs during the remote method call.
     */
    @Override
    public void signUp(String userID, String name, String password, String userType, String courseYear, String facultyType)
            throws RemoteException {

        if ("Admin".equalsIgnoreCase(userType)) {
            registerAdmin(userID, name, password, facultyType);
        } else if ("Student".equalsIgnoreCase(userType)) {
            registerStudent(userID, name, password, courseYear);
        } else {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    /**
     * Registers a new admin user.
     *
     * @param id          The unique ID of the admin.
     * @param name        The name of the admin.
     * @param password    The password of the admin.
     * @param facultyType The faculty type of the admin.
     * @return True if registration is successful, false otherwise.
     */
    private void registerAdmin(String id, String name, String password, String facultyType) {
        LinkedHashMap<String, Admin> admins = JSONUtility.loadAdmins(ADMIN_JSON_FILE);

        if (admins.containsKey(id)) {
            throw new RuntimeException("Admin already exists: " + id);
        }

        Admin newAdmin = new Admin(id, name, "Admin", password, facultyType);
        admins.put(id, newAdmin);
        JSONUtility.saveAdmins(admins, ADMIN_JSON_FILE);
    }

    /**
     * Registers a new student user.
     *
     * @param id         The unique ID of the student.
     * @param name       The name of the student.
     * @param password   The password of the student.
     * @param courseYear The course year of the student.
     * @return True if registration is successful, false otherwise.
     */
    /**
     * Registers a new student user.
     */
    private void registerStudent(String id, String name, String password, String courseYear) {
        LinkedHashMap<String, Student> students = JSONUtility.loadStudents(STUDENT_JSON_FILE);

        if (students.containsKey(id)) {
            throw new RuntimeException("Student already exists: " + id);
        }

        Student newStudent = new Student(id, name, password, courseYear);
        students.put(id, newStudent);
        JSONUtility.saveStudents(students, STUDENT_JSON_FILE);
    }


    /**
     * Handles user authentication (login).
     *
     * @param userID   The unique ID of the user.
     * @param password The password of the user.
     * @param userType The type of user (e.g., "Admin" or "Student").
     * @param clientIP The IP address of the client.
     * @return An array containing the login status, session token, and user name.
     * @throws RemoteException             If a communication-related exception occurs during the remote method call.
     * @throws InvalidCredentialsException If the provided credentials are invalid.
     * @throws AccountAlreadyLoggedIn      If the user is already logged in.
     */

    @Override
    public Object[] login(String userID, String password, String userType, String clientIP)
            throws RemoteException, InvalidCredentialsException, AccountAlreadyLoggedIn {
        if (activeClients.containsKey(userID)) {
            throw new AccountAlreadyLoggedIn("User is already logged in.");
        }

        Object[] response;
        if ("Admin".equalsIgnoreCase(userType)) {
            response = authenticateAdmin(userID, password, clientIP); // Fixed: Added clientIP
        } else if ("Student".equalsIgnoreCase(userType)) {
            response = authenticateStudent(userID, password, clientIP); // Fixed: Added clientIP
        } else {
            throw new InvalidCredentialsException("Invalid user type.");
        }

        logAction(userID, userType, "Login");
        return response;
    }



    @Override
    public void logout(String userID) throws RemoteException {
        if (userID == null || userID.isEmpty()) {
            return;
        }

        logAction(userID, "Student", "Logout"); // Log logout action

        if (activeClients.containsKey(userID)) {
            activeClients.remove(userID);
            System.out.println("[DEBUG] Successfully logged out: " + userID);
        } else {
            System.err.println("[ERROR] Logout failed: User not found in active clients.");
        }
    }



    /**
     * Authenticates an Admin user.
     *
     * @param userID   The unique ID of the admin.
     * @param password The password of the admin.
     * @param clientIP The IP address of the client.
     * @return An array containing the login status, session token, and user name.
     * @throws InvalidCredentialsException If the provided credentials are invalid.
     */
    private Object[] authenticateAdmin(String userID, String password, String clientIP)
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
     *
     * @param userID   The unique ID of the student.
     * @param password The password of the student.
     * @param clientIP The IP address of the client.
     * @return An array containing the login status, session token, and user name.
     * @throws InvalidCredentialsException If the provided credentials are invalid.
     */
    private Object[] authenticateStudent(String userID, String password, String clientIP)
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
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        logs.add(new Log(userID, userType, action, date, time));
        JSONUtility.saveLogs(logs, LOGS_JSON_FILE);

        System.out.println("[LOG] " + action + " - User: " + userID + " | Type: " + userType + " | Date: " + date + " | Time: " + time);
    }


    /**
     * Logs a client connection.
     *
     * @param clientIP The IP address of the client.
     * @throws RemoteException If a communication-related exception occurs during the remote method call.
     */
    @Override
    public void logClientConnection(String clientIP) throws RemoteException {
        ServerMain.logClientConnection(clientIP);
    }

    /**
     * Generates a session token for authentication.
     *
     * @return A unique session token.
     */
    private String generateSessionToken() {
        return "SESSION-" + UUID.randomUUID();
    }

    /**
     * Sends a notification to all logged-in clients.
     *
     * @param userID  The unique ID of the user triggering the notification.
     * @param message The notification message.
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
     * Implements the heartbeat mechanism to check the server's availability.
     * This method is called periodically to ensure the server is responsive.
     *
     * @throws RemoteException If a communication-related exception occurs during the remote method call.
     */
    @Override
    public void heartbeat() throws RemoteException {
    }
}
