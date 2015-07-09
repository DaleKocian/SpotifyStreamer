package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Followers;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by dkocian on 6/26/15.
 */
public class ParcelableArtist extends Artist implements Parcelable {
    private ParcelableFollowers parcelableFollowers;
    private List<String> genres;
    private List<Image> images;
    private Integer popularity;

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
        parcelableFollowers = new ParcelableFollowers(artist.followers);
        genres = artist.genres;
        images = artist.images;
        popularity = artist.popularity;
        followers = parcelableFollowers;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parcelableFollowers, 0);
        dest.writeStringList(this.genres);
        dest.writeList(this.images);
        dest.writeValue(this.popularity);
    }

    protected ParcelableArtist(Parcel in) {
        this.parcelableFollowers = in.readParcelable(ParcelableFollowers.class.getClassLoader());
        followers = parcelableFollowers;
        this.genres = in.createStringArrayList();
        this.images = new ArrayList<>();
        in.readList(this.images, List.class.getClassLoader());
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public Followers getFollowers() {
        if (parcelableFollowers != null && !parcelableFollowers.equals(followers)) {
            followers = parcelableFollowers;
        } else if (parcelableFollowers == null && followers != null) {
            parcelableFollowers = new ParcelableFollowers(followers);
            followers = parcelableFollowers;
        }
        return followers;
    }

    public void setFollowers(Followers followers) {
        this.parcelableFollowers = new ParcelableFollowers(followers);
        this.followers = parcelableFollowers;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }
}
