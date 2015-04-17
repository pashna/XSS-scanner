package xss.Tasks;

/**
 * Created by popka on 21.03.15.
 */
public class Opener extends BrowserRunnable {
    private String url="";

    public Opener(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        getWebDriver().navigate().to(url);
    }

}
