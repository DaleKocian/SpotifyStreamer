package dalekocian.github.io.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalekocian.github.io.spotifystreamer.Utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.adapters.TopTenTracksAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class TopTenTrackActivity extends AppCompatActivity {
    private static final String TAG = TopTenTrackActivity.class.getName();
    private TopTenTracksAdapter topTenTracksAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_ui);
        ListView lvListItems = (ListView) findViewById(R.id.lvListItems);
        String artistId = getIntent().getStringExtra(ExtraKeys.ARTIST_ID);
        topTenTracksAdapter = new TopTenTracksAdapter(this, R.layout.lv_row_top_ten_tracks, new ArrayList<Track>(0));
        lvListItems.setAdapter(topTenTracksAdapter);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchArtistsTopTracks extends AsyncTask<String, Integer, Tracks> {
        protected Tracks doInBackground(String... query) {
            String q = query[0];
            SpotifyService spotifyService = new SpotifyApi().getService();
            Map<String, Object> params = new HashMap<>();
            params.put("country", "US");
            return spotifyService.getArtistTopTrack(q, params);
        }

        protected void onPostExecute(Tracks tracks) {
            List<Track> trackList = tracks.tracks;
            topTenTracksAdapter.addAll(trackList);
            topTenTracksAdapter.notifyDataSetChanged();
        }
    }
}
