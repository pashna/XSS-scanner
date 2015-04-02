package Tasks;

import org.openqa.selenium.JavascriptExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 03.04.15.
 */
public class ReflectXssSearcher extends BrowserRunnable{
    private String url;

    public ReflectXssSearcher(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        String[] xss = {"<script>location.hash=10</script>"};
        System.out.println("Проверяем урл " + url + "   на ReflectedXSS");
        for (int i=0; i<xss.length; i++) {
            String urlWithXss = url.replaceAll(XssPreparer.INPUT_VALUE, xss[i]); // Заменяем INPUT_VALUE на XSS

            getWebDriver().navigate().to(urlWithXss);
            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
            if (wasScriptExecuted()) {
                System.out.println("МЫ НАШЛИ XSS!!!");
            }

        }
    }

    private boolean wasScriptExecuted() {
        JavascriptExecutor js = (JavascriptExecutor)getWebDriver();
        Boolean result = (Boolean)js.executeScript("return location.hash == \"#10\"" );
        return result;
    }
}
