import LinkContainer.LinkContainer;
import Tasks.BrowserRunnable;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.InvalidCookieDomainException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by popka on 21.03.15.
 */
public class BrowserPool {

    private static AtomicInteger countOfRunningThreads = new AtomicInteger(0);
    private final int nBrowsers;
    private final BrowserWorker[] browsers;
    private final LinkedList queue;
    private TasksEndListener tasksEndListener;

    public BrowserPool(int nBrowsers) {
        this.nBrowsers = nBrowsers;
        queue = new LinkedList<BrowserRunnable>();
        browsers = new BrowserWorker[nBrowsers];

        for (int i=0; i<nBrowsers; i++) {
            browsers[i] = new BrowserWorker(i);
            browsers[i].start();
        }
    }

    private boolean isTaskEnded() {
        boolean isEmptyRunningTask = countOfRunningThreads.get() == 0 ? true: false;
        return (queue.isEmpty() && isEmptyRunningTask);
    }

    public void execute(BrowserRunnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    public void setTasksEndListener(TasksEndListener tasksEndListener) {
        this.tasksEndListener = tasksEndListener;
    }

    private class BrowserWorker extends Thread {
        private WebDriver webDriver;
        private int number;

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
                    //System.out.println("Количество запущенных = " + countOfRunningThreads.incrementAndGet());

                }

                // If we don't catch RuntimeException,
                // the pool could leak browsers
                try {
                    r.run(webDriver);
                    //System.out.println("Количество запущенных = " + countOfRunningThreads.decrementAndGet());
                    //System.out.println(webDriver.getCurrentUrl() + " запущен из потока №" + this.number);
                    if (isTaskEnded()) {
                        if (tasksEndListener != null)
                            tasksEndListener.onTaskEnd();
                    }
                }
                catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Всем воркерам поставить куки
     */
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

    public interface TasksEndListener {
        public void onTaskEnd();
    }
}
