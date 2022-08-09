package controllers;

import infrastructure.DbContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.AppointmentTypeItem;
import models.DivisionReportItem;
import services.AppointmentService;
import services.LocationService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController extends BaseController implements Initializable {


    @FXML
    private Button backButton;

    // Division report fields

    @FXML
    private TableColumn<DivisionReportItem, Integer> divisionAmountCol;

    @FXML
    private TableColumn<DivisionReportItem, String> divisionCol;

    @FXML
    private TableView<DivisionReportItem> divisionTable;


    // Appointment types per month fields
    @FXML
    private TableView<AppointmentTypeItem> typeByMonthTable;
    @FXML
    private TableColumn<AppointmentTypeItem, String> monthCol;
    @FXML
    private TableColumn<AppointmentTypeItem, String> typeByMonthTypeCol;
    @FXML
    private TableColumn<AppointmentTypeItem, Integer> amountCol;


    // Contact Schedule table fields
    @FXML
    private TableView<Appointment> contactScheduleTable;
    @FXML
    private TableColumn<Appointment, String> contactNameCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;
    @FXML
    private TableColumn<Appointment, String> dateCol;
    @FXML
    private TableColumn<Appointment, String> descriptionCol;
    @FXML
    private TableColumn<Appointment, String> endTimeCol;

    @FXML
    private TableColumn<Appointment, String> startTimeCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;


    private final AppointmentService _appointmentService;
    private final LocationService _locationService;

    public ReportsController() {
        Connection conn = DbContext.getConnection();
        _appointmentService = new AppointmentService(conn);
        _locationService = new LocationService(conn);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeContactScheduleTable(_appointmentService.getAllAppointments());
        this.initializeAppointmentsByMonthTable(_appointmentService.getAppointmentTypesByMonth());
        this.initializeDivisionReportTable(_locationService.getDivisionReportItems());
    }

    @FXML
    public void onCancel(ActionEvent event) throws IOException {
        this.navigate(event, "/views/Home.fxml", "Home");
    }

    /**
     * @Lambda_Expressions - true
     * Populates the Appointments Table View with appointment data. This method uses lambda functions to access
     * nested data from within a class and set the data-format for Table cells
     */
    private void initializeContactScheduleTable(List<Appointment> appointments) {
        ObservableList<Appointment> aptList = FXCollections.observableList(appointments);
        contactScheduleTable.setItems(aptList);

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContact().getName()));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateCol.setCellValueFactory(c -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yyyy");
            return new SimpleStringProperty(dateFormat.format(c.getValue().getStart()));
        });
        startTimeCol.setCellValueFactory(c -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            return new SimpleStringProperty(dateFormat.format(c.getValue().getStart()));
        });
        endTimeCol.setCellValueFactory(c -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            return new SimpleStringProperty(dateFormat.format(c.getValue().getEnd()));
        });

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }

    private void initializeAppointmentsByMonthTable(List<AppointmentTypeItem> items) {
        typeByMonthTable.setItems(FXCollections.observableList(items));
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        typeByMonthTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

    }

    private void initializeDivisionReportTable(List<DivisionReportItem> items) {
        divisionTable.setItems(FXCollections.observableList(items));
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        divisionAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }
}
