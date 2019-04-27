package com.zxl.zither.video.common;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxl.common.DebugUtil;

import java.io.File;


public class CommonUtils {
    public static final String TAG = "CommonUtils";

    /** 判断是否是快速点击 */
    private static long lastClickTime;

    public static Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        return TelephonyMgr.getDeviceId();
    }

    public static int px2dip(int pxValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static float dip2px(float dipValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }

    public static int screenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        DebugUtil.d(TAG,"isFastDoubleClick::timeD = " + timeD);
        if (0 < timeD && timeD < 500) {

            return true;
        }
        lastClickTime = time;
        return false;

    }

    public static final void hideIputKeyboard(Activity activity) {
        InputMethodManager mInputKeyBoard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    public static String parseGalleryPath(Context context,Uri uri){
        String pathHead = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context,uri)){

            String authority = uri.getAuthority();
            DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::authority = " + authority);

            String id = DocumentsContract.getDocumentId(uri);
            DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::id = " + id);

            String[] idArray = id.split(":");
            String type = idArray.length > 0 ? idArray[0] : "";

            Uri contentUri = null;


            if(isExternalStorageDocument(uri)){
                DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isExternalStorageDocument");
            }else if(isDownloadsDocument(uri)){
                DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isDownloadsDocument");

                contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(id));
                return pathHead + getDataColumn(context,contentUri,null,null);

            }else if(isMediaDocument(uri)){
                DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument");

                if("image".equals(type)){
                    DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::image");

                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if("video".equals(type)){
                    DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::video");

                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }else if("audio".equals(type)){
                    DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::audio");

                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                DebugUtil.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::idArray.length = " + idArray.length);

                if(idArray.length >= 2){
                    String selection = "_id = ? ";
                    String[] selectionArgs = new String[]{idArray[1]};
                    return pathHead + getDataColumn(context,contentUri,selection,selectionArgs);
                }
            }

        }else if("content".equalsIgnoreCase(uri.getScheme())){
            String data = getDataColumn(context,uri,null,null);
            DebugUtil.d(TAG,"parseGalleryPath::content::data = " + data);
            return pathHead + data;
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            String filePath = uri.getPath();
            DebugUtil.d(TAG,"parseGalleryPath::file::filePath = " + filePath);
            return pathHead + filePath;
        }
        return "";
    }

    private static String getDataColumn(Context context,Uri uri,String selection,String[] selectionArgs){
        DebugUtil.d(TAG,"getDataColumn::uri = " + uri);
        DebugUtil.d(TAG,"getDataColumn::selection = " + selection);
        DebugUtil.d(TAG,"getDataColumn::selectionArgs = " + selectionArgs);
        String column = "_data";
        String[] projections = new String[]{column};

        ContentResolver cr = context.getContentResolver();
        Cursor mCursor = cr.query(uri,projections,selection,selectionArgs,null);
        if(mCursor != null){
            if(mCursor.moveToFirst()){
                return mCursor.getString(mCursor.getColumnIndex(column));
            }
            mCursor.close();
        }
        return "";
    }

    private static boolean isExternalStorageDocument(Uri uri){
        String authority = uri.getAuthority();
        return "com.android.externalstorage.documents".equals(authority);
    }

    private static boolean isDownloadsDocument(Uri uri){
        String authority = uri.getAuthority();
        return "com.android.providers.downloads.documents".equals(authority);
    }

    private static boolean isMediaDocument(Uri uri){
        String authority = uri.getAuthority();
        return "com.android.providers.media.documents".equals(authority);
    }

    private static Uri getFileUri(Context context,String filePath){
        Uri mUri = null;
        File mFile = new File(filePath);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            mUri = FileProvider.getUriForFile(context,"com.zxl.test_picture_camera",mFile);
        }else{
            mUri = Uri.fromFile(mFile);
        }
        return mUri;
    }

    public static long getInternetSpeed(Context context) {
        long speed = 0;
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            //转为KB
            speed = TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return speed;
    }
}
