package dalekocian.github.io.spotifystreamer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.callbacks.ImageLoadedCallback;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Dale Kocian on 6/10/2015.
 */
public class TopTenTracksAdapter extends ArrayAdapter<Track> {
    private final Activity activity;
    private final int resource;
    private final List<Track> trackList;

    public TopTenTracksAdapter(Activity activity, int resource, List<Track> trackList) {
        super(activity, resource, trackList);
        this.activity = activity;
        this.resource = resource;
        this.trackList = trackList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(resource, null, true);
            viewContainer = new ViewContainer();
            viewContainer.pbArtistLoadingImage = (ProgressBar) rowView.findViewById(R.id.pbArtistLoadingImage);
            viewContainer.ivAlbumImage = (ImageView) rowView.findViewById(R.id.ivAlbumImage);
            viewContainer.tvAlbumName = (TextView) rowView.findViewById(R.id.tvAlbumName);
            viewContainer.tvTrackName = (TextView) rowView.findViewById(R.id.tvTrackName);
            rowView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) rowView.getTag();
        }
        Track track = trackList.get(position);
        AlbumSimple album = track.album;
        if (hasImageUrl(album)) {
            Picasso.with(activity).load(album.images.get(0).url).into(viewContainer.ivAlbumImage,
                    new ImageLoadedCallback(viewContainer.pbArtistLoadingImage));
        } else {
            viewContainer.ivAlbumImage.setImageResource(R.drawable.spotify_default_cover);
        }
        viewContainer.tvTrackName.setText(Utils.emptyToNull(track.name));
        viewContainer.tvAlbumName.setText(getNameOfAlbum(album));
        return rowView;
    }

    private boolean hasImageUrl(AlbumSimple album) {
        return album != null && !Utils.isNullOrEmpty(album.images) && album.images.get(0) != null
                && !Utils.isNullOrEmpty(album.images.get(0).url);
    }

    private String getNameOfAlbum(AlbumSimple album) {
        return album == null ? "" : Utils.emptyToNull(album.name);
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    static class ViewContainer {
        public ProgressBar pbArtistLoadingImage;
        public ImageView ivAlbumImage;
        public TextView tvAlbumName;
        public TextView tvTrackName;
    }
}
