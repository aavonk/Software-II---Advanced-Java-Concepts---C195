package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Screen;
import javafx.stage.Stage;
import models.Appointment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class BaseController {
   private static Stage stage;
   private static Parent pane;

    public static void setRootStage(Stage root){
        stage = root;
    }
    protected boolean isEmpty(List<TextField> textFields) {

        boolean empty = false;
        for (TextField item : textFields
        ) {
            if (item.getText().isBlank() || item.getText().isEmpty()) {
                empty = true;
                break;
            }
        }
        return empty;
    }

    protected void navigate(ActionEvent event, String viewPath, String title) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        pane = FXMLLoader.load(getClass().getResource(viewPath));
        stage.setTitle(title);
        stage.setScene(new Scene(pane));
        stage.show();

        // Try to center the stage in the device window
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
    }
    protected void navigate(String viewPath, String title) throws IOException {
        pane = FXMLLoader.load(getClass().getResource(viewPath));
        stage.setTitle(title);
        stage.setScene(new Scene(pane));
        stage.show();

        // Try to center the stage in the device window
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
    }

    protected void displayWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }


}
