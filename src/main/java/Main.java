import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


/**
 * Created by popka on 16.03.15.
 */
public class Main {
    public static void main(String[] args) {
        BrowserPool browserPool = new BrowserPool(4);
        String[] urls = {"yandex.ru", "mail.ru", "google.ru", "yahoo.com", "rambler.ru", "pikabu.ru", "wikipedia.ru",
            "dps.ru", "gibdd.ru"};
        for (int i=0; i<urls.length*4; i++) {
            browserPool.execute(new Handler("http://www." + urls[i%urls.length]));
        }
    }
}
