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
import android.widget.TextView;
import android.widget.Toast;

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
            viewHolder.mItemVideoListFileNameTv.setText(mVideoFileInfoList.get(i).mVideoName);

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
        public TextView mItemVideoListFileNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemVideoListFileNameTv = itemView.findViewById(R.id.item_video_list_file_name_tv);
        }
    }
}
