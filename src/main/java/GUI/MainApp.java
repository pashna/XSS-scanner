package GUI;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by popka on 17.04.15.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/mainWindow.fxml";
        String cssFile = "/fxml/number_spinner.css";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(root);

        stage.setTitle("XSS-Scanner");
        stage.setScene(scene);
        stage.setResizable(false);

        String path = MainApp.class.getResource(cssFile).toExternalForm();
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
