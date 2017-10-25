package com.haiwell.android.androidtvapptutorial3;

import android.app.Activity;
import android.app.LoaderManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v17.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/10/23.
 */

public class PicassoBackgroundManager {
    private static final String TAG = SimpleBackgroundManager.class.getSimpleName();

    private static final int BACKGROUND_UPDATE_DELAY = 500;
    private final int DEFAULT_BACKGROUND_RES_ID = R.drawable.default_background;
    private static Drawable mDefaultBackground;
    // Handler attached with main thread
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Activity mActivity;
    private BackgroundManager mBackgroundManager = null;
    private DisplayMetrics mMetrics;
    private URI mBackgroundURI;
    private PicassoBackgroundManagerTarget mBackgroundManagerTarget;

    Timer mBackgroundTimer;

    public PicassoBackgroundManager(Activity activity) {
        mActivity = activity;
        mDefaultBackground = activity.getDrawable(DEFAULT_BACKGROUND_RES_ID);
        mBackgroundManager = BackgroundManager.getInstance(activity);
        mBackgroundManager.attach(activity.getWindow());
        mBackgroundManagerTarget = new PicassoBackgroundManagerTarget(mBackgroundManager);
        mMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    /**
     * if UpdateBackgroundTask is already running, cancel this task and start new task.
     */
    private void startBackgroundTimer() {
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
                    }
                }
            });
        }
    }

    private void updateBackground(URI backgroundURI) {
        try {
            Picasso.with(mActivity)
                    .load(backgroundURI.toString())
                    .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                    .centerCrop()
                    .error(mDefaultBackground)
                    .into(mBackgroundManagerTarget);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    public void updateBackgroundWithDelay(String url) {
        try {
            URI uri = new URI(url);
            updateBackgroundWithDelay(uri);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void updateBackgroundWithDelay(URI uri) {
        mBackgroundURI = uri;
        startBackgroundTimer();
    }

    /**
     * Copied from AOSP sample code.
     * Inner class
     * Picasso target for updating default_background images
     */
    private class PicassoBackgroundManagerTarget implements Target{
        BackgroundManager mBackgroundManager;

        public PicassoBackgroundManagerTarget(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            this.mBackgroundManager.setBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            this.mBackgroundManager.setDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            PicassoBackgroundManagerTarget that = ((PicassoBackgroundManagerTarget) obj);
            if (!mBackgroundManager.equals(that.mBackgroundManager)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return mBackgroundManager.hashCode();
        }
    }
}
