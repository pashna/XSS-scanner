package xss.Tasks;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import xss.LinkContainer.XssContainer;
import xss.LinkContainer.XssStruct;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 03.04.15.
 */
public class ReflectXssChecker extends BrowserRunnable{
    private String url;
    private ArrayList<String> xssArrayList;
    private XssContainer xssContainer;

    public ReflectXssChecker(String url, ArrayList<String> xssArrayList, XssContainer xssContainer) {
        this.url = url;
        this.xssArrayList = xssArrayList;
        this.xssContainer = xssContainer;
    }

    @Override
    public void run() {
        System.out.println("Проверяем урл " + url + "   на ReflectedXSS");

        for (String xss : xssArrayList) {
            String urlWithXss = url.replaceAll(XssPreparer.INPUT_VALUE, xss); // Заменяем INPUT_VALUE на XSS
            try {
                getWebDriver().navigate().to(urlWithXss);
                getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
                if (wasScriptExecuted()) {
                    synchronized (xssContainer) {
                        xssContainer.add(new XssStruct(url, xss, XssStruct.REFLECTED));
                    }
                }
            } catch (TimeoutException e) {

            }

        }
    }

    private boolean wasScriptExecuted() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
            Boolean result = (Boolean) js.executeScript("return location.hash == \"#10\"");
            return result;
        } catch (WebDriverException e) {
            return false;
        }
    }
}
