package dalekocian.github.io.spotifystreamer.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by k557782 on 6/25/2015.
 */
public class ArtistSearchService {

    private static final String TAG = ArtistSearchService.class.getName();
    public static final String NO_CURRENT_SEARCHES_IN_THE_STACK = "No current searches in the stack";
    private final Context context;
    private SpotifyService spotifyService;
    public static final String OFFSET_KEY = "offset";
    public static final String LIMIT_KEY = "limit";
    private int total;
    private int offset;
    private int limit;
    private String artistName;
    private ResponseListener response;
    private Stack<AsyncArtistSearch> artistSearchStack;
    private Callback callback;

    private static final Callback EMPTY_CALLBACK =  new Callback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute() {

        }
    };

    public ArtistSearchService(Context context, ResponseListener response) {
        this.context = context;
        spotifyService = new SpotifyApi().getService();
        this.response = response;
        artistSearchStack = new Stack<>();
        callback = EMPTY_CALLBACK;
    }

    public boolean searchArtistNext() {
        if (offset + limit >= total) {
            return false;
        }
        offset += limit;
        checkNetworkAndExecute(artistName, false);
        return true;
    }

    public void searchArtist(String artistName) {
        total = 0;
        offset = 0;
        limit = 20;
        checkNetworkAndExecute(artistName);
    }

    public void cancel() {
        cancelPreviousSearch();
    }

    private void checkNetworkAndExecute(String artistName) {
        checkNetworkAndExecute(artistName, true);
    }

    private void checkNetworkAndExecute(String artistName, boolean isFirstCall) {
        if (Utils.hasInternetConnection(context)) {
            if (isFirstCall) {
                callback.onPreExecute();
            }
            cancelPreviousSearch();
            artistSearchStack.addElement(new AsyncArtistSearch());
            artistSearchStack.peek().execute(artistName);
        } else {
            Toast.makeText(context, Constants.NETWORK_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    public ArtistSearchService setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    private void cancelPreviousSearch() {
        try {
            artistSearchStack.pop().cancel(true);
        } catch (EmptyStackException e) {
            Log.d(TAG, NO_CURRENT_SEARCHES_IN_THE_STACK);
        }
    }

    public void setTotal(int total) {
        this.total = total;
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
            callback.onPostExecute();
            artistSearchStack.pop();
            response.onResponse(result);
        }
    }

    public interface Callback {
        void onPreExecute();
        void onPostExecute();
    }

    public interface ResponseListener {
        void onResponse(ArtistsPager result);
    }
}
