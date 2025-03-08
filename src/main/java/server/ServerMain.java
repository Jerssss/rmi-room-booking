package server;

import shared.interfaces.RMIServer;
import server.RMIServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.*;
import java.io.*;

public class ServerMain {
    private static final int BROADCAST_PORT = 12345;
    private static volatile boolean isRunning = false;

    public static void main(String[] args) {
        startRMIServer();
        startBroadcastingServer();
    }

    public static void startRMIServer() {
        try {
            RMIServer server = new RMIServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RMIServer", server);
            isRunning = true;
            System.out.println("RMI Server started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startBroadcastingServer() {
        new Thread(() -> {
            try (DatagramSocket broadcastSocket = new DatagramSocket(BROADCAST_PORT)) {
                System.out.println("Broadcasting server IP on port " + BROADCAST_PORT);

                while (isRunning) {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    broadcastSocket.receive(receivePacket);
                    System.out.println("Connection request received from: " + receivePacket.getAddress());

                    String serverIP = InetAddress.getLocalHost().getHostAddress();
                    byte[] sendData = serverIP.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                            receivePacket.getAddress(), receivePacket.getPort());
                    broadcastSocket.send(sendPacket);
                }
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Error during server broadcasting: " + e.getMessage());
                }
            }
        }).start();
    }
}
