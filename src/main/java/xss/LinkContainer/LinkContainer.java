package xss.LinkContainer;

import java.util.LinkedHashSet;

/**
 * Created by popka on 22.03.15.
 */
public class LinkContainer extends LinkedHashSet<String> {

    LinkContainerCallback callback;

    public LinkContainer() {
    }

    @Override
    public boolean add(String url) {
        url = url.toLowerCase();
        boolean wasAdded = super.add(url);
        if (wasAdded)
            callback.onLinkAdded(url);
        return wasAdded;
    }

    public void setCallback(LinkContainerCallback callback) {
        this.callback = callback;
    }

    public interface LinkContainerCallback {
        public void onLinkAdded(String url);
    }
}
