package Tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 10.04.15.
 */
public class StoredXssChecker extends BrowserRunnable {

    private String url;
    private int formNumber;
    private final String FORM = "form";

    private final String TEXT_TO_REPLACE = "asd1wd1";
    private final String FILL_FORM_SCRIPT =
            "var inputs = document.getElementsByTagName('input');\n" +
                    "for (var i=0; i<inputs.length; i++) {\n" +
                    "    inputs[i].value = '"+TEXT_TO_REPLACE+"';\n" +
                    "    inputs[i].checked = \"true\";\n" +
                    "}\n" +
                    "var selects = document.getElementsByTagName(\"select\");\n" +
                    "for (var i=0; i<selects.length; i++) {\n" +
                    "    selects[i].children[1].selected = \"true\";\n" +
                    "}\n" +
                    "var textareas = document.getElementsByTagName(\"textarea\");\n" +
                    "for (var i=0; i<textareas.length; i++) {\n" +
                    "    textareas[i].value=' " +TEXT_TO_REPLACE +"';\n" +
                    "}";


    private final String FORM_NUMBER_TO_REPLACE = "olo123qwe";
    private String SUBMIT_FORM = "document.forms[" + FORM_NUMBER_TO_REPLACE +"].querySelector(\"[type=submit]\").click()";

    private ArrayList<String> xssArrayList;

    public StoredXssChecker(String url, int formNumber, ArrayList<String> xssArrayList) {
        this.url = url;
        this.formNumber = formNumber;
        this.xssArrayList = xssArrayList;
        SUBMIT_FORM = SUBMIT_FORM.replace(FORM_NUMBER_TO_REPLACE, formNumber + "");
    }


    @Override
    public void run() {

        for (String xss : xssArrayList) {
            getWebDriver().navigate().to(url);
            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки

            fillForm(xss);
            submitForm();

            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
            if (wasScriptExecuted()) {
                System.out.println("XSS STORED WAS FOUND" + "   " + xss);
                break;
            }

        }


    }

    private void fillForm(String xss_text) {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        String fillFormScript = FILL_FORM_SCRIPT.replaceAll(TEXT_TO_REPLACE, xss_text);
        js.executeScript(fillFormScript);
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

    private void submitForm() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        try {
            js.executeScript(SUBMIT_FORM);
        } catch (WebDriverException e) { // Если ошибка в js (Мало ли?! Нету кнопки сабмит, то просто отправляем форму)
            List<WebElement> listForms = getWebDriver().findElements(By.tagName(FORM));
            WebElement form = listForms.get(formNumber);
            try {
                form.submit();
            } catch (WebDriverException exception) {};
        }
    }
}
