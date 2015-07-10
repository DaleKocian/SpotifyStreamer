package dalekocian.github.io.spotifystreamer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.activities.TopTenTrackActivity;
import dalekocian.github.io.spotifystreamer.adapters.ArtistSearchResultsAdapter;
import dalekocian.github.io.spotifystreamer.listeners.LazyLoadListener;
import dalekocian.github.io.spotifystreamer.utils.ExtraKeys;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by dkocian on 7/9/2015.
 */
public class ArtistSearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArtistSearchResultsAdapter artistSearchResultsAdapter;
    private ListView lvListItems;
    private LinearLayout llProgressView;
    private Callback callback;
    LazyLoadListener lazyLoadListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_results_ui, container, false);
        lvListItems = (ListView) view.findViewById(R.id.lvListItems);
        llProgressView = (LinearLayout) view.findViewById(R.id.llProgressView);
        artistSearchResultsAdapter = new ArtistSearchResultsAdapter(getActivity(), R.layout.lv_row_search_results, new ArrayList<Artist>(0));
        lvListItems.setAdapter(artistSearchResultsAdapter);
        lvListItems.setOnItemClickListener(this);
        lazyLoadListener = new LazyLoadListener() {
            @Override
            public void addNewElements() {
               callback.onLoadMore();
            }
        };
        lvListItems.setOnScrollListener(lazyLoadListener);
        return view;
    }



    public void updateArrayAdapter(Pager<Artist> artistPager) {
        if (artistPager.previous == null) {
            artistSearchResultsAdapter.clear();
        }
        artistSearchResultsAdapter.addAll(artistPager.items);
        llProgressView.setVisibility(View.GONE);
        artistSearchResultsAdapter.notifyDataSetChanged();
        if (artistPager.previous == null) {
            lvListItems.setSelection(0);
        }
    }

   public void doneLazyLoading() {
       lazyLoadListener.doneLoading();
   }

    public void showLoading() {
        llProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist selectedArtist = artistSearchResultsAdapter.getItem(position);
        Intent topTenTrackIntent = new Intent(getActivity(), TopTenTrackActivity.class);
        topTenTrackIntent.putExtra(ExtraKeys.ARTIST_ID, selectedArtist.id);
        topTenTrackIntent.putExtra(ExtraKeys.ARTIST_NAME, selectedArtist.name);
        startActivity(topTenTrackIntent);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        ArrayList<ParcelableArtist> parcelableArtistArrayList = new ArrayList<>(artistSearchResultsAdapter.getArtistList().size());
//        for (Artist artist : artistSearchResultsAdapter.getArtistList()) {
//            parcelableArtistArrayList.add(new ParcelableArtist(artist));
//        }
//        outState.putParcelableArrayList(ARTISTS_BUNDLE_KEY, parcelableArtistArrayList);
//        outState.putCharSequence(SEARCH_STRING_BUNDLE_KEY, searchString);
//        outState.putInt(Constants.LIST_POSITION_BUNDLE_KEY, lvListItems.getFirstVisiblePosition());
//    }
//
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(Constants.LIST_POSITION_BUNDLE_KEY, 0);
            ArrayList<ParcelableArtist> parcelableArrayList = savedInstanceState.getParcelableArrayList(ARTISTS_BUNDLE_KEY);
            searchString = savedInstanceState.getString(SEARCH_STRING_BUNDLE_KEY);
            artistSearchResultsAdapter.clear();
            for (ParcelableArtist artist : parcelableArrayList) {
                artistSearchResultsAdapter.add(artist.getArtist());
            }
            artistSearchResultsAdapter.notifyDataSetChanged();
            if (lvListItems.getId() == resourceId) {
                callback.onResults();
            }
            lvListItems.setSelection(position);
        }*/
    }



    public interface Callback {
        void onLoadMore();
    }
}
