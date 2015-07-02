package dalekocian.github.io.spotifystreamer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.ArtistSearchResultsAdapter;
import dalekocian.github.io.spotifystreamer.listeners.LazyLoadListener;
import dalekocian.github.io.spotifystreamer.model.SerializableArtist;
import dalekocian.github.io.spotifystreamer.services.ArtistSearchService;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    private static final String TAG = ArtistSearchActivity.class.getName();
    public static final String ARTISTS_BUNDLE_KEY = "ARTISTS_BUNDLE_KEY";
    public static final String SEARCH_STRING_BUNDLE_KEY = "SEARCH_STRING";
    public static final String RESOURCE_ID_VISIBLE_LAYOUT_BUNDLE_KEY = "RESOURCE_ID_VISIBLE_LAYOUT";
    private ArtistSearchResultsAdapter artistSearchResultsAdapter;
    private ArtistSearchService artistSearchService;
    private LinearLayout llInstructionScreen;
    private ListView lvListItems;
    private LinearLayout llProgressView;
    private RelativeLayout rlLoadingScreen;
    public static final String NO_ARTISTS_FOUND = "No artists found!";
    private String searchString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_search_ui);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
        lvListItems = (ListView) findViewById(R.id.lvListItems);
        llInstructionScreen = (LinearLayout) findViewById(R.id.llInstructionScreen);
        llProgressView = (LinearLayout) findViewById(R.id.llProgressView);
        rlLoadingScreen = (RelativeLayout) findViewById(R.id.rlLoadingScreen);
        artistSearchResultsAdapter = new ArtistSearchResultsAdapter(this, R.layout.lv_row_search_results, new ArrayList<Artist>(0));
        lvListItems.setAdapter(artistSearchResultsAdapter);
        lvListItems.setOnItemClickListener(this);
        LazyLoadListener lazyLoadListener = new LazyLoadListener() {
            @Override
            public void addNewElements() {
                boolean executedSearch = artistSearchService.searchArtistNext();
                if (executedSearch) {
                    llProgressView.setVisibility(View.VISIBLE);
                }
            }
        };
        artistSearchService = new ArtistSearchService(this, getArtistSearchResponseListener(lazyLoadListener));
        lvListItems.setOnScrollListener(lazyLoadListener);
    }

    private ArtistSearchService.ResponseListener getArtistSearchResponseListener(final LazyLoadListener lazyLoadListener) {
        return new ArtistSearchService.ResponseListener() {
            @Override
            public void onResponse(ArtistsPager result) {
                Pager<Artist> artistPager = result.artists;
                showResults();
                if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty()) {
                    Toast.makeText(ArtistSearchActivity.this, NO_ARTISTS_FOUND, Toast.LENGTH_SHORT).show();
                } else {
                    artistSearchService.setTotal(artistPager.total);
                    updateArrayAdapter(artistPager);
                }
                lazyLoadListener.doneLoading();
            }
        };
    }

    private void updateArrayAdapter(Pager<Artist> artistPager) {
        if (artistPager.previous == null) {
            artistSearchResultsAdapter.clear();
        }
        artistSearchResultsAdapter.addAll(artistPager.items);
        llProgressView.setVisibility(View.GONE);
        artistSearchResultsAdapter.notifyDataSetChanged();
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
        ArrayList<SerializableArtist> serializableArtists = new ArrayList<>(artistSearchResultsAdapter.getArtistList().size());
        for (Artist artist : artistSearchResultsAdapter.getArtistList()) {
            serializableArtists.add(new SerializableArtist(artist));
        }
        outState.putSerializable(ARTISTS_BUNDLE_KEY, serializableArtists);
        outState.putCharSequence(SEARCH_STRING_BUNDLE_KEY, searchString);
        outState.putInt(RESOURCE_ID_VISIBLE_LAYOUT_BUNDLE_KEY, getResourceIdForVisibleLayout());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        List<SerializableArtist> artistList = (ArrayList<SerializableArtist>) savedInstanceState.getSerializable(ARTISTS_BUNDLE_KEY);
        searchString = savedInstanceState.getString(SEARCH_STRING_BUNDLE_KEY);
        int resourceId = savedInstanceState.getInt(RESOURCE_ID_VISIBLE_LAYOUT_BUNDLE_KEY);
        artistSearchResultsAdapter.clear();
        artistSearchResultsAdapter.addAll(artistList);
        artistSearchResultsAdapter.notifyDataSetChanged();
        if (lvListItems.getId() == resourceId) {
            showResults();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String artistName) {
        showLoadingScreen();
        artistSearchService.searchArtist(artistName);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchString = newText;
        if (searchString.isEmpty()) {
            showInstructionScreen();
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist selectedArtist = artistSearchResultsAdapter.getItem(position);
        Intent topTenTrackIntent = new Intent(this, TopTenTrackActivity.class);
        topTenTrackIntent.putExtra(ExtraKeys.ARTIST_ID, selectedArtist.id);
        startActivity(topTenTrackIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        artistSearchService.cancel();
    }

    private int getResourceIdForVisibleLayout() {
        if (Utils.isVisible(lvListItems)) {
            return lvListItems.getId();
        }
        return llInstructionScreen.getId();
    }

    private void showLoadingScreen() {
        lvListItems.setVisibility(View.GONE);
        llInstructionScreen.setVisibility(View.GONE);
        rlLoadingScreen.setVisibility(View.VISIBLE);
    }

    private void showInstructionScreen() {
        rlLoadingScreen.setVisibility(View.GONE);
        lvListItems.setVisibility(View.GONE);
        llInstructionScreen.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        rlLoadingScreen.setVisibility(View.GONE);
        llInstructionScreen.setVisibility(View.GONE);
        lvListItems.setVisibility(View.VISIBLE);
    }
}