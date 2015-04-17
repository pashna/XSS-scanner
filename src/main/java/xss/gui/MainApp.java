package xss.gui;

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

    final String fxmlFile = "fxml/mainWindow.fxml";
    final String cssFile = "fxml/style.css";

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        //ClassLoader loader1 = getClass().getClassLoader();
        //InputStream stream = getClass().getClassLoader().getResourceAsStream(fxmlFile);
        Parent root = (Parent)loader.load(getClass().getClassLoader().getResourceAsStream(fxmlFile));

        //Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
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
