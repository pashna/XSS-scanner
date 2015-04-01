
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by popka on 16.03.15.
 */
public class Main {

    static private String url = "http://obuv-umka.ru/";
    static private int nBrowser = 4;

    public static void main(String[] args) {

        Engine engine = new Engine(url, nBrowser);
        engine.createMapOfSite();

    }

}
