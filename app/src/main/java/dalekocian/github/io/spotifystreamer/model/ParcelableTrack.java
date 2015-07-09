package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.LinkedTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class ParcelableTrack extends Track implements Parcelable {
    public static final Creator<ParcelableTrack> CREATOR = new Creator<ParcelableTrack>() {
        public ParcelableTrack createFromParcel(Parcel source) {
            return new ParcelableTrack(source);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };

    public ParcelableTrack() {
    }

    public ParcelableTrack(Track track) {
        if (track != null) {
            this.album = track.album;
            this.external_ids = track.external_ids;
            this.popularity = track.popularity;
            this.artists = track.artists;
            this.available_markets = track.available_markets;
            this.is_playable = track.is_playable;
            this.linked_from = track.linked_from;
            this.disc_number = track.disc_number;
            this.duration_ms = track.duration_ms;
            this.explicit = track.explicit;
            this.external_urls = track.external_urls;
            this.href = track.href;
            this.id = track.id;
            this.name = track.name;
            this.preview_url = track.preview_url;
            this.track_number = track.track_number;
            this.type = track.type;
            this.uri = track.uri;
        }
    }

    protected ParcelableTrack(Parcel in) {
        this.album = in.readParcelable(ParcelableAlbumSimple.class.getClassLoader());
        this.external_ids = Utils.createMapFromBundle(in.readBundle());
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
        List<ParcelableArtistSimple> parcelableArtistSimpleList = new ArrayList<>();
        in.readList(parcelableArtistSimpleList, List.class.getClassLoader());
        for (ParcelableArtistSimple parcelableArtistSimple : parcelableArtistSimpleList) {
            this.artists.add(parcelableArtistSimple.getArtistSimple());
        }
        this.available_markets = in.createStringArrayList();
        this.is_playable = in.readByte() != 0;
        this.linked_from = in.readParcelable(LinkedTrack.class.getClassLoader());
        this.disc_number = in.readInt();
        this.duration_ms = in.readLong();
        this.explicit = in.readByte() != 0;
        this.external_urls = Utils.createMapFromBundle(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.preview_url = in.readString();
        this.track_number = in.readInt();
        this.type = in.readString();
        this.uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(new ParcelableAlbumSimple(album), flags);
        dest.writeBundle(Utils.createBundleFromMap(external_ids));
        dest.writeValue(this.popularity);
        List<ParcelableArtistSimple> parcelableArtistSimpleList = new ArrayList<>(artists.size());
        for (ArtistSimple artistSimple : artists) {
            parcelableArtistSimpleList.add(new ParcelableArtistSimple(artistSimple));
        }
        dest.writeList(parcelableArtistSimpleList);
        dest.writeStringList(available_markets);
        dest.writeByte(Utils.getByteFromBoolean(is_playable));
        dest.writeParcelable(new ParcelableLinkedTrack(this.linked_from), 0);
        dest.writeInt(disc_number);
        dest.writeLong(duration_ms);
        dest.writeByte(Utils.getByteFromBoolean(explicit));
        dest.writeBundle(Utils.createBundleFromMap(external_urls));
        dest.writeString(href);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(preview_url);
        dest.writeInt(track_number);
        dest.writeString(type);
        dest.writeString(uri);
    }

    public Track getTrack() {
        Track track = new Track();
        track.album = this.album;
        track.external_ids = this.external_ids;
        track.popularity = this.popularity;
        track.artists = this.artists;
        track.available_markets = this.available_markets;
        track.is_playable = this.is_playable;
        track.linked_from = this.linked_from;
        track.disc_number = this.disc_number;
        track.duration_ms = this.duration_ms;
        track.explicit = this.explicit;
        track.external_urls = this.external_urls;
        track.href = this.href;
        track.id = this.id;
        track.name = this.name;
        track.preview_url = this.preview_url;
        track.track_number = this.track_number;
        track.type = this.type;
        track.uri = this.uri;
        return track;
    }
}
