import LinkContainer.LinkContainer;
import Tasks.BrowserRunnable;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.InvalidCookieDomainException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 21.03.15.
 */
public class BrowserPool {

    private final int nBrowsers;
    private final BrowserWorker[] browsers;
    private final LinkedList queue;

    public BrowserPool(int nBrowsers) {
        this.nBrowsers = nBrowsers;
        queue = new LinkedList<BrowserRunnable>();
        browsers = new BrowserWorker[nBrowsers];

        for (int i=0; i<nBrowsers; i++) {
            browsers[i] = new BrowserWorker(i);
            browsers[i].start();
        }
    }

    public void execute(BrowserRunnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }


    private class BrowserWorker extends Thread {
        private WebDriver webDriver;
        int number;

        public BrowserWorker(int number) {
            webDriver = new FirefoxDriver();
            this.number = number;
        }

        public WebDriver getWebDriver() {
            return webDriver;
        }

        public void run() {
            BrowserRunnable r;

            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        }
                        catch (InterruptedException ignored) {}
                    }

                    r = (BrowserRunnable) queue.removeFirst();
                }

                // If we don't catch RuntimeException,
                // the pool could leak browsers
                try {
                    r.run(webDriver);
                    System.out.println(webDriver.getCurrentUrl() + " запущен из потока №" + this.number);
                }
                catch (RuntimeException e) {
                    // LOG
                }
            }
        }
    }

    public void setCookie(String url, Set<Cookie> cookieSet) {
        for (int i=0; i<nBrowsers; i++) {
            browsers[i].getWebDriver().navigate().to(url); // Проходим по ургу
            browsers[i].getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
            System.out.println(browsers[i].getWebDriver().getCurrentUrl());
            for (Cookie cookie:cookieSet) {
                try {
                    browsers[i].getWebDriver().manage().addCookie(cookie); // Заполняем куки
                } catch (InvalidCookieDomainException e) {}
            }
            browsers[i].getWebDriver().navigate().refresh(); // Обновляем страничку
            browsers[i].getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки

        }
    }

    public void openUrl(String url) {
        for (int i=0; i<nBrowsers; i++) {
            browsers[i].getWebDriver().navigate().to(url);
            browsers[i].getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
        }
    }
}
