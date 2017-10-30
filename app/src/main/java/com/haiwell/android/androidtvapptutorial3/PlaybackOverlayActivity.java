package com.haiwell.android.androidtvapptutorial3;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

import java.util.ArrayList;

public class PlaybackOverlayActivity extends Activity {
    private static final String TAG = PlaybackOverlayFragment.class.getSimpleName();

    private VideoView mVideoView;
    private ArrayList<Movie> mItems = new ArrayList<Movie>();
    private PlaybackController mPlaybackController;

    private LeanbackPlayBackState mPlayBackState = LeanbackPlayBackState.IDLE;

    private int mPostion = 0;
    private long mStartTimeMillis;
    private long mDuration = -1;

    public void playPause(boolean doPlay) {
        if (mPlayBackState == LeanbackPlayBackState.IDLE) {
            setupCallbacks();
        }
        if (doPlay && mPlayBackState != LeanbackPlayBackState.PLAYING) {
            mPlayBackState = LeanbackPlayBackState.PLAYING;
            if (mPostion > 0) {
                mVideoView.seekTo(mPostion);
            }
            mVideoView.start();
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPlayBackState = LeanbackPlayBackState.PAUSED;
            int timeElapsedSinceStart = (int) (System.currentTimeMillis() - mStartTimeMillis);
            setPostion(mPostion + timeElapsedSinceStart);
            mVideoView.pause();
        }
    }

    private void setupCallbacks() {
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mVideoView.stopPlayback();
                mPlayBackState = LeanbackPlayBackState.IDLE;
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mPlayBackState == LeanbackPlayBackState.PLAYING) {
                    mVideoView.start();
                }
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mPlayBackState = LeanbackPlayBackState.IDLE;
            }
        });
    }

    public void fastForward() {
        if (mDuration != -1) {
            setPostion(mVideoView.getCurrentPosition() + (10 * 1000));
            mVideoView.seekTo(mPostion);
        }
    }

    public void rewind() {
        setPostion(mVideoView.getCurrentPosition() - (10 * 1000));
        mVideoView.seekTo(mPostion);
    }

    public enum LeanbackPlayBackState {
        PLAYING,PAUSED,IDLE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_overlay);

        loadViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!requestVisibleBehind(true)) {
            mPlaybackController.playPause(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mVideoView.suspend();
        mVideoView.setVideoURI(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playback_overlay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadViews() {
        mVideoView = ((VideoView) findViewById(R.id.videoView));
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);

        Movie movie = ((Movie) getIntent().getSerializableExtra(DetailsActivity.MOVIE));
        setVideoPath(movie.getVideoUrl());
    }

    public void setVideoPath(String videoUrl) {
        setPostion(0);
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0;
        mDuration = Utils.getDuration(videoUrl);
    }

    private void setPostion(int position) {
        if (position > mDuration) {
            mPostion = (int) mDuration;
        } else if (position < 0) {
            mPostion = 0;
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPostion = position;
        }
        mStartTimeMillis = System.currentTimeMillis();
        Log.d(TAG, "position set to" + mPostion);
    }

    public int getPostion() {
        return mPostion;
    }

    public void setPlayBackState(LeanbackPlayBackState playBackState) {
        mPlayBackState = playBackState;
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

}
