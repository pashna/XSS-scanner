package xss.report;

import xss.LinkContainer.XssContainer;
import xss.LinkContainer.XssStruct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

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
            "\t<head>\n" +
            "\t\t<meta charset=\"UTF-8\" />\n" +
            "\t\t<title>XSS-Scanner Отчет</title>\n" +
            "\t\t<link rel=\"shortcut icon\" href=\"../favicon.ico\">\n" +
            "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/demo.css\" />\n" +
            "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/component.css\" />\n" +
            "\t</head>\n" +
            "\t<body>\n" +
            "\t\t<div class=\"container\">\n" +
            "\t\t\t<header>\n" +
            "\t\t\t\t<h1> <em>XSS-Scanner</em>\n" +
            "\t\t\t\t</h1>\t\n" +
            "\t\t\t</header>\n" +
            "\t\t\t<div class=\"component\">\n" +
            "\t\t\t\t<div class=\"reportInfo\">\n" +
            "\t\t\t\t\t<h2>Отчет</h2>\n" +
            "\t\t\t\t\t<p>Проверка url <a href=\""+URL+"\">"+URL+"</a> от " + DATE +"</p>\n" +
            "\t\t\t\t\t<p>Продолжительность проверки "+DURATION+"</p>\n" +
            "\t\t\t\t\t<p>Обнаружено <B>"+COUNT+"</B> уязвимостей</p>\n" +
            "\t\t\t\t</div>" +
            "\t\t\t\t<table>\n" +
            "\t\t\t\t\t<thead>\n" +
            "\t\t\t\t\t\t<tr>\n" +
            "\t\t\t\t\t\t\t<th>Тип уязвимости</th>\n" +
            "\t\t\t\t\t\t\t<th>Url</th>\n" +
            "\t\t\t\t\t\t\t<th>Номер формы</th>\n" +
            "\t\t\t\t\t\t\t<th>XSS</th>\n" +
            "\t\t\t\t\t\t</tr>\n" +
            "\t\t\t\t\t</thead>\n" +
            "\t\t\t\t\t<tbody>";

    private String tableRow =
            "<tr>\n" +
            "\t\t\t\t\t\t<td>"+TYPE+"</td>\n" +
            "\t\t\t\t\t\t<td>"+URL+"</td>\n" +
            "\t\t\t\t\t\t<td>"+FORM_NUMBER+"</td>\n" +
            "\t\t\t\t\t\t<td>"+XSS+"</td>\n" +
            "\t\t\t\t\t</tr>";

    private String footer =
            "\t\t\t\t\t\t</tbody>\n" +
            "\t\t\t\t</table>" +
            "</div>\n" +
            "\t\t<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js\"></script>\n" +
            "\t\t<script src=\"http://cdnjs.cloudflare.com/ajax/libs/jquery-throttle-debounce/1.1/jquery.ba-throttle-debounce.min.js\"></script>\n" +
            "\t\t<script src=\"js/jquery.stickyheader.js\"></script>\n" +
            "\t</body>\n" +
            "</html>";

    public Reporter(XssContainer xssContainer) {
        this.xssContainer = xssContainer;
    }

    public String generateReport(String url, String duration) {
        String html = replaceHeader(url, duration);
        for (XssStruct xssStruct:xssContainer) {
            html += generateTableRow(xssStruct);
        }
        html += footer;
        saveFile(html);
        return html;
    }

    private String replaceHeader(String url, String duration) {
        header = header.replaceAll(URL, url);
        header = header.replace(DATE, new Date().toString());
        header = header.replace(DURATION, duration);
        header = header.replace(COUNT, xssContainer.size() + "");
        return header;
    }

    private String generateTableRow(XssStruct xssStruct) {
        String generatingTableRow = tableRow;
        generatingTableRow = generatingTableRow.replace(URL, xssStruct.url);
        String xss = xssStruct.xss;

        xss.replaceAll("<", "&lt");
        xss.replaceAll("<", "&gt");

        generatingTableRow = generatingTableRow.replace(XSS, xss);
        if (xssStruct.type == XssStruct.STORED) {
            generatingTableRow = generatingTableRow.replace(TYPE, "STORED");
            generatingTableRow = generatingTableRow.replace(FORM_NUMBER, xssStruct.form + "");
        }
        else {
            generatingTableRow = generatingTableRow.replace(TYPE, "REFLECTED");
            generatingTableRow = generatingTableRow.replace(FORM_NUMBER, "");
        }

        return generatingTableRow;
    }

    private void saveFile(String text) {
        String filePath = "/home/popka/Diplom/report/StickyTableHeaders/" + new Date().toString() + ".html";
        File f = new File(filePath);

        try {
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.println(text);
            writer.close();
        } catch (FileNotFoundException|UnsupportedEncodingException e) {}

    }
}
