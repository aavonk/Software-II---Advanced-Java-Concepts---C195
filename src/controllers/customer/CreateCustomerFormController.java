package controllers.customer;

import infrastructure.DbContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import models.Country;
import models.Customer;
import services.AppointmentService;
import services.CustomerService;
import services.LocationService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller responsible for rendering the UI and handling interactions for Customer Creation
 */
public class CreateCustomerFormController extends CustomerBaseController implements Initializable {


    public CreateCustomerFormController() {
        Connection context = DbContext.getConnection();
        this._locationService = new LocationService(context);
        this._customerService = new CustomerService(context, new AppointmentService(context));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Country> allCountries = _locationService.getAllCountries();

        this.initializeComboBoxes(allCountries, allCountries.get(0).getCountryId());
        countryComboBox.setPromptText("Select a country");
        divisionComboBox.setPromptText("Select a division");
    }


    /**
     * Event handler called when the form is submitted. This method validates that the values are valid
     * and that the form is able to save a customer to the database. If saving is not successful,
     * it displays helpful error messages.
     */
    @FXML
    private void onSubmit(ActionEvent event) {
        if (this.isFormValid()) {
            Customer customer = new Customer(nameInput.getText(), addressInput.getText(), postalCodeInput.getText(), phoneInput.getText());
            customer.setDivision(divisionComboBox.getValue());

            try {
                _customerService.createCustomer(customer);
                this.navigate(event, "/views/Home.fxml", "Home");
            } catch (SQLException | IOException e) {
                this.displayError("We were unable to save the customer at this time. Please try again later");
                System.out.println(e.getMessage());
            }
        }
    }




}
