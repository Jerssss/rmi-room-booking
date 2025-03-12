package client.admin.view;

import client.admin.controller.ReportGeneratorController;
import javafx.collections.ObservableList;
//import server.utility.LogReport;
//import server.utility.ReservationReport;

public class ReportGeneratorView {
    private final ReportGeneratorController controller;

    public ReportGeneratorView(ReportGeneratorController controller) {
        this.controller = controller;
    }

    public void displayActivityReport() {
//        controller.loadLogsData();
//        controller.loadReservationReports();
    }

//    public void setLogsData(ObservableList<LogReport> logs) {
//        System.out.println("\n===== Log Reports =====");
//        for (LogReport log : logs) {
//            System.out.println(log);
//        }
//    }

//    public void setReservationReports(ObservableList<ReservationReport> reservations) {
//        System.out.println("\n===== Reservation Reports =====");
//        for (ReservationReport reservation : reservations) {
//            System.out.println(reservation);
//        }
//    }
}
