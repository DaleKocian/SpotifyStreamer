package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import dalekocian.github.io.spotifystreamer.utils.ParcelableUtils;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.AlbumSimple;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class ParcelableAlbumSimple extends AlbumSimple implements Parcelable {

    public static final Creator<ParcelableAlbumSimple> CREATOR = new Creator<ParcelableAlbumSimple>() {
        public ParcelableAlbumSimple createFromParcel(Parcel source) {
            return new ParcelableAlbumSimple(source);
        }

        public ParcelableAlbumSimple[] newArray(int size) {
            return new ParcelableAlbumSimple[size];
        }
    };

    public ParcelableAlbumSimple(AlbumSimple albumSimple) {
        this.album_type = albumSimple.album_type;
        this.available_markets = albumSimple.available_markets;
        this.external_urls = albumSimple.external_urls;
        this.href = albumSimple.href;
        this.id = albumSimple.id;
        this.images = albumSimple.images;
        this.name = albumSimple.name;
        this.type = albumSimple.type;
        this.uri = albumSimple.uri;
    }

    protected ParcelableAlbumSimple(Parcel in) {
        this.album_type = in.readString();
        this.available_markets = in.createStringArrayList();
        this.external_urls = Utils.createMapFromBundle(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        ArrayList<ParcelableImage> arrayList = new ArrayList<>();
        in.readList(arrayList, ParcelableImage.class.getClassLoader());
        this.images = ParcelableUtils.getImagesFromParcelableImages(arrayList);
        this.name = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.album_type);
        dest.writeStringList(this.available_markets);
        dest.writeBundle(Utils.createBundleFromMap(external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeList(ParcelableUtils.getListOfParcelableImages(images));
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }
}
