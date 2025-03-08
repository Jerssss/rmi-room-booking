package server.utility;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class LogsXMLHandler {
    private static final String LOG_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/data/logs.xml";

    public static synchronized void saveLog(String userID, String action, String userType) {
        try {
            File file = new File(LOG_FILE_PATH);
            Document doc = loadOrCreateDocument(file);

            Element log = doc.createElement("Log");
            log.appendChild(createElement(doc, "UserID", userID));
            log.appendChild(createElement(doc, "UserType", userType)); // Correct Field Order
            log.appendChild(createElement(doc, "Action", action));     // Correct Field Order
            log.appendChild(createElement(doc, "Date", java.time.LocalDate.now().toString()));
            log.appendChild(createElement(doc, "Time", java.time.LocalTime.now().withNano(0).toString()));

            doc.getDocumentElement().appendChild(log);
            removeWhitespaceNodes(doc);
            saveDocument(doc, file);
            System.out.println("Log saved: " + userID + " - " + action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void logLogout(String userID, String userType) {
        saveLog(userID, "Logout", userType);
    }


    private static Document loadOrCreateDocument(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (file.exists() && file.length() > 0) {
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } else {
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("Logs");
            doc.appendChild(root);
            return doc;
        }
    }

    private static Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        return element;
    }

    private static void saveDocument(Document document, File file) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static void removeWhitespaceNodes(Document doc) {
        NodeList nodeList = doc.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            removeWhitespaceNodesRecursive(nodeList.item(i));
        }
    }

    private static void removeWhitespaceNodesRecursive(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = childNodes.getLength() - 1; i >= 0; i--) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodesRecursive(child);
            }
        }
    }
}
