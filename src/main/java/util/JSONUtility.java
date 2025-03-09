package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import shared.Admin;
import shared.Student;
import shared.Log;
import shared.Terminal;
import shared.Reservation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class JSONUtility {

    private static final Gson defaultGson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create();

    /**
     * Loads Admins from a JSON file (admin.json).
     * @param filePath the path to the JSON file containing the admin list
     * @return a HashMap representing the loaded admins
     */
    public static LinkedHashMap<String, Admin> loadAdmins(File filePath) {
        if (!filePath.exists()) {
            System.out.println("Admin file not found. Creating a new one.");
            saveAdmins(new LinkedHashMap<>(), filePath); // Create an empty file
            return new LinkedHashMap<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Map<String, List<Admin>>>>() {}.getType();
            Map<String, Map<String, List<Admin>>> data = defaultGson.fromJson(reader, type);

            if (data == null || data.get("Admins") == null || data.get("Admins").get("Admin") == null) {
                System.out.println("Invalid or empty JSON file. Initializing with empty data.");
                return new LinkedHashMap<>();
            }

            List<Admin> adminList = data.get("Admins").get("Admin");
            LinkedHashMap<String, Admin> adminMap = new LinkedHashMap<>();

            for (Admin admin : adminList) {
                if (admin.getId() != null) {  // Ensure no null admins
                    adminMap.put(admin.getId(), admin);
                }
            }
            return adminMap;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading admin data: " + ex.getMessage(), ex);
        }
    }


    /**
     * Saves Admins to a JSON file (admin.json).
     * @param adminMap the admin data to be saved
     * @param filePath the destination JSON file
     */
    public static void saveAdmins(LinkedHashMap<String, Admin> adminMap, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Admin> adminList = new ArrayList<>(adminMap.values());

            // Debug: Ensure all Admin objects are correct
            for (Admin admin : adminList) {
                System.out.println("[DEBUG] Saving Admin: " + admin);
            }

            // Wrap the list in the nested structure
            Map<String, Map<String, List<Admin>>> nestedData = new LinkedHashMap<>();
            Map<String, List<Admin>> adminsMap = new LinkedHashMap<>();
            adminsMap.put("Admin", adminList);
            nestedData.put("Admins", adminsMap);

            // Save in expected JSON structure
            defaultGson.toJson(nestedData, writer);

            System.out.println("[DEBUG] Admins successfully saved to: " + filePath.getAbsolutePath());
        } catch (IOException ex) {
            throw new RuntimeException("Error saving admin data: " + ex.getMessage(), ex);
        }
    }



    /**
     * Loads Students from a JSON file (student.json).
     * @param filePath the JSON file path
     * @return a HashMap of students
     */
    public static LinkedHashMap<String, Student> loadStudents(File filePath) {
        if (!filePath.exists()) {
            System.out.println("Student file not found. Creating a new one.");
            saveStudents(new LinkedHashMap<>(), filePath); // Create an empty file
            return new LinkedHashMap<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Map<String, List<Student>>>>() {}.getType();
            Map<String, Map<String, List<Student>>> data = defaultGson.fromJson(reader, type);

            if (data == null || data.get("Students") == null || data.get("Students").get("Student") == null) {
                System.out.println("Invalid or empty JSON file. Initializing with empty data.");
                return new LinkedHashMap<>();
            }

            List<Student> students = data.get("Students").get("Student");
            LinkedHashMap<String, Student> studentMap = new LinkedHashMap<>();

            for (Student student : students) {
                if (student.getId() != null) {  // Ensure no null students
                    studentMap.put(student.getId(), student);
                }
            }
            return studentMap;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading student data: " + ex.getMessage(), ex);
        }
    }



    /**
     * Saves Students to a JSON file (student.json).
     * @param studentMap the student data to save
     * @param filePath the JSON file path
     */
    public static void saveStudents(LinkedHashMap<String, Student> studentMap, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Student> studentList = new ArrayList<>(studentMap.values());

            // Debug: Ensure all Student objects are correct
            for (Student student : studentList) {
                System.out.println("[DEBUG] Saving Student: " + student);
            }

            // Wrap the list in the nested structure
            Map<String, Map<String, List<Student>>> nestedData = new LinkedHashMap<>();
            Map<String, List<Student>> studentsMap = new LinkedHashMap<>();
            studentsMap.put("Student", studentList);
            nestedData.put("Students", studentsMap);

            // Save in expected JSON structure
            defaultGson.toJson(nestedData, writer);

            System.out.println("[DEBUG] Students successfully saved to: " + filePath.getAbsolutePath());
        } catch (IOException ex) {
            throw new RuntimeException("Error saving student data: " + ex.getMessage(), ex);
        }
    }



    /**
     * Loads Logs from a JSON file (logs.json).
     * @param filePath the JSON file path
     * @return a List of Log objects
     */
    public static List<Log> loadLogs(File filePath) {
        if (!filePath.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("Logs")) {
                JsonObject root = jsonElement.getAsJsonObject();
                JsonArray logsArray = root.getAsJsonObject("Logs").getAsJsonArray("Log");
                Type listType = new TypeToken<List<Log>>() {}.getType();
                return defaultGson.fromJson(logsArray, listType);
            } else {
                throw new JsonSyntaxException("Invalid JSON structure in logs.json");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error loading log data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Logs to a JSON file (logs.json).
     * @param logList the log data to save
     * @param filePath the JSON file path
     */
    public static void saveLogs(List<Log> logList, File filePath) {
        Map<String, Map<String, List<Log>>> nestedStructure = new LinkedHashMap<>();
        Map<String, List<Log>> logMap = new LinkedHashMap<>();
        logMap.put("Log", logList);
        nestedStructure.put("Logs", logMap);

        try (FileWriter writer = new FileWriter(filePath)) {
            defaultGson.toJson(nestedStructure, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving log data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Terminals from a JSON file (terminal.json).
     * @param filePath the JSON file path
     * @return a List of Terminal objects
     */
    public static List<Terminal> loadTerminals(File filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Terminal>>() {}.getType();
            return defaultGson.fromJson(reader, type);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading terminal data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Terminals to a JSON file (terminal.json).
     * @param terminalList the terminal data to save
     * @param filePath the JSON file path
     */
    public static void saveTerminals(List<Terminal> terminalList, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            defaultGson.toJson(terminalList, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving terminal data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Reservations from a JSON file (reservation_approval.json).
     * @param filePath the JSON file path
     * @return a List of Reservation objects
     */
    public static List<Reservation> loadReservations(File filePath) {
        if (!filePath.exists()) {
            System.err.println("[ERROR] Reservation file not found: " + filePath.getAbsolutePath());
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Map<String, List<Reservation>>>>() {}.getType();
            Map<String, Map<String, List<Reservation>>> data = defaultGson.fromJson(reader, type);

            if (data == null || !data.containsKey("Reservations") || !data.get("Reservations").containsKey("Reservation")) {
                System.err.println("[ERROR] Invalid JSON structure. Expected 'Reservations' -> 'Reservation'.");
                return new ArrayList<>();
            }

            List<Reservation> reservations = data.get("Reservations").get("Reservation");

            if (reservations.isEmpty()) {
                System.out.println("[DEBUG] No reservations found.");
            } else {
                System.out.println("[DEBUG] Loaded " + reservations.size() + " reservations.");
            }

            return reservations;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading reservation data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Reservations to a JSON file (reservation_approval.json).
     * @param reservationList the reservation data to save
     * @param filePath the JSON file path
     */
    public static void saveReservations(List<Reservation> reservationList, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            defaultGson.toJson(reservationList, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving reservation data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Converts a list of Admins or Students into a HashMap for easier lookup by ID
     * @param list List of Admin or Student objects
     * @return a HashMap of ID to Admin or Student
     */
    private static <T> HashMap<String, T> toMap(List<T> list) {
        HashMap<String, T> map = new HashMap<>();
        for (T item : list) {
            if (item instanceof Admin) {
                map.put(((Admin) item).getId(), item);
            } else if (item instanceof Student) {
                map.put(((Student) item).getId(), item);
            }
        }
        return map;
    }
}