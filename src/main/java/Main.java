
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

    static private String url = "http://www.lic1.vrn.ru/";
    static private int nBrowser = 4;

    public static void main(String[] args) {

        Engine engine = new Engine(url, nBrowser);
        engine.createMapOfSite();
        //String uuu = "http://www.lic1.vrn.ru/zavuch/kopilka-rodit.htm#%D0%9A%D0%B0%D0%BA_%D0%BE%D1%82%D0%BD%D0%BE%D1%81%D0%B8%D1%82%D1%8C%D1%81%D1%8F_%D0%BA_%D0%BE%D1%82%D0%BC%D0%B5%D1%82%D0%BA%D0%B0%D0%BC_%D1%80%D0%B5%D0%B1%D1%91%D0%BD%D0%BA%D0%B0.";
        //cutArgAndHash(uuu);

    }

    static private void cutArgAndHash(String url) {
        int placeOfArg = url.indexOf("?");
        if (placeOfArg < 0) {
            int placeOfHash = url.indexOf("#");
            if (placeOfHash > 0) {
                url = url.substring(0, placeOfHash);
            }

        } else {
            url = url.substring(0, placeOfArg);
        }
    }

}
