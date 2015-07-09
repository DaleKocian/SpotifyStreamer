package dalekocian.github.io.spotifystreamer.callbacks;

import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

/**
 * Created by dkocian on 6/29/2015.
 */
public class ImageLoadedCallback implements Callback {
    private final ProgressBar progressBar;

    public ImageLoadedCallback(ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        hideProgressBar();
    }

    @Override
    public void onError() {
        hideProgressBar();
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
