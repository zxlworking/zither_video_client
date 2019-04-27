package com.zxl.zither.video.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.model.data.UserInfo;

/**
 * Created by zxl on 2018/9/14.
 */

public class SharePreUtils {

    private static final String S_F_NAME = "zither_video";

    private static SharePreUtils mSharePreUtils;

    private static SharedPreferences mSharedPreferences;

    private static Object mLock = new Object();

    private SharePreUtils(Context context){
        mSharedPreferences = context.getSharedPreferences(S_F_NAME,Context.MODE_PRIVATE);
    }

    public static SharePreUtils getInstance(Context context){
        if(null == mSharePreUtils){
            synchronized (mLock){
                if(null == mSharePreUtils){
                    mSharePreUtils = new SharePreUtils(context);
                }
            }
        }
        return mSharePreUtils;
    }

    public void saveUserInfo(UserInfo userInfo){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if(userInfo != null){
            editor.putString("UserInfo",CommonUtils.mGson.toJson(userInfo));
        }else{
            editor.putString("UserInfo","");
        }
        editor.commit();
    }

    public UserInfo getUserInfo(){
        String str = mSharedPreferences.getString("UserInfo","");
        if(TextUtils.isEmpty(str)){
            return null;
        }else{
            return CommonUtils.mGson.fromJson(str,UserInfo.class);
        }
    }
}
