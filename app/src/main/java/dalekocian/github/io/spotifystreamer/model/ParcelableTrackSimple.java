package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by dkocian on 8/12/2015.
 */
public class ParcelableTrackSimple extends TrackSimple implements Parcelable {

    public ParcelableTrackSimple() {
    }

    public ParcelableTrackSimple(TrackSimple trackSimple) {
        this.artists = trackSimple.artists;
        this.available_markets = trackSimple.available_markets;
        this.is_playable = trackSimple.is_playable;
        this.linked_from = trackSimple.linked_from;
        this.disc_number = trackSimple.disc_number;
        this.duration_ms = trackSimple.duration_ms;
        this.explicit = trackSimple.explicit;
        this.external_urls = trackSimple.external_urls;
        this.href = trackSimple.href;
        this.id = trackSimple.id;
        this.name = trackSimple.name;
        this.preview_url = trackSimple.preview_url;
        this.track_number = trackSimple.track_number;
        this.type = trackSimple.type;
        this.uri = trackSimple.uri;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.artists);
        dest.writeStringList(this.available_markets);
        dest.writeValue(this.is_playable);
        dest.writeParcelable(new ParcelableLinkedTrack(this.linked_from), flags);
        dest.writeInt(this.disc_number);
        dest.writeLong(this.duration_ms);
        dest.writeByte(explicit ? (byte) 1 : (byte) 0);
        dest.writeBundle(Utils.createBundleFromMap(this.external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.preview_url);
        dest.writeInt(this.track_number);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    protected ParcelableTrackSimple(Parcel in) {
        this.artists = new ArrayList<>();
        in.readList(this.artists, List.class.getClassLoader());
        this.available_markets = in.createStringArrayList();
        this.is_playable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        ParcelableLinkedTrack parcelableLinkedTrack = in.readParcelable(ParcelableLinkedTrack.class.getClassLoader());
        this.linked_from = parcelableLinkedTrack.getLinkedTrack();
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

    public static final Parcelable.Creator<ParcelableTrackSimple> CREATOR = new Parcelable.Creator<ParcelableTrackSimple>() {
        public ParcelableTrackSimple createFromParcel(Parcel source) {
            return new ParcelableTrackSimple(source);
        }

        public ParcelableTrackSimple[] newArray(int size) {
            return new ParcelableTrackSimple[size];
        }
    };
}
