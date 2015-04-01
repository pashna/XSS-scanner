package Tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 01.04.15.
 */
public class XssAnalyser extends BrowserRunnable {
    private String url;
    private final String FORM = "FORM";

    private final String TEXT_TO_REPLACE = "TEXT_TO_REPLACE";
    private final String INPUT_VALUE = "999999999";

    private final String FILL_FORM_SCRIPT =
            "var inputs = document.getElementsByTagName(\"input\");\n" +
            "for (var i=0; i<inputs.length; i++) {\n" +
            "    inputs[i].value = \"" + TEXT_TO_REPLACE + "\";\n" +
            "    inputs[i].checked = \"true\";\n" +
            "}\n" +
            "var selects = document.getElementsByTagName(\"select\");\n" +
            "for (var i=0; i<selects.length; i++) {\n" +
            "    selects[i].children[1].selected = \"true\";\n" +
            "}\n" +
            "var textareas = document.getElementsByTagName(\"textarea\");\n" +
            "for (var i=0; i<textareas.length; i++) {\n" +
            "    textareas[i].value=\""+TEXT_TO_REPLACE+ "\";\n" +
            "}";

    private final String SUBMIT_FORM = "document.forms[" + TEXT_TO_REPLACE +"].querySelector(\"[type=submit]\").click()";


    public XssAnalyser(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        List<WebElement> listForms = getWebDriver().findElements(By.tagName(FORM));
        String currentUrl = getWebDriver().getCurrentUrl(); // Получаем текущий урл

        for (int i=0; i<listForms.size(); i++) {
            getWebDriver().navigate().to(currentUrl); // Отправляемся по урлу
            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
            fillAllInput(); // заполняем все формы на страничке
            submitForm(i); // отправляем форму
            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
            clearAllInput();
            if (getWebDriver().getCurrentUrl().equals(currentUrl)) { // Если равен текущему урлу (значит, мы не перешли)
                System.out.println("STORED");
            } else {
                String encodedUrl = URLEncoder.encode(getWebDriver().getCurrentUrl());
                if (encodedUrl.contains(INPUT_VALUE)) {
                    System.out.println("REFLECT");
                }
            }
        }
    }

    private void fillAllInput() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        String fillFormScript = FILL_FORM_SCRIPT.replaceAll(TEXT_TO_REPLACE, INPUT_VALUE);
        js.executeScript(fillFormScript);
    }

    private void clearAllInput() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        String fillFormScript = FILL_FORM_SCRIPT.replaceAll(TEXT_TO_REPLACE, "");
        js.executeScript(fillFormScript);
    }

    private void submitForm(int number) {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        String submitForm = SUBMIT_FORM.replace(TEXT_TO_REPLACE, number + "");
        js.executeScript(submitForm);
    }
}
