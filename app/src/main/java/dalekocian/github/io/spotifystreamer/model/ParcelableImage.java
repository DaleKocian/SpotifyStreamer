package dalekocian.github.io.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Dale Kocian on 7/9/2015.
 */
public class ParcelableImage extends Image implements Parcelable {
    public ParcelableImage() {
    }

    public ParcelableImage(Image image) {
        this.width = image.width;
        this.height = image.height;
        this.url = image.url;
    }

    protected ParcelableImage(Parcel in) {
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
        this.url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.width);
        dest.writeValue(this.height);
        dest.writeString(this.url);
    }


    public static final Creator<ParcelableImage> CREATOR = new Creator<ParcelableImage>() {
        public ParcelableImage createFromParcel(Parcel source) {
            return new ParcelableImage(source);
        }

        public ParcelableImage[] newArray(int size) {
            return new ParcelableImage[size];
        }
    };

    public Image getImage() {
        Image image = new Image();
        image.width = this.width;
        image.height = this.height;
        image.url = this.url;
        return image;
    }
}
