package com.zxl.zither.video.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zxl.common.DebugUtil;
import com.zxl.zither.video.R;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.http.FileRequestBody;
import com.zxl.zither.video.http.HttpUtils;
import com.zxl.zither.video.http.RetrofitCallback;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.data.FileInfo;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.utils.Constants;
import com.zxl.zither.video.utils.SharePreUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class UpLoadVideoFileActivity extends BaseActivity {
    private static final String TAG = "UpLoadVideoFileActivity";

    public static final String UPLOAD_VIDEO_FILE_PATH_EXTRA = "VIDEO_PLAY_EXTRA";

    private TextInputEditText mVideoNameInputEt;
    private TextInputEditText mVideoDescInputEt;
    private View mUploadVideoFileView;
    private TextView mUploadProgressTv;

    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    private String mUploadVideoFilePath;

    private RetrofitCallback mRetrofitCallback = new RetrofitCallback() {
        @Override
        public void onSuccess(Call call, Response response) {
            DebugUtil.d(TAG,"RetrofitCallback::onSuccess");
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    mUploadProgressTv.setVisibility(View.GONE);
                    Toast.makeText(mActivity,"上传完成",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onLoading(final long total, final long progress) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    float p = (float) (100 * progress*1.0/total);

                    mUploadProgressTv.setVisibility(View.VISIBLE);
                    mUploadProgressTv.setText("已上传"+mDecimalFormat.format(p)+"%");
                    if(p >= 100){
                        mUploadProgressTv.setVisibility(View.GONE);
                    }
                }
            });
            DebugUtil.d(TAG,"RetrofitCallback::onLoading = " + (progress * 1.0 / total));
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            DebugUtil.d(TAG,"RetrofitCallback::onLoading");
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    mUploadProgressTv.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    public int getResLayout() {
        return R.layout.activity_upload_video_file;
    }

    @Override
    public void initView() {
        mVideoNameInputEt = findViewById(R.id.video_name_input_et);
        mVideoDescInputEt = findViewById(R.id.video_desc_input_et);
        mUploadVideoFileView = findViewById(R.id.upload_video_file_view);
        mUploadProgressTv = findViewById(R.id.upload_progress_tv);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mUploadVideoFilePath = bundle.getString(UPLOAD_VIDEO_FILE_PATH_EXTRA);
            DebugUtil.d(TAG,"initView::mUploadVideoFilePath = " + mUploadVideoFilePath);

            File uploadVideoFile = new File(mUploadVideoFilePath);
            mVideoNameInputEt.setText(uploadVideoFile.getName());

        }

        mUploadVideoFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mVideoNameInputEt.getText().toString())){
                    Toast.makeText(mActivity,"文件名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(mVideoDescInputEt.getText().toString())){
                    Toast.makeText(mActivity,"描述不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(SharePreUtils.getInstance(mActivity).getUserInfo() == null){
                    Toast.makeText(mActivity,"无效用户",Toast.LENGTH_SHORT).show();
                    return;
                }

                File uploadVideoFile = new File(mUploadVideoFilePath);

                MultipartBody.Builder builder = new MultipartBody.Builder();

                RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), uploadVideoFile);
                builder.addFormDataPart("file", uploadVideoFile.getName(), requestBody);
                builder.setType(MultipartBody.FORM);


                MultipartBody multipartBody = builder.build();
                FileRequestBody fileRequestBody = new FileRequestBody(multipartBody,mRetrofitCallback);

                Map<String,String> params = new HashMap<>();
                params.put("video_name", mVideoNameInputEt.getText().toString());
                params.put("video_desc", mVideoDescInputEt.getText().toString());
                params.put("user_id", SharePreUtils.getInstance(mActivity).getUserInfo().mUserId);

                HttpUtils.getInstance().uploadVideoFile(params, fileRequestBody, new NetRequestListener() {
                    @Override
                    public void onSuccess(ResponseBaseBean responseBaseBean) {
                        Toast.makeText(mActivity,"上传完成",Toast.LENGTH_SHORT).show();
                        finish();
                        setResult(Activity.RESULT_OK);
                    }

                    @Override
                    public void onNetError() {

                    }

                    @Override
                    public void onNetError(Throwable e) {

                    }

                    @Override
                    public void onServerError(ResponseBaseBean responseBaseBean) {

                    }
                });
            }
        });
    }
}
