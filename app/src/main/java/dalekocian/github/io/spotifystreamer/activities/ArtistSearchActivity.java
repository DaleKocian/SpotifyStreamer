package dalekocian.github.io.spotifystreamer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ArtistSearchFragment.Callback {
    private static final String TAG = ArtistSearchActivity.class.getName();
    public static final String ARTISTS_BUNDLE_KEY = "ARTISTS_BUNDLE_KEY";
    public static final String SEARCH_STRING_BUNDLE_KEY = "SEARCH_STRING";
    private ArtistSearchFragment artistSearchFragment;
    private String searchString = "";
    private String fragmentTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_search_ui);
        fragmentTag = ArtistSearchInstructionsFragment.class.getSimpleName();
        artistSearchFragment = new ArtistSearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fContainer, new ArtistSearchInstructionsFragment(), fragmentTag)
                .add(R.id.fContainer, artistSearchFragment, ArtistSearchFragment.class.getSimpleName())
                .hide(artistSearchFragment).commit();
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
        this.searchString = searchString;
        if (searchString.isEmpty()) {
            showScreen(new ArtistSearchInstructionsFragment(), ArtistSearchInstructionsFragment.class.getSimpleName());
        } else {
            artistSearchFragment.search(searchString);
        }
        return false;
    }

    private void showScreen(Fragment fragment, String fragmentTag) {
        hideArtistSearchFragmentIfVisible();
        if (!this.fragmentTag.equals(fragmentTag)) {
            removeFragmentIfNotArtistSearchFragment(getCurrentFragment());
            getSupportFragmentManager().beginTransaction().add(R.id.fContainer, fragment, fragmentTag).commit();
            this.fragmentTag = fragmentTag;
        }
    }

    private void removeFragmentIfNotArtistSearchFragment(Fragment fragment) {
        if (fragment != null && !ArtistSearchFragment.class.getSimpleName().equals(this.fragmentTag)) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void hideArtistSearchFragmentIfVisible() {
        if (artistSearchFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(artistSearchFragment).commit();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(this.fragmentTag);
    }

    @Override
    public void onNoResults() {
        showScreen(new NoResultsFragment(), NoResultsFragment.class.getSimpleName());
    }

    @Override
    public void onLoading() {
        showScreen(new LoadingFragment(), LoadingFragment.class.getSimpleName());
    }

    @Override
    public void onResults() {
        if (!ArtistSearchFragment.class.getSimpleName().equals(this.fragmentTag)) {
            removeFragmentIfNotArtistSearchFragment(getCurrentFragment());
            this.fragmentTag = ArtistSearchFragment.class.getSimpleName();
            getSupportFragmentManager().beginTransaction().show(artistSearchFragment).commit();
        }
    }
}