package client;

import client.landingpage.LandingPageController;
import client.landingpage.LandingPageView;
import client.utility.ClientView;
import client.utility.ServerConnectionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;

public class ClientMain extends Application {
    private static String SERVER_IP = null;
    private static final int SERVER_DISCOVERY_PORT = 12345;

    public static void main(String[] args) {
        requestServerAddress(); // Find the server IP before starting UI
        launch(args); // Launch the GUI
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/landing_page.fxml"));
            Parent root = loader.load();

            LandingPageView landingPageView = loader.getController();

            new LandingPageController(landingPageView); // Pass the view

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not load landing_page.fxml");
        }
    }


    public static void requestServerAddress() {
        if (SERVER_IP != null) return; // Skip if already found

        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.println("Broadcasting server discovery request...");
            socket.setBroadcast(true);

            byte[] sendData = "DISCOVER_SERVER_REQUEST".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName("255.255.255.255"), SERVER_DISCOVERY_PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            SERVER_IP = receivePacket.getAddress().getHostAddress();
            System.out.println("Server found at IP: " + SERVER_IP);
        } catch (IOException e) {
            System.err.println("Error during server discovery: " + e.getMessage());
        }
    }

    public static String getServerIP() {
        if (SERVER_IP == null) {
            requestServerAddress();
        }
        return SERVER_IP;
    }
}
