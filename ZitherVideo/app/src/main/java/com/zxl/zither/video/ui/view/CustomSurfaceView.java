package com.zxl.zither.video.ui.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.zxl.common.DebugUtil;

import java.io.IOException;

public class CustomSurfaceView extends SurfaceView {
    private static final String TAG = "CustomSurfaceView";

    private Context mContext;

    private MediaPlayer mMediaPlayer;

    private IMediaPlayerListener mIMediaPlayerListener;

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
                if(mIMediaPlayerListener != null){
                    mIMediaPlayerListener.onPrepared(mp);
                }
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
                width = getWidth();
                height = getHeight();
                DebugUtil.d(TAG,"onVideoSizeChanged::width = " + width + "::height = " + height);
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                DebugUtil.d(TAG,"onVideoSizeChanged::videoWidth = " + videoWidth + "::videoHeight = " + videoHeight);
                if(videoWidth * height > videoHeight * width){
                    height = videoHeight * width / videoWidth;
                } else if(videoHeight * width > videoWidth * height){
                    width = videoWidth * height / videoHeight;
                }
                DebugUtil.d(TAG,"onVideoSizeChanged::width = " + width + "::height = " + height);

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                layoutParams.gravity = Gravity.CENTER;
                setLayoutParams(layoutParams);
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                DebugUtil.d(TAG,"onCompletion");
                if(mIMediaPlayerListener != null){
                    mIMediaPlayerListener.onCompletion(mp);
                }
                mp.start();
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
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });
    }

    public void setIMediaPlayerListener(IMediaPlayerListener listener){
        mIMediaPlayerListener = listener;
    }

    public void setDataSource(String videoUrl){
        if(TextUtils.isEmpty(videoUrl)){
            return;
        }

        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(videoUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
