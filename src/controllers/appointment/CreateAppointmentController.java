package controllers.appointment;

import infrastructure.DbContext;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.StringConverter;
import models.Appointment;
import models.Contact;
import services.AppointmentService;
import services.ContactService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ResourceBundle;

/**
 * Controller responsible for rendering the UI and handling interactions for Appointment Creation
 */
public class CreateAppointmentController extends AppointmentBaseController implements Initializable {

    public CreateAppointmentController() {
        Connection conn = DbContext.getConnection();
        _contactService = new ContactService(conn);
        _appointmentService = new AppointmentService(conn);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
        contactComboBox.setItems(FXCollections.observableList(_contactService.getAllContacts()));
        this.initContactComboBoxConverter();
        this.initTimeComboBoxes();

    }

    /**
     * Event handler called when the form is submitted. This method validates that the values are valid
     * and that the form is able to save an appointment to the database. If saving is not successful,
     * it displays helpful error messages.
     */
    @FXML
    void onSubmit(ActionEvent event) throws IOException {
        if (this.isFormValid()) {
            LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), timeFormatter);

            Appointment appointment = new Appointment(titleInput.getText(), descriptionInput.getText(), locationInput.getText(),
                    typeInput.getText(), startTime, endTime, datePicker.getValue(), contactComboBox.getValue(),
                    Integer.parseInt(userIdInput.getText()), Integer.parseInt(customerIdInput.getText()));

            try {
                if (_appointmentService.hasConflicts(appointment.getStart(), appointment.getEnd(), 0)) {
                    this.displayError("There is already a scheduled appointment at this time. Please select another time");
                    return;
                }

                _appointmentService.createAppointment(appointment);
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
