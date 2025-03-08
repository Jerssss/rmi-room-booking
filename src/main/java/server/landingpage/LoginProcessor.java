package server.landingpage;

import util.XMLUtility;
import server.utility.LogsXMLHandler;
import shared.Admin;
import shared.Student;

import java.io.File;
import java.util.Map;

public class LoginProcessor {
    private static final String ADMIN_XML_PATH = "src/main/resources/data/admin.xml";
    private static final String STUDENT_XML_PATH = "src/main/resources/data/student.xml";

    /**
     * Retrieves the user's name if authentication is successful.
     *
     * @param userID   The user ID.
     * @param password The password.
     * @param userType The user type ("Admin" or "Student").
     * @return The user's name if authentication is successful; otherwise, null.
     */
    public static String getUserName(String userID, String password, String userType) {
        System.out.println("[LOGIN] Processing login for UserID: " + userID + " Type: " + userType);

        Object userData = XMLUtility.loadXMLData(new File(getUserFilePath(userType)));

        if (userData instanceof Map<?, ?> userMap) {
            System.out.println("[DEBUG] Loaded user data: " + userMap);

            if (userType.equalsIgnoreCase("Admin")) {
                Admin admin = (Admin) userMap.get(userID);
                if (admin != null) {
                    System.out.println("[DEBUG] Found Admin: " + admin.getName());
                    if (admin.getPassword().equals(password)) {
                        LogsXMLHandler.saveLog(userID, "Login", userType);
                        return admin.getName();
                    } else {
                        System.out.println("[ERROR] Incorrect password for Admin ID: " + userID);
                    }
                } else {
                    System.out.println("[ERROR] Admin ID not found: " + userID);
                }
            } else {
                Student student = (Student) userMap.get(userID);
                if (student != null) {
                    System.out.println("[DEBUG] Found Student: " + student.getName());
                    if (student.getPassword().equals(password)) {
                        LogsXMLHandler.saveLog(userID, "Login", userType);
                        return student.getName();
                    } else {
                        System.out.println("[ERROR] Incorrect password for Student ID: " + userID);
                    }
                } else {
                    System.out.println("[ERROR] Student ID not found: " + userID);
                }
            }
        } else {
            System.out.println("[ERROR] Failed to load user data.");
        }
        return null;
    }


    /**
     * Validates if a user exists in the XML file.
     *
     * @param userID   The user ID.
     * @param password The password.
     * @param userType The user type ("Admin" or "Student").
     * @return True if the user is valid, false otherwise.
     */
    public static boolean validateUser(String userID, String password, String userType) {
        Object userData = XMLUtility.loadXMLData(new File(getUserFilePath(userType)));

        if (userData instanceof Map<?, ?> userMap) {
            if (userType.equalsIgnoreCase("Admin")) {
                Admin admin = (Admin) userMap.get(userID);
                if (admin != null && admin.getPassword().equals(password)) {
                    LogsXMLHandler.saveLog(userID, "Login", userType);
                    return true;
                }
            } else {
                Student student = (Student) userMap.get(userID);
                if (student != null && student.getPassword().equals(password)) {
                    LogsXMLHandler.saveLog(userID, "Login", userType);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the correct XML file path based on the user type.
     *
     * @param userType The user type.
     * @return The corresponding XML file path.
     */
    private static String getUserFilePath(String userType) {
        return userType.equalsIgnoreCase("Admin") ? ADMIN_XML_PATH : STUDENT_XML_PATH;
    }
}
