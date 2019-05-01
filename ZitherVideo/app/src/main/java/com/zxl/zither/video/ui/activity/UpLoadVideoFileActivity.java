package com.zxl.zither.video.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.common.DebugUtil;
import com.zxl.zither.video.R;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.http.FileRequestBody;
import com.zxl.zither.video.http.HttpUtils;
import com.zxl.zither.video.http.RetrofitCallback;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.response.ResponseBaseBean;
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

    private static final int OPEN_VIDEO_REQUEST_CODE = 1;
    private static final int OPEN_STUDENT_VIDEO_REQUEST_CODE = 2;
    private static final int OPEN_GALLERY_REQUEST_CODE = 3;

    public static final String UPLOAD_VIDEO_FILE_PATH_EXTRA = "VIDEO_PLAY_EXTRA";

    private EditText mVideoNameEt;
    private EditText mStudentVideoNameEt;
    private EditText mVideoDescInputEt;
    private View mUploadVideoFileView;
    private TextView mUploadProgressTv;
    private ImageView mVideoImg;
    private TextView mChooseStudentVideoTv;
    private TextView mChooseVideoTv;
    private TextView mChooseVideoImgTv;

    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    private String mUploadVideoFilePath;
    private String mUploadStudentVideoFilePath;
    private String mUploadVideoImgFilePath;

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
        mVideoNameEt = findViewById(R.id.video_name_et);
        mStudentVideoNameEt = findViewById(R.id.student_video_name_et);
        mVideoDescInputEt = findViewById(R.id.video_desc_input_et);
        mUploadVideoFileView = findViewById(R.id.upload_video_file_view);
        mUploadProgressTv = findViewById(R.id.upload_progress_tv);
        mVideoImg = findViewById(R.id.video_img);
        mChooseVideoTv = findViewById(R.id.choose_video_tv);
        mChooseStudentVideoTv = findViewById(R.id.choose_student_video_tv);
        mChooseVideoImgTv = findViewById(R.id.choose_video_img_tv);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mUploadVideoFilePath = bundle.getString(UPLOAD_VIDEO_FILE_PATH_EXTRA);
            DebugUtil.d(TAG,"initView::mUploadVideoFilePath = " + mUploadVideoFilePath);

            File uploadVideoFile = new File(mUploadVideoFilePath);
            mVideoNameEt.setText(uploadVideoFile.getName());

        }

        mChooseVideoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mOpenGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mOpenGalleryIntent.setType("video/*");
                startActivityForResult(mOpenGalleryIntent,OPEN_VIDEO_REQUEST_CODE);
            }
        });
        mChooseStudentVideoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mOpenGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mOpenGalleryIntent.setType("video/*");
                startActivityForResult(mOpenGalleryIntent,OPEN_STUDENT_VIDEO_REQUEST_CODE);
            }
        });
        mVideoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mOpenGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mOpenGalleryIntent.setType("image/*");
                startActivityForResult(mOpenGalleryIntent,OPEN_GALLERY_REQUEST_CODE);
            }
        });

        mUploadVideoFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mVideoNameEt.getText()) || TextUtils.isEmpty(mUploadStudentVideoFilePath)){
                    Toast.makeText(mActivity,"视频文件不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(mStudentVideoNameEt.getText().toString())){
                    Toast.makeText(mActivity,"描述不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(SharePreUtils.getInstance(mActivity).getUserInfo() == null){
                    Toast.makeText(mActivity,"无效用户",Toast.LENGTH_SHORT).show();
                    return;
                }

                File uploadVideoFile = new File(mUploadVideoFilePath);
                File uploadStudentVideoFile = new File(mUploadStudentVideoFilePath);
                File uploadVideoImgFile = new File(mUploadVideoImgFilePath);

//                MultipartBody.Builder builder = new MultipartBody.Builder();
//
//                RequestBody requestBody = RequestBody.create(MediaType.parse("video/*"), uploadVideoFile);
//                builder.addFormDataPart("file", uploadVideoFile.getName(), requestBody);
//                builder.setType(MultipartBody.FORM);
//
//                MultipartBody multipartBody = builder.build();
//                FileRequestBody fileRequestBody = new FileRequestBody(multipartBody,mRetrofitCallback);


                RequestBody videoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadVideoFile);
                RequestBody studentVideoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadStudentVideoFile);
                RequestBody imgRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadVideoImgFile);
