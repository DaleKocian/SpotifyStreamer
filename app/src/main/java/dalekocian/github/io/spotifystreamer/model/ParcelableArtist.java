package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

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
        if (artist != null) {
            this.followers = artist.followers;
            this.genres = artist.genres;
            this.images = artist.images;
            this.popularity = artist.popularity;
            this.external_urls = artist.external_urls;
            this.href = artist.href;
            this.type = artist.type;
            this.uri = artist.uri;
        }
    }

    protected ParcelableArtist(Parcel in) {
        this.followers = in.readParcelable(ParcelableFollowers.class.getClassLoader());
        this.genres = in.createStringArrayList();
        this.images = new ArrayList<>();
        ArrayList<ParcelableImage> arrayList = new ArrayList<>();
        in.readList(arrayList, ParcelableImage.class.getClassLoader());
        this.images = new ArrayList<>(arrayList.size());
        for (ParcelableImage parcelableImage : arrayList) {
            this.images.add(parcelableImage.getImage());
        }
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(new ParcelableFollowers(followers), 0);
        dest.writeStringList(this.genres);
        ArrayList<ParcelableImage> arrayList = new ArrayList<>(this.images.size());
        for (Image image : images) {
            arrayList.add(new ParcelableImage(image));
        }
        dest.writeList(arrayList);
        dest.writeValue(this.popularity);
        dest.writeBundle(Utils.createBundleFromMap(external_urls));
        dest.writeString(this.href);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    public Artist getArtist() {
        Artist artist = new Artist();
        artist.followers = this.followers;
        artist.genres = this.genres;
        artist.images = this.images;
        artist.popularity = this.popularity;
        artist.external_urls = this.external_urls;
        artist.href = this.href;
        artist.type = this.type;
        artist.uri = this.uri;
        return artist;
    }
}
