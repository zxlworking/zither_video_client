package com.zxl.zither.video.model.data;

import com.google.gson.annotations.SerializedName;

public class VideoFileInfo {
    @SerializedName("VideoId")
    public String mVideoId = "";
    @SerializedName("VideoName")
    public String mVideoName = "";
    @SerializedName("VideoDesc")
    public String mVideoDesc = "";
    @SerializedName("UserId")
    public String mUserId = "";
    @SerializedName("ConvertVideo")
    public String mConvertVideo = "";

    @Override
    public String toString() {
        return "VideoFileInfo{" +
                "mVideoId='" + mVideoId + '\'' +
                ", mVideoName='" + mVideoName + '\'' +
                ", mVideoDesc='" + mVideoDesc + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", ConvertVideo='" + mConvertVideo + '\'' +
                '}';
    }
}
