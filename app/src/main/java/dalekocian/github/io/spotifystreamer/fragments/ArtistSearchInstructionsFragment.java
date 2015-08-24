package dalekocian.github.io.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dalekocian.github.io.spotifystreamer.R;

/**
 * Created by dkocian on 7/9/2015.
 */
public class ArtistSearchInstructionsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.artist_search_instructions_ui, container, false);
    }
}
