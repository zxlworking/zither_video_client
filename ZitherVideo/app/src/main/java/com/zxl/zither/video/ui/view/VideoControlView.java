package com.zxl.zither.video.ui.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zxl.zither.video.R;

import java.text.DecimalFormat;
import java.util.logging.Handler;

/**
 * Created by zhangxiaolong on 19-4-28.
 */
public class VideoControlView extends LinearLayout {
    private static final String TAG = "VideoControlView";

    private Context mContext;

    private MediaPlayer mMediaPlayer;

    private ImageView mPlayPauseImg;
    private SeekBar mSeekBar;
    private TextView mCurrentTimeTv;
    private TextView mTotalTimeTv;

    private HandlerThread mHandlerThread = new HandlerThread(TAG);
    private android.os.Handler mTaskHandler;

    private android.os.Handler mUIHandler = new android.os.Handler();

    private Object mLock = new Object();

    private Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayer == null){
                        return;
                    }
                    synchronized (mLock){
                        long currentTime = mMediaPlayer.getCurrentPosition();
                        long totalTime = mMediaPlayer.getDuration();
                        mSeekBar.setProgress((int) (currentTime * 100 / totalTime));
                        mCurrentTimeTv.setText(formatTime(currentTime));
                        mTotalTimeTv.setText(formatTime(totalTime));
                    }
                }
            });
            if(mMediaPlayer == null){
                return;
            }else{
                mTaskHandler.postDelayed(this, 1000);
            }
        }
    };

    public VideoControlView(Context context) {
        super(context);
        init(context);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mMediaPlayer = null;
        mTaskHandler.removeCallbacks(mLoopRunnable);
        mHandlerThread.quit();
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.video_control_view, this);

        mHandlerThread.start();
        mTaskHandler = new android.os.Handler(mHandlerThread.getLooper());

        mPlayPauseImg = findViewById(R.id.play_pause_img);
        mSeekBar = findViewById(R.id.seek_bar);
        mCurrentTimeTv = findViewById(R.id.current_time_tv);
        mTotalTimeTv = findViewById(R.id.total_time_tv);

        if(mMediaPlayer != null){
            mCurrentTimeTv.setText(formatTime(mMediaPlayer.getCurrentPosition()));
            mTotalTimeTv.setText(formatTime(mMediaPlayer.getDuration()));
        }

        mPlayPauseImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer == null){
                    return;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayPauseImg.setImageResource(R.drawable.ic_play_vector);
                } else {
                    mMediaPlayer.start();
                    mPlayPauseImg.setImageResource(R.drawable.ic_pause_vector);
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer == null){
                    return;
                }
                int progress = seekBar.getProgress();
                mMediaPlayer.seekTo(progress * mMediaPlayer.getDuration() / 100);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getVisibility() == VISIBLE){
                    setVisibility(GONE);
                }
            }
        });
    }

    private String formatTime(long time) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String timeStr = "";
        if (time < 60 * 1000) {
//            timeStr = "00:" + time;
            timeStr = "00:" + decimalFormat.format(time / 1000);
        } else if (time < 60 * 60 * 1000) {
            long minute = time / 60 / 1000;
            long second = time / 1000 % 60;
            if (minute < 10) {
                timeStr = "0" + minute;
            } else {
                timeStr = "" + minute;
            }
//            if (second < 10) {
//                timeStr = timeStr + ":0" + second;
//            } else {
//                timeStr = timeStr + ":" + second;
//            }
            timeStr = timeStr + decimalFormat.format(second);
        } else {
            long hour = time / 60 / 60 / 1000;
            long minute = time / 60 / 1000 % 60;
            long second = time / 1000 % (60 * 60);
            if (hour < 10) {
                timeStr = "0" + hour;
            } else {
                timeStr = "" + hour;
            }
            if (minute < 10) {
                timeStr = ":0" + minute;
            } else {
                timeStr = ":" + minute;
            }
//            if (second < 10) {
//                timeStr = timeStr + ":0" + second;
//            } else {
//                timeStr = timeStr + ":" + second;
//            }
            timeStr = timeStr + decimalFormat.format(second);
        }
        return timeStr;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer){
        mMediaPlayer = mediaPlayer;
    }

    public void setPrepared(){
        mTaskHandler.post(mLoopRunnable);
    }

    public void setComplete(){
        mTaskHandler.removeCallbacks(mLoopRunnable);
    }

    public void setError(){
        mTaskHandler.removeCallbacks(mLoopRunnable);
    }
}
