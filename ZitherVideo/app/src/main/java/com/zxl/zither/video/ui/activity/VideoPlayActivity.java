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

    public static final String VIDEO_PLAY_EXTRA = "VIDEO_PLAY_EXTRA";

    private CustomSurfaceView mCustomSurfaceView;
    private VideoView videoView;
    private VideoControlView mVideoControlView;

    private ScrollView mViewContentScrollView;
    private TextView mVideoNameTv;
    private TextView mVideoDescTv;
    private ImageView mVideoImg;

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

        mViewContentScrollView = findViewById(R.id.view_content_scroll_view);
        mVideoNameTv = findViewById(R.id.video_name_tv);
        mVideoDescTv = findViewById(R.id.video_desc_tv);
        mVideoImg = findViewById(R.id.video_img);

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

                mVideoNameTv.setText(videoFileInfo.mVideoName);
                mVideoDescTv.setText(" Chinaunix首页 | 论坛 | 博客  登录 | 注册\n" +
                        "   \n" +
                        "一程山水一程歌\n" +
                        "7月12日12点博客新版上线，暂停写入操作\n" +
                        "ITPUB博客全新升级 夜间维护暂停公告\n" +
                        "为响应国家“净网2018”行动号召进行内容整治\n" +
                        "首页　| 　博文目录　| 　关于我\n" +
                        "\n" +
                        "Qidi_Huang\n" +
                        "\n" +
                        "博客访问： 142461\n" +
                        "博文数量： 41\n" +
                        "博客积分： 0\n" +
                        "博客等级： 民兵\n" +
                        "技术积分： 476\n" +
                        "用 户 组： 普通用户\n" +
                        "注册时间： 2016-09-01 19:08\n" +
                        "个人简介\n" +
                        "Android/Linux/音频/驱动\n" +
                        "\n" +
                        "文章分类\n" +
                        "全部博文（41）\n" +
                        "\n" +
                        "hack（1）\n" +
                        "Markdown/Haroopa（1）\n" +
                        "git（4）\n" +
                        "Linux（3）\n" +
                        "Linux驱动开发（5）\n" +
                        "Android开发技术（12）\n" +
                        "音频开发技术（14）\n" +
                        "未分配的博文（1）\n" +
                        "文章存档\n" +
                        "2017年（21）\n" +
                        "\n" +
                        "2016年（20）\n" +
                        "\n" +
                        "我的朋友\n" +
                        "\n" +
                        "niejun00\n" +
                        "\n" +
                        "\n" +
                        "Sen_Dark\n" +
                        "\n" +
                        "最近访客\n" +
                        "\n" +
                        "lnykhmx\n" +
                        "\n" +
                        "\n" +
                        "hellolin\n" +
                        "\n" +
                        "\n" +
                        "bzhao\n" +
                        "\n" +
                        "\n" +
                        "tomcodin\n" +
                        "\n" +
                        "\n" +
                        "1cpuer\n" +
                        "\n" +
                        "\n" +
                        "可怜的猪\n" +
                        "\n" +
                        "\n" +
                        "yejia805\n" +
                        "\n" +
                        "\n" +
                        "fuleru\n" +
                        "\n" +
                        "\n" +
                        "高_嘉嘉\n" +
                        "\n" +
                        "推荐博文\n" +
                        "·负载均衡使用nginx sticky实...\n" +
                        "·分布式架构原理解析，Java开...\n" +
                        "·APAP中REFRESH、CLEAR和FREE...\n" +
                        "·fail-fast是个什么策略？（转...\n" +
                        "·C# 打印PDF文档的10种方法...\n" +
                        "相关博文\n" +
                        "·人生就是如此\n" +
                        "·测试你的google使用水平...\n" +
                        "·CSS1规范\n" +
                        "·windows XP 系统文件诠释B（...\n" +
                        "·Fenng\n" +
                        "·eygle\n" +
                        "·ZR_FI_012\n" +
                        "·30岁前不必在乎的30件事...\n" +
                        "·明茨伯格的经理角色学派（198...\n" +
                        "·Switch Undo Tablespace\n" +
                        "使用git命令修改指定的commit提交记录并提交到远程服务器的方法\n" +
                        " 分类： LINUX2017-03-13 12:51:10\n" +
                        "【正文】\n" +
                        "\n" +
                        "    如果要修改最新的一次commit，我们知道可以使用 git commit --amend 命令。但是如果我们想要修改更早的提交记录，应该怎么做呢？比如我现在按照时间从旧到新，有 Commit1 到 Commit4 总共 4 次提交，其中 Commit4 是最新的一次提交，但是我只想修改 Commit3 的 Commit 信息并提交到远程服务器。关系如下：\n" +
                        "\n" +
                        "      Commit4\n" +
                        "\n" +
                        "      Commit3\n" +
                        "\n" +
                        "      Commit2\n" +
                        "\n" +
                        "      Commit1\n" +
                        "\n" +
                        "    我们可以使用如下命令实现：\n" +
                        "\n" +
                        "    1、执行命令 git  rebase  --interactive  HEAD^^ 以交互模式进行 rebase 操作。当然，我们也可以使用 Commit3 的 commit-id 作为参数，比如 Commit3 的 commit-id 是 a1b2c3 的话，使用命令 git  rebase --interactive  a1b2c3^ 效果是一样的。\n" +
                        "\n" +
                        "    2、上面这条命令会使我们进入到一个可编辑的界面。在编辑区顶部，会列出这次 rebase 操作所涉及的所有 commit 提交记录的摘要，它们每一行默认都是以 pick 开头的。找到你想要修改的那个 commit，将行首的 pick 关键字修改为 edit。然后保存并退出。这么做可以在执行和指定 commit 有关的 rebase 操作时暂停下来让我们对该 commit 的信息进行修改。\n" +
                        "\n" +
                        "    3、执行命令 git  commit  --amend 对上一个步骤指定的 commit 信息进行修改。这一步骤的界面就是填写 commit 信息的界面，更改信息后别忘记保存。（如果你在完成这一步骤后使用 git log 命令查看本地的代码提交记录，会发现看不到 Commit4 的信息。这是因为当前仍处于 rebase 的过程中，是正常现象）\n" +
                        "\n" +
                        "    4、执行 git  rebase  --continue 命令完成剩余的 rebase 操作。\n" +
                        "\n" +
                        "    5、执行 git  log 命令查看我们刚才的修改情况，确认已修改的内容和预期一致。\n" +
                        "\n" +
                        "    6、执行 git  push  xxxx  HEAD^:yyyy 命令将 Commit3 提交到远程服务器。其中 xxxx 和 yyyy 分别是你自己环境所使用的远程仓库名和分支名。当然，和 步骤1 一样，我们也可以使用 commit-id 作为参数实现提交，比如 Commit3 的 commit-id 是 a1b2c3 的话，使用命令 git  push  xxxx  a1b2c3:yyyy 效果是一样的。\n" +
                        "\n" +
                        "\n" +
                        "【参考资料】\n" +
                        "\n" +
                        "    [1] 《How to modify a specified commit in git》\n" +
                        "\n" +
                        "    [2] 《How to push only one commit with Git》\n" +
                        "\n" +
                        "\n" +
                        "阅读(1644) | 评论(0) | 转发(0) |\n" +
                        "0\n" +
                        "上一篇：近期博客更新频次降低之说明\n" +
                        "\n" +
                        "下一篇：定位和解决git am冲突的方法\n" +
                        "\n" +
                        "给主人留下些什么吧！~~\n" +
                        "评论热议\n" +
                        "请登录后评论。\n" +
                        "登录 注册\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "关于我们 | 关于IT168 | 联系方式 | 广告合作 | 法律声明 | 免费注册\n" +
                        "Copyright 2001-2010 ChinaUnix.net All Rights Reserved 北京皓辰网域网络信息技术有限公司. 版权所有\n" +
                        "\n" +
                        "感谢所有关心和支持过ChinaUnix的朋友们\n" +
                        "16024965号-6");
                Glide.with(mActivity).load("/storage/emulated/0/DCIM/Screenshots/Screenshot_2019-04-26-18-51-50-412_com.sdu.didi.psnger.png").into(mVideoImg);

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
