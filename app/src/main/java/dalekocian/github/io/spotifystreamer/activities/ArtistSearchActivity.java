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
import android.view.inputmethod.EditorInfo;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.fragments.ArtistSearchFragment;
import dalekocian.github.io.spotifystreamer.fragments.ArtistSearchInstructionsFragment;
import dalekocian.github.io.spotifystreamer.fragments.LoadingFragment;
import dalekocian.github.io.spotifystreamer.fragments.NoResultsFragment;
import dalekocian.github.io.spotifystreamer.fragments.TopTenTracksFragment;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import kaaes.spotify.webapi.android.models.Artist;

public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ArtistSearchFragment.Callback {
    private static final String TAG = ArtistSearchActivity.class.getName();

    public static final String SEARCH_STRING_BUNDLE_KEY = "SEARCH_STRING";
    public static final String VISIBLE_FRAGMENT_TAG_BUNDLE_KEY = "VISIBLE_FRAGMENT_TAG";
    public static final String SAVED_FRAGMENT = "SAVED_FRAGMENT";
    private ArtistSearchFragment artistSearchFragment;
    private String searchString = "";
    private String visibleFragmentTag = "";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_search_ui);
        if (findViewById(R.id.item_list) != null) {
            mTwoPane = true;
            ((ArtistSearchFragment) getSupportFragmentManager().findFragmentById(R.id.item_list)).setActivateOnItemClick(true);
        }
        if (savedInstanceState == null) {
            visibleFragmentTag = ArtistSearchInstructionsFragment.class.getName();
            artistSearchFragment = new ArtistSearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fContainer, new ArtistSearchInstructionsFragment(), visibleFragmentTag)
                    .add(R.id.fContainer, artistSearchFragment, ArtistSearchFragment.class.getName())
                    .hide(artistSearchFragment).commit();
        } else {
            searchString = savedInstanceState.getString(SEARCH_STRING_BUNDLE_KEY);
            visibleFragmentTag = savedInstanceState.getString(VISIBLE_FRAGMENT_TAG_BUNDLE_KEY);
            artistSearchFragment = (ArtistSearchFragment) getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT);
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
            showScreen(new ArtistSearchInstructionsFragment(), ArtistSearchInstructionsFragment.class.getName());
        } else if (!searchString.equals(this.searchString)) {
            this.searchString = searchString;
            artistSearchFragment.search(searchString);
        }
        return false;
    }

    private void showScreen(Fragment fragment, String fragmentTag) {
        hideArtistSearchFragmentIfVisible();
        if (!this.visibleFragmentTag.equals(fragmentTag)) {
            removeFragmentIfNotArtistSearchFragment(getCurrentFragment());
            getSupportFragmentManager().beginTransaction().add(R.id.fContainer, fragment, fragmentTag).commit();
            this.visibleFragmentTag = fragmentTag;
        }
    }

    private void removeFragmentIfNotArtistSearchFragment(Fragment fragment) {
        if (fragment != null && !ArtistSearchFragment.class.getName().equals(this.visibleFragmentTag)) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void hideArtistSearchFragmentIfVisible() {
        if (artistSearchFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(artistSearchFragment).commit();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(this.visibleFragmentTag);
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
        if (!ArtistSearchFragment.class.getName().equals(this.visibleFragmentTag)) {
            removeFragmentIfNotArtistSearchFragment(getCurrentFragment());
            this.visibleFragmentTag = ArtistSearchFragment.class.getName();
            getSupportFragmentManager().beginTransaction().show(artistSearchFragment).commit();
        }
    }

    @Override
    public void onItemSelected(Artist artist) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ExtraKeys.ARTIST_ID, artist.id);
            arguments.putString(ExtraKeys.ARTIST_NAME, artist.name);
            TopTenTracksFragment fragment = new TopTenTracksFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
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
        outState.putString(VISIBLE_FRAGMENT_TAG_BUNDLE_KEY, visibleFragmentTag);
        getSupportFragmentManager().putFragment(outState, SAVED_FRAGMENT, artistSearchFragment);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }
}