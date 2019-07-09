package com.zxl.zither.video.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zxl.zither.video.common.DebugUtil;
import com.zxl.zither.video.http.HttpGetProxy;

import java.io.IOException;

public class CustomSurfaceView extends SurfaceView {
    private static final String TAG = "CustomSurfaceView";

    private Context mContext;

    private VideoControlView mVideoControlView;

    private MediaPlayer mMediaPlayer;

    private IMediaPlayerListener mIMediaPlayerListener;

    private int mScreenOrientation = Configuration.ORIENTATION_PORTRAIT;

    private boolean isPlaying = false;

    public CustomSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                DebugUtil.d(TAG,"onPrepared");
                mp.start();
                mp.setScreenOnWhilePlaying(true);
                if(mIMediaPlayerListener != null){
                    mIMediaPlayerListener.onPrepared(mp);
                }
                if(mVideoControlView != null){
                    mVideoControlView.setPrepared();
                    mVideoControlView.isPlaying(true);
                }
                isPlaying = true;

            }
        });
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                DebugUtil.d(TAG,"onBufferingUpdate::percent = " + percent);
                if(mIMediaPlayerListener != null){
                    mIMediaPlayerListener.onBufferingUpdate(mp, percent);
                }
            }
        });
        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                DebugUtil.d(TAG,"onInfo::what = " + what);
                if(mIMediaPlayerListener != null){
                    mIMediaPlayerListener.onInfo(mp, what, extra);
                }
                return false;
            }
        });
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                resetViewSize(mp);
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                DebugUtil.d(TAG,"onCompletion");
                mp.setScreenOnWhilePlaying(false);
                if(mVideoControlView != null){
                    mVideoControlView.setComplete();
                }
                isPlaying = false;
                if(mIMediaPlayerListener != null){
                    mIMediaPlayerListener.onCompletion(mp);
                }

//                mp.start();
//                if(mVideoControlView != null){
//                    mVideoControlView.setPrepared();
//                }
//                isPlaying = true;

            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                DebugUtil.d(TAG,"onSeekComplete");

            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                DebugUtil.d(TAG,"onError");
                if(mVideoControlView != null){
                    mVideoControlView.setError();
                }
                closeMediaPlayer();
                isPlaying = false;
                return false;
            }
        });

        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                DebugUtil.d(TAG,"surfaceDestroyed");
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPlaying){
                    return;
                }
                if(mVideoControlView != null){
                    mVideoControlView.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void resetViewSize(MediaPlayer mp) {
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        DebugUtil.d(TAG,"resetViewSize::videoWidth = " + videoWidth + "::videoHeight = " + videoHeight);

        if(videoWidth == 0 || videoHeight == 0){
            return;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);
        int width;
        int height;
        if(mScreenOrientation == Configuration.ORIENTATION_PORTRAIT){
            width = mContext.getResources().getDisplayMetrics().widthPixels;
            height = videoHeight * width / videoWidth;
        }else{
            height = mContext.getResources().getDisplayMetrics().heightPixels;
            width = videoWidth * height / videoHeight;
        }
        DebugUtil.d(TAG,"resetViewSize::width = " + width + "::height = " + height);

        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        //getHolder().setFixedSize(width, height);
    }

    public void setIMediaPlayerListener(IMediaPlayerListener listener){
        mIMediaPlayerListener = listener;
    }

    public void setVideoControlView(VideoControlView videoControlView){
        mVideoControlView = videoControlView;
        mVideoControlView.setMediaPlayer(mMediaPlayer);
    }

    public void setDataSource(String videoUrl){
        DebugUtil.d(TAG,"setDataSource::videoUrl = " + videoUrl + "::mediaplayer = " + mMediaPlayer);
        if(TextUtils.isEmpty(videoUrl) || mMediaPlayer == null){
            return;
        }

        if(mVideoControlView != null){
            mVideoControlView.isPlaying(false);
        }

        mMediaPlayer.reset();

        try {
            //String localUrl = HttpGetProxy.getInstance().getLocalURL(videoUrl);
            //DebugUtil.d(TAG,"setDataSource::localUrl = " + localUrl);
            mMediaPlayer.setDataSource(videoUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        DebugUtil.d(TAG,"start()");
        mMediaPlayer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        DebugUtil.d(TAG,"onDetachedFromWindow");
        super.onDetachedFromWindow();
        if(mVideoControlView != null){
            mVideoControlView.isPlaying(false);
        }
        closeMediaPlayer();
        HttpGetProxy.getInstance().stop();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DebugUtil.d(TAG,"onConfigurationChanged::newConfig = " + newConfig.orientation + "::ORIENTATION_PORTRAIT = " + Configuration.ORIENTATION_PORTRAIT + "::ORIENTATION_LANDSCAPE = " + Configuration.ORIENTATION_LANDSCAPE);
        resetViewSize(mMediaPlayer);
    }

    private void closeMediaPlayer(){
        DebugUtil.d(TAG,"closeMediaPlayer");
        setKeepScreenOn(false);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
