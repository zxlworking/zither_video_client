package com.zxl.zither.video;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.zxl.common.DebugUtil;
import com.zxl.zither.video.common.GlobalCrashHandler;
import com.zxl.zither.video.utils.EventBusUtils;

public class ZitherVideoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugUtil.IS_DEBUG = DebugUtil.STATE_OPEN;
        EventBusUtils.init();

        GlobalCrashHandler.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);//低版本支持全屏的矢量图标
    }
}
