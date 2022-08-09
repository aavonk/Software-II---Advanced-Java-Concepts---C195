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
 * Controller responsible for rendering the UI and handling interactions for Customer Editing
 */
public class EditCustomerFormController extends CustomerBaseController implements Initializable {

    private static Customer selectedCustomer;

    public EditCustomerFormController(){
        Connection context = DbContext.getConnection();
        this._locationService = new LocationService(context);
        this._customerService = new CustomerService(context, new AppointmentService(context));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idInput.setText(String.valueOf(selectedCustomer.getId()));
        nameInput.setText(selectedCustomer.getCustomerName());
        phoneInput.setText(selectedCustomer.getPhoneNumber());
        addressInput.setText(selectedCustomer.getAddress());
        postalCodeInput.setText(selectedCustomer.getPostalCode());


        Country country = _locationService.getCountryById(selectedCustomer.getDivision().getCountryId());

        this.initializeComboBoxes(_locationService.getAllCountries(), country.getCountryId());
        countryComboBox.setValue(country);
        divisionComboBox.setValue(selectedCustomer.getDivision());

    }

    public static void setSelectedCustomer(Customer customer) {
        selectedCustomer = customer;
    }

    /**
     * Event handler called when the form is submitted. This method validates that the values are valid
     * and that the form is able to save a customers updates to the database. If saving is not successful,
     * it displays helpful error messages.
     */
    @FXML
    public void onSubmit(ActionEvent event) {
        if (this.isFormValid()) {
            Customer customer = new Customer(nameInput.getText(), addressInput.getText(), postalCodeInput.getText(), phoneInput.getText());
            String id = idInput.getText();
            customer.setId(Integer.parseInt(idInput.getText()));
            customer.setDivision(divisionComboBox.getValue());
            try {
                this._customerService.updateCustomer(customer);
                this.navigate(event, "/views/Home.fxml", "Home");
            } catch (SQLException | IOException e) {
                this.displayError("We were unable to update the customer at this time. Please try again later");
                System.out.println(e.getMessage());
            }
        }
    }

}
