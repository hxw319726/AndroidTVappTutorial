package com.haiwell.android.androidtvapptutorial3;

import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;

/**
 * Created by Administrator on 2017/10/23.
 */

public class CustomFullWidthDetailsOverviewRowPresenter extends FullWidthDetailsOverviewRowPresenter {
    private static final String TAG = CustomFullWidthDetailsOverviewRowPresenter.class.getSimpleName();

    CustomFullWidthDetailsOverviewRowPresenter(Presenter presenter) {
        super(presenter);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        this.setState(((ViewHolder) holder), FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
    }
}
