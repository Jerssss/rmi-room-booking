// File: client/admin/model/ReservationApprovalModel.java
package client.admin.model;

//import client.utility.ServerConnection;
//import client.utility.ServerConnectionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import server.reservationapproval.ReservationApprovalProcessor ;
import shared.Reservation;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ReservationApprovalModel {

//    private static ServerConnection serverConnection;

    public ReservationApprovalModel() {

        try {
//            serverConnection = ServerConnectionManager.getConnection();
        } catch (IOException e) {
            showErrorDialog("Server connection failed.");
        }
    }

//    public ObservableList<Reservation> loadReservationData() {
////        if (serverConnection != null) {
////            List<Reservation> reservations = ReservationApprovalProcessor.parseXML();
//            if (reservations == null) {
//                System.out.println("No reservation data received from server!");
//                return FXCollections.observableArrayList();
//            }
//            return FXCollections.observableArrayList(reservations);
//        }
//        return FXCollections.observableArrayList();
//    }

//    public void saveReservationData(ObservableList<Reservation> reservations) {
//        if (serverConnection != null) {
//            List<Reservation> reservationList = new ArrayList<>(reservations);
//            ReservationApprovalProcessor.saveToXML(reservationList);
//        }
//    }

//    public static boolean sendReservationApprovalData(List<Reservation> reservations) {
//        System.out.println("[DEBUG] sendReservationApprovalData called with multiple reservations.");
//
//        if (serverConnection == null) {
//            showErrorDialog("No server connection available.");
//            System.out.println("[DEBUG] sendReservationApprovalData aborted: serverConnection is null.");
//            return false;
//        }
//
//        try {
//            String requestXML = createXMLRequest(reservations);
//            System.out.println("[DEBUG] XML Request created:\n" + requestXML);
//            serverConnection.sendMessage(requestXML);
//            System.out.println("[DEBUG] XML Request sent to server.");
//
//            // Read full response from server
//            StringBuilder responseBuilder = new StringBuilder();
//            String line;
//            while ((line = serverConnection.readMessage()) != null) {
//                responseBuilder.append(line);
//                if (line.contains("</Response>")) {  // assuming the response XML ends with </Response>
//                    break;
//                }
//            }
//
//            String responseXML = responseBuilder.toString();
//            System.out.println("[DEBUG] Complete XML Response received from server:\n" + responseXML);
//
//            boolean result = parseXMLResponse(responseXML);
//            System.out.println("[DEBUG] Parsed XML Response result: " + result);
//            return result;
//
//        } catch (IOException | ParserConfigurationException | TransformerException e) {
//            showErrorDialog("Error occurred: " + e.getMessage());
//            System.out.println("[DEBUG] Exception in sendReservationApprovalData: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }


    /**
     * Creates the XML request using a style similar to the logs handler,
     * but omits the XML declaration.
     */
    private static String createXMLRequest(List<Reservation> reservations) throws ParserConfigurationException, TransformerException {
        System.out.println("[DEBUG] Creating XML request for multiple reservations.");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("Reservations");
        doc.appendChild(root);
        System.out.println("[DEBUG] Root element 'Reservations' created.");

        for (Reservation reservation : reservations) {
            Element resElement = doc.createElement("Reservation");
            root.appendChild(resElement);

            resElement.appendChild(createElement(doc, "reservation_id", reservation.getReservationID()));
            resElement.appendChild(createElement(doc, "user_id", reservation.getUserID()));
            resElement.appendChild(createElement(doc, "terminal_id", reservation.getTerminalID()));
            resElement.appendChild(createElement(doc, "room_id", reservation.getRoomID()));
            resElement.appendChild(createElement(doc, "reservation_date", reservation.getReservationDate()));
            resElement.appendChild(createElement(doc, "start_time", reservation.getStartTime()));
            resElement.appendChild(createElement(doc, "end_time", reservation.getEndTime()));
            resElement.appendChild(createElement(doc, "status", reservation.getStatus()));

            System.out.println("[DEBUG] Added reservation with ID: " + reservation.getReservationID());
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        System.out.println("[DEBUG] XML Request created successfully.");

        // Ensure it's a true one-liner by removing all unnecessary whitespace
        return writer.toString().replaceAll(">\\s+<", "><").trim();
    }


    private static Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        return element;
    }

    /**
     * Helper method to remove extraneous whitespace nodes from the document.
     */
    private static void removeWhitespaceNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }

    private static boolean parseXMLResponse(String xmlResponse) {
        System.out.println("[DEBUG] Parsing XML response in parseXMLResponse.");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            String statusValue = root.getElementsByTagName("Status").item(0).getTextContent();
            System.out.println("[DEBUG] XML Response Status: " + statusValue);
            return statusValue.equalsIgnoreCase("SUCCESS");
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in parseXMLResponse: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void showErrorDialog(String message) {
        Platform.runLater(() -> JOptionPane.showMessageDialog(
                null, message, "Server Error", JOptionPane.ERROR_MESSAGE)
        );
    }
}