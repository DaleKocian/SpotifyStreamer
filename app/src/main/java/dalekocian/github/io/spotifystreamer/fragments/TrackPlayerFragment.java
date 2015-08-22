package dalekocian.github.io.spotifystreamer.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.intellij.lang.annotations.MagicConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.services.MediaPlayerService;
import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import dalekocian.github.io.spotifystreamer.utils.Utils;

/**
 * Created by dkocian on 8/11/2015.
 */
public class TrackPlayerFragment extends Fragment implements View.OnClickListener {
    private static final int PLAY = 0;
    private static final int PAUSE = 1;

    @MagicConstant(intValues = {PLAY, PAUSE})
    @interface MediaPlayerActionName {
    }

    private static final String TAG = TrackPlayerFragment.class.getName();
    public static final int MAX_PROGRESS = 100;
    public static final int DELAY_MILLIS = 100;
    @Bind(R.id.tvArtistName)
    TextView mTvArtistName;
    @Bind(R.id.tvAlbumName)
    TextView mTvAlbumName;
    @Bind(R.id.ivAlbumArt)
    ImageView mIvAlbumArt;
    @Bind(R.id.sbDuration)
    SeekBar mSbDuration;
    @Bind(R.id.ivPrevious)
    ImageView mIvPrevious;
    @Bind(R.id.ivPlayPause)
    ImageView mIvPlayPause;
    @Bind(R.id.ivNext)
    ImageView mIvNext;
    @Bind(R.id.tvTrackName)
    TextView mTvTrackName;
    @Bind(R.id.tvCurrentTime)
    TextView mTvCurrentTime;
    @Bind(R.id.tvDuration)
    TextView mTvDuration;
    private MediaPlayerService mMediaPlayerService;
    private List<ParcelableTrack> trackList;
    private boolean mBound = false;
    private int currentPosition;
    private Handler mHandler = new Handler();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.MEDIA_PLAYER_START_ACTION.equals(intent.getAction())) {
                updateProgressBar();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_player_ui, container, false);
        ButterKnife.bind(this, view);
        trackList = getArguments().getParcelableArrayList(ExtraKeys.TRACK_LIST);
        currentPosition = getArguments().getInt(ExtraKeys.POSITION, 0);
        setupView(trackList.get(currentPosition));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.MEDIA_PLAYER_START_ACTION));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        intent.putParcelableArrayListExtra(ExtraKeys.TRACK_LIST, (ArrayList<ParcelableTrack>) trackList);
        intent.putExtra(ExtraKeys.POSITION, currentPosition);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupView(String artistName, String albumName, String albumImageUrl, String trackName) {
        mTvArtistName.setText(artistName);
        mTvAlbumName.setText(albumName);
        Picasso.with(getActivity()).load(albumImageUrl).into(mIvAlbumArt);
        mTvTrackName.setText(trackName);
        mTvCurrentTime.setText((Utils.millisecondsToTime(0)));
    }

    private void setupView(ParcelableTrack parcelableTrack) {
        setupView(parcelableTrack.artists.get(0).name, parcelableTrack.album.name, parcelableTrack.album.images.get(0).url,
                parcelableTrack.name);
    }

    private void setupView(MediaPlayerService.CurrentTrackInfo currentTrackInfo) {
        setupView(currentTrackInfo.getArtistName(), currentTrackInfo.getAlbumName(), currentTrackInfo.getAlbumImage().url,
                currentTrackInfo.getTrackName());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mIvPrevious.getId()) {
            playPreviousTrack();
        } else if (view.getId() == mIvNext.getId()) {
            playNextTrack();
        } else if (view.getId() == mIvPlayPause.getId() &&
                mIvPlayPause.getContentDescription().equals(getActivity().getString(R.string.pause_button_context_description))) {
            pauseSong();
        } else {
            playSong();
        }
    }

    private void playPreviousTrack() {
        mMediaPlayerService.prevTrack();
        playSong();
    }

    private void playNextTrack() {
        mMediaPlayerService.nextTrack();
        playSong();
    }

    private void pauseSong() {
        mMediaPlayerService.pauseTrack();
        swapPlayPauseButton(PLAY);
    }

    private void playSong() {
        mMediaPlayerService.playTrack();
        swapPlayPauseButton(PAUSE);
        resetSeekBar();
        setupView(mMediaPlayerService.getCurrentTrackInfo());
    }

    private void swapPlayPauseButton(@MediaPlayerActionName int value) {
        int ic_media_play_pause;
        int play_pause_button_context_description;
        switch (value) {
            case PAUSE:
                ic_media_play_pause = android.R.drawable.ic_media_pause;
                play_pause_button_context_description = R.string.pause_button_context_description;
                break;
            case PLAY:
                ic_media_play_pause = android.R.drawable.ic_media_play;
                play_pause_button_context_description = R.string.play_button_context_description;
                break;
            default:
                return;
        }
        mIvPlayPause.setImageResource(ic_media_play_pause);
        mIvPlayPause.setContentDescription(getActivity().getString(play_pause_button_context_description));
    }

    private void resetSeekBar() {
        mSbDuration.setProgress(0);
        mSbDuration.setMax(MAX_PROGRESS);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, DELAY_MILLIS);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mMediaPlayerService.getDuration();
            long currentDuration = mMediaPlayerService.getCurrentPosition();
            // Displaying Total Duration time
            mTvDuration.setText(Utils.millisecondsToTime(totalDuration));
            // Displaying time completed playing
            mTvCurrentTime.setText(Utils.millisecondsToTime(currentDuration));
            // Updating progress bar
            int progress = Utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            mSbDuration.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, DELAY_MILLIS);
        }
    };

    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mMediaPlayerService = binder.getService();
            mBound = true;
            setupOnClickListeners(TrackPlayerFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            setupOnClickListeners(null);
        }
    };

    private void setupOnClickListeners(View.OnClickListener clickListener) {
        mIvPrevious.setOnClickListener(clickListener);
        mIvPlayPause.setOnClickListener(clickListener);
        mIvNext.setOnClickListener(clickListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
