package controllers.appointment;

import controllers.BaseController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import models.Contact;
import services.AppointmentService;
import services.ContactService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Base Appointment controller used for initializing UI elements that are shared between
 * the Create Appointment Form and the Edit Appointment Form.
 */
public abstract class AppointmentBaseController extends BaseController {
    protected final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    @FXML
    protected Button cancelButton;

    @FXML
    protected ComboBox<Contact> contactComboBox;

    @FXML
    protected TextField descriptionInput;

    @FXML
    protected TextField idInput;

    @FXML
    protected TextField locationInput;

    @FXML
    public DatePicker datePicker;
    @FXML
    public TextField customerIdInput;
    @FXML
    protected ComboBox<String> startTimeComboBox;
    @FXML
    protected ComboBox<String> endTimeComboBox;

    @FXML
    protected Button submitButton;

    @FXML
    protected TextField titleInput;

    @FXML
    protected TextField typeInput;

    @FXML
    protected TextField userIdInput;

    protected ContactService _contactService;
    protected AppointmentService _appointmentService;

    @FXML
    private void onCancel(ActionEvent event) throws IOException {
        this.navigate(event, "/views/Home.fxml", "Home");
    }

    protected boolean isFormValid() {
        Contact contact = contactComboBox.getValue();
        String start = startTimeComboBox.getValue();
        String end = endTimeComboBox.getValue();
        LocalDate date = datePicker.getValue();
        boolean allFieldsHaveValues = contact != null && start != null && !start.isBlank() && end != null && !end.isBlank() && date != null && !isEmpty(Arrays.asList(titleInput, descriptionInput, locationInput, typeInput, customerIdInput, userIdInput));

        if (!allFieldsHaveValues) {
            this.displayError("All fields are required");
            return false;
        }

        try {
            Integer.parseInt(customerIdInput.getText());
            Integer.parseInt(userIdInput.getText());
        } catch (NumberFormatException e) {
            // If unable to parse ID's into ints, it is not valid
            this.displayError("User ID and Customer ID must be an integer");
            return false;
        }

        // Make sure that start time is before end time
        LocalTime startTime = LocalTime.parse(start, timeFormatter);
        LocalTime endTime = LocalTime.parse(end, timeFormatter);
        if (startTime.isAfter(endTime) || start.equals(end)) {
            this.displayError("The appointments end time must be after the start time");
            return false;
        }

        return true;
    }

    protected void initContactComboBoxConverter(){
        contactComboBox.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                if (contact != null) {
                    return contact.getName();
                }
                return "";
            }

            @Override
            public Contact fromString(String s) {
                return null;
            }
        });
    }

    protected void initTimeComboBoxes(){
        // Initialize the start & end dates with a list of times that the office is open.
        // Office hours are 8:00 am to 10:00pm
        List<String> times = new ArrayList<>();
        LocalTime time = LocalTime.of(8, 0);

        // Add times in 15 minute increments from when the office opens to when it closes.
        while (!time.equals(LocalTime.of(22, 15))) {
            times.add(time.format(timeFormatter));
            time = time.plusMinutes(15);
        }

        // Remove the last item from the start times as an appointment cannot be scheduled at the time the office closes.
        startTimeComboBox.setItems(FXCollections.observableList(times.subList(0, times.size() - 1)));
        // Remove the first item from the end times as an appointment cannot end at the time the office opens.
        endTimeComboBox.setItems(FXCollections.observableList(times.subList(1, times.size())));
    }


}
