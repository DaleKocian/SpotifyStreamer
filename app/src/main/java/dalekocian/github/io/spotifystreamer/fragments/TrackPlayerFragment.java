package dalekocian.github.io.spotifystreamer.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import kaaes.spotify.webapi.android.models.Image;

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
    @Bind(R.id.ivPlay)
    ImageView mIvPlay;
    @Bind(R.id.btnNext)
    ImageView mBtnNext;
    @Bind(R.id.tvTrackName)
    TextView mTvTrackName;
    @Bind(R.id.tvCurrentTime)
    TextView mTvCurrentTime;
    @Bind(R.id.tvDuration)
    TextView mTvDuration;

    private List<ParcelableTrack> trackList;
    private int currentPosition;
    private CurrentTrackInfo currentTrackInfo;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_player_ui, container, false);
        ButterKnife.bind(this, view);
        mediaPlayer = new MediaPlayer();
        trackList = getArguments().getParcelableArrayList(ExtraKeys.TRACK_LIST);
        currentPosition = getArguments().getInt(ExtraKeys.POSITION, 0);
        currentTrackInfo = new CurrentTrackInfo(trackList.get(currentPosition));
        setupView();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mIvPrevious.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        return view;
    }

    private void setupView() {
        mTvArtistName.setText(currentTrackInfo.artistName);
        mTvAlbumName.setText(currentTrackInfo.albumName);
        Picasso.with(getActivity()).load(currentTrackInfo.albumImage.url).into(mIvAlbumArt);
        mTvTrackName.setText(currentTrackInfo.trackName);
        mTvDuration.setText(String.valueOf(currentTrackInfo.duration));
    }

    private void playTrack() {
        try {
            mediaPlayer.setDataSource(currentTrackInfo.url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private int incrementPosition() {
        return ++currentPosition % trackList.size();
    }

    private int decrementPosition() {
        if (--currentPosition < 0) {
            return trackList.size() + currentPosition;
        }
        return currentPosition;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mIvPrevious.getId()) {
            currentTrackInfo = new CurrentTrackInfo(trackList.get(decrementPosition()));
        } else if (v.getId() == mBtnNext.getId()) {
            currentTrackInfo = new CurrentTrackInfo(trackList.get(incrementPosition()));
        }
        playTrack();
    }

    @Override
     public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    class CurrentTrackInfo {
        String url;
        String artistName;
        String trackName;
        String albumName;
        long duration;

        Image albumImage;
        public CurrentTrackInfo(ParcelableTrack parcelableTrack) {
            this.url = parcelableTrack.preview_url;
            this.artistName = parcelableTrack.artists.get(0).name;
            this.trackName = parcelableTrack.name;
            this.albumName = parcelableTrack.album.name;
            this.duration = parcelableTrack.duration_ms;
            this.albumImage = parcelableTrack.album.images.get(0);
        }

    }
}
