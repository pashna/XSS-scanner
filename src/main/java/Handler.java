import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by popka on 21.03.15.
 */
public class Handler extends BrowserRunnable {
    private String url="";

    public Handler(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        getWebDriver().navigate().to(url);
    }

}
