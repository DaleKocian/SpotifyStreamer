package dalekocian.github.io.spotifystreamer.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.AlbumSimple;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class ParcelableAlbumSimple extends AlbumSimple implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.album_type);
        dest.writeStringList(this.available_markets);
        dest.writeBundle(getBundle(external_urls));
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeList(this.images);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.uri);
    }


    protected ParcelableAlbumSimple(Parcel in) {
        this.album_type = in.readString();
        this.available_markets = in.createStringArrayList();
        this.external_urls = getMap(in.readBundle());
        this.href = in.readString();
        this.id = in.readString();
        this.images = new ArrayList<>();
        in.readList(this.images, List.class.getClassLoader());
        this.name = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<ParcelableAlbumSimple> CREATOR = new Creator<ParcelableAlbumSimple>() {
        public ParcelableAlbumSimple createFromParcel(Parcel source) {
            return new ParcelableAlbumSimple(source);
        }

        public ParcelableAlbumSimple[] newArray(int size) {
            return new ParcelableAlbumSimple[size];
        }
    };

    public Bundle getBundle(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    public Map<String, String> getMap(Bundle bundle) {
        Map<String, String> map = new HashMap<>(bundle.keySet().size());
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }
}
