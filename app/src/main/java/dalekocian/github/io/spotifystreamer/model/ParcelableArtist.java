package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import dalekocian.github.io.spotifystreamer.utils.ParcelableUtils;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Followers;

/**
 * Created by dkocian on 6/26/15.
 */
public class ParcelableArtist extends Artist implements Parcelable {
    public static final Creator<ParcelableArtist> CREATOR = new Creator<ParcelableArtist>() {
        public ParcelableArtist createFromParcel(Parcel source) {
            return new ParcelableArtist(source);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };

    public ParcelableArtist() {
    }

    public ParcelableArtist(Artist artist) {
        this.followers = artist.followers;
        this.genres = artist.genres;
        this.images = artist.images;
        this.popularity = artist.popularity;
        this.external_urls = artist.external_urls;
        this.href = artist.href;
        this.id = artist.id;
        this.name = artist.name;
        this.type = artist.type;
        this.uri = artist.uri;
    }

    protected ParcelableArtist(Parcel in) {
        ParcelableFollowers parcelableFollowers = in.readParcelable(ParcelableFollowers.class.getClassLoader());
        this.followers = parcelableFollowers.getFollowers();
        this.genres = in.createStringArrayList();
        this.images = new ArrayList<>();
        ArrayList<ParcelableImage> arrayList = new ArrayList<>();
        in.readList(arrayList, ParcelableImage.class.getClassLoader());
        this.images = ParcelableUtils.getImagesFromParcelableImages(arrayList);
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.external_urls = Utils.createMapFromBundle(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getParcelableFollowers(this.followers), 0);
        dest.writeStringList(this.genres);
        dest.writeList(ParcelableUtils.getListOfParcelableImages(this.images));
        dest.writeValue(this.popularity);
        dest.writeBundle(Utils.createBundleFromMap(this.external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private ParcelableFollowers getParcelableFollowers(Followers followers) {
        if (followers == null) {
            return null;
        }
        return new ParcelableFollowers(followers);
    }

    public Artist getArtist() {
        Artist artist = new Artist();
        artist.followers = this.followers;
        artist.genres = this.genres;
        artist.images = this.images;
        artist.popularity = this.popularity;
        artist.external_urls = this.external_urls;
        artist.href = this.href;
        artist.id = this.id;
        artist.name = this.name;
        artist.type = this.type;
        artist.uri = this.uri;
        return artist;
    }
}