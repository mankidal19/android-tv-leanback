package com.android.example.leanback.fastlane;

import android.content.Context;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.util.Log;

import com.android.example.leanback.R;
import com.android.example.leanback.data.Video;

//Before we get the image and create the DetailsOverviewRow we need to define a Presenter to bind the data.
// The Leanback framework provides the AbstractDetailsDescriptionPresenter class for this purpose,
// a nearly complete implementation of the presenter for media item details.

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    private final Context mContext;

    private DetailsDescriptionPresenter() {
        super();
        this.mContext = null;
    }

    public DetailsDescriptionPresenter(Context ctx) {
        super();
        this.mContext = ctx;
    }

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Video video = (Video) item;

        if (video != null) {
            Log.d("Presenter", String.format("%s, %s, %s", video.getTitle(), video.getThumbUrl(), video.getDescription()));
            viewHolder.getTitle().setText(video.getTitle());
            viewHolder.getSubtitle().setText(String.format(mContext.getString(R.string.rating), video.getRating()));
            viewHolder.getBody().setText(video.getDescription());
        }
    }
}




