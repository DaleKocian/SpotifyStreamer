package dalekocian.github.io.spotifystreamer.fragments;

import android.content.Context;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.ArtistSearchResultsAdapter;
import dalekocian.github.io.spotifystreamer.listeners.LazyLoadListener;
import dalekocian.github.io.spotifystreamer.model.ParcelableArtist;
import dalekocian.github.io.spotifystreamer.services.ArtistSearchService;
import dalekocian.github.io.spotifystreamer.utils.Constants;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by dkocian on 7/9/2015.
 */
public class ArtistSearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.lvListItems)
    ListView mLvListItems;
    @Bind(R.id.llProgressView)
    LinearLayout mllProgressView;
    private ArtistSearchResultsAdapter artistSearchResultsAdapter;
    private Callback callback;
    private ArtistSearchService artistSearchService;
    public static final String ARTISTS_BUNDLE_KEY = "ARTISTS_BUNDLE_KEY";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (Callback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_results_ui, container, false);
        ButterKnife.bind(this, view);
        artistSearchResultsAdapter = new ArtistSearchResultsAdapter(getActivity(), R.layout.lv_row_search_results, new ArrayList<Artist>(0));
        mLvListItems.setAdapter(artistSearchResultsAdapter);
        mLvListItems.setOnItemClickListener(this);
        final LazyLoadListener lazyLoadListener = new LazyLoadListener() {
            @Override
            public void addNewElements() {
                boolean executedSearch = artistSearchService.searchArtistNext();
                if (executedSearch) {
                    mllProgressView.setVisibility(View.VISIBLE);
                }
            }
        };
        mLvListItems.setOnScrollListener(lazyLoadListener);
        artistSearchService = new ArtistSearchService(getActivity(), getArtistSearchResponseListener(lazyLoadListener))
                .setCallback(getArtistSearchCallback());
        return view;
    }

    private ArtistSearchService.Callback getArtistSearchCallback() {
        return new ArtistSearchService.Callback() {
            @Override
            public void onPreExecute() {
                callback.onLoading();
            }

            @Override
            public void onPostExecute() {
            }
        };
    }

    private ArtistSearchService.ResponseListener getArtistSearchResponseListener(final LazyLoadListener lazyLoadListener) {
        return new ArtistSearchService.ResponseListener() {
            @Override
            public void onResponse(ArtistsPager result) {
                Pager<Artist> artistPager = result.artists;
                if (artistPager == null || artistPager.items == null || artistPager.items.isEmpty()) {
                    callback.onNoResults();
                } else {
                    callback.onResults();
                    artistSearchService.setTotal(artistPager.total);
                    updateArrayAdapter(artistPager);
                }
                lazyLoadListener.doneLoading();
            }
        };
    }

    public void updateArrayAdapter(Pager<Artist> artistPager) {
        if (artistPager.previous == null) {
            artistSearchResultsAdapter.clear();
        }
        artistSearchResultsAdapter.addAll(artistPager.items);
        mllProgressView.setVisibility(View.GONE);
        artistSearchResultsAdapter.notifyDataSetChanged();
        if (artistPager.previous == null) {
            mLvListItems.setSelection(0);
        }
    }

    public void search(String artistName) {
        artistSearchService.searchArtist(artistName);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback.onItemSelected(artistSearchResultsAdapter.getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<ParcelableArtist> parcelableArtistArrayList = new ArrayList<>(artistSearchResultsAdapter.getArtistList().size());
        for (Artist artist : artistSearchResultsAdapter.getArtistList()) {
            parcelableArtistArrayList.add(new ParcelableArtist(artist));
        }
        outState.putParcelableArrayList(ARTISTS_BUNDLE_KEY, parcelableArtistArrayList);
        outState.putInt(Constants.LIST_POSITION_BUNDLE_KEY, mLvListItems.getFirstVisiblePosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<ParcelableArtist> parcelableArrayList = savedInstanceState.getParcelableArrayList(ARTISTS_BUNDLE_KEY);
            if (parcelableArrayList != null) {
                int position = savedInstanceState.getInt(Constants.LIST_POSITION_BUNDLE_KEY, 0);
                artistSearchResultsAdapter.clear();
                for (ParcelableArtist artist : parcelableArrayList) {
                    artistSearchResultsAdapter.add(artist.getArtist());
                }
                artistSearchResultsAdapter.notifyDataSetChanged();
                mLvListItems.setSelection(position);
            }
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mLvListItems.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface Callback {
        void onNoResults();

        void onLoading();

        void onResults();

        void onItemSelected(Artist artist);
    }
}
