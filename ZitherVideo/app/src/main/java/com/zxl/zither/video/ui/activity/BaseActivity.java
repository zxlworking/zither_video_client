package com.zxl.zither.video.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zxl.zither.video.common.DebugUtil;
import com.zxl.zither.video.R;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    private View mLoadingView;
    private TextView mLoadingTv;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    protected Activity mActivity;
    protected View.OnClickListener mErrorRefreshClickListener;

    protected Handler mUIHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());

        mActivity = this;

        mLoadingView = findViewById(R.id.loading_view);
        mLoadingTv = findViewById(R.id.loading_tv);
        mLoadErrorView = findViewById(R.id.load_error_view);
        mBtnErrorRefresh = findViewById(R.id.load_error_btn);


        requestPermission();

        initView();

        if(mBtnErrorRefresh != null && mErrorRefreshClickListener != null){
            mBtnErrorRefresh.setOnClickListener(mErrorRefreshClickListener);
        }
    }

    public abstract int getResLayout();
    public abstract void initView();

    protected void showLoading(){
        if(mLoadingView != null){
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadErrorView.setVisibility(View.GONE);
        }
    }

    protected void hideLoading(boolean isShowError){
        if(mLoadingView != null){
            mLoadingView.setVisibility(View.GONE);
            if(isShowError){
                mLoadErrorView.setVisibility(View.VISIBLE);
            }else{
                mLoadErrorView.setVisibility(View.GONE);
            }
        }
    }

    protected void setLoadingMessage(String msg){
        if(mLoadingTv != null){
            mLoadingTv.setText(msg);
        }
    }

    private void requestPermission() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionOk = true;
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermissionOk = false;
                    break;
                }
            }
        }
        DebugUtil.d(TAG,"onRequestPermissionsResult::isPermissionOk = " + isPermissionOk);
    }
}
