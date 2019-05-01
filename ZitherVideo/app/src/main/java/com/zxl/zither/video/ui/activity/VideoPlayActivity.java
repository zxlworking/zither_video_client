package com.zxl.zither.video.ui.activity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
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

    private static final int PLAY_VIDEO_TYPE = 1;
    private static final int PLAY_STUDENT_VIDEO_TYPE = 2;
    private static final int PLAY_CIRCLE_VIDEO_TYPE = 3;

    public static final String VIDEO_PLAY_EXTRA = "VIDEO_PLAY_EXTRA";

    private CustomSurfaceView mCustomSurfaceView;
    private VideoView videoView;
    private VideoControlView mVideoControlView;

    private ScrollView mViewContentScrollView;
    private TextView mVideoNameTv;
    private TextView mVideoDescTv;
    private ImageView mVideoImg;

    private Button mPlayVideoBtn;
    private Button mPlayStudentVideoBtn;
    private Button mPlayCircleVideoBtn;

    private VideoFileInfo mVideoFileInfo;

    private long mLastTime;
    private long mLastInternalSpeed;

    private int mCurrentPlayType = PLAY_VIDEO_TYPE;

    private String mVideoUrl = "";

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

        mViewContentScrollView = findViewById(R.id.view_content_scroll_view);
        mVideoNameTv = findViewById(R.id.video_name_tv);
        mVideoDescTv = findViewById(R.id.video_desc_tv);
        mVideoImg = findViewById(R.id.video_img);

        mPlayVideoBtn = findViewById(R.id.play_video_btn);
        mPlayStudentVideoBtn = findViewById(R.id.play_student_video_btn);
        mPlayCircleVideoBtn = findViewById(R.id.play_circle_video_btn);

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
                playVideo();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String videoPlayStr = bundle.getString(VIDEO_PLAY_EXTRA);
            DebugUtil.d(TAG,"initView::videoPlayStr = " + videoPlayStr);
            mVideoFileInfo = CommonUtils.mGson.fromJson(videoPlayStr,VideoFileInfo.class);
            if(mVideoFileInfo != null){
                DebugUtil.d(TAG,"initView::mConvertVideo = " + mVideoFileInfo.mConvertVideo);

                mVideoNameTv.setText(mVideoFileInfo.mVideoName);
                mVideoDescTv.setText(mVideoFileInfo.mVideoDesc);
                Glide.with(mActivity).load(Constants.BASE_IMG_URL + mVideoFileInfo.mImgName).into(mVideoImg);

                playVideo();
            }
        }

        mPlayVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPlayType = PLAY_VIDEO_TYPE;
                playVideo();
            }
        });
        mPlayStudentVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPlayType = PLAY_STUDENT_VIDEO_TYPE;
                playVideo();
            }
        });
        mPlayCircleVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPlayType = PLAY_CIRCLE_VIDEO_TYPE;
                playVideo();
            }
        });
    }

    private void playVideo(){
        String videoUrl = "";
        String studentVideoUrl = "";
        String playUrl = "";
        if(TextUtils.equals(mVideoFileInfo.mConvertVideo, "1")){
            videoUrl = Constants.BASE_PLAY_VIDEO_URL+mVideoFileInfo.mVideoPath+".flv";
        }else{
            videoUrl = Constants.BASE_PLAY_VIDEO_URL+mVideoFileInfo.mVideoPath;
        }
        if(TextUtils.equals(mVideoFileInfo.mConvertVideo, "1")){
            studentVideoUrl = Constants.BASE_PLAY_VIDEO_URL+mVideoFileInfo.mStudentVideoPath+".flv";
        }else{
            studentVideoUrl = Constants.BASE_PLAY_VIDEO_URL+mVideoFileInfo.mStudentVideoPath;
        }
        switch (mCurrentPlayType){
            case PLAY_VIDEO_TYPE:
                playUrl = videoUrl;
                break;
            case PLAY_STUDENT_VIDEO_TYPE:
                playUrl = studentVideoUrl;
                break;
            case PLAY_CIRCLE_VIDEO_TYPE:
                if(TextUtils.equals(mVideoUrl, videoUrl)){
                    playUrl = studentVideoUrl;
                }else{
                    playUrl = videoUrl;
                }
                break;
        }

        DebugUtil.d(TAG,"playVideo::mVideoUrl = " + mVideoUrl);

        if(!TextUtils.isEmpty(playUrl)){
            mShowInternalSpeedRunnable.start();
            if(TextUtils.equals(mVideoUrl, playUrl)){
                mCustomSurfaceView.start();
            }else{
                mVideoUrl = playUrl;
                showLoading();
                mCustomSurfaceView.setDataSource(mVideoUrl);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DebugUtil.d(TAG,"onConfigurationChanged::newConfig = " + newConfig.orientation + "::ORIENTATION_PORTRAIT = " + Configuration.ORIENTATION_PORTRAIT + "::ORIENTATION_LANDSCAPE = " + Configuration.ORIENTATION_LANDSCAPE);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mViewContentScrollView.setVisibility(View.VISIBLE);
        }else{
            mViewContentScrollView.setVisibility(View.GONE);
        }
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
