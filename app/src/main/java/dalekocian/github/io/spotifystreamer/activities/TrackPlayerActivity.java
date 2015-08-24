package dalekocian.github.io.spotifystreamer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.fragments.TrackPlayerFragment;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;

/**
 * Created by dkocian on 8/11/2015.
 */
public class TrackPlayerActivity extends AppCompatActivity {
    private static final String TAG = TrackPlayerActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_player_ui);
        Bundle args = new Bundle();
        args.putParcelableArrayList(ExtraKeys.TRACK_LIST, getIntent().getParcelableArrayListExtra(ExtraKeys.TRACK_LIST));
        args.putInt(ExtraKeys.POSITION, getIntent().getIntExtra(ExtraKeys.POSITION, 0));
        TrackPlayerFragment trackPlayerFragment = new TrackPlayerFragment();
        trackPlayerFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fContainer, trackPlayerFragment, TrackPlayerFragment.class.getName()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
