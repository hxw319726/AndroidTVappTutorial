package com.haiwell.android.androidtvapptutorial3;


import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

/**
 * Created by Administrator on 2017/10/24.
 */

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder vh, Object item) {
        Movie movie = ((Movie) item);
        if (movie != null) {
            vh.getTitle().setText(movie.getTitle());
            vh.getSubtitle().setText(movie.getStudio());
            vh.getBody().setText(movie.getDescription());
        }
    }
}
