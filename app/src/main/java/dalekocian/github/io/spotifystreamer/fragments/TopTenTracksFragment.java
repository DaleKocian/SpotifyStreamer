package dalekocian.github.io.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;

/**
 * Created by dkocian on 8/12/2015.
 */
public class TopTenTracksFragment extends Fragment {
    private static final String TAG = TopTenTracksFragment.class.getName();
    @Bind(R.id.vsNoResultsFound)
    ViewStub mVsNoResultsFound;
    @Bind(R.id.pbSpotifyLoading)
    ProgressBar mPbSpotifyLoading;
    @Bind(R.id.ivSpotifyIcon)
    ImageView mIvSpotifyIcon;
    @Bind(R.id.rlLoadingScreen)
    RelativeLayout mRlLoadingScreen;
    @Bind(R.id.lvListItems)
    ListView mLvListItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_ten_tracks_ui, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
