package controllers.appointment;

import infrastructure.DbContext;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import models.Appointment;
import services.AppointmentService;
import services.ContactService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller responsible for rendering the UI and handling interactions for Appointment Editing
 */
public class EditAppointmentController extends AppointmentBaseController implements Initializable {

    private static Appointment selectedAppointment;

    public EditAppointmentController() {
        Connection conn = DbContext.getConnection();
        _appointmentService = new AppointmentService(conn);
        _contactService = new ContactService(conn);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idInput.setText(String.valueOf(selectedAppointment.getId()));
        titleInput.setText(selectedAppointment.getTitle());
        descriptionInput.setText(selectedAppointment.getDescription());
        locationInput.setText(selectedAppointment.getLocation());
        typeInput.setText(selectedAppointment.getType());

        contactComboBox.setItems(FXCollections.observableList(_contactService.getAllContacts()));
        contactComboBox.setValue(selectedAppointment.getContact());
        this.initContactComboBoxConverter();

        // Initialize start & end time combo boxes
        this.initTimeComboBoxes();
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedAppointment.getStart());

        LocalTime start = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        startTimeComboBox.setValue(start.format(timeFormatter));

        cal.setTime(selectedAppointment.getEnd());
        LocalTime end = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        endTimeComboBox.setValue(end.format(timeFormatter));

        // Initialize Date combo box
        cal.setTime(selectedAppointment.getStart());
        datePicker.setValue(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));

        // Initialize customer & user ID
        customerIdInput.setText(String.valueOf(selectedAppointment.getCustomerId()));
        userIdInput.setText(String.valueOf(selectedAppointment.getUserId()));
    }

    public static void setSelectedAppointment(Appointment appointment) {
        selectedAppointment = appointment;
    }

    /**
     * Event handler called when the form is submitted. This method validates that the values are valid
     * and that the form is able to save an appointment's values to the database. If the update is not successful,
     * it displays helpful error messages.
     */
    @FXML
    public void onSubmit(ActionEvent event) throws IOException {
        if (this.isFormValid()) {
            LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), timeFormatter);

            Appointment appointment = new Appointment(titleInput.getText(), descriptionInput.getText(), locationInput.getText(),
                    typeInput.getText(), startTime, endTime, datePicker.getValue(), contactComboBox.getValue(),
                    Integer.parseInt(userIdInput.getText()), Integer.parseInt(customerIdInput.getText()));

            appointment.setId(selectedAppointment.getId());

            try {
                if (_appointmentService.hasConflicts(appointment.getStart(), appointment.getEnd(), appointment.getId())) {
                    this.displayError("There is already a scheduled appointment at this time. Please select another time");
                    return;
                }
                _appointmentService.updateAppointment(appointment);
                this.navigate(event, "/views/Home.fxml", "Home");
            } catch (SQLException e) {
                String message = e.getMessage();
                System.out.println(message);
                if (message.contains("foreign key constraint")) {
                    this.displayError("Please make sure Customer ID and User ID are valid.");
                } else {
                    this.displayError("There was an error saving your appointment. Please try again later");
                }
            }
        }
    }
}
