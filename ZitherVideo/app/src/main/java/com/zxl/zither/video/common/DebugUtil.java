package com.zxl.zither.video.common;

import android.util.Log;

/**
 * Created by zhangxiaolong on 19-7-9.
 */
public class DebugUtil {
    public static boolean STATE_OPEN = true;
    public static boolean STATE_CLOSE = false;
    public static boolean IS_DEBUG = false;

    public static void d(String tag,String message){
        if(IS_DEBUG){
            Log.d(tag,message);
        }
    }

    public static void d(Object object,String message){
        if(IS_DEBUG){
            Log.d(object.getClass().getSimpleName(),message);
        }
    }

    public static void e(String tag,String message){
        if(IS_DEBUG){
            Log.e(tag,message);
        }
    }

    public static void e(Object object,String message){
        if(IS_DEBUG){
            Log.e(object.getClass().getSimpleName(),message);
        }
    }
}
