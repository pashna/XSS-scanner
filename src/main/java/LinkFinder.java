import LinkContainer.LinkContainer;

import Tasks.BrowserRunnable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by popka on 22.03.15.
 */
public class LinkFinder extends BrowserRunnable {

    final private String LINK_TAG = "a";
    final private String HREF = "HREF";

    private String url;
    private LinkContainer linkContainer;
    private String domain;
    private boolean isSinglePageApp = false;

    public LinkFinder(LinkContainer linkContainer, String urlToFind, String baseUrl) {
        this.linkContainer = linkContainer;
        this.url = urlToFind;

        domain = baseUrl.replace("http://", "");
        domain = domain.replace("https://", "");
        if (domain.startsWith("www.")) {
            domain = domain.replace("www.", "");
        }
        int indexOfSlash = domain.indexOf("/");
        domain = domain.substring(0, indexOfSlash-1);
    }


    @Override
    public void run() {
        getWebDriver().navigate().to(url);
        getWebDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Ждем загрузки ????

        List<WebElement> links = getWebDriver().findElements(By.tagName(LINK_TAG));
        Iterator<WebElement> iterator = links.listIterator();
        WebElement element;

        while (iterator.hasNext()) {
            element = iterator.next();
            String link = element.getAttribute(HREF);

            if (isInnerLink(link)) {
                if (!isSinglePageApp) link = cutArgAndHash(link); // Обрезаем Аргументы и Хэш

                synchronized (linkContainer) {
                    linkContainer.add(link);
                }

            }
        }
    }

    private boolean isInnerLink(String link) {

        if (link == null) return false;

        // Если не тот протогол - не подходит
        if (!(link.startsWith("http://")||(link.startsWith("https://")))) return false;

        // ======
        // Запрет поиска в поддоменах
        String linkWithoutProtocol = link.replace("https://", "");
        linkWithoutProtocol = linkWithoutProtocol.replace("http://", "");
        linkWithoutProtocol = linkWithoutProtocol.replace("www.", "");
        // =======

        if (linkWithoutProtocol.startsWith(domain)) { // Находимся ли мы в пределах домена
            Pattern ends = Pattern.compile("\\.\\w+$");
            Matcher m = ends.matcher(link);
            if (m.find()) {
                String expansion = m.group(); // Получаем расширение, чтобы не скачавать файлы типа .doc и т.д.
                if (!(expansion.equals(".htm")||expansion.equals(".html"))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /*
    Обрезает аргументы и ХЭШ.
     */
    private String cutArgAndHash(String url) {
        int placeOfArg = url.indexOf("?");
        if (placeOfArg < 0) {
            int placeOfHash = url.indexOf("#");
            if (placeOfHash > 0) {
                url = url.substring(0, placeOfHash);
            }

        } else {
            url = url.substring(0, placeOfArg);
        }
        return url;
    }
}
