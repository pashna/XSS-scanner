import org.openqa.selenium.WebDriver;

/**
 * Created by popka on 21.03.15.
 */
public abstract class BrowserRunnable implements Runnable {
    private WebDriver webDriver;

    public void run(WebDriver webDriver) {
        this.webDriver = webDriver;
        run();
    }


    public WebDriver getWebDriver() {
        return webDriver;
    }
}
