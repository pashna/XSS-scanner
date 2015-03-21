import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.LinkedList;

/**
 * Created by popka on 21.03.15.
 */
public class BrowserPool {

    private final int nBrowsers;
    private final PoolWorker[] threads;
    private final LinkedList queue;

    public BrowserPool(int nBrowsers) {
        this.nBrowsers = nBrowsers;
        queue = new LinkedList<BrowserRunnable>();
        threads = new PoolWorker[nBrowsers];

        for (int i=0; i<nBrowsers; i++) {
            threads[i] = new PoolWorker(i);
            threads[i].start();
        }
    }

    public void execute(BrowserRunnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    private class PoolWorker extends Thread {
        WebDriver webDriver;
        int number;

        public PoolWorker(int number) {
            webDriver = new FirefoxDriver();
            this.number = number;
        }

        public void run() {
            BrowserRunnable r;

            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }

                    r = (BrowserRunnable) queue.removeFirst();
                }

                // If we don't catch RuntimeException,
                // the pool could leak threads
                try {
                    r.run(webDriver);
                    System.out.print(webDriver.getCurrentUrl() + " запущен из потока №" + this.number);
                }
                catch (RuntimeException e) {
                    // LOG
                }
            }
        }
    }
}
