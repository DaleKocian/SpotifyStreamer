package dalekocian.github.io.spotifystreamer.services;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.Prefkeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class TopTenTrackSearchService {
    private final Context context;
    private Callback callback;
    private ResponseListener response;

    private static final Callback EMPTY_CALLBACK = new Callback() {
        @Override
        public void onPreExecute() {

        }
    };

    public TopTenTrackSearchService(Context context, ResponseListener response) {
        this.context = context;
        this.response = response;
        this.callback = EMPTY_CALLBACK;
    }

    public void searchTopTenTracks(String artistId, String countryCode) {
        checkNetworkAndExecute(artistId, countryCode);
    }

    private void checkNetworkAndExecute(String artistId, String countryCode) {
        if (Utils.hasInternetConnection(context)) {
            callback.onPreExecute();
            new SearchArtistsTopTracks().execute(artistId, countryCode);
        } else {
            Toast.makeText(context, Constants.NETWORK_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    class SearchArtistsTopTracks extends AsyncTask<String, Integer, Tracks> {
        protected Tracks doInBackground(String... query) {
            String artistId = query[0];
            String countryCode = query[1];
            SpotifyService spotifyService = new SpotifyApi().getService();
            Map<String, Object> params = new HashMap<>();
            params.put(Prefkeys.COUNTRY_PARAM_KEY, countryCode);
            try {
                return spotifyService.getArtistTopTrack(artistId, params);
            } catch (RetrofitError e) {
                return null;
            }
        }

        protected void onPostExecute(Tracks tracks) {
            response.onResponse(tracks);
        }
    }

    public TopTenTrackSearchService setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public interface Callback {
        void onPreExecute();
    }

    public interface ResponseListener {
        void onResponse(Tracks tracks);
    }
}
