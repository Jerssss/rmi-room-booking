package util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import shared.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class JSONUtility {
    private static final Gson defaultGson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Loads Admins from a JSON file (admin.json).
     * @param filePath the path to the JSON file containing the admin list
     * @return a HashMap representing the loaded admins
     */
    public static HashMap<String, Admin> loadAdmins(File filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<HashMap<String, Admin>>() {}.getType();
            return defaultGson.fromJson(reader, type);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading admin data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Admins to a JSON file (admin.json).
     * @param adminMap the admin data to be saved
     * @param filePath the destination JSON file
     */
    public static void saveAdmins(HashMap<String, Admin> adminMap, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            defaultGson.toJson(adminMap, writer);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving admin data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Loads Students from a JSON file (student.json).
     * @param filePath the JSON file path
     * @return a HashMap of students
     */
    public static HashMap<String, Student> loadStudents(File filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<HashMap<String, Student>>() {}.getType();
            return defaultGson.fromJson(reader, type);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading student data: " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves Students to a JSON file (student.json).
     * @param studentMap the student data to save
     * @param filePath the JSON file path
     */
    public static void saveStudents(HashMap<String, Student> studentMap, File filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            defaultGson.toJson(studentMap, writer);
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
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Log>>() {}.getType();
            return defaultGson.fromJson(reader, type);
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
        try (FileWriter writer = new FileWriter(filePath)) {
            defaultGson.toJson(logList, writer);
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
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Reservation>>() {}.getType();
            return defaultGson.fromJson(reader, type);
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
}
