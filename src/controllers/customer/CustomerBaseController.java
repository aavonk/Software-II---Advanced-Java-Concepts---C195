package controllers.customer;

import controllers.BaseController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import models.Country;
import models.Division;
import services.CustomerService;
import services.LocationService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Base Customer controller used for initializing UI elements that are shared between
 * the Create Customer Form and the Edit Customer Form.
 */
public class CustomerBaseController extends BaseController {
    @FXML
    protected TextField addressInput;
    @FXML
    protected Button cancelButton;
    @FXML
    protected ComboBox<Country> countryComboBox;
    @FXML
    protected ComboBox<Division> divisionComboBox;
    @FXML
    protected TextField idInput;
    @FXML
    protected TextField nameInput;
    @FXML
    protected TextField phoneInput;
    @FXML
    protected TextField postalCodeInput;
    @FXML
    protected Button submitButton;

    protected LocationService _locationService;
    protected CustomerService _customerService;

    @FXML
    private void onCancel(ActionEvent event) throws IOException {
        this.navigate(event, "/views/Home.fxml", "Home");
    }

    protected void onCountrySelected(ActionEvent event) {
        Country selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
        List<Division> divisions = _locationService.getDivisionsForCountry(selectedCountry.getCountryId());
        divisionComboBox.getSelectionModel().clearSelection();
        divisionComboBox.getSelectionModel().select(null);
//        divisionComboBox.getSelectionModel().
//        divisionComboBox.setValue(divisions.get(0));
        divisionComboBox.setPromptText("Select a division");
        divisionComboBox.setItems(FXCollections.observableList(divisions));
    }

    protected void initializeComboBoxes(List<Country> allCountries, int initialCountryId){

        // Initialize the country Combo Box with country data
        countryComboBox.setOnAction(this::onCountrySelected);
        countryComboBox.getItems().addAll(allCountries);
        countryComboBox.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country country) {
                return country.getName();
            }

            @Override
            public Country fromString(String s) {
                return null;
            }
        });

        // Initialize the Division Country box with default data with
        List<Division> divisions = _locationService.getDivisionsForCountry(initialCountryId);
        divisionComboBox.getItems().addAll(divisions);
        divisionComboBox.setConverter(new StringConverter<Division>() {
            @Override
            public String toString(Division division) {
                if (division != null) {
                    return division.getDivision();
                };
                return "";
            }

            @Override
            public Division fromString(String s) {
                return null;
            }
        });
    }

    protected boolean isFormValid() {
        Division division = divisionComboBox.getValue();
        Country country = countryComboBox.getValue();
        if (country == null || division == null || this.isEmpty(Arrays.asList(nameInput, phoneInput, postalCodeInput, addressInput))) {
            this.displayWarning("Please make sure all fields are filled out");
            return false;
        }
        return true;
    }
}
