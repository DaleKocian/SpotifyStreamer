package dalekocian.github.io.spotifystreamer.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import dalekocian.github.io.spotifystreamer.model.ParcelableTrack;
import dalekocian.github.io.spotifystreamer.utils.Constants;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by dkocian on 8/12/2015.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayerInterface {
    private static final String TAG = MediaPlayerService.class.getName();
    private static final int NOTIFICATION_ID = 100;
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mMediaPlayer = null;
    private WifiManager.WifiLock wifiLock;
    private List<ParcelableTrack> trackList;
    private int currentPosition;
    private CurrentTrackInfo currentTrackInfo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        trackList = intent.getParcelableArrayListExtra(ExtraKeys.TRACK_LIST);
        currentPosition = intent.getIntExtra(ExtraKeys.POSITION, 0);
        currentTrackInfo = new CurrentTrackInfo(trackList.get(currentPosition));
        initWifiLock();
        try {
            initMediaPlayer();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return mBinder;
    }



    public void foregroundService() {
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MediaPlayerService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setTicker("").setContentTitle("MusicPlayerSample").setContentText("Playing: " + "").setSmallIcon(0).setLargeIcon(null).
                setContentIntent(resultPendingIntent);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    public void initWifiLock() {
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();
    }

    public void initMediaPlayer() throws IOException {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(currentTrackInfo.url);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Intent intent = new Intent();
        intent.setAction(Constants.MEDIA_PLAYER_START_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        // The MediaPlayer has moved to the Error state, must be reset!
        return false;
    }

    private void startPlayer() {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(currentTrackInfo.url);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public CurrentTrackInfo getCurrentTrackInfo() {
        return currentTrackInfo;
    }

    @Override
    public void nextTrack() {
        currentTrackInfo = new CurrentTrackInfo(trackList.get(incrementPosition()));
    }

    @Override
    public void prevTrack() {
        currentTrackInfo = new CurrentTrackInfo(trackList.get(decrementPosition()));
    }

    @Override
    public void playTrack() {
        startPlayer();
    }

    @Override
    public void pauseTrack() {
        mMediaPlayer.pause();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }



    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
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
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (wifiLock != null) {
            wifiLock.release();
        }
        stopForeground(true);
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerService.this;
        }
    }

    public class CurrentTrackInfo {
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

        public String getUrl() {
            return url;
        }

        public String getArtistName() {
            return artistName;
        }

        public String getTrackName() {
            return trackName;
        }

        public String getAlbumName() {
            return albumName;
        }

        public long getDuration() {
            return duration;
        }

        public Image getAlbumImage() {
            return albumImage;
        }
    }
}
