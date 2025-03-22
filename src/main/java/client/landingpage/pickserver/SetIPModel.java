package client.landingpage.pickserver;

public class SetIPModel {
    private String serverIP;

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public boolean isValidIP(String ip) {
        // Basic IP validation (e.g., check if it matches the IPv4 pattern)
        return ip.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }
}