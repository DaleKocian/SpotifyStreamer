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
        ArrayList<ParcelableArtistSimple> parcelableArtistSimpleList = new ArrayList<>();
        in.readList(parcelableArtistSimpleList, ParcelableArtistSimple.class.getClassLoader());
        this.artists = getArtistFromParcelableArtist(parcelableArtistSimpleList);
        this.available_markets = in.createStringArrayList();
        this.is_playable = Utils.getBooleanFromInt(in.readInt());
        ParcelableLinkedTrack parcelableLinkedTrack = in.readParcelable(ParcelableLinkedTrack.class.getClassLoader());
        this.linked_from = parcelableLinkedTrack == null ? null : parcelableLinkedTrack.getLinkedTrack();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(new ParcelableAlbumSimple(album), flags);
        dest.writeBundle(Utils.createBundleFromMap(external_ids));
        dest.writeValue(popularity);
        dest.writeList(getParcelableArtistSimpleList(artists));
        dest.writeStringList(available_markets);
        dest.writeInt(Utils.getIntFromBoolean(is_playable));
        dest.writeParcelable(getParcelableLinkedTrack(linked_from), flags);
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

    @Override
    public int describeContents() {
        return 0;
    }

    private ParcelableLinkedTrack getParcelableLinkedTrack(LinkedTrack linked_from) {
        if (linked_from == null) {
            return null;
        }
        return new ParcelableLinkedTrack(this.linked_from);
    }

    private List<ArtistSimple> getArtistFromParcelableArtist(ArrayList<ParcelableArtistSimple> parcelableArtistSimpleArrayList) {
        if (parcelableArtistSimpleArrayList == null) {
            return null;
        }
        List<ArtistSimple> artistSimpleList = new ArrayList<>(parcelableArtistSimpleArrayList.size());
        for (ParcelableArtistSimple parcelableArtistSimple : parcelableArtistSimpleArrayList) {
            artistSimpleList.add(parcelableArtistSimple.getArtistSimple());
        }
        return artistSimpleList;
    }

    private ArrayList<ParcelableArtistSimple> getParcelableArtistSimpleList(List<ArtistSimple> artistSimpleList) {
        if (artistSimpleList == null) {
            return null;
        }
        ArrayList<ParcelableArtistSimple> parcelableArtistSimpleList = new ArrayList<>(artistSimpleList.size());
        for (ArtistSimple artistSimple : artistSimpleList) {
            parcelableArtistSimpleList.add(new ParcelableArtistSimple(artistSimple));
        }
        return parcelableArtistSimpleList;
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
