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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.ArtistSearchResultsAdapter;
import dalekocian.github.io.spotifystreamer.listeners.LazyLoadListener;
import dalekocian.github.io.spotifystreamer.model.ParcelableArtist;
import dalekocian.github.io.spotifystreamer.services.ArtistSearchService;
import dalekocian.github.io.spotifystreamer.utils.Constants;
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
    private LinearLayout llNoResultsFound;
    private ListView lvListItems;
    private TextView tvNoResultsFound;
    private LinearLayout llProgressView;
    private RelativeLayout rlLoadingScreen;
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
        llNoResultsFound = (LinearLayout) findViewById(R.id.llNoResultsFound);
        tvNoResultsFound = (TextView) llNoResultsFound.findViewById(R.id.tvNoResultsFound);
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
        artistSearchService = new ArtistSearchService(this, getArtistSearchResponseListener(lazyLoadListener))
                .setCallback(getArtistSearchCallback());
        lvListItems.setOnScrollListener(lazyLoadListener);
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

    private ArtistSearchService.ResponseListener getArtistSearchResponseListener(final LazyLoadListener lazyLoadListener) {
        return new ArtistSearchService.ResponseListener() {
            @Override
            public void onResponse(ArtistsPager result) {
                Pager<Artist> artistPager = result.artists;
                showResults();
                if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty()) {
                    showNoResults();
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
        if (artistPager.previous == null) {
            lvListItems.setSelection(0);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<ParcelableArtist> parcelableArtistArrayList = new ArrayList<>(artistSearchResultsAdapter.getArtistList().size());
        for (Artist artist : artistSearchResultsAdapter.getArtistList()) {
            parcelableArtistArrayList.add(new ParcelableArtist(artist));
        }
        outState.putParcelableArrayList(ARTISTS_BUNDLE_KEY, parcelableArtistArrayList);
        outState.putCharSequence(SEARCH_STRING_BUNDLE_KEY, searchString);
        outState.putInt(RESOURCE_ID_VISIBLE_LAYOUT_BUNDLE_KEY, getResourceIdForVisibleLayout());
        outState.putInt(Constants.LIST_POSITION_BUNDLE_KEY, lvListItems.getFirstVisiblePosition());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt(Constants.LIST_POSITION_BUNDLE_KEY, 0);
        ArrayList<ParcelableArtist> parcelableArrayList = savedInstanceState.getParcelableArrayList(ARTISTS_BUNDLE_KEY);
        searchString = savedInstanceState.getString(SEARCH_STRING_BUNDLE_KEY);
        int resourceId = savedInstanceState.getInt(RESOURCE_ID_VISIBLE_LAYOUT_BUNDLE_KEY);
        artistSearchResultsAdapter.clear();
        for (ParcelableArtist artist : parcelableArrayList) {
            artistSearchResultsAdapter.add(artist.getArtist());
        }
        artistSearchResultsAdapter.notifyDataSetChanged();
        if (lvListItems.getId() == resourceId) {
            showResults();
        }
        lvListItems.setSelection(position);
    }

    @Override
    public boolean onQueryTextSubmit(String artistName) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchString) {
        this.searchString = searchString;
        if (searchString.isEmpty()) {
            artistSearchResultsAdapter.clear();
            artistSearchResultsAdapter.notifyDataSetChanged();
            showInstructionScreen();
        } else {
            artistSearchService.searchArtist(searchString);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist selectedArtist = artistSearchResultsAdapter.getItem(position);
        Intent topTenTrackIntent = new Intent(this, TopTenTrackActivity.class);
        topTenTrackIntent.putExtra(ExtraKeys.ARTIST_ID, selectedArtist.id);
        topTenTrackIntent.putExtra(ExtraKeys.ARTIST_NAME, selectedArtist.name);
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
        llNoResultsFound.setVisibility(View.GONE);
        rlLoadingScreen.setVisibility(View.VISIBLE);
    }

    private void showInstructionScreen() {
        rlLoadingScreen.setVisibility(View.GONE);
        lvListItems.setVisibility(View.GONE);
        llNoResultsFound.setVisibility(View.GONE);
        llInstructionScreen.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        rlLoadingScreen.setVisibility(View.GONE);
        llInstructionScreen.setVisibility(View.GONE);
        llNoResultsFound.setVisibility(View.GONE);
        lvListItems.setVisibility(View.VISIBLE);
    }

    private void showNoResults() {
        tvNoResultsFound.setText(Constants.NO_RESULTS + Constants.SINGLE_QUOTE + searchString + Constants.SINGLE_QUOTE);
        rlLoadingScreen.setVisibility(View.GONE);
        llInstructionScreen.setVisibility(View.GONE);
        lvListItems.setVisibility(View.GONE);
        llNoResultsFound.setVisibility(View.VISIBLE);
    }
}