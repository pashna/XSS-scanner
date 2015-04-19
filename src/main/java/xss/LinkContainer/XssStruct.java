package xss.LinkContainer;

/**
 * Created by popka on 19.04.15.
 */
public class XssStruct {
    static public int REFLECTED = 1;
    static public int STORED = 2;

    private String url;
    private int form;
    private int type;

    public XssStruct(String url, int type) {
        this(url, type, -1);
    }

    public XssStruct(String url, int type, int form) {
        this.url = url;
        this.form = form;
        this.type = type;
    }

}
