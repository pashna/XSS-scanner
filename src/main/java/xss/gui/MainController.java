package xss.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import xss.Engine;
import xss.FileReader;
import xss.LinkContainer.XssStruct;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by popka on 17.04.15.
 */
public class MainController implements Engine.EngineListener{

    private Scene scene;
    Engine engine;
    Timeline timer;

    int xssCount = 0;

    Button startBtn;
    Button cancelBtn;
    Label timerLabel;

    AnchorPane stateLayout;
    Label stateLabel;
    Label xssCountLabel;
    Label directoryError;

    private final int ALL_SITE = 1;
    private final int ONE_PAGE = 2;

    private int levelOfDepth;
    private boolean isAuthFlag = false;
    private int mode = ALL_SITE;

    private final int SEC = 30;
    private int nBrowsers;
    private String url;
    private File selectedDirectory = null;

    public static final String LOW_LEVEL = "Поверхностный";
    public static final String MEDIUM_LEVEL = "Средний";
    public static final String HIGH_LEVEL = "Глубокий";

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void initGraphic() {
        stateLayout = (AnchorPane) scene.lookup("#stateLayout");
        stateLabel = (Label) scene.lookup("#stateLabel");
        xssCountLabel = (Label) scene.lookup("#xssCountLabel");
        startBtn = (Button) scene.lookup("#startBtn");
        cancelBtn = (Button) scene.lookup("#cancelBtn");
        timerLabel = (Label) scene.lookup("#timer");
        directoryError = (Label) scene.lookup("#directoryError");
    }


    @FXML
    public void onClickStart() {
        xssCount = 0;
        timerLabel.setText("");

        TextField urlTextField = (TextField) scene.lookup("#url_textfield");
        url = urlTextField.getText();

        Label urlErrorLabel = (Label) scene.lookup("#urlError");
        if (isCorrectUrl(url)) { //Коректный ли урл
            if (!url.endsWith("/")) url += "/"; // Если не оканчивается на "/", дописать его
            urlErrorLabel.setVisible(false);
        } else {
            urlErrorLabel.setVisible(true);
            urlTextField.requestFocus();
            return; // Объяснить, что урл введен неправильно!
        }

        if (selectedDirectory == null) {
            directoryError.setVisible(true);
            return;
        }

        NumberSpinner nBrowserSpinner = (NumberSpinner) scene.lookup("#nBrowsers");
        nBrowsers = nBrowserSpinner.getValue();


        ComboBox<String> comboBox = (ComboBox<String>) scene.lookup("#chooseDepth");
        String value = comboBox.getValue();
        if (value.equals(LOW_LEVEL)) {
            levelOfDepth = FileReader.LOW_LEVEL;
        }
        else if (value.equals(MEDIUM_LEVEL)) {
            levelOfDepth = FileReader.MEDIUM_LEVEL;
        }
        else if (value.equals(HIGH_LEVEL)) {
            levelOfDepth = FileReader.HIGH_LEVEL;
        }



        CheckBox isAuth = (CheckBox) scene.lookup("#checkAuth");
        if (isAuth.isSelected())
            isAuthFlag = true;



        RadioButton radioSite = (RadioButton) scene.lookup("#radioSite");
        if (radioSite.isSelected())
            mode = ALL_SITE;
        else
            mode = ONE_PAGE;


        startBtn.setDisable(true);
        cancelBtn.setDisable(true);

        Starter starter = new Starter(this);
        Thread thread = new Thread(starter);
        thread.start();

        hideMainLayout();

    }

    @FXML
    public void onCancelClick() {
        engine.stopAnalyse();
        showMainLayout();

    }

    @FXML
    public void selectDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите, куда сохранить отчет");
        selectedDirectory = directoryChooser.showDialog(null);

        if(selectedDirectory == null){
            directoryError.setVisible(true);
        }else{
            directoryError.setVisible(false);
        }

    }

    private void hideMainLayout() {
        startBtn.setDisable(false);
        startBtn.setVisible(false);
        cancelBtn.setVisible(true);
        stateLayout.setVisible(true);

    }

    private void showMainLayout() {
        cancelBtn.setVisible(false);
        startBtn.setVisible(true);
        stateLayout.setVisible(false);

        timer.stop(); //
    }

    private boolean isCorrectUrl(String url) {
        String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern urlPattern = Pattern.compile(regex);
        Matcher m = urlPattern.matcher(url);
        if (m.find()) return true;
        return false;
    }

    private void setupTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            private int time = 0;
            @Override
            public void handle(ActionEvent event) {
                String timeString = String.format("%02d:%02d:%02d",
                        TimeUnit.SECONDS.toHours(time),
                        TimeUnit.SECONDS.toMinutes(time),
                        TimeUnit.SECONDS.toSeconds(time) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(time))
                );
                timerLabel.setText(timeString);
                time++;
                //
                //НЕ ЗАБЫТЬ ОСТАНОВИТЬ ТАЙМЕР!!!
                //
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

    }

    @Override
    public void onCreateMapEnds() {
        System.out.println("createMapsEnds");

        Platform.runLater(new Runnable() { // Выполняем в UI потоке
            @Override
            public void run() {
                switchToXSSMode();
            }
        });

        engine.prepareXSS(levelOfDepth);
    }

    @Override
    public void onXssPrepareEnds() {
        System.out.println("AnalyseEnds");
        engine.generateReport(selectedDirectory, timerLabel.getText());
        engine.stopAnalyse();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showMainLayout();
            }
        });
    }

    @Override
    public void onBrowsersReady() {
        setupTimer();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stateLabel.setText("Собираем карту сайта...");
                cancelBtn.setDisable(false);
            }
        });
    }

    @Override
    public void onXssAdded(XssStruct xssStruct) {
        System.out.println("МЫ НАШЛИ XSS!!! " + xssStruct + "  по урлу " + url);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                xssCount++;
                xssCountLabel.setText("Уже найдено:  " + xssCount);
            }
        });
    }

    private void switchToXSSMode() {
        xssCountLabel.setVisible(true);
        stateLabel.setText("Ищем XSS...");
    }


    private class Starter implements Runnable {
        Engine.EngineListener listener;

        public Starter(Engine.EngineListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {

            if (isAuthFlag)
                engine = new Engine(url, nBrowsers, listener, SEC);
            else
                engine = new Engine(url, nBrowsers, listener);

            if (mode == ALL_SITE)
                engine.createMapOfSite();
            else {
                engine.addUrlToAnalyse(url);
                onCreateMapEnds();
            }

            //hideMainLayout();

        }
    }

}
