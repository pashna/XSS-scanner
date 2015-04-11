package LinkContainer;

import java.util.LinkedHashSet;

/**
 * Created by popka on 22.03.15.
 */
public class XssStoredContainer extends LinkedHashSet<XssStored> {

    XssContainerCallback callback;

    public XssStoredContainer() {
    }

    @Override
    public boolean add(XssStored xssStored) {
        xssStored.url = xssStored.url.toLowerCase();
        boolean wasAdded = super.add(xssStored);
        if (wasAdded)
            callback.onLinkAdded(xssStored);
        return wasAdded;
    }

    public void setCallback(XssContainerCallback callback) {
        this.callback = callback;
    }

    public interface XssContainerCallback {
        public void onLinkAdded(XssStored url);
    }

}