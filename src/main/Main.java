package main;

import controllers.BaseController;
import infrastructure.DbContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        stage.setTitle("Log In");
        stage.setScene(new Scene(root));
        stage.show();

        BaseController.setRootStage(stage);
    }

    public static void main(String[] args) {
        DbContext.openConnection();
        launch(args);
        DbContext.closeConnection();
    }
}
