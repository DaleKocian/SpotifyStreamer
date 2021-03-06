package dalekocian.github.io.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.fragments.LoadingFragment;
import dalekocian.github.io.spotifystreamer.fragments.NoResultsFragment;
import dalekocian.github.io.spotifystreamer.fragments.TopTenTracksFragment;
import dalekocian.github.io.spotifystreamer.fragments.TopTenTracksFragment.TopTenTracksCallback;
import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Track;

public class TopTenTrackActivity extends AppCompatActivity implements TopTenTracksCallback {
    public static final String TOP_TEN_TRACKS_FRAGMENT = "TOP_TEN_TRACKS_FRAGMENT";
    public static final String VISIBLE_FRAGMENT = "VISIBLE_FRAGMENT";
    public static final String VISIBLE_FRAGMENT_TAG = "VISIBLE_FRAGMENT_TAG";
    private static final String TAG = TopTenTrackActivity.class.getName();
    private TopTenTracksFragment topTenTracksFragment;
    private String visibleFragmentTag = "";

    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_ten_tracks_ui);
        countryCode = Utils.getCountryCodeFromSettings(this);
        if (savedInstanceState == null) {
            visibleFragmentTag = LoadingFragment.class.getName();
            topTenTracksFragment = new TopTenTracksFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fContainer, new LoadingFragment(), visibleFragmentTag)
                    .add(R.id.fContainer, topTenTracksFragment, TopTenTracksFragment.class.getName())
                    .hide(topTenTracksFragment).commit();
        } else {
            topTenTracksFragment = (TopTenTracksFragment) getSupportFragmentManager().getFragment(savedInstanceState, TOP_TEN_TRACKS_FRAGMENT);
            visibleFragmentTag = savedInstanceState.getString(VISIBLE_FRAGMENT_TAG);
            if (visibleFragmentTag == null) {
                visibleFragmentTag = TopTenTracksFragment.class.getName();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fContainer, topTenTracksFragment, visibleFragmentTag).commit();
            } else {
                Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, VISIBLE_FRAGMENT);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fContainer, fragment, visibleFragmentTag)
                        .add(R.id.fContainer, topTenTracksFragment, TopTenTracksFragment.class.getName())
                        .hide(topTenTracksFragment).commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentCountryCode = Utils.getCountryCodeFromSettings(this);
        if (!countryCode.equals(currentCountryCode)) {
            countryCode = currentCountryCode;
            callTopTenTrackSearch();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_ten_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, TOP_TEN_TRACKS_FRAGMENT, getSupportFragmentManager().findFragmentByTag
                (TopTenTracksFragment.class.getName()));
        if (!topTenTracksFragment.isVisible()) {
            outState.putString(VISIBLE_FRAGMENT_TAG, visibleFragmentTag);
            getSupportFragmentManager().putFragment(outState, VISIBLE_FRAGMENT, getSupportFragmentManager().findFragmentByTag(visibleFragmentTag));
        }
    }

    private void callTopTenTrackSearch() {
        String artistId = getIntent().getStringExtra(ExtraKeys.ARTIST_ID);
        topTenTracksFragment.searchTopTenTracks(artistId, countryCode);
    }

    private void hideTopTenTracksFragmentIfVisible() {
        if (topTenTracksFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(topTenTracksFragment).commit();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(this.visibleFragmentTag);
    }

    private void removeFragmentIfNotTopTenTracksFragment(Fragment fragment) {
        if (fragment != null && !TopTenTracksFragment.class.getName().equals(this.visibleFragmentTag)) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void showScreen(Fragment fragment, String fragmentTag) {
        hideTopTenTracksFragmentIfVisible();
        if (!this.visibleFragmentTag.equals(fragmentTag)) {
            removeFragmentIfNotTopTenTracksFragment(getCurrentFragment());
            getSupportFragmentManager().beginTransaction().add(R.id.fContainer, fragment, fragmentTag).commit();
            this.visibleFragmentTag = fragmentTag;
        }
    }

    @Override
    public void onNoResults() {
        showScreen(new NoResultsFragment(), NoResultsFragment.class.getName());
    }

    @Override
    public void onLoading() {
        showScreen(new LoadingFragment(), LoadingFragment.class.getName());
    }

    @Override
    public void onResults() {
        if (!TopTenTracksFragment.class.getName().equals(this.visibleFragmentTag)) {
            removeFragmentIfNotTopTenTracksFragment(getCurrentFragment());
            this.visibleFragmentTag = TopTenTracksFragment.class.getName();
            getSupportFragmentManager().beginTransaction().show(topTenTracksFragment).commit();
        }
    }

    @Override
    public void onViewCreated() {
        callTopTenTrackSearch();
    }

    @Override
    public void onItemSelected(List<Track> trackList, int position) {
        Intent intent = new Intent(this, TrackPlayerActivity.class);
        ArrayList<ParcelableTrack> parcelableTrackArrayList = new ArrayList<>(trackList.size());
        for (Track track : trackList) {
            parcelableTrackArrayList.add(new ParcelableTrack(track));
        }
        intent.putParcelableArrayListExtra(ExtraKeys.TRACK_LIST, parcelableTrackArrayList);
        intent.putExtra(ExtraKeys.POSITION, position);
        startActivity(intent);
    }
}
