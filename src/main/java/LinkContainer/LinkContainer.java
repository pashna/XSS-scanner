package LinkContainer;

import java.util.LinkedHashSet;

/**
 * Created by popka on 22.03.15.
 */
public class LinkContainer extends LinkedHashSet<String> {
    LinkContainerCallback callback;

    public LinkContainer(LinkContainerCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean add(String url) {
        boolean wasAdded = super.add(url);
        if (wasAdded) callback.onLinkAdded(url);
        return wasAdded;
    }

    public interface LinkContainerCallback {
        public void onLinkAdded(String url);
    }
}
