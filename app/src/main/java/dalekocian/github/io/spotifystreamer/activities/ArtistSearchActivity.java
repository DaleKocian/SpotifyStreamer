package dalekocian.github.io.spotifystreamer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.fragments.ArtistSearchFragment;
import dalekocian.github.io.spotifystreamer.fragments.ArtistSearchInstructionsFragment;
import dalekocian.github.io.spotifystreamer.fragments.LoadingFragment;
import dalekocian.github.io.spotifystreamer.fragments.NoResultsFragment;
import dalekocian.github.io.spotifystreamer.fragments.TopTenTracksFragment;
import dalekocian.github.io.spotifystreamer.fragments.TrackPlayerFragment;
import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ArtistSearchFragment.Callback,
        TopTenTracksFragment.TopTenTracksCallback{
    private static final String TAG = ArtistSearchActivity.class.getName();
    public static final String SEARCH_STRING_BUNDLE_KEY = "SEARCH_STRING";
    public static final String SAVED_FRAGMENT = "SAVED_FRAGMENT";
    @Bind(R.id.fContainer)
    FrameLayout mFContainer;
    private ArtistSearchFragment artistSearchFragment;
    private TopTenTracksFragment topTenTracksFragment;
    private String searchString = "";
    private boolean mTwoPane;
    private String countryCode;
    private String selectedArtistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_search_ui);
        ButterKnife.bind(this);
        mTwoPane = isTablet();
        if (savedInstanceState == null) {
            artistSearchFragment = (ArtistSearchFragment) getSupportFragmentManager().findFragmentById(R.id.artistSearchFragment);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fContainer, new ArtistSearchInstructionsFragment())
                    .commit();
        } else {
            searchString = savedInstanceState.getString(SEARCH_STRING_BUNDLE_KEY);
            artistSearchFragment = (ArtistSearchFragment) getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT);
        }
        if (mTwoPane) {
            artistSearchFragment.setActivateOnItemClick(true);
            countryCode = Utils.getCountryCodeFromSettings(this);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(artistSearchFragment).commit();
        }
    }

    private boolean isTablet() {
        return mFContainer.getTag() != null && mFContainer.getTag().toString().equals(getResources().getString(R.string.tablet_tag));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTwoPane) {
            String currentCountryCode = Utils.getCountryCodeFromSettings(this);
            if (!countryCode.equals(currentCountryCode)) {
                countryCode = currentCountryCode;
                String artistId = getIntent().getStringExtra(ExtraKeys.ARTIST_ID);
                topTenTracksFragment.searchTopTenTracks(artistId, countryCode);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.artist_search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setQuery(searchString, false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
    public boolean onQueryTextSubmit(String artistName) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchString) {
        if (searchString.isEmpty()) {
            showAndReplaceFragment(new ArtistSearchInstructionsFragment(), ArtistSearchInstructionsFragment.class.getName());
        } else if (!searchString.equals(this.searchString)) {
            this.searchString = searchString;
            artistSearchFragment.search(searchString);
        }
        return false;
    }

    private void showAndReplaceFragment(Fragment fragment, String fragmentTag) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fContainer, fragment, fragmentTag).commit();
            hideArtistSearchFragmentIfVisible();
            mFContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideArtistSearchFragmentIfVisible() {
        if (!mTwoPane && artistSearchFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(artistSearchFragment).commit();
        }
    }

    @Override
    public void onNoResults() {
        showAndReplaceFragment(new NoResultsFragment(), NoResultsFragment.class.getName());
    }

    @Override
    public void onLoading() {
        if (!mTwoPane) {
            showAndReplaceFragment(new LoadingFragment(), LoadingFragment.class.getName());
        }
    }

    @Override
    public void onResults() {
        if (!mTwoPane && !artistSearchFragment.isVisible()) {
            mFContainer.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().show(artistSearchFragment).commit();
        }
    }

    @Override
    public void onViewCreated() {
        topTenTracksFragment.searchTopTenTracks(selectedArtistId, countryCode);
    }

    @Override
    public void onItemSelected(List<Track> trackList, int position) {
        if (mTwoPane) {
            ArrayList<ParcelableTrack> parcelableTrackArrayList = new ArrayList<>(trackList.size());
            for (Track track : trackList) {
                parcelableTrackArrayList.add(new ParcelableTrack(track));
            }
            Bundle args = new Bundle();
            args.putParcelableArrayList(ExtraKeys.TRACK_LIST, parcelableTrackArrayList);
            args.putInt(ExtraKeys.POSITION, position);
            TrackPlayerFragment trackPlayerFragment = new TrackPlayerFragment();
            trackPlayerFragment.setArguments(args);
            trackPlayerFragment.show(getSupportFragmentManager(), TrackPlayerFragment.class.getName());
        }
    }

    @Override
    public void onItemSelected(Artist artist) {
        if (mTwoPane) {
            selectedArtistId = artist.id;
/*            Bundle arguments = new Bundle();
            arguments.putString(ExtraKeys.ARTIST_ID, artist.id);
            arguments.putString(ExtraKeys.ARTIST_NAME, artist.name);*/
            topTenTracksFragment = new TopTenTracksFragment();
//            topTenTracksFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.fContainer, topTenTracksFragment).commit();
        } else {
            Intent topTenTrackIntent = new Intent(this, TopTenTrackActivity.class);
            topTenTrackIntent.putExtra(ExtraKeys.ARTIST_ID, artist.id);
            topTenTrackIntent.putExtra(ExtraKeys.ARTIST_NAME, artist.name);
            startActivity(topTenTrackIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_STRING_BUNDLE_KEY, searchString);
        getSupportFragmentManager().putFragment(outState, SAVED_FRAGMENT, artistSearchFragment);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }
}