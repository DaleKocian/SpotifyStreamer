package dalekocian.github.io.spotifystreamer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import dalekocian.github.io.spotifystreamer.listeners.LazyLoadListener;
import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by k557782 on 6/25/2015.
 */
public class ArtistSearch {
    public static final String NO_ARTISTS_FOUND = "No artists found!";
    private final Context context;
    private final ArrayAdapter<Artist> arrayAdapter;
    private final LazyLoadListener lazyLoadListener;
    private SpotifyService spotifyService;
    public static final String OFFSET_KEY = "offset";
    public static final String LIMIT_KEY = "limit";
    private int total;
    private int offset;
    private int limit;
    private String artistName;

    public ArtistSearch(Context context, ArrayAdapter<Artist> arrayAdapter, LazyLoadListener lazyLoadListener) {
        this.context = context;
        this.arrayAdapter = arrayAdapter;
        this.lazyLoadListener = lazyLoadListener;
        spotifyService = new SpotifyApi().getService();
    }

    public boolean searchArtistNext() {
        if (offset + limit >= total) {
            return false;
        }
        offset += limit;
        checkNetworkAndExecute(artistName);
        return true;
    }

    public void searchArtist(String artistName) {
        total = 0;
        offset = 0;
        limit = 20;
        checkNetworkAndExecute(artistName);
    }

    private void checkNetworkAndExecute(String artistName) {
        if (Utils.hasInternetConnection(context)) {
            new AsyncArtistSearch().execute(artistName);
        } else {
            Toast.makeText(context, Constants.NETWORK_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateArrayAdapter(Pager<Artist> artistPager) {
        if (artistPager.previous == null) {
            arrayAdapter.clear();
        }
        arrayAdapter.addAll(artistPager.items);
        arrayAdapter.notifyDataSetChanged();
    }

    class AsyncArtistSearch extends AsyncTask<String, Integer, ArtistsPager> {

        @Override
        protected ArtistsPager doInBackground(String... query) {
            artistName = query[0];
            Map<String, Object> params = new HashMap<>();
            params.put(OFFSET_KEY, offset);
            params.put(LIMIT_KEY, limit);
            return spotifyService.searchArtists(artistName, params);
        }

        @Override
        protected void onPostExecute(ArtistsPager result) {
            Pager<Artist> artistPager = result.artists;
            if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty()) {
                Toast.makeText(context, NO_ARTISTS_FOUND, Toast.LENGTH_SHORT).show();
            } else {
                total = artistPager.total;
                updateArrayAdapter(artistPager);
            }
            lazyLoadListener.finishLoading();
        }
    }
}
