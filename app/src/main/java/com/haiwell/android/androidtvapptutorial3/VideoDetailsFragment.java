package com.haiwell.android.androidtvapptutorial3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/23.
 */

public class VideoDetailsFragment extends DetailsFragment {
    private static final String TAG = VideoDetailsFragment.class.getSimpleName();

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final String MOVIE = "Movie";
    private static final int ACTION_PLAY_VIDEO = 1;

    private CustomFullWidthDetailsOverviewRowPresenter mFwdorPresenter;
    private PicassoBackgroundManager mPicassoBackgroundManager;

    private Movie mSelectedMovie;
    private DetailsRowBuilderTask mDetailsRowBuilderTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        mPicassoBackgroundManager = new PicassoBackgroundManager(getActivity());
        mSelectedMovie = (Movie) getActivity().getIntent().getSerializableExtra(MOVIE);

        mDetailsRowBuilderTask = ((DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie));
        mPicassoBackgroundManager.updateBackgroundWithDelay(mSelectedMovie.getCardImageUrl());

    }

    @Override
    public void onStop() {
        mDetailsRowBuilderTask.cancel(true);
        super.onStop();
    }

    private class DetailsRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Movie... movies) {
            DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
            try {
                Bitmap poster = Picasso.with(getActivity())
                        .load(mSelectedMovie.getCardImageUrl())
                        .resize(Utils.convertDpToPixel(getActivity().getApplication(), DETAIL_THUMB_WIDTH),
                                Utils.convertDpToPixel(getActivity().getApplication(), DETAIL_THUMB_HEIGHT))
                        .centerCrop()
                        .get();
                row.setImageBitmap(getActivity(), poster);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow detailsOverviewRow) {
            /*1st row:DetailsOverviewRow*/
//            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
//            for (int i = 0; i < 10; i++) {
//                sparseArrayObjectAdapter.set(i, new Action(i, "lable1", "lable2"));
//            }
//            detailsOverviewRow.setActionsAdapter(sparseArrayObjectAdapter);
            /*action setting*/
            SparseArrayObjectAdapter sparseArrayObjectAdapter;
            sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
            sparseArrayObjectAdapter.set(0, new Action(ACTION_PLAY_VIDEO, "Play Video"));
            sparseArrayObjectAdapter.set(1, new Action(1, "Action 2", "lable"));
            sparseArrayObjectAdapter.set(2, new Action(2, "Action 3", "lable"));

            detailsOverviewRow.setActionsAdapter(sparseArrayObjectAdapter);
            mFwdorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
                @Override
                public void onActionClicked(Action action) {
                    if (action.getId() == ACTION_PLAY_VIDEO) {
                        Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                        intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie);
                        intent.putExtra(getResources().getString(R.string.should_start), true);
                        startActivity(intent);
                    }
                }
            });

            /*2nd row:ListRow*/
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
//            for (int i = 0; i < 10; i++) {
//                Movie movie = new Movie();
//                if(i%3 == 0) {
//                    movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
//                } else if (i%3 == 1) {
//                    movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02630.jpg");
//                } else {
//                    movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02529.jpg");
//                }
//                movie.setTitle("title" + i);
//                movie.setStudio("studio" + i);
//                listRowAdapter.add(movie);
//            }
            ArrayList<Movie> mItems = MovieProvider.getMovieItems();
            for (Movie movie : mItems) {
                listRowAdapter.add(movie);
            }
            HeaderItem headerItem = new HeaderItem(0, "Ralated Videos");

            ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
//            mFwdorPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
            Log.e(TAG, "mFwdorPresenter.getInitialState:" + mFwdorPresenter.getInitialState());

            classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFwdorPresenter);
            classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(classPresenterSelector);
            /*1st row*/
            adapter.add(detailsOverviewRow);
            /*2nd row*/
            adapter.add(new ListRow(headerItem, listRowAdapter));
            /*3rd row*/
            setAdapter(adapter);
        }
    }
}