//                HashMap<String, RequestBody> map = new HashMap<>();
//                map.put(uploadVideoFile.getName(), requestBody);
//                map.put(uploadVideoImgFile.getName(), requestBody2);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.addFormDataPart("value", uploadVideoFile.getName(), videoRequestBody);
                builder.addFormDataPart("value", uploadStudentVideoFile.getName(), studentVideoRequestBody);
                builder.addFormDataPart("value", uploadVideoImgFile.getName(), imgRequestBody);
                builder.setType(MultipartBody.FORM);
                MultipartBody multipartBody = builder.build();
                FileRequestBody fileRequestBody = new FileRequestBody(multipartBody,mRetrofitCallback);


                Map<String,String> params = new HashMap<>();
                params.put("video_name", mVideoNameEt.getText().toString());
                params.put("video_real_name", uploadVideoFile.getName());
                params.put("student_video_name", mStudentVideoNameEt.getText().toString());
                params.put("student_video_real_name", uploadStudentVideoFile.getName());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugUtil.d(TAG,"onActivityResult::requestCode = " + requestCode);
        DebugUtil.d(TAG,"onActivityResult::resultCode = " + resultCode);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case OPEN_VIDEO_REQUEST_CODE:
                    if(data == null){
                        DebugUtil.d(TAG,"onActivityResult::OPEN_VIDEO_REQUEST_CODE::data null");
                    }else{
                        DebugUtil.d(TAG,"onActivityResult::OPEN_VIDEO_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mActivity,data.getData());
                        DebugUtil.d(TAG,"onActivityResult::OPEN_VIDEO_REQUEST_CODE::mGalleryPath = " + mGalleryPath);
                        mUploadVideoFilePath = mGalleryPath;
                        File file = new File(mUploadVideoFilePath);
                        mChooseVideoTv.setVisibility(View.GONE);
                        mVideoNameEt.setVisibility(View.VISIBLE);
                        mVideoNameEt.setText(file.getName());
                    }
                    break;
                case OPEN_STUDENT_VIDEO_REQUEST_CODE:
                    if(data == null){
                        DebugUtil.d(TAG,"onActivityResult::OPEN_STUDENT_VIDEO_REQUEST_CODE::data null");
                    }else{
                        DebugUtil.d(TAG,"onActivityResult::OPEN_STUDENT_VIDEO_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mActivity,data.getData());
                        DebugUtil.d(TAG,"onActivityResult::OPEN_STUDENT_VIDEO_REQUEST_CODE::mGalleryPath = " + mGalleryPath);
                        mUploadStudentVideoFilePath = mGalleryPath;
                        File file = new File(mUploadStudentVideoFilePath);
                        mChooseStudentVideoTv.setVisibility(View.GONE);
                        mStudentVideoNameEt.setVisibility(View.VISIBLE);
                        mStudentVideoNameEt.setText(file.getName());
                    }
                    break;
                case OPEN_GALLERY_REQUEST_CODE:
                    if(data == null){
                        DebugUtil.d(TAG,"onActivityResult::GALLERY_OPEN_REQUEST_CODE::data null");
                    }else{
                        DebugUtil.d(TAG,"onActivityResult::GALLERY_OPEN_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mActivity,data.getData());
                        DebugUtil.d(TAG,"onActivityResult::GALLERY_OPEN_REQUEST_CODE::mGalleryPath = " + mGalleryPath);
                        mUploadVideoImgFilePath = mGalleryPath;

                        mChooseVideoImgTv.setVisibility(View.GONE);

                        Glide.with(mActivity).load(mUploadVideoImgFilePath).into(mVideoImg);
                    }
                    break;
            }
        }
    }
}
