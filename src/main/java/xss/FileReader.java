package xss;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by popka on 12.04.15.
 */
public class FileReader {

    public static final int LOW_LEVEL = 0;
    public static final int MEDIUM_LEVEL = 1;
    public static final int HIGH_LEVEL = 2;
    public static final int TEST = 3;

    public final String lowLevel = "xssLow";
    public final String mediumLevel = "xssMedium";
    public final String highLevel = "xssHigh";
    public final String cheatSheet = "xssCheatSheet";

    private int level;
    private ArrayList<String> filenameArrayList = new ArrayList<String>();

    public FileReader(int level) {
        switch (level) {
            case LOW_LEVEL:
                filenameArrayList.add(lowLevel);
                break;

            case MEDIUM_LEVEL:
                filenameArrayList.add(lowLevel);
                filenameArrayList.add(mediumLevel);
                break;

            case HIGH_LEVEL:
                filenameArrayList.add(lowLevel);
                filenameArrayList.add(mediumLevel);
                filenameArrayList.add(highLevel);
                break;

            case TEST:
                filenameArrayList.add(cheatSheet);
                break;
        }
    }

    public ArrayList<String> readFile() {

        ArrayList<String> result = new ArrayList<String>();
        ClassLoader classLoader = getClass().getClassLoader();

        for (String filename: filenameArrayList) {
            File file = new File(classLoader.getResource(filename).getFile());

            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.add(line);

                }

                scanner.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

}
