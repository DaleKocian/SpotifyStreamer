package dalekocian.github.io.spotifystreamer.services;

/**
 * Created by dkocian on 8/12/2015.
 */
public interface MediaPlayerInterface {
    void nextTrack();
    void prevTrack();
    void playTrack();
    void pauseTrack();
    boolean isPlaying();
}