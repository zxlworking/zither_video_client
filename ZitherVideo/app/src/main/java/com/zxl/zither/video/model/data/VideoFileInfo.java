package com.zxl.zither.video.model.data;

import com.google.gson.annotations.SerializedName;

public class VideoFileInfo {
    @SerializedName("VideoId")
    public String mVideoId = "";
    @SerializedName("VideoName")
    public String mVideoName = "";
    @SerializedName("VideoPath")
    public String mVideoPath = "";
    @SerializedName("StudentVideoName")
    public String mStudentVideoName = "";
    @SerializedName("StudentVideoPath")
    public String mStudentVideoPath = "";
    @SerializedName("VideoDesc")
    public String mVideoDesc = "";
    @SerializedName("ImgName")
    public String mImgName = "";
    @SerializedName("UserId")
    public String mUserId = "";
    @SerializedName("ConvertVideo")
    public String mConvertVideo = "";

    @Override
    public String toString() {
        return "VideoFileInfo{" +
                "mVideoId='" + mVideoId + '\'' +
                ", mVideoName='" + mVideoName + '\'' +
                ", mVideoPath='" + mVideoPath + '\'' +
                ", mStudentVideoName='" + mStudentVideoName + '\'' +
                ", mStudentVideoPath='" + mStudentVideoPath + '\'' +
                ", mVideoDesc='" + mVideoDesc + '\'' +
                ", mImgName='" + mImgName + '\'' +
                ", mUserId='" + mUserId + '\'' +
                ", mConvertVideo='" + mConvertVideo + '\'' +
                '}';
    }
}
