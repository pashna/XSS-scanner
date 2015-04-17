package xss;

/**
 * Created by popka on 02.04.15.
 */
public class Starter implements Engine.EngineListener{

    private String url = "http://www.insecurelabs.org/Task";
    //private String url = "https://xss-game.appspot.com/level2/frame";
    private int nBrowser = 6;
    private Engine engine;

    public void start() {
        engine = new Engine(url, nBrowser);
        engine.setEngineListener(this);
        engine.createMapOfSite();

        //System.out.print(readFile("xssCheatSheet"));
        //xss.XmlParser parser = new xss.XmlParser();
        //parser.parse();

    }

    @Override
    public void onCreateMapEnds() {
        System.out.println("createMapsEnds");
        engine.prepareXSS(FileReader.TEST);
    }

    @Override
    public void onXssPrepareEnds() {
        System.out.println("XssPreparedEnds");
    }

}
