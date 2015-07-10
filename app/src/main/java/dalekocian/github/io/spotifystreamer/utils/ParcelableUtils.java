package dalekocian.github.io.spotifystreamer.utils;

import java.util.ArrayList;
import java.util.List;

import dalekocian.github.io.spotifystreamer.model.ParcelableImage;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by dkocian on 7/9/2015.
 */
public class ParcelableUtils {
    public static List<Image> getImagesFromParcelableImages(ArrayList<ParcelableImage> arrayList) {
        List<Image> images = new ArrayList<>(arrayList.size());
        for (ParcelableImage parcelableImage : arrayList) {
            images.add(parcelableImage.getImage());
        }
        return images;
    }

    public static ArrayList<ParcelableImage> getListOfParcelableImages(List<Image> images) {
        if (images == null) {
            return null;
        }
        ArrayList<ParcelableImage> parcelableImageArrayList = new ArrayList<>(images.size());
        for (Image image : images) {
            parcelableImageArrayList.add(new ParcelableImage(image));
        }
        return parcelableImageArrayList;
    }
}
