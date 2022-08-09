package controllers;

import infrastructure.DbContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.User;
import services.AuthenticationService;
import services.Logger;

import java.net.URL;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/*
    The Login Controller is responsible for populating the Login Form with
    localization and internationalization data. Depending on where the user
    is located, the form will be translated into English or French.
 */
public class LoginController extends BaseController implements Initializable {
    private AuthenticationService _authService;
    public Text locationLabel;
    private ResourceBundle bundle;
    @FXML
    private Button loginInButton;

    @FXML
    private TextField passwordInput;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField userIdInput;

    @FXML
    private Label userIdLabel;
    @FXML
    private Text welcomeMessage;

    public LoginController() {
        _authService = new AuthenticationService(DbContext.getConnection());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        bundle = ResourceBundle.getBundle("util/Login", Locale.getDefault());

        locationLabel.setText(String.format("Location: %s Zone", ZoneId.systemDefault().getDisplayName(TextStyle.FULL, Locale.getDefault())));

        welcomeMessage.setText(bundle.getString("WelcomeMessage"));
        userIdInput.setPromptText(bundle.getString("UserID"));
        userIdLabel.setText(bundle.getString("UserID"));

        passwordInput.setPromptText(bundle.getString("Password"));
        passwordLabel.setText(bundle.getString("Password"));
        loginInButton.setText(bundle.getString("ButtonText"));
    }

    @FXML
    void onSubmit(ActionEvent event) {
       try {
           // TODO: Log attempted sign-in
           // Validate that the text fields aren't empty
           if (isEmpty(Arrays.asList(userIdInput, passwordInput))) {
               Logger.logAttemptedLogin(userIdInput.getText(), false);
               displayError(bundle.getString("EmptyInputsErrorMessage"));
               return;
           }
           int userId = Integer.parseInt(userIdInput.getText());
           User user = _authService.getUser(userId, passwordInput.getText());

           if (user == null) {
               Logger.logAttemptedLogin(userIdInput.getText(), false);
               displayError(bundle.getString("FailedAuthErrorMessage"));
               return;
           }
           Logger.logAttemptedLogin(userIdInput.getText(), true);
           HomeController.setShouldDisplayUpcomingAppointments(true);
           this.navigate(event, "/views/Home.fxml", "Home");
       } catch (NumberFormatException format) {
           displayError(bundle.getString("InvalidIdErrorMessage"));
           Logger.logAttemptedLogin(userIdInput.getText(), false);
       } catch (Exception e) {
           displayError(bundle.getString("FailedAuthErrorMessage"));
           Logger.logAttemptedLogin(userIdInput.getText(), false);
           System.out.println(e.getMessage());
       }
    }

    @Override
    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("ErrorTitle"));
        alert.setHeaderText(bundle.getString("ErrorHeader"));
        alert.setContentText(message);
        alert.showAndWait();
    }

}
