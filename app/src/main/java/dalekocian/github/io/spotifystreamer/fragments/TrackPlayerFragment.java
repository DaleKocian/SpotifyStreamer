package dalekocian.github.io.spotifystreamer.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.services.MediaPlayerService;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;

/**
 * Created by dkocian on 8/11/2015.
 */
public class TrackPlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = TrackPlayerFragment.class.getName();
    @Bind(R.id.tvArtistName)
    TextView mTvArtistName;
    @Bind(R.id.tvAlbumName)
    TextView mTvAlbumName;
    @Bind(R.id.ivAlbumArt)
    ImageView mIvAlbumArt;
    @Bind(R.id.pbDuration)
    ProgressBar mPbDuration;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_player_ui, container, false);
        ButterKnife.bind(this, view);
        trackList = getArguments().getParcelableArrayList(ExtraKeys.TRACK_LIST);
        currentPosition = getArguments().getInt(ExtraKeys.POSITION, 0);
        setupView(trackList.get(currentPosition));
        mIvPrevious.setOnClickListener(this);
        mIvPlayPause.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
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

    private void setupView(ParcelableTrack parcelableTrack) {
        mTvArtistName.setText(parcelableTrack.artists.get(0).name);
        mTvAlbumName.setText(parcelableTrack.album.name);
        Picasso.with(getActivity()).load(parcelableTrack.album.images.get(0).url).into(mIvAlbumArt);
        mTvTrackName.setText(parcelableTrack.name);
        mTvDuration.setText(String.valueOf(parcelableTrack.duration_ms));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mIvPrevious.getId()) {
            mMediaPlayerService.prevTrack();
            mIvPlayPause.setImageResource(android.R.drawable.ic_media_play);
            mIvPlayPause.setContentDescription("Play current track.");
        } else if (view.getId() == mIvNext.getId()) {
            mMediaPlayerService.nextTrack();
        } else if (mMediaPlayerService.isPlaying()) {
            mMediaPlayerService.pauseTrack();
        } else {
            mMediaPlayerService.playTrack();
            mIvPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            mIvPlayPause.setContentDescription("Pause current track.");
        }
    }

    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mMediaPlayerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
