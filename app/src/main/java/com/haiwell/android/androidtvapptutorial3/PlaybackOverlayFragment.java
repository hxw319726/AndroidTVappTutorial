package com.haiwell.android.androidtvapptutorial3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/24.
 */

public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment{
    private static final String TAG = PlaybackOverlayFragment.class.getSimpleName();
    private static final int SIMULATED_BUFFERED_TIME = 10000;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;
    private static final boolean SHOW_IMAGE = true;

    private static Context sContext;

    private Movie mSelectedMovie;
    private PlaybackControlsRow mPlaybackControlsRow;
    private ArrayObjectAdapter mPrimaryActionAdapter;
    private ArrayObjectAdapter mSecondaryActionAdapter;
    private int mCurrentPlaybackState;
    private Handler mHandler;
    private Runnable mRunnable;
    private ArrayList<Movie> mItems = new ArrayList<Movie>();

    private ArrayObjectAdapter mRowsAdapter;

    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private PlaybackControlsRow.RepeatAction mRepeatAction;
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction;
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction;
    private PlaybackControlsRow.ShuffleAction mShuffleAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow.HighQualityAction mHighQualityAction;
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction;
    private PlaybackControlsRow.MoreActions mMoreActions;
    private int mCurrentItem;
    private PicassoPlaybackControlsRowTarget mPlaybackControlsRowTarget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mSelectedMovie = ((Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE));
        sContext = getActivity();
        mHandler = new Handler(Looper.getMainLooper());

        setBackgroundType(PlaybackOverlayFragment.BG_LIGHT);
        setFadingEnabled(true);

        mItems = MovieProvider.getMovieItems();
        mCurrentItem = (int) mSelectedMovie.getId() - 1;

        setUpRows();
    }

    private void setUpRows() {
        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        playbackControlsRowPresenter = new PlaybackControlsRowPresenter(new DetailsDescriptionPresenter());

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();


        addOtherRows();

        /*onClick*/
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PLAY);
                } else if (action.getId() == mSkipNextAction.getId()) {
                    next(mCurrentPlaybackState == PlaybackState.STATE_PLAYING);
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    prev(mCurrentPlaybackState == PlaybackState.STATE_PLAYING);
                } else if (action.getId() == mFastForwardAction.getId()) {
                    fastForward();
                } else if (action.getId() == mRewindAction.getId()) {
                    rewind();
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    /* Following action is subclass of MultiAction
                     * - PlayPauseAction
                     * - FastForwardAction
                     * - RewindAction
                     * - ThumbsAction
                     * - RepeatAction
                     * - ShuffleAction
                     * - HighQualityAction
                     * - ClosedCaptioningAction
                     */
                    notifyChanged(action);
                    /*Changed icon*/
                    if (action instanceof PlaybackControlsRow.ThumbsUpAction ||
                            action instanceof PlaybackControlsRow.ThumbsDownAction ||
                            action instanceof PlaybackControlsRow.RepeatAction ||
                            action instanceof PlaybackControlsRow.ShuffleAction ||
                            action instanceof PlaybackControlsRow.HighQualityAction ||
                            action instanceof PlaybackControlsRow.ClosedCaptioningAction) {
                        ((PlaybackControlsRow.MultiAction) action).nextIndex();
                    }

                }
            }
        });
        setAdapter(mRowsAdapter);
    }

    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private void prev(boolean autoPlay) {
        if (--mCurrentItem < 0) {
            mCurrentItem = mItems.size() - 1;
        }
        if (autoPlay) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
        }
        Movie movie = mItems.get(mCurrentItem);
        if (movie != null) {
            ((PlaybackOverlayActivity) getActivity()).setVideoPath(movie.getVideoUrl());
            ((PlaybackOverlayActivity) getActivity()).setPlayBackState(PlaybackOverlayActivity.LeanbackPlayBackState.PAUSED);
            ((PlaybackOverlayActivity) getActivity()).playPause(autoPlay);

        }
    }

    private void next(boolean autoPlay) {
        if (++mCurrentItem >= mItems.size()) {
            mCurrentItem = 0;
        }
        if (autoPlay) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
        }
        Movie movie = mItems.get(mCurrentItem);
        if (movie != null) {
            ((PlaybackOverlayActivity) getActivity()).setVideoPath(movie.getVideoUrl());
            ((PlaybackOverlayActivity) getActivity()).setPlayBackState(PlaybackOverlayActivity.LeanbackPlayBackState.PAUSED);
            ((PlaybackOverlayActivity) getActivity()).playPause(autoPlay);

        }
        playbackStateChanged();
        updatePlaybackRow(mCurrentItem);
    }

    private void updatePlaybackRow(int index) {
        Log.d(TAG, "updatePlaybackRow");
        if (mPlaybackControlsRow.getItem() != null) {
            Movie item = ((Movie) mPlaybackControlsRow.getItem());
            item.setTitle(mItems.get(mCurrentItem).getTitle());
            item.setStudio(mItems.get(mCurrentItem).getStudio());

            mRowsAdapter.notifyArrayItemRangeChanged(0, 1);

            int duration = ((int) Utils.getDuration(mItems.get(mCurrentItem).getVideoUrl()));
            Log.i(TAG, "videoUrl:" + mItems.get(mCurrentItem).getVideoUrl());
            Log.i(TAG, "duration=" + duration);
            mPlaybackControlsRow.setTotalTime(duration);
            mPlaybackControlsRow.setCurrentTime(0);
            mPlaybackControlsRow.setBufferedProgress(0);
        }
        if (SHOW_IMAGE) {
            mPlaybackControlsRowTarget = new PicassoPlaybackControlsRowTarget(mPlaybackControlsRow);
            updateVideoImage(mItems.get(mCurrentItem).getCardImageURI());
        }
    }

    private void updateVideoImage(URI cardImageURI) {
        Picasso.with(sContext)
                .load(cardImageURI.toString())
                .resize(Utils.convertDpToPixel(sContext, CARD_WIDTH),
                        Utils.convertDpToPixel(sContext, CARD_HEIGHT))
                .into(mPlaybackControlsRowTarget);
    }

    private void rewind() {
        ((PlaybackOverlayActivity) getActivity()).rewind();

        int currentTime = ((PlaybackOverlayActivity) getActivity()).getPostion();
        mPlaybackControlsRow.setCurrentTime(currentTime);
        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
    }

    private void fastForward() {
        /*Video control part*/
        ((PlaybackOverlayActivity) getActivity()).fastForward();

        /*ui part*/
        int currentTime = ((PlaybackOverlayActivity) getActivity()).getPostion();
        mPlaybackControlsRow.setCurrentTime(currentTime);
        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
    }

    private void togglePlayback(boolean playPause) {
        /*Video control part*/
        ((PlaybackOverlayActivity) getActivity()).playPause(playPause);
        /*UI control part*/
        playbackStateChanged();

    }

    private void playbackStateChanged() {
        if (mCurrentPlaybackState != PlaybackState.STATE_PLAYING) {
            mCurrentPlaybackState = PlaybackState.STATE_PLAYING;
            startProgressAutomation();
            setFadingEnabled(true);
            mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PAUSE);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PAUSE));
            notifyChanged(mPlayPauseAction);
        } else if (mCurrentPlaybackState != PlaybackState.STATE_PAUSED) {
            mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
            stopProgressAutomation();
            mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PLAY);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PLAY));
            notifyChanged(mPlayPauseAction);
        }
        int currentTime = ((PlaybackOverlayActivity) getActivity()).getPostion();
        mPlaybackControlsRow.setCurrentTime(currentTime);
        mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
    }

    private void startProgressAutomation() {
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    int updatePeriod = getUpdatePeriod();
                    int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                    int totalTime = mPlaybackControlsRow.getTotalTime();
                    mPlaybackControlsRow.setCurrentTime(currentTime);
                    mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
                    if (totalTime > 0 && totalTime <= currentTime) {
                        stopProgressAutomation();
                    } else {
                        mHandler.postDelayed(this, updatePeriod);
                    }
                }
            };
            mHandler.postDelayed(mRunnable, getUpdatePeriod());
        }
    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
    }

    private void addOtherRows() {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        Movie movie = new Movie();
        movie.setTitle("Title");
        movie.setStudio("studio");
        movie.setDescription("Description");
        movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
        listRowAdapter.add(movie);
        listRowAdapter.add(movie);

        HeaderItem header = new HeaderItem(0, "OtherRows");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void addPlaybackControlsRow() {
        mPlaybackControlsRow = new PlaybackControlsRow(mSelectedMovie);
        mRowsAdapter.add(mPlaybackControlsRow);

        ControlButtonPresenterSelector presenterSelector;
        presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionAdapter = new ArrayObjectAdapter(presenterSelector);
        mSecondaryActionAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionAdapter);

        Activity activity = getActivity();
        mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(activity);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(activity);
        mThumbsUpAction=new PlaybackControlsRow.ThumbsUpAction(activity) ;
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(activity);
        mShuffleAction = new PlaybackControlsRow.ShuffleAction(activity);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(activity);
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(activity);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(activity);
        mRewindAction = new PlaybackControlsRow.RewindAction(activity);
        mHighQualityAction = new PlaybackControlsRow.HighQualityAction(activity);
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(activity);
        mMoreActions = new PlaybackControlsRow.MoreActions(activity);

        /*PrimaryAction setting*//*ksjdj*/
        mPrimaryActionAdapter.add(mSkipPreviousAction);
        mPrimaryActionAdapter.add(mRewindAction);
        mPrimaryActionAdapter.add(mPlayPauseAction);
        mPrimaryActionAdapter.add(mFastForwardAction);
        mPrimaryActionAdapter.add(mSkipNextAction);

        /*SecondaryAction setting*/
        mSecondaryActionAdapter.add(mThumbsUpAction);
        mSecondaryActionAdapter.add(mThumbsDownAction);
        mSecondaryActionAdapter.add(mRepeatAction);
        mSecondaryActionAdapter.add(mShuffleAction);
        mSecondaryActionAdapter.add(mHighQualityAction);
        mSecondaryActionAdapter.add(mClosedCaptioningAction);
        mSecondaryActionAdapter.add(mMoreActions);

    }

    private static class PicassoPlaybackControlsRowTarget implements Target{
        PlaybackControlsRow mPlaybackControlsRow;

        public PicassoPlaybackControlsRowTarget(PlaybackControlsRow playbackControlsRow) {
            mPlaybackControlsRow = playbackControlsRow;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable bitmapDrawable = new BitmapDrawable(sContext.getResources(), bitmap);
            mPlaybackControlsRow.setImageDrawable(bitmapDrawable);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mPlaybackControlsRow.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
