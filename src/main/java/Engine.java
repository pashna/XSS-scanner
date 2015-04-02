import LinkContainer.LinkContainer;
import Tasks.LinkFinder;
import Tasks.Opener;
import Tasks.XssAnalyser;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Set;

/**
 * Created by popka on 22.03.15.
 */
public class Engine implements LinkContainer.LinkContainerCallback {
    private BrowserPool browserPool;
    private int nBrowser;
    private String url = "";
    private LinkContainer linkContainer;


    private EngineListener engineListener; // Может убрать в один интерфейс

    public void setEngineListener(EngineListener engineListener) {
        this.engineListener = engineListener;
    }

    public Engine(String url, int nBrowser) {
        this.url = url;
        browserPool = new BrowserPool(nBrowser);
        browserPool.execute(new Opener(url));
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

    public void createMapOfSite(){
        linkContainer = new LinkContainer(this);
        linkContainer.add(url);

        browserPool.setTasksEndListener(new BrowserPool.TasksEndListener() {
            @Override
            public void onTaskEnd() {
                if (engineListener != null) {
                    engineListener.onCreateMapEnds();
                    System.out.println("TASK WAS DONE");
                    System.out.println(linkContainer);
                }
            }
        });

    }

    public void prepareXSS(String url) {
        browserPool.execute(new XssAnalyser(url));
        browserPool.setTasksEndListener(new BrowserPool.TasksEndListener() {
            @Override
            public void onTaskEnd() {
                if (engineListener != null) {
                    engineListener.onXssPrepareEnds();
                }
            }
        });

    }

    @Override
    public void onLinkAdded(String urlToAnalise) {
        browserPool.execute(new LinkFinder(linkContainer, urlToAnalise, url));
    }

    public interface EngineListener {
        public void onCreateMapEnds();
        public void onXssPrepareEnds();
    }


}
