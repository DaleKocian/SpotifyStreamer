package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.ArtistSimple;

/**
 * Created by k557782 on 7/9/2015.
 */
public class ParcelableArtistSimple extends ArtistSimple implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(Utils.createBundleFromMap(this.external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    public ParcelableArtistSimple() {
    }

    public ParcelableArtistSimple(ArtistSimple artistSimple) {
        this.external_urls = artistSimple.external_urls;
        this.href = artistSimple.href;
        this.id = artistSimple.id;
        this.name = artistSimple.name;
        this.type = artistSimple.type;
        this.uri = artistSimple.uri;
    }

    protected ParcelableArtistSimple(Parcel in) {
        this.external_urls = Utils.createMapFromBundle(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<ParcelableArtistSimple> CREATOR = new Creator<ParcelableArtistSimple>() {
        public ParcelableArtistSimple createFromParcel(Parcel source) {
            return new ParcelableArtistSimple(source);
        }

        public ParcelableArtistSimple[] newArray(int size) {
            return new ParcelableArtistSimple[size];
        }
    };

    public ArtistSimple getArtistSimple() {
        ArtistSimple artistSimple = new ArtistSimple();
        artistSimple.external_urls = this.external_urls;
        artistSimple.href = this.href;
        artistSimple.id = this.id;
        artistSimple.name = this.name;
        artistSimple.type = this.type;
        artistSimple.uri = this.uri;
        return artistSimple;
    }
}
