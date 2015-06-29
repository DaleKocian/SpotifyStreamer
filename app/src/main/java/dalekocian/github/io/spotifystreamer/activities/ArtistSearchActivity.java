package dalekocian.github.io.spotifystreamer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.ArtistSearchResultsAdapter;
import dalekocian.github.io.spotifystreamer.asynctasks.ArtistSearch;
import dalekocian.github.io.spotifystreamer.listeners.LazyLoadListener;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;


public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    private static final String TAG = ArtistSearchActivity.class.getName();
    public static final String ARTISTS_BUNDLE_KEY = "ARTISTS_BUNDLE_KEY";
    private ArtistSearchResultsAdapter artistSearchResultsAdapter;
    private ArtistSearch artistSearch;
    private LinearLayout llProgressView;
    public static final String NO_ARTISTS_FOUND = "No artists found!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_ui);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
        ListView lvListItems = (ListView) findViewById(R.id.lvListItems);
        llProgressView = (LinearLayout) findViewById(R.id.llProgressView);
        artistSearchResultsAdapter = new ArtistSearchResultsAdapter(this, R.layout.lv_row_search_results, new ArrayList<Artist>(0));
        lvListItems.setAdapter(artistSearchResultsAdapter);
        lvListItems.setOnItemClickListener(this);
        LazyLoadListener lazyLoadListener = new LazyLoadListener() {
            @Override
            public void addNewElements() {
                boolean executedSearch = artistSearch.searchArtistNext();
                if (executedSearch) {
                    llProgressView.setVisibility(View.VISIBLE);
                }
            }
        };
        artistSearch = new ArtistSearch(this, getArtistSearchResponseListener(lazyLoadListener));
        lvListItems.setOnScrollListener(lazyLoadListener);

    }

    private ArtistSearch.ResponseListener getArtistSearchResponseListener(final LazyLoadListener lazyLoadListener) {
        return new ArtistSearch.ResponseListener() {
            @Override
            public void onResponse(ArtistsPager result) {
                Pager<Artist> artistPager = result.artists;
                if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty()) {
                    Toast.makeText(ArtistSearchActivity.this, NO_ARTISTS_FOUND, Toast.LENGTH_SHORT).show();
                } else {
                    artistSearch.setTotal(artistPager.total);
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

/*    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<ParcelableArtist> serializableArtists = new ArrayList<>(artistSearchResultsAdapter.getArtistList().size());
        for (Artist artist : artistSearchResultsAdapter.getArtistList()) {
            serializableArtists.add(new ParcelableArtist(artist));
        }
        outState.putParcelableArrayList(ARTISTS_BUNDLE_KEY, serializableArtists);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        List<ParcelableArtist> artistList = (ArrayList<ParcelableArtist>)savedInstanceState.getSerializable(ARTISTS_BUNDLE_KEY);
        artistSearchResultsAdapter.clear();
        artistSearchResultsAdapter.addAll(artistList);
        artistSearchResultsAdapter.notifyDataSetChanged();
        super.onRestoreInstanceState(savedInstanceState);
    }*/

    @Override
    public boolean onQueryTextSubmit(String artistName) {
        artistSearch.searchArtist(artistName);
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist selectedArtist = artistSearchResultsAdapter.getItem(position);
        Intent topTenTrackIntent = new Intent(this, TopTenTrackActivity.class);
        topTenTrackIntent.putExtra(ExtraKeys.ARTIST_ID, selectedArtist.id);
        startActivity(topTenTrackIntent);
    }
}