package xss.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by popka on 24.05.15.
 */


public class DirectoryCopier  {
    /*public static void main(String[] args)
    {
        File srcFolder = new File("c:\\mkyong");
        File destFolder = new File("c:\\mkyong-new");

        //make sure source exists
        if(!srcFolder.exists()){

            System.out.println("Directory does not exist.");
            //just exit
            System.exit(0);

        }else{

            try{
                copyFolder(srcFolder,destFolder);
            }catch(IOException e){
                e.printStackTrace();
                //error, just exit
                System.exit(0);
            }
        }

        System.out.println("Done");
    }*/

    public void copyFolder(File src, File dest)
            throws IOException{

        if(src.isDirectory()){

            //Если файла не существует -создаем его
            if(!dest.exists()){
                dest.mkdir();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }

            //Забираем все файлики из директории
            String files[] = src.list();

            for (String file : files) {
                // Рекурсивно создаем копии
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile,destFile);
            }

        }else{

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //Записываем в файл
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
            System.out.println("File copied from " + src + " to " + dest);
        }
    }
}
