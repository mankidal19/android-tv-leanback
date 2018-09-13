package com.android.example.leanback.fastlane;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.leanback.R;
import com.android.example.leanback.data.Video;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CardPresenter extends Presenter {

    //class variables to store the desired ImageCardView height and width and the application context
    private static int CARD_WIDTH = 200;
    private static int CARD_HEIGHT = 200;

    private static Context mContext;

    //onCreateViewHolder is called to create a new view.
    //In it we'll handle the logic of storing the context, and creating a new ImageCardView
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {

        Log.d("onCreateViewHolder", "creating viewholder");

        //Create the ImageCardView
        mContext = viewGroup.getContext();
        ImageCardView cardView = new ImageCardView(mContext);

        // set the cardView Focusable and FocusableInTouchMode to true
        // to enable it to be selected when browsing through the rows of content
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);

        //set the TextColor of the ImageCardView to light gray.
        ((TextView)cardView.findViewById(R.id.content_text)).setTextColor(Color.LTGRAY);

        return new ViewHolder(cardView);
    }

    //define the data binding logic in onBindViewHolder
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object o) {

        // cast the Object that's being passed in as our Video data
        Video video = (Video) o;

        //set the title text, subtext / content text, and image dimensions
        ((ViewHolder) viewHolder).mCardView.setTitleText(video.getTitle());
        ((ViewHolder) viewHolder).mCardView.setContentText(video.getDescription());
        ((ViewHolder) viewHolder).mCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

        // load the image with a thumbnail URL
        ((ViewHolder) viewHolder).updateCardViewImage(video.getThumbUrl());

    }
    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    //to handle image loading, create inner static class
    // PicassoImageCardViewTarget implementing com.squareup.picasso.Target

    static class PicassoImageCardViewTarget implements Target {

        //Add a variable to store the ImageCardView we'll draw into once the bitmap is loaded.
        private ImageCardView mImageCardView;

        // Create a constructor with the target ImageCardView as the
        // parameter and store it as the instance's mImageCardView
        public PicassoImageCardViewTarget(ImageCardView mImageCardView) {
            this.mImageCardView = mImageCardView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            //create a new Drawable from the bitmap and set it as
            // the main image for the ImageCardView
            Drawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            mImageCardView.setMainImage(bitmapDrawable);

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            //set the ImageCardView image to the error default.
            mImageCardView.setMainImage(errorDrawable);

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    //ViewHolder inner class to store all of the data associated with the view.
    static class ViewHolder extends Presenter.ViewHolder {

        //Define class variables to store the ImageCardView, Drawable, and PicassoImageCardViewTarget
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;
        private PicassoImageCardViewTarget mImageCardViewTarget;

        //constructor
        public ViewHolder(View view) {
            super(view);

            // cast the view parameter as an ImageCardView and store it in mCardView
            mCardView = (ImageCardView) view;

            //Instantiate a new PicassoImageCardViewTarget passing the cardView
            // as the target parameter
            mImageCardViewTarget = new PicassoImageCardViewTarget(mCardView);

            //get the default card image from resources
            mDefaultCardImage = mContext
                    .getResources()
                    .getDrawable(R.drawable.filmi);
        }

        //getter for mCardView
        public ImageCardView getCardView() {
            return mCardView;
        }

        // loads the image from a String URL
        protected void updateCardViewImage(String url) {

            Picasso.with(mContext)
                    .load(url)
                    .resize(CARD_WIDTH * 2, CARD_HEIGHT * 2)
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(mImageCardViewTarget);
        }


    }

}
