import LinkContainer.LinkContainer;
import LinkContainer.XssStoredContainer;
import LinkContainer.XssStoredContainer.XssContainerCallback;
import LinkContainer.XssStored;
import Tasks.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by popka on 22.03.15.
 */
public class Engine {
    private BrowserPool browserPool;
    private int nBrowser;
    private String url = "";

    private LinkContainer linkContainer; // "Карта сайта"
    private LinkContainer reflectXSSUrlContainer; // Список урлов с параметрами с потенциальной ReflectXSS-уязвимостью
    private XssStoredContainer storedXSSUrlContainer; // Список урлов с параметрами с потенциальной StoredXSS-уязвимостью

    private EngineListener engineListener;

    private ArrayList<String> xssArrayList;

    public void setEngineListener(EngineListener engineListener) {
        this.engineListener = engineListener;
    }

    // Конструктор без авторизации
    public Engine(String url, int nBrowser) {
        this.url = url;
        browserPool = new BrowserPool(nBrowser);
        browserPool.execute(new Opener(url));
    }

    // Конструктор с авторизацией
    public Engine(String url, int nBrowser, int sec) {
        this.url = url;
        this.nBrowser = nBrowser;
        auth(sec);
    }

    // авторизация
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
        browserPool.setCookie(url, cookieSet); // Всем куки!
    }


    /*
    Создает "карту сайта"
     */
    public void createMapOfSite(){
        linkContainer = new LinkContainer();

        linkContainer.setCallback(new LinkContainer.LinkContainerCallback() { // Срабатывает, при добавлении в linkContainer записи
            @Override
            public void onLinkAdded(String urlToAnalise) {
                browserPool.execute(new LinkFinder(linkContainer, urlToAnalise, url));
            }
        });

        linkContainer.add(url);

        browserPool.setTasksEndListener(new BrowserPool.TasksEndListener() {
            @Override
            public void onTaskEnd() {
                if (engineListener != null) {
                    engineListener.onCreateMapEnds();
                    System.out.println(linkContainer);
                }
            }
        });

    }

    public void prepareXSS() {
        browserPool.setTasksEndListener(new BrowserPool.TasksEndListener() {
            @Override
            public void onTaskEnd() {
                if (engineListener != null) {
                    engineListener.onXssPrepareEnds();
                    System.out.println(reflectXSSUrlContainer);
                }
            }
        });

        FileReader fileReader = new FileReader(FileReader.TEST);
        xssArrayList = fileReader.readFile();

        reflectXSSUrlContainer = new LinkContainer();
        reflectXSSUrlContainer.setCallback(new LinkContainer.LinkContainerCallback() { // При добавлении ссылки в контейнер хранения урлов с параметрами для ReflectXSS
            @Override
            public void onLinkAdded(String url) {
                browserPool.execute(new ReflectXssChecker(url, xssArrayList));
            }
        });

        storedXSSUrlContainer = new XssStoredContainer();
        storedXSSUrlContainer.setCallback(new XssContainerCallback() {
            @Override
            public void onLinkAdded(XssStored xssStored) {
                browserPool.execute(new StoredXssChecker(xssStored.url, xssStored.formNumber, xssArrayList));
            }
        });


        for (String url:linkContainer)
            browserPool.execute(new XssPreparer(url, linkContainer, reflectXSSUrlContainer, storedXSSUrlContainer));

    }

    public interface EngineListener {
        public void onCreateMapEnds();
        public void onXssPrepareEnds();
    }



}
