package xss.report;

import org.apache.commons.io.IOUtils;
import xss.LinkContainer.XssContainer;
import xss.LinkContainer.XssStruct;
import xss.Tasks.XssPreparer;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    private final String REPORT = "reportStyle.zip";

    private String header = "<!DOCTYPE html>\n" +
            "<html lang=\"en\" class=\"no-js\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\" />\n" +
            "<title>XSS-Scanner Отчет</title>\n" +
            "<link rel=\"shortcut icon\" href=\"../favicon.ico\">\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportStyle/xssScanerReportStyle/css/demo.css\" />\n" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"reportStyle/xssScanerReportStyle/css/component.css\" />\n" +
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
            "<script src=\"reportStyle/xssScanerReportStyle/js/jquery.stickyheader.js\"></script>\n" +
            "</body>\n" +
            "</html>";

    public Reporter(XssContainer xssContainer) {
        this.xssContainer = xssContainer;
    }

    public void generateReport(String url, File directory, String duration) {


        String html = replaceHeader(url, duration);

        boolean isFirst = true;
        for (XssStruct xssStruct:xssContainer) {
            if (XssStruct.REFLECTED==xssStruct.type) {
                if (isFirst) {
                    html += tableHeaderReflected;
                    isFirst = false;
                }
                html += generateTableRow(xssStruct, XssStruct.REFLECTED);
            }
        }
        if (!isFirst)
            html += tableFooter;

        isFirst = true;
        for (XssStruct xssStruct:xssContainer) {
            if (XssStruct.STORED==xssStruct.type) {
                if (isFirst) {
                    html += tableHeaderStored;
                    isFirst = false;
                }
                html += generateTableRow(xssStruct, XssStruct.STORED);
            }
        }
        if (!isFirst)
            html += tableFooter;


        html += footer;

        copyJsCssToDirectory(directory);
        saveFile(html, directory);

    }

    private String replaceHeader(String url, String duration) {
        header = header.replaceAll(URL, url);


        DateFormat dateFormat = new SimpleDateFormat("dd MMMM YYYY", new RussianDateFormatSymbols() );
        header = header.replace(DATE, dateFormat.format( new Date() ));

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



    private void saveFile(String text, File directory) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMMM YYYY", new RussianDateFormatSymbols() );
        String formattedDate = dateFormat.format( new Date() );

        if (directory.isDirectory()) {
            String filePath = directory.getAbsolutePath().toString() +File.separator+ formattedDate + ".html";
            File reportHTML = new File(filePath);

            try {
                PrintWriter writer = new PrintWriter(reportHTML, "UTF-8");
                writer.println(text);
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    private void copyJsCssToDirectory(File directory) {
        /*ClassLoader classLoader = getClass().getClassLoader();
        File reportStyle = new File(classLoader.getResource(REPORT).getFile());



        DirectoryCopier directoryCopier = new DirectoryCopier();

        try{
            directoryCopier.copyFolder(reportStyle, directory);
        }
        catch(IOException e){
            e.printStackTrace();
        }*/

        InputStream is = getClass().getClassLoader().getResourceAsStream(REPORT);
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;

        try {
            while ((entry = zis.getNextEntry()) != null) {

                // Create a file on HDD in the destinationPath directory
                // destinationPath is a "root" folder, where you want to extract your ZIP file
                File entryFile = new File(directory, entry.getName());
                if (entry.isDirectory()) {

                    if (entryFile.exists()) {
                        System.out.print("Directory Already Exist");
                    } else {
                        entryFile.mkdirs();
                    }

                } else {

                    // Make sure all folders exists (they should, but the safer, the better ;-))
                    if (entryFile.getParentFile() != null && !entryFile.getParentFile().exists()) {
                        entryFile.getParentFile().mkdirs();
                    }

                    // Create file on disk...
                    if (!entryFile.exists()) {
                        entryFile.createNewFile();
                    }

                    // and rewrite data from stream
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(entryFile);
                        IOUtils.copy(zis, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
        }
        catch (IOException e) {

        }
        finally {
            IOUtils.closeQuietly(zis);
        }

    }

}

