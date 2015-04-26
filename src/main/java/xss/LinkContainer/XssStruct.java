package xss.LinkContainer;

/**
 * Created by popka on 19.04.15.
 */
public class XssStruct {
    static public int REFLECTED = 1;
    static public int STORED = 2;

    public String url;
    public String xss;
    public int form;
    public int type;

    public XssStruct(String url, String xss, int type) {
        this(url, xss, type, -1);
    }

    public XssStruct(String url, String xss, int type, int form) {
        this.xss = xss;
        this.url = url;
        this.form = form;
        this.type = type;
    }

}
