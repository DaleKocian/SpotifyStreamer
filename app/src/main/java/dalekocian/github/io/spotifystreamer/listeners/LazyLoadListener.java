package dalekocian.github.io.spotifystreamer.listeners;

import android.widget.AbsListView;

/**
 * Created by k557782 on 6/25/2015.
 */
public abstract class LazyLoadListener implements AbsListView.OnScrollListener {
    private boolean isLoading = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount >= (totalItemCount * 3)/4 && totalItemCount != 0) {
            if (!isLoading) {
                isLoading = true;
                addNewElements();
            }
        }
    }

    public void startLoading() {
        isLoading = true;
    }
    /*
        Need to call this method when addNewElements finishes adding new elements to the list view, since it will only
        add new elements when LazyLoadListener is not loading new elements.
     */
    public void doneLoading() {
        isLoading = false;
    }

    public abstract void addNewElements();
}