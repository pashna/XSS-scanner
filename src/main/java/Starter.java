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
