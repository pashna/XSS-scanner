package xss.gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    Button startBtn;
    Button cancelBtn;

    private final int ALL_SITE = 1;
    private final int ONE_PAGE = 2;

    private int codeValue;
    private boolean isAuthFlag = false;
    private int mode = ALL_SITE;

    private final int SEC = 30;
    private int nBrowsers;
    private String url;

    public static final String LOW_LEVEL = "Поверхностный";
    public static final String MEDIUM_LEVEL = "Средний";
    public static final String HIGH_LEVEL = "Глубокий";
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @FXML
    public void onClickStart() {
        TextField urlTextField = (TextField) scene.lookup("#url_textfield");
        url = urlTextField.getText();
        if (!isCorrectUrl(url)) {
            return; // Объяснить, что урл введен неправильно!
        }

        NumberSpinner nBrowserSpinner = (NumberSpinner) scene.lookup("#nBrowsers");
        nBrowsers = nBrowserSpinner.getValue();



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



        CheckBox isAuth = (CheckBox) scene.lookup("#checkAuth");
        if (isAuth.isSelected())
            isAuthFlag = true;



        RadioButton radioSite = (RadioButton) scene.lookup("#radioSite");
        if (radioSite.isSelected())
            mode = ALL_SITE;
        else
            mode = ONE_PAGE;


        startBtn = (Button) scene.lookup("#startBtn");
        cancelBtn = (Button) scene.lookup("#cancelBtn");
        startBtn.setDisable(true);

        Starter starter = new Starter(this);
        Thread thread = new Thread(starter);
        thread.start();

    }

    @FXML
    public void onCancelClick() {
        engine.stopAnalyse();
        showStartBtn();

    }

    private void hideStartBtn() {
        startBtn.setDisable(false);
        startBtn.setVisible(false);
        cancelBtn.setVisible(true);
    }

    private void showStartBtn() {
        cancelBtn.setVisible(false);
        startBtn.setVisible(true);
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
        System.out.println("AnalyseEnds");
        showStartBtn();
        engine.stopAnalyse();
    }

    private class Starter implements Runnable {
        Engine.EngineListener listener;

        public Starter(Engine.EngineListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {

            if (isAuthFlag)
                engine = new Engine(url, nBrowsers, SEC);
            else
                engine = new Engine(url, nBrowsers);

            engine.setEngineListener(listener);

            if (mode == ALL_SITE)
                engine.createMapOfSite();

            hideStartBtn();

        }
    }

}
