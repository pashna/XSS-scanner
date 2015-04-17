package xss;

/**
 * Created by popka on 02.04.15.
 */
public class Starter implements Engine.EngineListener{

    private String url = "http://www.insecurelabs.org/Task";
    private int nBrowser = 4;
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
        engine.prepareXSS();
    }

    @Override
    public void onXssPrepareEnds() {
        System.out.println("XssPreparedEnds");
    }

}
