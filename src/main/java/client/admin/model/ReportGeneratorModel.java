package client.admin.model;

import shared.Log;
import util.JSONUtility;
import java.io.File;
import java.util.List;

public class ReportGeneratorModel {

    private static final File LOGS_FILE = new File("src/main/resources/data/logs.json");

    public List<Log> generateActivityReport() {
        try {
            System.out.println("[ReportGeneratorModel] Generating activity report...");
            return JSONUtility.loadLogs(LOGS_FILE);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to generate activity report: " + e.getMessage());
            return null;
        }
    }
}
