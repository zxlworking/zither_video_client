package com.zxl.zither.video.ui.activity;

import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zxl.zither.video.R;
import com.zxl.zither.video.common.DebugUtil;

/**
 * Created by zhangxiaolong on 19-7-9.
 */
public class WebViewActivity extends BaseActivity {

    private static final String TAG = "WebViewActivity";

    public static final String EXTRA_URL = "EXTRA_URL";

    private WebView mWebView;

    private ProgressBar mProgressBar;

    @Override
    public int getResLayout() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView() {
        mWebView = findViewById(R.id.web_view);
        mProgressBar = findViewById(R.id.progress_bar);

        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);

        String url = getIntent().getStringExtra(EXTRA_URL);
        DebugUtil.d(TAG,"initView::url = " + url);

        mWebView.loadUrl(url);
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            DebugUtil.d(TAG,"onProgressChanged::newProgress = " + newProgress);
            if(newProgress >= 100){
                mProgressBar.setVisibility(View.GONE);
            }else{
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mProgressBar.setProgress(newProgress);
        }
    };

    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings()
                        .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            handler.proceed();
        }
    };
}
