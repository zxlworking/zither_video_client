package com.zxl.zither.video.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zxl.common.DebugUtil;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.data.UserInfo;
import com.zxl.zither.video.model.response.FileInfoResponse;
import com.zxl.zither.video.model.response.LoginResponseBean;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.model.response.VideoFileInfoResponse;
import com.zxl.zither.video.utils.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zxl on 2018/9/5.
 */

public class HttpUtils {
    private static final String TAG = "HttpUtils";

    private static HttpUtils mHttpUtils;

    private static Object mLock = new Object();

    private static HttpAPI mHttpAPI;

    private HttpUtils(){
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(30, TimeUnit.SECONDS);
        okBuilder.readTimeout(30,TimeUnit.SECONDS);
        OkHttpClient okHttpClient = okBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        Retrofit retrofit = retrofitBuilder
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        mHttpAPI = retrofit.create(HttpAPI.class);
    }

    public static HttpUtils getInstance(){
        DebugUtil.d(TAG,"getInstance");

        if(null == mHttpUtils){
            synchronized (mLock){
                if(null == mHttpUtils){
                    mHttpUtils = new HttpUtils();
                }
            }
        }
        return mHttpUtils;
    }


    public void register(Context context, UserInfo userInfo, final NetRequestListener listener){
        DebugUtil.d(TAG,"register::userName = " + userInfo.mUserName + "::passWord = " + userInfo.mPassWord);

//        Call<ResponseBody> call = mHttpAPI.register(userName, passWord);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    String s = new String(response.body().bytes());
//                    DebugUtil.d(TAG, "register::s = " + s);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

        if(isNetworkAvailable(context)){
            Observable<ResponseBaseBean> observable = mHttpAPI.register(userInfo.mUserName, userInfo.mPassWord);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBaseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"register::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"register::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(ResponseBaseBean responseBaseBean) {
                            DebugUtil.d(TAG,"register::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"register::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void login(Context context, UserInfo userInfo, final NetRequestListener listener){
        DebugUtil.d(TAG,"login::userName = " + userInfo.mUserName + "::passWord = " + userInfo.mPassWord);

        if(isNetworkAvailable(context)){
            Observable<LoginResponseBean> observable = mHttpAPI.login(userInfo.mUserName, userInfo.mPassWord);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<LoginResponseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"login::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"login::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(LoginResponseBean responseBaseBean) {
                            DebugUtil.d(TAG,"login::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.mResponseBaseBean.code == 0){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"login::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getVideoFileList(Context context, final NetRequestListener listener){
        DebugUtil.d(TAG,"getVideoFileList");

        if(isNetworkAvailable(context)){
            Observable<VideoFileInfoResponse> observable = mHttpAPI.getVideoFileList();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<VideoFileInfoResponse>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getVideoFileList::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getVideoFileList::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(VideoFileInfoResponse responseBaseBean) {
                            DebugUtil.d(TAG,"getVideoFileList::onNext::responseBaseBean = " + responseBaseBean);
                            if(responseBaseBean.mResponseBaseBean.code == 0 || responseBaseBean.mResponseBaseBean.code == -1){
                                if(listener != null){
                                    listener.onSuccess(responseBaseBean);
                                }
                            }else{
                                if(listener != null){
                                    listener.onServerError(responseBaseBean);
                                }
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getVideoFileList::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void uploadVideoFile(Map<String,String> params, FileRequestBody fileRequestBody, final NetRequestListener listener){
        DebugUtil.d(TAG,"uploadVideoFile::params = " + params);

//        Call<ResponseBody> call = mHttpAPI.uploadVideoFile(params, fileRequestBody);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    String s = new String(response.body().bytes());
//                    DebugUtil.d(TAG, "uploadVideoFile::s = " + s);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

        Observable<ResponseBaseBean> observable = mHttpAPI.uploadVideoFile(params, fileRequestBody);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBaseBean>() {
                    @Override
                    public void onCompleted() {
                        DebugUtil.d(TAG,"uploadVideoFile::onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(TAG,"uploadVideoFile::onError::e = " + e);
                        if(listener != null){
                            listener.onNetError(e);
                        }
                    }

                    @Override
                    public void onNext(ResponseBaseBean responseBaseBean) {
                        DebugUtil.d(TAG,"uploadVideoFile::onNext::responseBaseBean = " + responseBaseBean);
                        if(responseBaseBean.code == 0){
                            if(listener != null){
                                listener.onSuccess(responseBaseBean);
                            }
                        }else{
                            if(listener != null){
                                listener.onServerError(responseBaseBean);
                            }
                        }
                    }
                });
    }

    public void uploadFile(FileRequestBody fileRequestBody, final NetRequestListener listener){
        Call<ResponseBody> call = mHttpAPI.uploadFile(fileRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response != null && response.body() != null){
                    try {
                        String s = new String(response.body().bytes());
                        DebugUtil.d(TAG, "uploadFile::s = " + s);

                        if(listener != null){
                            listener.onSuccess(null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        if(listener != null){
                            listener.onNetError();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(listener != null){
                    listener.onNetError();
                }
            }
        });
    }

    //==============NetworkAvailable===============
    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    public static boolean isNetworkAvailable(Context context) {
//        return isNetConnect(getNetWorkState(context));
        return true;
    }

    private static boolean isNetConnect(int state) {
        DebugUtil.d(TAG, "isNetConnect::state = " + state);
        if (state == NETWORK_WIFI) {
            return true;
        } else if (state == NETWORK_MOBILE) {
            return true;
        } else if (state == NETWORK_NONE) {
            return false;
        }
        return false;
    }

    private static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    public boolean isNetworkAvailable2(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        // 获取NetworkInfo对象
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        if (networkInfo != null && networkInfo.length > 0) {
            for (int i = 0; i < networkInfo.length; i++) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        return false;
    }
}
