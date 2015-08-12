package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.LinkedTrack;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class ParcelableLinkedTrack extends LinkedTrack implements Parcelable {
    public static final Creator<ParcelableLinkedTrack> CREATOR = new Creator<ParcelableLinkedTrack>() {
        public ParcelableLinkedTrack createFromParcel(Parcel source) {
            return new ParcelableLinkedTrack(source);
        }

        public ParcelableLinkedTrack[] newArray(int size) {
            return new ParcelableLinkedTrack[size];
        }
    };

    public ParcelableLinkedTrack() {
    }

    public ParcelableLinkedTrack(LinkedTrack linkedTrack) {
        this.external_urls = linkedTrack.external_urls;
        this.href = linkedTrack.href;
        this.id = linkedTrack.id;
        this.type = linkedTrack.type;
        this.uri = linkedTrack.uri;
    }

    protected ParcelableLinkedTrack(Parcel in) {
        this.external_urls = Utils.createMapFromBundle(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(Utils.createBundleFromMap(external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public LinkedTrack getLinkedTrack() {
        LinkedTrack linkedTrack = new LinkedTrack();
        linkedTrack.external_urls = this.external_urls;
        linkedTrack.href = this.href;
        linkedTrack.id = this.id;
        linkedTrack.type = this.type;
        linkedTrack.uri = this.uri;
        return linkedTrack;
    }
}
