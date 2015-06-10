package dalekocian.github.io.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.Utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.adapters.ArtistSearchResultsAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;


public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    private static final String TAG = ArtistSearchActivity.class.getName();
    public static final String NO_ARTISTS_FOUND = "No artists found!";
    public static final String ARTISTS_BUNDLE_KEY = "ARTISTS_BUNDLE_KEY";
    private SpotifyService spotifyService;
    private ArtistSearchResultsAdapter artistSearchResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
        spotifyService = new SpotifyApi().getService();
        ListView lvArtistResults = (ListView) findViewById(R.id.lvArtistResults);
        artistSearchResultsAdapter = new ArtistSearchResultsAdapter(this, R.layout.lv_row_search_results, new ArrayList<Artist>(0));
        lvArtistResults.setAdapter(artistSearchResultsAdapter);
        lvArtistResults.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(this);
        return true;
    }

/*    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARTISTS_BUNDLE_KEY, (ArrayList<Artist>) artistSearchResultsAdapter.getArtistList());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        List<Artist> artistList = (ArrayList<Artist>)savedInstanceState.getSerializable(ARTISTS_BUNDLE_KEY);
        updateListView(artistList);
        super.onRestoreInstanceState(savedInstanceState);
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        new SearchArtists().execute(query);
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

    public void updateListView(List<Artist> artistList) {
        artistSearchResultsAdapter.clear();
        artistSearchResultsAdapter.addAll(artistList);
        artistSearchResultsAdapter.notifyDataSetChanged();
    }

    private class SearchArtists extends AsyncTask<String, Integer, ArtistsPager> {
        protected ArtistsPager doInBackground(String... query) {
            String q = query[0];
            return spotifyService.searchArtists(q);
        }

        protected void onPostExecute(ArtistsPager result) {
            Pager<Artist> artistPager = result.artists;
            if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty() ) {
                Toast.makeText(ArtistSearchActivity.this, NO_ARTISTS_FOUND, Toast.LENGTH_SHORT).show();
            } else {
                updateListView(artistPager.items);
            }
        }
    }
}
