package dalekocian.github.io.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalekocian.github.io.spotifystreamer.Utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.Utils.Utils;
import dalekocian.github.io.spotifystreamer.adapters.TopTenTracksAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


public class TopTenTrackActivity extends AppCompatActivity {
    private static final String TAG = TopTenTrackActivity.class.getName();
    public static final String COUNTRY_PARAM_KEY = "country";
    public static final String NO_TRACKS_FOUND = "No Tracks Found";
    private TopTenTracksAdapter topTenTracksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_ui);
        ListView lvListItems = (ListView) findViewById(R.id.lvListItems);
        topTenTracksAdapter = new TopTenTracksAdapter(this, R.layout.lv_row_top_ten_tracks, new ArrayList<Track>(0));
        lvListItems.setAdapter(topTenTracksAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String artistId = getIntent().getStringExtra(ExtraKeys.ARTIST_ID);
        new SearchArtistsTopTracks().execute(artistId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_ten_track, menu);
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

    private class SearchArtistsTopTracks extends AsyncTask<String, Integer, Tracks> {
        protected Tracks doInBackground(String... query) {
            String q = query[0];
            SpotifyService spotifyService = new SpotifyApi().getService();
            Map<String, Object> params = new HashMap<>();
            params.put(COUNTRY_PARAM_KEY, Utils.getCountryCode(TopTenTrackActivity.this));
            try {
                return spotifyService.getArtistTopTrack(q, params);
            } catch (RetrofitError e) {
                return null;
            }
        }

        protected void onPostExecute(Tracks tracks) {
            if (tracks != null) {
                List<Track> trackList = tracks.tracks;
                topTenTracksAdapter.addAll(trackList);
                topTenTracksAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(TopTenTrackActivity.this, NO_TRACKS_FOUND, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
