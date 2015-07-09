package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by k557782 on 7/9/2015.
 */
public class ParcelableTrack extends Track implements Parcelable {
    public ParcelableTrack() {
    }

    public ParcelableTrack(Track track) {
        this.album = track.album;
        this.external_ids = track.external_ids;
        this.popularity = track.popularity;
    }

    protected ParcelableTrack(Parcel in) {
        this.album = in.readParcelable(ParcelableAlbumSimple.class.getClassLoader());
        this.external_ids = Utils.getMapFromBundle(in.readBundle());
        this.popularity = (Integer) in.readValue(Integer.class.getClassLoader());
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
    }


    public static final Creator<ParcelableTrack> CREATOR = new Creator<ParcelableTrack>() {
        public ParcelableTrack createFromParcel(Parcel source) {
            return new ParcelableTrack(source);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };

    public Track getTrack() {
        Track track = new Track();
        track.album = this.album;
        track.external_ids = this.external_ids;
        track.popularity = this.popularity;
        return track;
    }
}
