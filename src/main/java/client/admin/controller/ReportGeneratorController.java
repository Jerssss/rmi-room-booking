package client.admin.controller;

import client.admin.model.ReportGeneratorModel;
import client.admin.view.ReportGeneratorView;
import javafx.collections.ObservableList;
//import server.utility.LogReport;
//import server.utility.ReservationReport;

public class ReportGeneratorController {

    private final ReportGeneratorModel model;
    private final ReportGeneratorView view;

    public ReportGeneratorController(ReportGeneratorView view) {
        this.model = new ReportGeneratorModel();
        this.view = view;
    }

//    public void loadLogsData() {
//        ObservableList<LogReport> logs = model.loadLogsFromServer();
//        view.setLogsData(logs);
//    }

//    public void loadReservationReports() {
//        ObservableList<ReservationReport> reservations = model.loadReservationsFromServer();
//        System.out.println("Reservations loaded into View: " + reservations.size());
//        view.setReservationReports(reservations);
//    }
//
//    public void searchReports(String query) {
//        ObservableList<LogReport> logs = model.searchLogs(query);
//        ObservableList<ReservationReport> reservations = model.searchReservations(query);
//        view.setLogsData(logs);
//        view.setReservationReports(reservations);
//    }
}