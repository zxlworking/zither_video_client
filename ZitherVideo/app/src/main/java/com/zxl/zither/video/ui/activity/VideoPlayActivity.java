package com.zxl.zither.video.ui.activity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zxl.common.DebugUtil;
import com.zxl.zither.video.R;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.model.data.FileInfo;
import com.zxl.zither.video.model.data.VideoFileInfo;
import com.zxl.zither.video.ui.view.CustomSurfaceView;
import com.zxl.zither.video.ui.view.IMediaPlayerListener;
import com.zxl.zither.video.ui.view.VideoControlView;
import com.zxl.zither.video.utils.Constants;

import java.text.DecimalFormat;

public class VideoPlayActivity extends BaseActivity {
    private static final String TAG = "VideoPlayActivity";

    public static final String VIDEO_PLAY_EXTRA = "VIDEO_PLAY_EXTRA";

    private CustomSurfaceView mCustomSurfaceView;
    private VideoView videoView;
    private VideoControlView mVideoControlView;

    private String mVideoUrl;

    private long mLastTime;
    private long mLastInternalSpeed;

    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    private ShowInternalSpeedRunnable mShowInternalSpeedRunnable = new ShowInternalSpeedRunnable();

    @Override
    public int getResLayout() {
        return R.layout.activity_video_play;
    }

    @Override
    public void initView() {
        mCustomSurfaceView = findViewById(R.id.surfaceView);
        videoView = findViewById(R.id.video_view);
        mVideoControlView = findViewById(R.id.video_control_view);

        mCustomSurfaceView.setVideoControlView(mVideoControlView);
        mCustomSurfaceView.setIMediaPlayerListener(new IMediaPlayerListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mShowInternalSpeedRunnable.stop();
                hideLoading(false);
            }

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        showLoading();
                        mShowInternalSpeedRunnable.start();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        hideLoading(false);
                        mShowInternalSpeedRunnable.stop();
                        break;
                }
                return false;
            }

            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String videoPlayStr = bundle.getString(VIDEO_PLAY_EXTRA);
            DebugUtil.d(TAG,"initView::videoPlayStr = " + videoPlayStr);
            VideoFileInfo videoFileInfo = CommonUtils.mGson.fromJson(videoPlayStr,VideoFileInfo.class);
            if(videoFileInfo != null){
                DebugUtil.d(TAG,"initView::mConvertVideo = " + videoFileInfo.mConvertVideo);
                if(TextUtils.equals(videoFileInfo.mConvertVideo, "1")){
                    mVideoUrl = Constants.BASE_PLAY_VIDEO_URL+videoFileInfo.mVideoId+".flv";
                }else{
                    mVideoUrl = Constants.BASE_PLAY_VIDEO_URL+videoFileInfo.mVideoId;
                }
                DebugUtil.d(TAG,"initView::mVideoUrl = " + mVideoUrl);

                if(!TextUtils.isEmpty(mVideoUrl)){
                    //mVideoUrl = "http://129.211.4.46:8080";
                    //mVideoUrl = "http://129.211.4.46:8081/out.flv";
                    showLoading();
                    mShowInternalSpeedRunnable.start();
                    mCustomSurfaceView.setDataSource(mVideoUrl);

//                    videoView.setMediaController(new MediaController(this));
//                    videoView.setVideoURI(Uri.parse(mVideoUrl));
//                    videoView.start();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class ShowInternalSpeedRunnable implements Runnable {

        private boolean isStop = false;

        public void start(){
            isStop = false;
            mUIHandler.post(mShowInternalSpeedRunnable);
        }

        public void stop(){
            isStop = true;
            mUIHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            long currentInternalSpeed = CommonUtils.getInternetSpeed(mActivity);
            long currentTime = System.currentTimeMillis();
            if(mLastTime > 0){
                long speed = (currentInternalSpeed - mLastInternalSpeed) * 1000 / (currentTime - mLastTime);
                if(speed > 1024){
                    float speed2 = speed * 1.0f / 1024;
                    setLoadingMessage(mDecimalFormat.format(speed2) + "MB/S");
                }else{
                    setLoadingMessage(speed + "KB/S");
                }
            }
            mLastInternalSpeed = currentInternalSpeed;
            mLastTime = currentTime;

            if(!isStop){
                mUIHandler.postDelayed(this, 1000);
            }
        }
    }
}
