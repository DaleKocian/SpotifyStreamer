package dalekocian.github.io.spotifystreamer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.utils.Utils;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by k557782 on 6/10/2015.
 */
public class ArtistSearchResultsAdapter extends ArrayAdapter<Artist> {
    private final Activity activity;
    private final int resource;
    private final List<Artist> artistList;

    public ArtistSearchResultsAdapter(Activity activity, int resource, List<Artist> artistList) {
        super(activity, resource, artistList);
        this.activity = activity;
        this.resource = resource;
        this.artistList = artistList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(resource, null, true);
            viewContainer = new ViewContainer();
            viewContainer.ivArtistImage = (ImageView) rowView.findViewById(R.id.ivArtistImage);
            viewContainer.tvArtistName = (TextView) rowView.findViewById(R.id.tvArtistName);
            rowView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) rowView.getTag();
        }
        Artist artist = artistList.get(position);
        if (artist.images != null && artist.images.size() > 0) {
            Picasso.with(activity).load(artist.images.get(0).url).into(viewContainer.ivArtistImage);
        } else {
            viewContainer.ivArtistImage.setImageResource(R.drawable.spotify_default_cover);
        }
        viewContainer.tvArtistName.setText(Utils.returnEmptyStringIfNull(artist.name));
        return rowView;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    static class ViewContainer {
        public ImageView ivArtistImage;
        public TextView tvArtistName;
    }
}
