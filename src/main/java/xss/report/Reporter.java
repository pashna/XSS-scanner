package xss.report;

import xss.LinkContainer.XssContainer;
import xss.LinkContainer.XssStruct;
import xss.Tasks.XssPreparer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by popka on 26.04.15.
 */
public class Reporter {

    private XssContainer xssContainer;

    private final String URL = "URL";
    private final String DATE = "DATE";
    private final String DURATION = "DURATION";
    private final String COUNT = "COUNT";

    private final String TYPE = "TYPE";
    private final String FORM_NUMBER = "FORM_NUMBER";
    private final String XSS = "XSS";

    private String header = "<!DOCTYPE html>\n" +
            "<html lang=\"en\" class=\"no-js\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\" />\n" +
            "<title>XSS-Scanner Отчет</title>\n" +
            "<link rel=\"shortcut icon\" href=\"../favicon.ico\">\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/demo.css\" />\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/component.css\" />\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"container\">\n" +
            "<header>\n" +
            "<h1> <em>XSS-Scanner</em>\n" +
            "</h1>\n" +
            "</header>\n" +
            "<div class=\"component\">\n" +
            "<div class=\"reportInfo\">\n" +
            "<h2>Отчет</h2>\n" +
            "<p>Проверка url <a href=\""+URL+"\">"+URL+"</a> от " + DATE +"</p>\n" +
            "<p>Продолжительность проверки "+DURATION+"</p>\n" +
            "<p>Обнаружено <B>"+COUNT+"</B> уязвимостей</p>\n" +
            "</div>";

    private String tableRowReflected =
            "<tr>\n" +
            "<td>"+TYPE+"</td>\n" +
            "<td><textarea style=\"margin: 2px; height: 52px; width: 726px;\">"+XSS+"</textarea></td>\n" +
            "</tr>";

    private String tableHeaderReflected =
            "<table>\n" +
            "<thead>\n" +
            "<tr>\n" +
            "<th>Тип уязвимости</th>\n" +
            "<th>Url</th>\n" +
            "</tr>\n" +
            "</thead>\n" +
            "<tbody>";

    private String tableHeaderStored =
            "<table>\n" +
            "<thead>\n" +
            "<tr>\n" +
            "<th>Тип уязвимости</th>\n" +
            "<th>Url</th>\n" +
            "<th>XSS</th>\n" +
            "<th>Номер формы</th>\n" +
            "</tr>\n" +
            "</thead>\n" +
            "<tbody>";

    private String tableRowStored =
            "<tr>\n" +
            "<td>"+TYPE+"</td>\n" +
            "<td>"+URL+"</td>\n" +
            "<td><textarea style=\"margin: 2px; height: 94px; width: 329px;\">"+XSS+"</textarea></td>\n" +
            "<td style=\"text-align:center;\">"+FORM_NUMBER+"</td>\n" +
            "</tr>";

    private String tableFooter =
            "</tbody>\n" +
            "</table>";

    private String footer =
            "</div>\n" +
            "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js\"></script>\n" +
            "<script src=\"http://cdnjs.cloudflare.com/ajax/libs/jquery-throttle-debounce/1.1/jquery.ba-throttle-debounce.min.js\"></script>\n" +
            "<script src=\"js/jquery.stickyheader.js\"></script>\n" +
            "</body>\n" +
            "</html>";

    public Reporter(XssContainer xssContainer) {
        this.xssContainer = xssContainer;
    }

    public String generateReport(String url, String duration) {
        String html = replaceHeader(url, duration);

        boolean isFirst = true;
        for (XssStruct xssStruct:xssContainer) {
            if (XssStruct.REFLECTED==xssStruct.type) {
                if (isFirst)
                    html += tableHeaderReflected;
                html += generateTableRow(xssStruct, XssStruct.REFLECTED);
            }
            if (!isFirst)
                html += tableFooter;
        }

        isFirst = true;
        for (XssStruct xssStruct:xssContainer) {
            if (XssStruct.STORED==xssStruct.type) {
                if (isFirst)
                    html += tableHeaderStored;
                html += generateTableRow(xssStruct, XssStruct.STORED);
            }
            if (!isFirst)
                html += tableFooter;
        }


        html += footer;
        saveFile(html);
        return html;
    }

    private String replaceHeader(String url, String duration) {
        header = header.replaceAll(URL, url);

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, new Locale("ru"));//new RussianDateFormat());
        String formattedDate = df.format(new Date());
        header = header.replace(DATE, formattedDate);
        
        header = header.replace(DURATION, duration);
        header = header.replace(COUNT, xssContainer.size() + "");
        return header;
    }

    private String generateTableRow(XssStruct xssStruct, int type) {
        String generatingTableRow;
        if (type == XssStruct.REFLECTED) {
            generatingTableRow = tableRowReflected;
            generatingTableRow = generatingTableRow.replace(XSS, xssStruct.url.replace(XssPreparer.INPUT_VALUE, xssStruct.xss));
            generatingTableRow = generatingTableRow.replace(TYPE, "REFLECTED");
        }
        else {
            generatingTableRow = tableRowStored;
            generatingTableRow = generatingTableRow.replace(XSS, xssStruct.xss);
            generatingTableRow = generatingTableRow.replace(TYPE, "STORED");
            generatingTableRow = generatingTableRow.replace(URL, xssStruct.url);
            generatingTableRow = generatingTableRow.replace(FORM_NUMBER, xssStruct.form+"");
        }


        return generatingTableRow;
    }



    private void saveFile(String text) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, new Locale("ru"));//new RussianDateFormat());
        String formattedDate = df.format(new Date());

        String filePath = "/home/popka/Diplom/report/StickyTableHeaders/" + formattedDate + ".html";
        File f = new File(filePath);

        try {
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.println(text);
            writer.close();
        } catch (FileNotFoundException|UnsupportedEncodingException e) {}

    }
}
