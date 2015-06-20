package xss.Tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import xss.LinkContainer.XssContainer;
import xss.LinkContainer.XssStruct;

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

    private final String FORM_NUMBER_TO_REPLACE = "olo123qwe";

    private final String TEXT_TO_REPLACE = "asd1wd1";
    private final String FILL_ALL_FORM_SCRIPT =
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


    private final String GET_COUNT_OF_INPUT_INSIDE_FORM =
            "var input = document.forms["+FORM_NUMBER_TO_REPLACE+"].getElementsByTagName('input')\n" +
            "var array = new Array();\n" +
            "for (var i=0; i<input.length; i++) {\n" +
            "    if (input[i].type == undefined || input[i].type.toLowerCase() == \"text\") {\n" +
            "         array.push(input[i]);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "var textArea = document.forms[0].getElementsByTagName('textarea');\n" +
            "for (var i=0; i<textArea.length; i++) \n" +
            "    array.push(textArea[i]) \n" +
            "return array.length";

    private final String INPUT_NUMBER = "INPUT";

    private final String FILL_INPUT_INSIDE_FORM =
            "var input = document.forms["+FORM_NUMBER_TO_REPLACE+"].getElementsByTagName('input')\n" +
                    "var array = new Array();\n" +
                    "for (var i=0; i<input.length; i++) {\n" +
                    "    if (input[i].type == undefined || input[i].type.toLowerCase() == \"text\") {\n" +
                    "         array.push(input[i]);\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "var textArea = document.forms[0].getElementsByTagName('textarea');\n" +
                    "for (var i=0; i<textArea.length; i++) \n" +
                    "    array.push(textArea[i]) \n" +
                    "array[" + INPUT_NUMBER + "].value = ' " +TEXT_TO_REPLACE +"';\n";


    private String SUBMIT_FORM = "document.forms[" + FORM_NUMBER_TO_REPLACE +"].querySelector(\"[type=submit]\").click()";

    private ArrayList<String> xssArrayList;
    private XssContainer xssContainer;

    public StoredXssChecker(String url, int formNumber, ArrayList<String> xssArrayList, XssContainer xssContainer) {
        this.url = url;
        this.formNumber = formNumber;
        this.xssArrayList = xssArrayList;
        this.xssContainer = xssContainer;
        SUBMIT_FORM = SUBMIT_FORM.replace(FORM_NUMBER_TO_REPLACE, formNumber + "");
    }


    @Override
    public void run() {

        long inputCount = getCountOfInputInsideForm();
        for (int i=0; i<inputCount; i++) {
            for (String xss : xssArrayList) {
                getWebDriver().navigate().to(url);
                getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки

                fillAllForm("randomText");
                fillInputInsideForm(i, xss);

                submitForm();

                getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки
                if (wasScriptExecuted()) {
                    synchronized (xssContainer) {
                        xssContainer.add(new XssStruct(url, xss, XssStruct.STORED, formNumber));
                    }
                    System.out.println("XSS STORED WAS FOUND" + "   " + xss);
                    break;
                }

            }
        }


    }

    private void fillAllForm(String xss_text) {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        try {
            String fillFormScript = FILL_ALL_FORM_SCRIPT.replaceAll(TEXT_TO_REPLACE, xss_text);
            js.executeScript(fillFormScript);
        } catch (WebDriverException e) {}

    }

    private long getCountOfInputInsideForm() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        long count = 0;
        try {
            String fillFormScript = GET_COUNT_OF_INPUT_INSIDE_FORM.replaceAll(FORM_NUMBER_TO_REPLACE, formNumber+"");
            count = (Long)js.executeScript(fillFormScript);
        } catch (WebDriverException e) {
            return 0;
        }
        return count;
    }

    private void fillInputInsideForm(int inputNumber, String xss) {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();

        try {
            String fillFormScript = FILL_INPUT_INSIDE_FORM.replaceAll(FORM_NUMBER_TO_REPLACE, formNumber+"");
            fillFormScript = fillFormScript.replaceAll(INPUT_NUMBER, inputNumber+"");
            fillFormScript = fillFormScript.replaceAll(TEXT_TO_REPLACE, xss);
            js.executeScript(fillFormScript);
        } catch (WebDriverException e) {

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

    private void submitForm() {
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        try {
            js.executeScript(SUBMIT_FORM);
        } catch (WebDriverException e) { // Если ошибка в js (Мало ли?! Нету кнопки сабмит, то просто отправляем форму)
            List<WebElement> listForms = getWebDriver().findElements(By.tagName(FORM));
            WebElement form = listForms.get(formNumber);
            try {
                form.submit();
            } catch (WebDriverException exception) {
                exception.printStackTrace();
            };
        }
    }
}
