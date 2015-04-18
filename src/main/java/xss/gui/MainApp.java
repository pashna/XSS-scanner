package xss.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by popka on 17.04.15.
 */
public class MainApp extends Application {

    final String fxmlFile = "fxml/mainWindow.fxml";
    final String cssFile = "fxml/style.css";

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource(fxmlFile));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("XSS-Scanner");
        stage.setScene(scene);
        stage.setResizable(false);

        String path = getClass().getClassLoader().getResource(cssFile).toExternalForm();
        scene.getStylesheets().add(path);

        MainController myController = (MainController)loader.getController();
        myController.setScene(scene);

        stage.show();

    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @FXML
    public void onStart() {
        System.out.println("START");
    }
}
