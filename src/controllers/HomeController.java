package controllers;

import controllers.appointment.EditAppointmentController;
import controllers.customer.EditCustomerFormController;
import infrastructure.DbContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.Customer;
import services.AppointmentService;
import services.CustomerService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController extends BaseController implements Initializable {
    @FXML
    private Button reportsButton;

    @FXML
    public RadioButton weeklyAppointmentFilter;
    @FXML
    public RadioButton monthlyAppointmentFilter;
    @FXML
    private RadioButton allAppointmentsFilter;

    @FXML
    private MenuButton appointmentActionsDropdown;

    @FXML
    private TableColumn<Appointment, String> appointmentContactCol;

    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerIdCol;

    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionCol;

    @FXML
    private TableColumn<Appointment, String> appointmentDateCol;
    @FXML
    private TableColumn<Appointment, String> appointmentStartCol;
    @FXML
    private TableColumn<Appointment, String> appointmentEndCol;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeCol;
    @FXML
    private TableColumn<Appointment, Integer> appointmentUserIdCol;
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    public TableView<Customer> customerTable;
    @FXML
    private MenuButton customerActionsDropdown;
    public TableColumn<Customer, String> customerPostalCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol;
    @FXML
    private TableColumn<Customer, Integer> customerIdCol;
    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    /**
     * When set to true, an alert will appear informing the user of upcoming appointments.
     */
    private static boolean shouldDisplayUpcomingAppointments = false;

    private final CustomerService _customerService;
    private final AppointmentService _appointmentService;


    public HomeController() {
        Connection context = DbContext.getConnection();
        _appointmentService = new AppointmentService(context);
        _customerService = new CustomerService(context, _appointmentService);
    }

    public static void setShouldDisplayUpcomingAppointments(boolean shouldDisplayUpcomingAppointments) {
        HomeController.shouldDisplayUpcomingAppointments = shouldDisplayUpcomingAppointments;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeCustomerActions();
        this.initializeCustomerTable();
        this.initializeAppointmentsTable(_appointmentService.getAllAppointments());
        this.initializeAppointmentActions();

        reportsButton.setOnAction(this::onReportsButtonClick);
        allAppointmentsFilter.setOnAction(e -> {
            this.initializeAppointmentsTable(_appointmentService.getAllAppointments());
        });

        weeklyAppointmentFilter.setOnAction(e -> {
            this.initializeAppointmentsTable(_appointmentService.getWeeklyAppointments());
        });

        monthlyAppointmentFilter.setOnAction(e -> {
            this.initializeAppointmentsTable(_appointmentService.getMonthlyAppointments());
        });

        if (shouldDisplayUpcomingAppointments) {
            Appointment appointment = _appointmentService.getUpcomingAppointment();
            String message;
            if (appointment == null) {
                message = "You have no upcoming appointments scheduled.";
            } else {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M-dd-yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                String date = appointment.getStart().format(dateFormatter);
                String time = appointment.getStart().format(timeFormatter);
                message = String.format("You have an upcoming appointment. Please see below for appointment details: \n\nAppointment Id: %d\nDate: %s\nTime: %s", appointment.getId(), date, time);
            }
            this.displayInformation(message);
            shouldDisplayUpcomingAppointments = false;
        }
    }

    /**
     * Lambda_Expressions - true
     * Initializes the home controller action buttons with menu items. This method
     * uses Lambda expressions to set onAction event handlers on the menu items, that will
     * route them to their respective scenes when selected.
     */
    private void initializeCustomerActions() {
        MenuItem addCustomerItem = new MenuItem("Add customer");
        MenuItem removeCustomerItem = new MenuItem("Delete customer");
        MenuItem editCustomerItem = new MenuItem("Edit customer");

        addCustomerItem.setOnAction(event -> {
            try {
                this.navigate("/views/CreateCustomerForm.fxml", "New customer");
            } catch (IOException e) {
                System.out.println("Unable to navigate to CreateCustomerForm");
            }
        });

        editCustomerItem.setOnAction(this::onEditCustomer);
        removeCustomerItem.setOnAction(this::onRemoveCustomer);

        customerActionsDropdown.getItems().addAll(addCustomerItem, editCustomerItem, removeCustomerItem);
    }

    private void initializeAppointmentActions() {
        MenuItem addAptItem = new MenuItem("Add appointment");
        MenuItem editAptItem = new MenuItem("Edit appointment");
        MenuItem deleteAptItem = new MenuItem("Cancel appointment");

        addAptItem.setOnAction(event -> {
            try {
                this.navigate("/views/CreateAppointmentForm.fxml", "New appointment");
            } catch (IOException e) {
                System.out.println("Unable to navigate to CreateAppointmentForm");
            }
        });

        editAptItem.setOnAction(this::onEditAppointment);
        deleteAptItem.setOnAction(this::onRemoveAppointment);

        appointmentActionsDropdown.getItems().addAll(addAptItem, editAptItem, deleteAptItem);
    }


    /**
     * Lambda_Expressions - true
     * Populates the Customer Table View with customer data. This method uses lambda functions to access
     * nested data from within a class and set the data-format for Table cells
     */
    private void initializeCustomerTable() {
        ObservableList<Customer> customerList = FXCollections.observableList(_customerService.getAllCustomers());
        customerTable.setItems(customerList);

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customerAddressCol.setCellValueFactory(c -> {
            String address = c.getValue().getAddress();
            String division = c.getValue().getDivision().getDivision();
            String country = c.getValue().getDivision().getCountry().getName();
            return new SimpleStringProperty(String.format("%s, %s, %s", address, division, country));
        });
        customerPostalCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPostalCode()));
    }

    /**
     * Lambda_Expressions - true
     * Populates the Appointments Table View with appointment data. This method uses lambda functions to access
     * nested data from within a class and set the data-format for Table cells
     */
    private void initializeAppointmentsTable(List<Appointment> appointments) {
        ObservableList<Appointment> aptList = FXCollections.observableList(appointments);
        appointmentsTable.setItems(aptList);

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContact().getName()));
        appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDateCol.setCellValueFactory(c -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy");
            return new SimpleStringProperty(c.getValue().getStart().format(formatter));

        });
        appointmentStartCol.setCellValueFactory(c -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            return new SimpleStringProperty(c.getValue().getStart().format(formatter));

        });
        appointmentEndCol.setCellValueFactory(c -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            return new SimpleStringProperty(c.getValue().getEnd().format(formatter));

        });

        appointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    private void onReportsButtonClick(ActionEvent e) {
        try {
            this.navigate(e, "/views/ReportsView.fxml", "Reports");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void onRemoveCustomer(ActionEvent event) {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            this.displayWarning("Please select a customer to delete");
            return;
        }

        try {
            _customerService.deleteCustomer(customer);
            customerTable.getItems().remove(customer);
            // Re-fetch appointments
            appointmentsTable.setItems(FXCollections.observableList(_appointmentService.getAllAppointments()));
            this.displayInformation("Customer successfully deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.displayError("We ran into an error and can't delete the customer right now. Please try again later");
        }

    }

    private void onEditCustomer(ActionEvent event) {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            this.displayWarning("Please select a customer to edit");
            return;
        }
        try {
            EditCustomerFormController.setSelectedCustomer(customer);
            this.navigate("/views/EditCustomerForm.fxml", "Edit Customer");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void onEditAppointment(ActionEvent event) {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            this.displayWarning("Please select an appointment to edit");
            return;
        }

        try {
            EditAppointmentController.setSelectedAppointment(appointment);
            this.navigate("/views/EditAppointmentForm.fxml", "Edit Appointment");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void onRemoveAppointment(ActionEvent event) {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            this.displayWarning("Please select an appointment to delete");
            return;
        }

        try {
            _appointmentService.deleteAppointment(appointment.getId());
            appointmentsTable.getItems().remove(appointment);
            this.displayInformation(String.format("Appointment successfully deleted\nAppointment ID: %d \nType: %s", appointment.getId(), appointment.getType()));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.displayError("We ran into an error and can't delete the customer right now. Please try again later");
        }
    }
}
