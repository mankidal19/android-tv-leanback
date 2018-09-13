package com.android.example.leanback.fastlane;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.SinglePresenterSelector;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.android.example.leanback.PlayerActivity;
import com.android.example.leanback.R;
import com.android.example.leanback.data.Video;
import com.android.example.leanback.data.VideoDataManager;
import com.android.example.leanback.data.VideoItemContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.media.session.PlaybackState.ACTION_PLAY;
import static java.security.AccessController.getContext;

/*
In VideoDetailsFragment, we need to do several things:

        Define the details presenter
        Load the movie thumbnail image
        Create a DetailsOverviewRow to display video details
        Create a presenter to bind the video data to the view
        Add a ListRow for recommended items
        Handle user actions
*/


public class VideoDetailsFragment extends DetailsFragment {
    //define a few class variables to store the Video information
    // and some constants for image sizes and action ids
    private Video selectedVideo;
    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    private static final int ACTION_PLAY = 1;
    private static final int ACTION_WATCH_LATER = 2;

    // getting the selected video from the intent
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedVideo = (Video) getActivity()
                .getIntent()
                .getSerializableExtra(Video.INTENT_EXTRA_VIDEO);

        // instantiate and execute the DetailRowBuilderTask in the onCreate method of the VideoDetailsFragment
        new DetailRowBuilderTask().execute(selectedVideo);
    }
    //assist in calculating the appropriate screen size in DP, create a utility function
    public static int dpToPx(int dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    //In order to not block the main UI thread,
// we create an AsyncTask to load the thumbnail bitmap.
// In VideoDetailsFragment create a DetailsRowBuilderTask class
// extending AsyncTask with Video, Integer, and DetailsOverviewRow as the parameter,
// progress, and result respectively.
    private class DetailRowBuilderTask extends AsyncTask<Video, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Video... videos) {

            // instantiating a new DetailsOverviewRow passing
            // in the current Video as the main item for the details page
            DetailsOverviewRow row = new DetailsOverviewRow(videos[0]);

            //set poster as the bitmap
            Bitmap poster = null;
            try {
                // the Picasso library helps us dealing with images
                poster = Picasso.with(getActivity())
                        .load(videos[0].getThumbUrl())
                        .resize(dpToPx(DETAIL_THUMB_WIDTH, getActivity().getApplicationContext()),
                                dpToPx(DETAIL_THUMB_HEIGHT, getActivity().getApplicationContext()))
                        .centerCrop()
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            row.setImageBitmap(getActivity(), poster);

            //specify the actions by creating a SparseArrayObjectAdapter to hold our actions
            //setting this as the DetailsOverviewRow's action adapter
            SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
            adapter.set(ACTION_PLAY, new Action(ACTION_PLAY, getResources().getString(R.string.action_play)));
            adapter.set(ACTION_WATCH_LATER, new Action(ACTION_WATCH_LATER, getResources().getString(R.string.action_watch_later)));
            row.setActionsAdapter(adapter);

            return row;
        }



        @Override
        protected void onPostExecute(DetailsOverviewRow detailRow) {
            // Instantiate a new ClassPresenterSelector.
            // This object allows you to define the presenters for each portion of DetailFragment
            ClassPresenterSelector ps = new ClassPresenterSelector();

            // Instantiate a new FullWidthDetailsOverviewRowPresenter passing in a new Instance
            // of DetailsDescriptionPresenter as a parameter
            FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter(getContext()));

            //add a custom background color programmatically
            detailsPresenter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
            detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_FULL);

            // Add an onActionClickedListener by creating
            // a new OnActionClickedListener and implementing onActionClicked
            detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
                @Override
                public void onActionClicked(Action action) {
                    // check the actionid. If the action is ACTION_PLAY we want to Intent
                    // to the VideoPlayer Activity passing the video details
                    //Otherwise, we'll create a toast to display a String defining the action
                    if (action.getId() == ACTION_PLAY) {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra(Video.INTENT_EXTRA_VIDEO, selectedVideo);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Add the FullWidthDetailsOverviewRowPresenter to ClassPresenterSelector
            ps.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);

            //Instantiate a new ArrayObjectAdapter passing in the ClassPresenterSelector.
            // Then, add the DetailRow to the ArrayObjectAdapter
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(ps);
            adapter.add(detailRow);

            //set the adapter of the DetailsFragment
            setAdapter(adapter);


            //Add a row of related videos below the detail panel
            ps.addClassPresenter(ListRow.class, new ListRowPresenter());

            /** bonus code for adding related items to details fragment **/
            // <START>
            adapter.add(detailRow);

            String subcategories[] = {
                    "You may also like"
            };

            CursorObjectAdapter rowAdapter = new CursorObjectAdapter(new SinglePresenterSelector(new CardPresenter()));
            VideoDataManager manager = new VideoDataManager(getActivity(), getLoaderManager(), VideoItemContract.VideoItem.buildDirUri(), rowAdapter);
            manager.startDataLoading();
            HeaderItem header = new HeaderItem(0, subcategories[0]);
            adapter.add(new ListRow(header, rowAdapter));
            setAdapter(adapter);
            // <END>


        }
    }
}

