package server.landingpage;

import util.XMLUtility;
import server.utility.LogsXMLHandler;
import shared.Admin;
import shared.Student;

import java.io.File;
import java.util.Map;

public class SignUpProcessor {
    private static final String ADMIN_XML_PATH = "src/main/resources/data/admin.xml";
    private static final String STUDENT_XML_PATH = "src/main/resources/data/student.xml";

    public static boolean registerUser(String userID, String name, String password, String userType, String courseYear, String facultyType) {
        System.out.println("[SIGNUP] Processing sign-up for UserID: " + userID);

        File userFile = new File(getUserFilePath(userType));
        Object userData = XMLUtility.loadXMLData(userFile);

        if (userData instanceof Map<?, ?> userMap) {
            if (userMap.containsKey(userID)) {
                System.out.println("[ERROR] Duplicate UserID: " + userID);
                return false;
            }
        }

        if ("Admin".equalsIgnoreCase(userType)) {
            Admin newAdmin = new Admin(userID, name, "Admin", password, facultyType);
            ((Map<String, Admin>) userData).put(userID, newAdmin);
        } else {
            Student newStudent = new Student(userID, name, password, courseYear);
            ((Map<String, Student>) userData).put(userID, newStudent);
        }

        // Save the updated admin.xml
        boolean saved = XMLUtility.saveXMLData(userFile, userData);

        if (!saved) {
            System.out.println("[ERROR] Failed to save " + userType + " data!");
            return false;
        }

        LogsXMLHandler.saveLog(userID, "SignUp", userType);
        return true;
    }

    private static String getUserFilePath(String userType) {
        return userType.equalsIgnoreCase("Admin") ? ADMIN_XML_PATH : STUDENT_XML_PATH;
    }
}
