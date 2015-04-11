package Tasks;

import LinkContainer.LinkContainer;
import LinkContainer.XssStoredContainer;
import LinkContainer.XssStored;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by popka on 01.04.15.
 */

/*
Заходит на заданный урл.
Поочередно заполняет и отправляет все формы.
В зависимости от результата, определяет, возможно ли на этой страничке XSS.
Если определила потенциальный ReflectXSS, то добавляем этот урл с параметрами в StoredXSS

 */
public class XssPreparer extends BrowserRunnable {
    private String url;
    private final String FORM = "FORM";

    private final String TEXT_TO_REPLACE = "TEXT_TO_REPLACE";
    public static final String INPUT_VALUE = "999999999";
    private LinkContainer reflectXSSUrlContainer;
    private XssStoredContainer storedXSSUrlContainer;
    private LinkContainer linkContainer;

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

    private final String SUBMIT_FORM = "document.forms[" + TEXT_TO_REPLACE +"].querySelector(\"[type=submit]\").click()";


    public XssPreparer(String url, LinkContainer linkContainer, LinkContainer reflectXSSUrlContainer, XssStoredContainer storedXSSUrlContainer) {
        this.url = url;
        this.reflectXSSUrlContainer = reflectXSSUrlContainer;
        this.linkContainer = linkContainer;
        this.storedXSSUrlContainer = storedXSSUrlContainer;
    }

    @Override
    public void run() {
        getWebDriver().navigate().to(url); // Отправляемся по урлу
        getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
        List<WebElement> listForms = getWebDriver().findElements(By.tagName(FORM));
        String currentUrl = URLDecoder.decode(getWebDriver().getCurrentUrl()); // Получаем текущий урл

        for (int i=0; i<listForms.size(); i++) {
            getWebDriver().navigate().to(currentUrl); // Отправляемся по урлу
            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки

            fillAllInput(); // заполняем все формы на страничке
            submitForm(i); // отправляем форму

            getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки

            clearAllInput();

            if (isPageContainsInputValue()) {// если на страничке после чистки инпутов, есть слово, которое мы ввели (INPUT_VALUE), будем проверять эту страничку
                if (getWebDriver().getCurrentUrl().equals(currentUrl)) { // Если равен текущему урлу (значит, мы не перешли), стало быть проверяем на StoredXSS
                    System.out.println(currentUrl + "  STORED");
                    storedXSSUrlContainer.add(new XssStored(url, i));

                } else { // Добавляем в список проверок ReflectXSS

                    String decodedUrl = URLDecoder.decode(getWebDriver().getCurrentUrl());

                    if (decodedUrl.contains(INPUT_VALUE)) { // Если в списке параметров есть наш ввод - все ок
                        if (isPageContainsInputValue()) { // если на страничке после чистки инпутов, есть слово, которое мы ввели (INPUT_VALUE), будем проверять эту страничку
                            System.out.println(currentUrl + "  REFLECT");
                            synchronized (reflectXSSUrlContainer) {
                                reflectXSSUrlContainer.add(decodedUrl);
                            }

                        /*
                        Пока не оттестировано!
                         */
                            decodedUrl = decodedUrl.substring(0, decodedUrl.indexOf("?")); // Обрезаем по аргументы и добавляем в карту сайта (вдруг там новые ссылки)
                            synchronized (linkContainer) {
                                linkContainer.add(decodedUrl);
                            }

                        }
                    }
                }
            }
        }
    }

    /*
    Запоняем все инпуты на страничке
     */
    private void fillAllInput() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        String fillFormScript = FILL_FORM_SCRIPT.replaceAll(TEXT_TO_REPLACE, INPUT_VALUE);
        js.executeScript(fillFormScript);
    }

    /*
    Чистим все инпуты на страничке
     */
    private void clearAllInput() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        String fillFormScript = FILL_FORM_SCRIPT.replaceAll(TEXT_TO_REPLACE, "");
        js.executeScript(fillFormScript);
    }

    /*
    Отправляем форму #number
     */
    private void submitForm(int number) {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        try {
            String submitForm = SUBMIT_FORM.replace(TEXT_TO_REPLACE, number + "");
            js.executeScript(submitForm);
        } catch (WebDriverException e) { // Если ошибка в js (Мало ли?! Нету кнопки сабмит, то просто отправляем форму)
            List<WebElement> listForms = getWebDriver().findElements(By.tagName(FORM));
            WebElement form = listForms.get(number);
            try {
                form.submit();
            } catch (WebDriverException exception) {};
        }
    }

    /*
    Содержит ли данная страничка текст INPUT_VALUE
     */
    private boolean isPageContainsInputValue() {
        String pageSource = getWebDriver().getPageSource();
        return pageSource.contains(INPUT_VALUE);
    }

}
