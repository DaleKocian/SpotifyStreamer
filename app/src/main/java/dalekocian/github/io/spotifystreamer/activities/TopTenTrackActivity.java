package dalekocian.github.io.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.TopTenTracksAdapter;
import dalekocian.github.io.spotifystreamer.services.TopTenTrackSearchService;
import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTenTrackActivity extends AppCompatActivity {
    private static final String TAG = TopTenTrackActivity.class.getName();
    public static final String NO_TRACKS_FOUND = "No Tracks Found";
    public static final String TOP_TEN_TRACKS_BUNDLE_KEY = "TOP_TEN_TRACKS_BUNDLE_KEY";
    private TopTenTracksAdapter topTenTracksAdapter;
    private RelativeLayout rlLoadingScreen;
    private ListView lvListItems;
    private TopTenTrackSearchService topTenTrackSearchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_ten_tracks_ui);
        rlLoadingScreen = (RelativeLayout) findViewById(R.id.rlLoadingScreen);
        lvListItems = (ListView) findViewById(R.id.lvListItems);
        topTenTracksAdapter = new TopTenTracksAdapter(this, R.layout.lv_row_top_ten_tracks, new ArrayList<Track>(0));
        lvListItems.setAdapter(topTenTracksAdapter);
        topTenTrackSearchService = new TopTenTrackSearchService(this, getTopTenTrackSearchResponseListener());
    }

    private TopTenTrackSearchService.ResponseListener getTopTenTrackSearchResponseListener() {
        return new TopTenTrackSearchService.ResponseListener() {
            @Override
            public void onResponse(Tracks tracks) {
                showResults();
                if (tracks != null) {
                    List<Track> trackList = tracks.tracks;
                    topTenTracksAdapter.addAll(trackList);
                    topTenTracksAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TopTenTrackActivity.this, NO_TRACKS_FOUND, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        String artistId = getIntent().getStringExtra(ExtraKeys.ARTIST_ID);
        if (Utils.hasInternetConnection(this)) {
            showLoadingScreen();
            topTenTrackSearchService.searchTopTenTracks(artistId);
        } else {
            Toast.makeText(this, Constants.NETWORK_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
        }
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

    private void showLoadingScreen() {
        lvListItems.setVisibility(View.GONE);
        rlLoadingScreen.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        rlLoadingScreen.setVisibility(View.GONE);
        lvListItems.setVisibility(View.VISIBLE);
    }
}
