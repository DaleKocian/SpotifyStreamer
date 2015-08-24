package dalekocian.github.io.spotifystreamer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dalekocian.github.io.spotifystreamer.R;
import dalekocian.github.io.spotifystreamer.adapters.TopTenTracksAdapter;
import dalekocian.github.io.spotifystreamer.services.TopTenTrackSearchService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by dkocian on 8/12/2015.
 */
public class TopTenTracksFragment extends Fragment implements OnItemClickListener {
    private static final String TAG = TopTenTracksFragment.class.getName();
    @Bind(R.id.lvListItems)
    ListView mLvListItems;
    private TopTenTracksAdapter topTenTracksAdapter;
    private TopTenTrackSearchService topTenTrackSearchService;
    private TopTenTracksCallback topTenTracksCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            topTenTracksCallback = (TopTenTracksCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_ten_tracks_results_ui, container, false);
        ButterKnife.bind(this, view);
        topTenTracksAdapter = new TopTenTracksAdapter(getActivity(), R.layout.lv_row_top_ten_tracks, new ArrayList<Track>(0));
        mLvListItems.setAdapter(topTenTracksAdapter);
        mLvListItems.setOnItemClickListener(this);
        topTenTrackSearchService = new TopTenTrackSearchService(getActivity(), getTopTenTrackSearchResponseListener());
        topTenTrackSearchService.setCallback(new TopTenTrackSearchService.Callback() {
            @Override
            public void onPreExecute() {
                topTenTracksCallback.onLoading();
            }
        });
        topTenTracksCallback.onViewCreated();
        return view;
    }

    private TopTenTrackSearchService.ResponseListener getTopTenTrackSearchResponseListener() {
        return new TopTenTrackSearchService.ResponseListener() {
            @Override
            public void onResponse(Tracks tracks) {
                if (tracks != null) {
                    List<Track> trackList = tracks.tracks;
                    topTenTracksAdapter.clear();
                    topTenTracksAdapter.addAll(trackList);
                    topTenTracksAdapter.notifyDataSetChanged();
                    topTenTracksCallback.onResults();
                } else {
                    topTenTracksCallback.onNoResults();
                }
            }
        };
    }

    public void searchTopTenTracks(String artistId, String countryCode) {
        topTenTrackSearchService.searchTopTenTracks(artistId, countryCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        topTenTracksCallback.onItemSelected(topTenTracksAdapter.getTrackList(), position);
    }

    public interface TopTenTracksCallback {
        void onNoResults();

        void onLoading();

        void onResults();

        void onViewCreated();

        void onItemSelected(List<Track> trackList, int position);
    }
}
