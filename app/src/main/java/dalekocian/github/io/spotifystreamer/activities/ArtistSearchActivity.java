package dalekocian.github.io.spotifystreamer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.fragments.ArtistSearchFragment;
import dalekocian.github.io.spotifystreamer.fragments.ArtistSearchInstructionsFragment;
import dalekocian.github.io.spotifystreamer.services.ArtistSearchService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ArtistSearchFragment.Callback {
    private static final String TAG = ArtistSearchActivity.class.getName();
    public static final String ARTISTS_BUNDLE_KEY = "ARTISTS_BUNDLE_KEY";
    public static final String SEARCH_STRING_BUNDLE_KEY = "SEARCH_STRING";
    private String searchString = "";
    private ArtistSearchFragment artistSearchFragment;
    private ArtistSearchService artistSearchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_search_ui);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fContainer, new ArtistSearchInstructionsFragment(), ArtistSearchInstructionsFragment.class.getSimpleName()).commit();
        artistSearchFragment = new ArtistSearchFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fContainer, artistSearchFragment).commit();
        getSupportFragmentManager().beginTransaction()
                .hide(artistSearchFragment).commit();

        artistSearchService = new ArtistSearchService(this, getArtistSearchResponseListener())
                .setCallback(getArtistSearchCallback());
    }

    private ArtistSearchService.Callback getArtistSearchCallback() {
        return new ArtistSearchService.Callback() {
            @Override
            public void onPreExecute() {
                showLoadingScreen();
            }

            @Override
            public void onPostExecute() {
            }
        };
    }

    private ArtistSearchService.ResponseListener getArtistSearchResponseListener() {
        return new ArtistSearchService.ResponseListener() {
            @Override
            public void onResponse(ArtistsPager result) {
                Pager<Artist> artistPager = result.artists;
                if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty()) {
                    showNoResultsScreen();
                } else {
                    showResultsScreen();
                    artistSearchService.setTotal(artistPager.total);
                    artistSearchFragment.updateArrayAdapter(artistPager);
                }
                artistSearchFragment.doneLazyLoading();
            }
        };
    }

    private void showResultsScreen() {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(ArtistSearchInstructionsFragment
                .class.getSimpleName())).commit();
        getSupportFragmentManager().beginTransaction()
                .show(artistSearchFragment).commit();
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
            showInstructionScreen();
        } else {
            artistSearchService.searchArtist(searchString);
        }
        return false;
    }

    private void showInstructionScreen() {
        getSupportFragmentManager().beginTransaction()
                .hide(artistSearchFragment).commit();
        if (getSupportFragmentManager().findFragmentByTag(ArtistSearchInstructionsFragment
                        .class.getSimpleName()) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fContainer, new ArtistSearchInstructionsFragment(), ArtistSearchInstructionsFragment.class.getSimpleName()).commit();
        }
    }

    private void showLoadingScreen() {

    }

    private void showNoResultsScreen() {

    }

    @Override
    public void onLoadMore() {
        boolean executedSearch = artistSearchService.searchArtistNext();
        if (executedSearch) {
            artistSearchFragment.showLoading();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        artistSearchService.cancel();
    }
}