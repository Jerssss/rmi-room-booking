package util;

import org.w3c.dom.*;
import shared.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLUtility {
    // Builder and Transformer instances
    private static DocumentBuilderFactory dbf;
    private static DocumentBuilder db;
    private static Document document;

    public static Object loadXMLData(File filePath) {
        String filename = filePath.getName();
        return switch (filename) {
            case "admin.xml" -> loadAdmin(filePath);
            case "student.xml" -> loadStudent(filePath);
            case "logs.xml" -> loadLogsHistory(filePath);
            case "terminal.xml" -> loadTerminal(filePath);
            case "reservation_approval.xml" -> loadReservationApproval(filePath);
            default -> null; // Handle unknown file types
        };
    }

    private static Object loadAdmin(File filePath) {
        Map<String, Admin> adminMap = new HashMap<>();
        try {
            System.out.println("[DEBUG] Loading admin.xml from: " + filePath.getAbsolutePath());

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(filePath);

            NodeList adminList = document.getElementsByTagName("Admin");
            System.out.println("[DEBUG] Found " + adminList.getLength() + " admin entries");

            for (int i = 0; i < adminList.getLength(); i++) {
                Element adminElement = (Element) adminList.item(i);
                String id = adminElement.getElementsByTagName("Admin_ID").item(0).getTextContent();
                String name = adminElement.getElementsByTagName("Name").item(0).getTextContent();
                String password = adminElement.getElementsByTagName("Password").item(0).getTextContent();
                Admin admin = new Admin(id, name, "Admin", password, "Faculty");

                System.out.println("[DEBUG] Loaded Admin: ID=" + id + " Name=" + name);
                adminMap.put(id, admin);
            }
            return adminMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object loadStudent(File filePath) {
        Map<String, Student> studentMap = new HashMap<>();
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(filePath);

            NodeList studentList = document.getElementsByTagName("Student");
            for (int i = 0; i < studentList.getLength(); i++) {
                Element studentElement = (Element) studentList.item(i);
                String id = studentElement.getElementsByTagName("Student_ID").item(0).getTextContent();
                String name = studentElement.getElementsByTagName("Name").item(0).getTextContent();
                String password = studentElement.getElementsByTagName("Password").item(0).getTextContent();
                String courseYear = studentElement.getElementsByTagName("CourseYear").item(0).getTextContent();

                Student student = new Student(id, name, password, courseYear);
                studentMap.put(id, student);
            }
            return studentMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object loadLogsHistory(File filePath) {
        List<Log> logList = new ArrayList<>();

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(filePath);

            NodeList logNodes = document.getElementsByTagName("Log");

            for (int i = 0; i < logNodes.getLength(); i++) {
                Node logNode = logNodes.item(i);
                if (logNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element logElement = (Element) logNode;

                    String userID = logElement.getElementsByTagName("UserID").item(0).getTextContent();
                    String userType = logElement.getElementsByTagName("UserType").item(0).getTextContent();
                    String action = logElement.getElementsByTagName("Action").item(0).getTextContent();
                    String date = logElement.getElementsByTagName("Date").item(0).getTextContent();
                    String time = logElement.getElementsByTagName("Time").item(0).getTextContent();

                    Log log = new Log(userID, userType, action, date, time);
                    logList.add(log);
                }
            }
            return logList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object loadTerminal(File filePath) {
        List<Terminal> terminalList = new ArrayList<>();

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(filePath);

            NodeList terminalNodes = document.getElementsByTagName("Terminal");

            for (int i = 0; i < terminalNodes.getLength(); i++) {
                Node terminalNode = terminalNodes.item(i);
                if (terminalNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element terminalElement = (Element) terminalNode;

                    String terminalID = terminalElement.getElementsByTagName("terminal_id").item(0).getTextContent();
                    String room = terminalElement.getElementsByTagName("terminal_room").item(0).getTextContent();
                    String os = terminalElement.getElementsByTagName("terminal_os").item(0).getTextContent();
                    String status = terminalElement.getElementsByTagName("terminal_status").item(0).getTextContent();
                    String reservationDate = terminalElement.getElementsByTagName("reservation_date").item(0).getTextContent();
                    String startTime = terminalElement.getElementsByTagName("start_time").item(0).getTextContent();
                    String endTime = terminalElement.getElementsByTagName("end_time").item(0).getTextContent();

                    Terminal terminal = new Terminal(terminalID, room, os, status, reservationDate, startTime, endTime);
                    terminalList.add(terminal);
                }
            }
            return terminalList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object loadReservationApproval(File filePath) {
        List<Reservation> reservationList = new ArrayList<>();

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(filePath);

            NodeList reservationNodes = document.getElementsByTagName("Reservation");

            for (int i = 0; i < reservationNodes.getLength(); i++) {
                Node reservationNode = reservationNodes.item(i);
                if (reservationNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element reservationElement = (Element) reservationNode;

                    String reservationID = reservationElement.getElementsByTagName("reservation_id").item(0).getTextContent();
                    String userID = reservationElement.getElementsByTagName("user_id").item(0).getTextContent();
                    String terminalID = reservationElement.getElementsByTagName("terminal_id").item(0).getTextContent();
                    String roomID = reservationElement.getElementsByTagName("room_id").item(0).getTextContent();
                    String reservationDate = reservationElement.getElementsByTagName("reservation_date").item(0).getTextContent();
                    String startTime = reservationElement.getElementsByTagName("start_time").item(0).getTextContent();
                    String endTime = reservationElement.getElementsByTagName("end_time").item(0).getTextContent();
                    String status = reservationElement.getElementsByTagName("status").item(0).getTextContent();

                    Reservation reservation = new Reservation(reservationID, userID, terminalID, roomID,
                            reservationDate, startTime, endTime, status);
                    reservationList.add(reservation);
                }
            }
            return reservationList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates an XML Element with the given tag and text content.
     */
    private static Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        return element;
    }

    /**
     * Saves the XML document back to the file.
     */
    private static void saveDocument(Document document, File file) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Pretty print XML output
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // Write the document to file
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    public static boolean saveXMLData(File filePath, Object data) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement;

            if (filePath.getName().contains("admin")) {
                rootElement = doc.createElement("Admins");
                for (Admin admin : ((Map<String, Admin>) data).values()) {
                    Element adminElement = doc.createElement("Admin");

                    adminElement.appendChild(createElement(doc, "Admin_ID", admin.getId()));
                    adminElement.appendChild(createElement(doc, "Name", admin.getName()));
                    adminElement.appendChild(createElement(doc, "Type", admin.getType()));
                    adminElement.appendChild(createElement(doc, "Password", admin.getPassword()));
                    adminElement.appendChild(createElement(doc, "FacultyType", admin.getFacultyType()));

                    rootElement.appendChild(adminElement);
                }
            } else {
                rootElement = doc.createElement("Students");
                for (Student student : ((Map<String, Student>) data).values()) {
                    Element studentElement = doc.createElement("Student");

                    studentElement.appendChild(createElement(doc, "Student_ID", student.getId()));
                    studentElement.appendChild(createElement(doc, "Name", student.getName()));
                    studentElement.appendChild(createElement(doc, "Password", student.getPassword()));
                    studentElement.appendChild(createElement(doc, "CourseYear", student.getCourseYear()));

                    rootElement.appendChild(studentElement);
                }
            }

            doc.appendChild(rootElement);
            saveDocument(doc, filePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}