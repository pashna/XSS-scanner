package xss.LinkContainer;

import java.util.LinkedHashSet;

/**
 * Created by popka on 19.04.15.
 */
public class XssContainer extends LinkedHashSet<XssStruct> {

    XssContainerCallback callback;

    @Override
    public boolean add(XssStruct xssStruct) {
        boolean wasAdded = super.add(xssStruct);
        if (wasAdded)
            if (callback != null)
                callback.onXssAdded(xssStruct);
        return wasAdded;
    }

    public void setCallback(XssContainerCallback callback) {
        this.callback = callback;
    }

    public interface XssContainerCallback {
        public void onXssAdded(XssStruct xssStruct);
    }
}
