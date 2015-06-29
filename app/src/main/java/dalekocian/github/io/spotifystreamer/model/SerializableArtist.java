package dalekocian.github.io.spotifystreamer.model;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by k557782 on 6/29/2015.
 */
public class SerializableArtist extends Artist implements Serializable {
    public SerializableArtist() {

    }
    public SerializableArtist(Artist artist) {
        this.images = artist.images;
        this.name = artist.name;
        this.id = artist.id;
    }
}
