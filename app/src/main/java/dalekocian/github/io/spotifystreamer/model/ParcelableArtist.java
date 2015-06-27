package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by dkocian on 6/26/15.
 */
public class ParcelableArtist extends Artist implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.images);
        dest.writeString(this.name);
    }

    public ParcelableArtist() {

    }

    public ParcelableArtist(Artist artist) {
        this.images = artist.images;
        this.name = artist.name;

    }

    protected ParcelableArtist(Parcel in) {
        this.images = new ArrayList<>();
        in.readList(this.images, List.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Creator<ParcelableArtist> CREATOR = new Creator<ParcelableArtist>() {
        public ParcelableArtist createFromParcel(Parcel source) {
            return new ParcelableArtist(source);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };
}
