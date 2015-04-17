package xss.gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import xss.Engine;
import xss.FileReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by popka on 17.04.15.
 */
public class MainController implements Engine.EngineListener{

    private Scene scene;
    Engine engine;

    int codeValue;

    public static final String LOW_LEVEL = "Поверхностный";
    public static final String MEDIUM_LEVEL = "Средний";
    public static final String HIGH_LEVEL = "Глубокий";
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void onClickStart() {
        TextField urlTextField = (TextField) scene.lookup("#url_textfield");
        String url = urlTextField.getText();
        if (!isCorrectUrl(url)) {
            return; // Объяснить, что урл введен неправильно!
        }

        RadioButton radioSite = (RadioButton) scene.lookup("#radioSite");

        NumberSpinner nBrowserSpinner = (NumberSpinner) scene.lookup("#nBrowsers");
        int nBrowsers = nBrowserSpinner.getValue();

        engine = new Engine(url, nBrowsers);
        engine.setEngineListener(this);

        if (radioSite.isSelected()) {
            engine.createMapOfSite();
        } else {

        }

        ComboBox<String> comboBox = (ComboBox<String>) scene.lookup("#chooseDepth");
        String value = comboBox.getValue();

        if (value.equals(LOW_LEVEL)) {
            codeValue = FileReader.LOW_LEVEL;
        }
        else if (value.equals(MEDIUM_LEVEL)) {
            codeValue = FileReader.MEDIUM_LEVEL;
        }
        else if (value.equals(HIGH_LEVEL)) {
            codeValue = FileReader.HIGH_LEVEL;
        }

    }

    private boolean isCorrectUrl(String url) {
        String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]/";
        Pattern urlPattern = Pattern.compile(regex);
        Matcher m = urlPattern.matcher(url);
        if (m.find()) return true;
        return false;
    }

    @Override
    public void onCreateMapEnds() {
        System.out.println("createMapsEnds");
        engine.prepareXSS(codeValue);
    }

    @Override
    public void onXssPrepareEnds() {
        System.out.println("XssPreparedEnds");
    }

}
