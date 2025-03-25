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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class for handling JSON operations, including loading and saving data
 * for Admins, Students, Logs, Terminals, and Reservations.
 */
public class JSONUtility {

    /**
     * A Gson instance configured with pretty printing, null serialization,
     * and exclusion of fields without the {@code @Expose} annotation.
     */
    private static final Gson defaultGson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create();

    /**
     * Loads Admins from a JSON file.
     *
     * @param filePath the path to the JSON file containing the admin list
     * @return a {@code LinkedHashMap} of Admins, where the key is the Admin ID
     * @throws RuntimeException if there is an error reading the file or parsing the JSON
     */
    public static LinkedHashMap<String, Admin> loadAdmins(File filePath) {
        if (!filePath.exists()) {
            System.out.println("Admin file not found. Creating a new one.");
            saveAdmins(new LinkedHashMap<>(), filePath);
            return new LinkedHashMap<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (jsonElement == null || !jsonElement.isJsonObject()) {
                System.err.println("[ERROR] Invalid JSON format. Resetting file.");
                return new LinkedHashMap<>();
            }

            JsonObject root = jsonElement.getAsJsonObject();
            if (!root.has("Admins") || !root.getAsJsonObject("Admins").has("Admin")) {
                System.err.println("[ERROR] Missing 'Admins' or 'Admin' key.");
                return new LinkedHashMap<>();
            }

            JsonArray adminArray = root.getAsJsonObject("Admins").getAsJsonArray("Admin");
            LinkedHashMap<String, Admin> adminMap = new LinkedHashMap<>();

            for (JsonElement element : adminArray) {
                Admin admin = defaultGson.fromJson(element, Admin.class);
                if (admin.getId() != null) {
                    adminMap.put(admin.getId(), admin);
                }
            }

            return adminMap;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading admin data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Admins to a JSON file.
     *
     * @param adminMap the admin data to be saved
     * @param filePath the destination JSON file
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void saveAdmins(LinkedHashMap<String, Admin> adminMap, File filePath) {
        try {
            if (!filePath.getParentFile().exists()) {
                filePath.getParentFile().mkdirs();
            }

            JsonObject root = new JsonObject();
            JsonObject adminsWrapper = new JsonObject();
            JsonArray adminArray = new JsonArray();

            for (Admin admin : adminMap.values()) {
                JsonElement adminJson = defaultGson.toJsonTree(admin);
                adminArray.add(adminJson);
            }

            adminsWrapper.add("Admin", adminArray);
            root.add("Admins", adminsWrapper);

            try (FileWriter writer = new FileWriter(filePath)) {
                defaultGson.toJson(root, writer);
                System.out.println("[DEBUG] Admins saved successfully.");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error saving admin data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Students from a JSON file.
     *
     * @param filePath the path to the JSON file containing the student list
     * @return a {@code LinkedHashMap} of Students, where the key is the Student ID
     * @throws RuntimeException if there is an error reading the file or parsing the JSON
     */
    public static LinkedHashMap<String, Student> loadStudents(File filePath) {
        if (!filePath.exists()) {
            System.out.println("Student file not found. Creating a new one.");
            saveStudents(new LinkedHashMap<>(), filePath);
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
                if (student.getId() != null) {
                    studentMap.put(student.getId(), student);
                }
            }
            return studentMap;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading student data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Students to a JSON file.
     *
     * @param studentMap the student data to save
     * @param filePath the destination JSON file
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void saveStudents(LinkedHashMap<String, Student> studentMap, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<Student> studentList = new ArrayList<>(studentMap.values());

            for (Student student : studentList) {
                System.out.println("[DEBUG] Saving Student: " + student);
            }

            Map<String, Map<String, List<Student>>> nestedData = new LinkedHashMap<>();
            Map<String, List<Student>> studentsMap = new LinkedHashMap<>();
            studentsMap.put("Student", studentList);
            nestedData.put("Students", studentsMap);

            defaultGson.toJson(nestedData, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving student data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Logs from a JSON file.
     *
     * @param filePath the path to the JSON file containing the logs
     * @return a {@code List} of Log objects
     * @throws RuntimeException if there is an error reading the file or parsing the JSON
     */
    public static List<Log> loadLogs(File filePath) {
        if (!filePath.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement == null || !jsonElement.isJsonObject()) {
                System.err.println("[ERROR] Invalid JSON format in logs.json. Resetting file.");
                return new ArrayList<>();
            }

            JsonObject root = jsonElement.getAsJsonObject();
            if (!root.has("Logs") || !root.getAsJsonObject("Logs").has("Log")) {
                System.err.println("[ERROR] Missing 'Logs' structure in logs.json. Resetting file.");
                return new ArrayList<>();
            }

            JsonArray logsArray = root.getAsJsonObject("Logs").getAsJsonArray("Log");
            Type listType = new TypeToken<List<Log>>() {}.getType();
            List<Log> logs = defaultGson.fromJson(logsArray, listType);

            if (logs == null) {
                System.err.println("[ERROR] Failed to parse logs.json. Returning empty log list.");
                return new ArrayList<>();
            }

            return logs;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading log data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Logs a login event to a JSON file.
     *
     * @param userID the ID of the user logging in
     * @param userType the type of the user (e.g., Admin, Student)
     * @param filePath the path to the JSON file where the log will be saved
     */
    public static void logLoginToJson(String userID, String userType, File filePath) {
        List<Log> logs = loadLogs(filePath);

        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Log newLog = new Log(userID, userType, "Login", date, time);
        logs.add(newLog);

        saveLogs(logs, filePath);

        System.out.println("=====================================================");
        System.out.println("[LOGIN] Successfully logged in and saved: " + userID);
        System.out.println("=====================================================");
    }

    /**
     * Logs a logout event to a JSON file.
     *
     * @param userID the ID of the user logging out
     * @param userType the type of the user (e.g., Admin, Student)
     * @param filePath the path to the JSON file where the log will be saved
     */
    public static void logLogoutToJson(String userID, String userType, File filePath) {
        List<Log> logs = loadLogs(filePath);

        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        logs.add(new Log(userID, userType, "Logout", date, time));

        saveLogs(logs, filePath);
        System.out.println("=====================================================");
        System.out.println("[LOGOUT] Successfully logged out: " + userID);
        System.out.println("=====================================================");
    }

    /**
     * Saves Logs to a JSON file.
     *
     * @param logList the log data to save
     * @param filePath the destination JSON file
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void saveLogs(List<Log> logList, File filePath) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            Map<String, List<Log>> logMap = new LinkedHashMap<>();
            logMap.put("Log", logList);

            Map<String, Map<String, List<Log>>> nestedStructure = new LinkedHashMap<>();
            nestedStructure.put("Logs", logMap);

            defaultGson.toJson(nestedStructure, writer);
            writer.flush();
            System.out.println("[LOG] Logs successfully saved to file.");
        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to save logs to file: " + ex.getMessage());
            throw new RuntimeException("Error saving log data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Terminals from a JSON file.
     *
     * @param filePath the path to the JSON file containing the terminals
     * @return a {@code List} of Terminal objects
     * @throws RuntimeException if there is an error reading the file or parsing the JSON
     */
    public static List<Terminal> loadTerminals(File filePath) {
        if (!filePath.exists()) {
            System.err.println("[ERROR] Terminal file not found: " + filePath.getAbsolutePath());
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Map<String, List<Terminal>>>>() {}.getType();
            Map<String, Map<String, List<Terminal>>> data = defaultGson.fromJson(reader, type);

            if (data == null || !data.containsKey("Terminals") || !data.get("Terminals").containsKey("Terminal")) {
                System.err.println("[ERROR] Invalid JSON structure. Expected 'Terminals' -> 'Terminal'.");
                return new ArrayList<>();
            }

            List<Terminal> terminals = data.get("Terminals").get("Terminal");

            if (terminals.isEmpty()) {
                System.out.println("[DEBUG] No terminals found.");
            } else {
                System.out.println("[DEBUG] Loaded " + terminals.size() + " terminals.");
            }

            return terminals;
        } catch (IOException ex) {
            throw new RuntimeException("Error loading terminal data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Terminals to a JSON file.
     *
     * @param terminalList the terminal data to save
     * @param filePath the destination JSON file
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void saveTerminals(List<Terminal> terminalList, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            Map<String, Map<String, List<Terminal>>> nestedData = new LinkedHashMap<>();
            Map<String, List<Terminal>> terminalsMap = new LinkedHashMap<>();
            terminalsMap.put("Terminal", terminalList);
            nestedData.put("Terminals", terminalsMap);

            defaultGson.toJson(nestedData, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving terminal data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Reservations from a JSON file.
     *
     * @param filePath the path to the JSON file containing the reservations
     * @return a {@code List} of Reservation objects
     * @throws RuntimeException if there is an error reading the file or parsing the JSON
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
     * Saves Reservations to a JSON file.
     *
     * @param reservationList the reservation data to save
     * @param filePath the destination JSON file
     * @throws RuntimeException if there is an error writing to the file
     */
    public static void saveReservations(List<Reservation> reservationList, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            Map<String, Map<String, List<Reservation>>> nestedData = new LinkedHashMap<>();
            Map<String, List<Reservation>> reservationsMap = new LinkedHashMap<>();
            reservationsMap.put("Reservation", reservationList);
            nestedData.put("Reservations", reservationsMap);
            defaultGson.toJson(nestedData, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving reservation data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Converts a list of Admins or Students into a HashMap for easier lookup by ID.
     *
     * @param list List of Admin or Student objects
     * @return a {@code HashMap} of ID to Admin or Student
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