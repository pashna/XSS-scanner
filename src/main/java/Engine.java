import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Set;

/**
 * Created by popka on 22.03.15.
 */
public class Engine {
    private BrowserPool browserPool;
    private int nBrowser;
    private String url;


    public Engine(String url, int nBrowser) {
        this.url = url;
        browserPool = new BrowserPool(nBrowser);
    }

    public Engine(String url, int nBrowser, int sec) {
        this.url = url;
        this.nBrowser = nBrowser;
        auth(sec);
    }

    private void auth(int sec) {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.navigate().to(url); // Открываем страничку

        try {
            Thread.sleep(sec * 1000); // Ждем, пока пользователь авторизуется
        } catch (InterruptedException e) {}

        Set<Cookie> cookieSet = webDriver.manage().getCookies(); // Забираем куки
        System.out.println(cookieSet);

        webDriver.close(); // Закрываем браузер
        webDriver.quit();

        browserPool = new BrowserPool(nBrowser);
        browserPool.setCookie(url, cookieSet);
    }

    public void open() {

    }
}
