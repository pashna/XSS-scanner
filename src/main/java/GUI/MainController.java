package GUI;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by popka on 17.04.15.
 */
public class MainController {

    private Scene scene;
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void onClickStart() {
        TextField urlTextField = (TextField) scene.lookup("#url_textfield");
        if (!isCorrectUrl(urlTextField.getText())) {
            return; // Объяснить, что урл введен неправильно!
        }

        RadioButton radioSite = (RadioButton) scene.lookup("#radioSite");
        if (radioSite.isSelected()) {

        } else {

        }

        NumberSpinner nBrowserSpinner = (NumberSpinner) scene.lookup("#nBrowsers");
        int nBrowsers = nBrowserSpinner.getValue();

        engine = new Engine(url, nBrowser);
        engine.setEngineListener(this);
        engine.createMapOfSite();





    }

    private boolean isCorrectUrl(String url) {
        String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]/";
        Pattern urlPattern = Pattern.compile(regex);
        Matcher m = urlPattern.matcher(url);
        if (m.find()) return true;
        return false;
    }



}
