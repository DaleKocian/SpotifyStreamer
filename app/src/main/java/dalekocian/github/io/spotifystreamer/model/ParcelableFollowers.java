package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Followers;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class ParcelableFollowers extends Followers implements Parcelable {
    public static final Creator<ParcelableFollowers> CREATOR = new Creator<ParcelableFollowers>() {
        public ParcelableFollowers createFromParcel(Parcel source) {
            return new ParcelableFollowers(source);
        }

        public ParcelableFollowers[] newArray(int size) {
            return new ParcelableFollowers[size];
        }
    };

    public ParcelableFollowers() {
    }

    public ParcelableFollowers(Followers followers) {
        this.href = followers.href;
        this.total = followers.total;
    }

    protected ParcelableFollowers(Parcel in) {
        this.href = in.readString();
        this.total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.href);
        dest.writeInt(this.total);
    }

    public Followers getFollowers() {
        Followers followers = new Followers();
        followers.href = this.href;
        followers.total = this.total;
        return followers;
    }
}