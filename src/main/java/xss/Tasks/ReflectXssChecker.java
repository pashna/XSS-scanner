package xss.Tasks;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 03.04.15.
 */
public class ReflectXssChecker extends BrowserRunnable{
    private String url;
    private ArrayList<String> xssArrayList;

    public ReflectXssChecker(String url, ArrayList<String> xssArrayList) {
        this.url = url;
        this.xssArrayList = xssArrayList;
    }

    @Override
    public void run() {
        System.out.println("Проверяем урл " + url + "   на ReflectedXSS");

        for (String xss : xssArrayList) {
            String urlWithXss = url.replaceAll(XssPreparer.INPUT_VALUE, xss); // Заменяем INPUT_VALUE на XSS

            getWebDriver().navigate().to(urlWithXss);
            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
            if (wasScriptExecuted()) {
                System.out.println("МЫ НАШЛИ XSS!!! " + xss + "  по урлу " + url);
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
