package com.android.example.leanback.fastlane;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SinglePresenterSelector;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.android.example.leanback.R;
import com.android.example.leanback.data.Video;
import com.android.example.leanback.data.VideoDataManager;
import com.android.example.leanback.data.VideoItemContract;

import java.io.Serializable;

public class LeanbackBrowseFragment extends BrowseFragment {
    private ArrayObjectAdapter mRowsAdapter;

    //create some sample categories. Here we're defining them as constants,
    // but in a real app you would probably pull them from your database.
    private static final String[] HEADERS = new String[]{
            "Home", "Live Broadcasts", "TV Shows", "Movies"
    };

    public void init() {
        //we instantiate mRowsAdapter
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        // set it as the Adapter for the fragment
        setAdapter(mRowsAdapter);

        //set the onItemViewClickedListener. 
        // Here we're using a helper function to generate the onItemViewClickedListener
        setOnItemViewClickedListener(getDefaultItemViewClickedListener());


        //set our main color and badge which appears in the top right of the browse view
        setBrandColor(ContextCompat.getColor(getContext(), R.color.primary));
        setBadgeDrawable(ContextCompat.getDrawable(getContext(), R.drawable.filmi));

        // loop through the categories and create a row of content for each one
        for (int position = 0; position < HEADERS.length; position++) {
            ObjectAdapter rowContents = new CursorObjectAdapter((new SinglePresenterSelector(new CardPresenter())));
            VideoDataManager manager = new VideoDataManager(getActivity(),
                    getLoaderManager(),
                    VideoItemContract.VideoItem.buildDirUri(),
                    rowContents);
            manager.startDataLoading();

            HeaderItem headerItem = new HeaderItem(position, HEADERS[position]);
            mRowsAdapter.add(new ListRow(headerItem, manager.getItemList()));
        }
    }

    //Override the onViewCreated method and call init
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    //Create the helper function getDefaultItemViewClickerListener which returns a new OnItemViewClickedListener
    public OnItemViewClickedListener getDefaultItemViewClickedListener() {
        return new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder viewHolder, Object o,
                                      RowPresenter.ViewHolder viewHolder2, Row row) {

                Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
                intent.putExtra(Video.INTENT_EXTRA_VIDEO, (Serializable)o);
                startActivity(intent);
            }
        };
    }
}
