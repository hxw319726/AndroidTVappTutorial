package com.haiwell.android.androidtvapptutorial3;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/30.
 */

public class PlaybackController {
    private static final String TAG = PlaybackController.class.getSimpleName();
    private static final String MEDIA_SESSION_TAG = "AndroidTVappTutorialSession";

    private static final int MSG_STOP = 0;
    private static final int MSG_PAUSE = 1;
    private static final int MSG_PLAY = 2;
    private static final int MSG_REWIND = 3;
    private static final int MSG_SKIP_TO_PREVIOUS = 4;
    private static final int MSG_SKIP_TO_NEXT = 5;
    private static final int MSG_FAST_FORWARD = 6;
    private static final int MSG_SET_RATING = 7;
    private static final int MSG_SEEK_TO = 8;
    private static final int MSG_PLAY_PAUSE = 9;
    private static final int MSG_PLAY_FROM_MEDIA_ID = 10;
    private static final int MSG_PLAY_FROM_SEARCH = 11;
    private static final int MSG_SKIP_TO_QUEUE_ITEM = 12;

    private Activity mActivity;
    private MediaSession mSession;
    private MediaSessionCallback mMediaSessionCallback;
    private VideoView mVideoView;
    private static final ArrayList<Movie> mItems = MovieProvider.getMovieItems();

    private int mCurrentPlaybackState = PlaybackState.STATE_NONE;
    private int mCurrentItem;
    private int mPosition = 0;
    private long mStartTimeMillis;
    private long mDuration = -1;

    public int getCurrentPlaybackState() {
        return mCurrentPlaybackState;
    }

    public PlaybackController(Activity activity) {
        mActivity = activity;
        createMediaSession(mActivity);

    }

    public void setCurrentPlaybackState(int currentPlaybackState) {
        mCurrentPlaybackState = currentPlaybackState;
    }



    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int currentItem) {
        mCurrentItem = currentItem;
    }

    private void createMediaSession(Activity activity) {
        if (mSession == null) {
            mSession = new MediaSession(activity, MEDIA_SESSION_TAG);
            mMediaSessionCallback = new MediaSessionCallback();
            mSession.setCallback(mMediaSessionCallback);
            mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

            mSession.setActive(true);
            activity.setMediaController(new MediaController(activity, mSession.getSessionToken()));
        }
    }

    private class MediaSessionCallback extends MediaSession.Callback {

        @Override
        public void onPlay() {
            playPause(true);
        }

        @Override
        public void onPause() {
            playPause(false);
        }

        @Override
        public void onSkipToNext() {
            if (++mCurrentItem >= mItems.size()) {
                mCurrentItem = 0;
            }
            Movie movie = mItems.get(mCurrentItem);
            if (movie != null) {
                setVideoPath(movie.getVideoUrl());
                updateMetadata();
                playPause(mCurrentPlaybackState == PlaybackState.STATE_PLAYING);
            } else {
                Log.e(TAG, "onSkipToNext movie is null!");
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (--mCurrentItem < 0) {
                mCurrentItem = mItems.size() - 1;
            }
            Movie movie = mItems.get(mCurrentItem);
            if (movie != null) {
                setVideoPath(movie.getVideoUrl());
                updateMetadata();
                playPause(mCurrentPlaybackState == PlaybackState.STATE_PLAYING);
            } else {
                Log.e(TAG, "onSkipToNext movie is null!");
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mCurrentItem = Integer.parseInt(mediaId);
            Movie movie = mItems.get(mCurrentItem);
            if (movie != null) {
                setVideoPath(movie.getVideoUrl());
                updateMetadata();
                playPause(mCurrentPlaybackState == PlaybackState.STATE_PLAYING);
            }
        }

        @Override
        public void onSeekTo(long pos) {
            setPosition(((int) pos));
            mVideoView.seekTo(mPosition);
            updateMetadata();
        }

        @Override
        public void onFastForward() {
            fastforward();
        }

        @Override
        public void onRewind() {
            rewind();
        }
    }

    public void updateMetadata() {
        Movie movie = mItems.get(mCurrentItem);
        mDuration = Utils.getDuration(movie.getVideoUrl());
        updateMetadata(movie);
    }

    public void updateMetadata(Movie movie) {
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
        String title = movie.getTitle().replace("_", " -");

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, Long.toString(movie.getId()));
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, movie.getStudio());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, movie.getDescription());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, movie.getCardImageUrl());
        metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, mDuration);

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, movie.getStudio());

        Glide.with(mActivity)
                .load(Uri.parse(movie.getCardImageUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, resource);
                        mSession.setMetadata(metadataBuilder.build());
                    }
                });
    }

    public void releaseMediaSession() {
        if (mSession != null) {
            mSession.release();
        }
    }

    public void playPause(boolean doPlay) {
        if (mCurrentPlaybackState == PlaybackState.STATE_NONE) {
            setupCallback();
        }
        if (doPlay) {
            /*play*/
            if (mCurrentPlaybackState == PlaybackState.STATE_PLAYING) {
                return;
            } else {
                mCurrentPlaybackState = PlaybackState.STATE_PLAYING;
                mVideoView.start();
                mStartTimeMillis = System.currentTimeMillis();
            }
        } else {
            /*pause*/
            if (mCurrentPlaybackState == PlaybackState.STATE_PAUSED) {
                return;
            } else {
                mCurrentPlaybackState = PlaybackState.STATE_PAUSED;
            }
            setPosition(mVideoView.getCurrentPosition());
            mVideoView.pause();
        }
        updatePlaybackState();
    }

    private void setupCallback() {
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mVideoView.stopPlayback();
                mCurrentPlaybackState = PlaybackState.STATE_NONE;
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mCurrentPlaybackState == PlaybackState.STATE_PLAYING) {
                    mVideoView.start();
                }
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mCurrentPlaybackState = PlaybackState.STATE_NONE;
            }
        });
    }

    private void updatePlaybackState() {
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder().setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mCurrentPlaybackState == PlaybackState.STATE_PAUSED || mCurrentPlaybackState == PlaybackState.STATE_NONE) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, getCurrentPosition(), 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }
    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PAUSE |
                PlaybackState.ACTION_PLAY_PAUSE |
                PlaybackState.ACTION_REWIND |
                PlaybackState.ACTION_FAST_FORWARD |
                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                PlaybackState.ACTION_SKIP_TO_NEXT |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;
        return actions;
    }


    private void rewind() {
        setPosition(getCurrentPosition() - (10 * 1000));
        mVideoView.seekTo(mPosition);
    }

    private void fastforward() {
        if (mDuration != -1) {
            setPosition(getCurrentPosition() + (10 * 1000));
            mVideoView.seekTo(mPosition);
        }
    }
    public void setVideoPath(String videoUrl) {
        setPosition(0);
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0;
        mDuration = Utils.getDuration(videoUrl);
    }

    public void setPosition(int position) {
        if (position > mDuration) {
            mPosition = (int) mDuration;
        } else if (position < 0) {
            mPosition = 0;
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPosition = position;
        }
        mStartTimeMillis = System.currentTimeMillis();
        Log.d(TAG, "position set to" + mPosition);
    }

    public int getPosition() {
        return mPosition;
    }

    public int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }



    public void finishPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.suspend();
            mVideoView.setVideoURI(null);
        }
        releaseMediaSession();
    }

    public int getBufferPercentage() {
        return mVideoView.getBufferPercentage();
    }

    public int calcBufferedTime(int currentTime) {
        int bufferedTime;
        bufferedTime = ((int) (currentTime + ((mDuration - currentTime) * getBufferPercentage()))) / 100;
        return bufferedTime;
    }




}
