package dalekocian.github.io.spotifystreamer.services;

/**
 * Created by dkocian on 8/12/2015.
 */
interface MediaPlayerInterface {
    void seekTo(int position);
    void nextTrack();
    void prevTrack();
    void playTrack();
    void resumeTrack();
    void pauseTrack();
    int getDuration();
    long getCurrentPosition();
    boolean isPlaying();
}
