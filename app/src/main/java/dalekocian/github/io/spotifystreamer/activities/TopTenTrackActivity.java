package dalekocian.github.io.spotifystreamer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.TopTenTracksAdapter;
import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.services.TopTenTrackSearchService;
import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTenTrackActivity extends AppCompatActivity {
    private static final String TAG = TopTenTrackActivity.class.getName();
    public static final String TOP_TEN_TRACKS_BUNDLE_KEY = "TOP_TEN_TRACKS_BUNDLE_KEY";
    private TopTenTracksAdapter topTenTracksAdapter;
    private RelativeLayout rlLoadingScreen;
    private ListView lvListItems;
    private LinearLayout llNoResultsFound;
    private ViewStub vsNoResultsFound;
    private TopTenTrackSearchService topTenTrackSearchService;
    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_ten_tracks_ui);
        rlLoadingScreen = (RelativeLayout) findViewById(R.id.rlLoadingScreen);
        lvListItems = (ListView) findViewById(R.id.lvListItems);
        vsNoResultsFound = (ViewStub) findViewById(R.id.vsNoResultsFound);
        topTenTracksAdapter = new TopTenTracksAdapter(this, R.layout.lv_row_top_ten_tracks, new ArrayList<Track>(0));
        lvListItems.setAdapter(topTenTracksAdapter);
        topTenTrackSearchService = new TopTenTrackSearchService(this, getTopTenTrackSearchResponseListener());
        topTenTrackSearchService.setCallback(new TopTenTrackSearchService.Callback() {
            @Override
            public void onPreExecute() {
                showLoadingScreen();
            }
        });
        countryCode = Utils.getCountryCodeFromSettings(this);
        if (savedInstanceState == null) {
            callTopTenTrackSearch();
        }
    }

    private void setUpTextViewForNoResultsFound() {
        ((TextView) llNoResultsFound.findViewById(R.id.tvNoResultsFound)).setText(
                Constants.NO_RESULTS + Constants.SINGLE_QUOTE +
                        getIntent().getStringExtra(ExtraKeys.ARTIST_NAME) + Constants.SINGLE_QUOTE);
        ((TextView) llNoResultsFound.findViewById(R.id.tvDescription)).setText(Constants.PLEASE_TRY_ANOTHER_ARTIST);
    }

    private TopTenTrackSearchService.ResponseListener getTopTenTrackSearchResponseListener() {
        return new TopTenTrackSearchService.ResponseListener() {
            @Override
            public void onResponse(Tracks tracks) {
                if (tracks != null) {
                    List<Track> trackList = tracks.tracks;
                    topTenTracksAdapter.clear();
                    topTenTracksAdapter.addAll(trackList);
                    topTenTracksAdapter.notifyDataSetChanged();
                    showResults();
                } else {
                    showNoResultsFound();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentCountryCode = Utils.getCountryCodeFromSettings(this);
        if (!countryCode.equals(currentCountryCode)) {
            countryCode = currentCountryCode;
            callTopTenTrackSearch();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<ParcelableTrack> trackArrayList = new ArrayList<>(topTenTracksAdapter.getTrackList().size());
        for (Track track : topTenTracksAdapter.getTrackList()) {
            trackArrayList.add(new ParcelableTrack(track));
        }
        outState.putParcelableArrayList(TOP_TEN_TRACKS_BUNDLE_KEY, trackArrayList);
        outState.putInt(Constants.LIST_POSITION_BUNDLE_KEY, lvListItems.getFirstVisiblePosition());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt(Constants.LIST_POSITION_BUNDLE_KEY, 0);
        ArrayList<ParcelableTrack> trackArrayList = savedInstanceState.getParcelableArrayList(TOP_TEN_TRACKS_BUNDLE_KEY);
        topTenTracksAdapter.clear();
        for (ParcelableTrack parcelableTrack : trackArrayList) {
            topTenTracksAdapter.add(parcelableTrack.getTrack());
        }
        topTenTracksAdapter.notifyDataSetChanged();
        lvListItems.setSelection(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void showNoResultsFound() {
        if (llNoResultsFound == null) {
            llNoResultsFound = (LinearLayout) vsNoResultsFound.inflate();
            setUpTextViewForNoResultsFound();
        }
        lvListItems.setVisibility(View.GONE);
        rlLoadingScreen.setVisibility(View.GONE);
        llNoResultsFound.setVisibility(View.VISIBLE);
    }

    private void showLoadingScreen() {
        lvListItems.setVisibility(View.GONE);
        if (null != llNoResultsFound) {
            llNoResultsFound.setVisibility(View.GONE);
        }
        rlLoadingScreen.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        rlLoadingScreen.setVisibility(View.GONE);
        if (null != llNoResultsFound) {
            llNoResultsFound.setVisibility(View.GONE);
        }
        lvListItems.setVisibility(View.VISIBLE);
    }

    private void callTopTenTrackSearch() {
        String artistId = getIntent().getStringExtra(ExtraKeys.ARTIST_ID);
        topTenTrackSearchService.searchTopTenTracks(artistId, countryCode);
    }
}
