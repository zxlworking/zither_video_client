package com.zxl.zither.video.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.common.DebugUtil;
import com.zxl.zither.video.R;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.event.LogoutSuccessEvent;
import com.zxl.zither.video.event.UploadLogFileEvent;
import com.zxl.zither.video.http.FileRequestBody;
import com.zxl.zither.video.http.HttpUtils;
import com.zxl.zither.video.http.RetrofitCallback;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.data.FileInfo;
import com.zxl.zither.video.model.data.UserInfo;
import com.zxl.zither.video.model.data.VideoFileInfo;
import com.zxl.zither.video.model.response.FileInfoResponse;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.model.response.VideoFileInfoResponse;
import com.zxl.zither.video.utils.Constants;
import com.zxl.zither.video.utils.EventBusUtils;
import com.zxl.zither.video.utils.SharePreUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.zxl.zither.video.ui.activity.VideoPlayActivity.VIDEO_PLAY_EXTRA;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private static final int OPEN_GALLERY_REQUEST_CODE = 1;
    private static final int UP_LOAD_VIDO_FILE_REQUEST_CODE = 2;

    private boolean isLoading = false;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private VideoListAdapter mVideoListAdapter;

    private Menu mMenu;

    @Override
    public int getResLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mErrorRefreshClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        };

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getData();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mVideoListAdapter = new VideoListAdapter();
        mRecyclerView.setAdapter(mVideoListAdapter);

        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MainActivity.this);

        inflater.inflate(R.menu.main, menu);

        mMenu = menu;

        UserInfo userInfo = SharePreUtils.getInstance(this).getUserInfo();
        if(userInfo != null && userInfo.mUserType == Constants.USER_TYPE_NORMAL){
            menu.removeItem(R.id.menu_upload_video_file);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_upload_video_file:
                Intent mOpenGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mOpenGalleryIntent.setType("video/*");
                startActivityForResult(mOpenGalleryIntent,OPEN_GALLERY_REQUEST_CODE);
                break;
            case R.id.menu_logout:
                SharePreUtils.getInstance(mActivity).saveUserInfo(null);
                EventBusUtils.post(new LogoutSuccessEvent());
                startActivity(new Intent(mActivity, LoginActivity.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugUtil.d(TAG,"onActivityResult::requestCode = " + requestCode);
        DebugUtil.d(TAG,"onActivityResult::resultCode = " + resultCode);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case OPEN_GALLERY_REQUEST_CODE:
                    if(data == null){
                        DebugUtil.d(TAG,"onActivityResult::GALLERY_OPEN_REQUEST_CODE::data null");
                    }else{
                        DebugUtil.d(TAG,"onActivityResult::GALLERY_OPEN_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mActivity,data.getData());
                        DebugUtil.d(TAG,"onActivityResult::GALLERY_OPEN_REQUEST_CODE::mGalleryPath = " + mGalleryPath);
                        Intent intent = new Intent(mActivity, UpLoadVideoFileActivity.class);
                        intent.putExtra(UpLoadVideoFileActivity.UPLOAD_VIDEO_FILE_PATH_EXTRA, mGalleryPath);
                        startActivityForResult(intent, UP_LOAD_VIDO_FILE_REQUEST_CODE);
                    }
                    break;
                case UP_LOAD_VIDO_FILE_REQUEST_CODE:
                    getData();
                    break;
            }
        }
    }

    private void getData() {
        DebugUtil.d(TAG,"getData");
        if(isLoading){
            return;
        }
        isLoading = true;

        showLoading();
        mRecyclerView.setVisibility(View.GONE);

        HttpUtils.getInstance().getVideoFileList(this, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                VideoFileInfoResponse videoFileInfoResponse = (VideoFileInfoResponse) responseBaseBean;
                if(videoFileInfoResponse.mResponseBaseBean.code == 0){
                    List<VideoFileInfo> videoFileInfoList = new ArrayList<>();
                    for(VideoFileInfo videoFileInfo : videoFileInfoResponse.mVideoFileInfoList){
                        if(TextUtils.equals(videoFileInfo.mConvertVideo,"1")){
                            videoFileInfoList.add(videoFileInfo);
                        }
                    }
                    mVideoListAdapter.setData(videoFileInfoList);
                }else{
                    mVideoListAdapter.clearData();
                }
                hideLoading(false);
                mRecyclerView.setVisibility(View.VISIBLE);
                isLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.no_network_tip)),Toast.LENGTH_SHORT).show();
                hideLoading(true);
                isLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.network_error_tip)," 请检查网络"),Toast.LENGTH_SHORT).show();
                hideLoading(true);
                isLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.server_error_tip),responseBaseBean.desc),Toast.LENGTH_SHORT).show();
                hideLoading(true);
                isLoading = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    class VideoListAdapter extends RecyclerView.Adapter<ViewHolder>{

        private List<VideoFileInfo> mVideoFileInfoList = new ArrayList<>();

        public void setData(List<VideoFileInfo> list){
            mVideoFileInfoList.clear();
            mVideoFileInfoList.addAll(list);
            notifyDataSetChanged();
        }

        public void clearData(){
            mVideoFileInfoList.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_video_list_view, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            viewHolder.mItemVideoNameTv.setText(mVideoFileInfoList.get(i).mVideoName);
            viewHolder.mItemVideoDescTv.setText("{\"status\":0,\"ver\":1552556805,\"name\":\"为你推荐\",\"language\":\"zh_CN\",\"encoding\":\"UTF-8\",\"data\":[{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560228\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"深夜没在鼓楼买过醉 谁敢说懂北京？\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/-FWhgPqjln9AKfIZvDsmQJl-UH4dmT7r.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1491275979,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560228\"},\"id\":\"15560228\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560373\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"北京有一条文青小胡同 几平米就价值千万！\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/bhfBA3HAIwTUaOZwVw7Y7x-7Z4n0tLSc.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1497755407,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560373\"},\"id\":\"15560373\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560240\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"全北京最嗨的摇滚乐酒吧 来了就不想走\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/g09mfEgEzhk3loIY3us31D5jneZubiAJ.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1491358265,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560240\"},\"id\":\"15560240\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560331\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"这个说这一口北京腔的男生究竟是什么样的人呢\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/vue03NCEPJsV39in3u3-LUQ7kC51hCn8.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1493452497,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560331\"},\"id\":\"15560331\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560264\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"失败再站起 北京无腿老人用生命完成登珠峰\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/M-7j9hwnuYzInU663q6kZweStShGlFVn.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1494929132,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560264\"},\"id\":\"15560264\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560252\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"他带你体验老北京生活里的精品咖啡\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/ZlbEQ54nnHFyN-FOZOKri-s3pnyR~Lzx.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1491794933,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560252\"},\"id\":\"15560252\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15560327\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"老北京影像 很多人无法回去的故乡\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/gslb.miaopai.com\\/stream\\/fkj0DIC4HTJMnezQl4efWxkkEqUKWg0G.mp4?source=xiaomi\"}},\"video_type\":0,\"create_time\":1489891789,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15560327\"},\"id\":\"15560327\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15554804\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"北京五大情侣好去处\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/kuai.xl.ptxl.gitv.tv\\/22\\/E1\\/22E1975730D06341D2FA79010686B8AA.mp4?timestamp=1552529149&sign=d2a6206fdc98decae000bd8afbdc5e30\"},\"signkey\":\"8FgcV3FQ1rQ1\",\"expire_time\":1552572349},\"video_type\":0,\"create_time\":1505298620,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15554804\"},\"id\":\"15554804\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15596861\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"点亮北京足迹，领略古都风采 ：这里的破房子值上亿\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/kuai.xl.ptxl.gitv.tv\\/73\\/92\\/7392B72209895DEA49451576DF3B14F9.mp4?timestamp=1552529398&sign=bf90b75a683c61ac2a2fa6cc8531cf27\"},\"signkey\":\"8FgcV3FQ1rQ1\",\"expire_time\":1552572598},\"video_type\":0,\"create_time\":1526462733,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15596861\"},\"id\":\"15596861\"},{\"category\":86,\"stat\":{\"trace\":\"\",\"id\":\"15618873\",\"tp\":\"headline\"},\"subtitle\":\"\",\"title\":\"民宿大评测之广州北京路三层独栋小洋房\",\"play_url\":{\"media\":{\"source\":20000,\"playurl\":\"http:\\/\\/kuai.xl.ptxl.gitv.tv\\/0E\\/8F\\/0E8F9155B61B2D7CA4C60374C72DC20F.mp4?timestamp=1552529536&sign=a4ae4827a00c1589ff78de6205e1f841\"},\"signkey\":\"8FgcV3FQ1rQ1\",\"expire_time\":1552572736},\"video_type\":0,\"create_time\":1539324702,\"rec\":{\"url\":\"\\/tv\\/headline\\/v\\/rec?id=15618873\"},\"id\":\"15618873\"}]}");
            ///storage/emulated/0/DCIM/Screenshots/Screenshot_2019-04-26-18-51-50-412_com.sdu.didi.psnger.png
            Glide.with(mActivity).load("/storage/emulated/0/DCIM/Screenshots/Screenshot_2019-04-26-18-51-50-412_com.sdu.didi.psnger.png").into(viewHolder.mItemVideoImg);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity,VideoPlayActivity.class);
                    intent.putExtra(VIDEO_PLAY_EXTRA,CommonUtils.mGson.toJson(mVideoFileInfoList.get(i)));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            int size = mVideoFileInfoList.size();
            DebugUtil.d(TAG,"getItemCount::size = " + size);
            return size;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mItemVideoNameTv;
        public TextView mItemVideoDescTv;
        public ImageView mItemVideoImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemVideoNameTv = itemView.findViewById(R.id.item_video_name_tv);
            mItemVideoDescTv = itemView.findViewById(R.id.item_video_desc_tv);
            mItemVideoImg = itemView.findViewById(R.id.item_video_img);
        }
    }
}
