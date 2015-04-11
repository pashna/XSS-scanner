import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by popka on 02.04.15.
 */
public class Starter implements Engine.EngineListener{

    private String url = "https://xss-game.appspot.com/level2/frame";
    private int nBrowser = 1;
    private Engine engine;

    public void start() {
        engine = new Engine(url, nBrowser);
        engine.setEngineListener(this);
        engine.createMapOfSite();

        System.out.print(readFile("xssCheatSheet"));

    }

    @Override
    public void onCreateMapEnds() {
        System.out.println("createMapsEnds");
        engine.prepareXSS();
    }

    @Override
    public void onXssPrepareEnds() {
        System.out.println("XssPreparedEnds");
    }

    public String readFile(String filename) {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
