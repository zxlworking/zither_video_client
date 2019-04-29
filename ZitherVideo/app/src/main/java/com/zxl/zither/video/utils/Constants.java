package com.zxl.zither.video.utils;

import android.os.Environment;

public class Constants {
//    public static final String BASE_URL = "http://172.16.24.129:8080/";
//    public static final String BASE_PLAY_VIDEO_URL = "http://172.16.24.129:8081/video_file/";
//    public static final String BASE_IMG_URL = "http://172.16.24.129:8081/img_file/";
    public static final String BASE_URL = "http://129.211.4.46:8080/";
    public static final String BASE_PLAY_VIDEO_URL = "http://129.211.4.46:8081/video_file/";
    public static final String BASE_IMG_URL = "http://129.211.4.46:8081/img_file/";

    public static final String ROOT_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String APP_DIR_PATH = ROOT_DIR_PATH + "/" + "com.zxl.zither.video";
    public static final String APP_PICTURE_PATH = APP_DIR_PATH + "/" + "picture";
    public static final String APP_CRASH_PATH = APP_DIR_PATH + "/" + "crash";

    public static final int USER_TYPE_ADMIN = 0;
    public static final int USER_TYPE_NORMAL = 1;
}
