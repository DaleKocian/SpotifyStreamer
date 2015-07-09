package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.LinkedTrack;

/**
 * Created by k557782 on 7/9/2015.
 */
public class ParcelableLinkedTrack extends LinkedTrack implements Parcelable {
    public Map<String, String> external_urls;
    public String href;
    public String id;
    public String type;
    public String uri;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(Utils.createBundleFromMap(external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }

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
        this.external_urls = Utils.getMapFromBundle(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<ParcelableLinkedTrack> CREATOR = new Creator<ParcelableLinkedTrack>() {
        public ParcelableLinkedTrack createFromParcel(Parcel source) {
            return new ParcelableLinkedTrack(source);
        }

        public ParcelableLinkedTrack[] newArray(int size) {
            return new ParcelableLinkedTrack[size];
        }
    };
}
