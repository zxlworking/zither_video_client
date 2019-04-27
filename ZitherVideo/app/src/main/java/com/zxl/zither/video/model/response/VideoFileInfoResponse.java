package com.zxl.zither.video.model.response;

import com.google.gson.annotations.SerializedName;
import com.zxl.zither.video.model.data.VideoFileInfo;

import java.util.ArrayList;
import java.util.List;

public class VideoFileInfoResponse extends ResponseBaseBean {
    @SerializedName("BaseBean")
    public ResponseBaseBean mResponseBaseBean;

    @SerializedName("VideoFileInfoList")
    public List<VideoFileInfo> mVideoFileInfoList = new ArrayList<>();

    @Override
    public String toString() {
        return "FileInfoResponse{" +
                "mResponseBaseBean=" + mResponseBaseBean +
                ", mVideoFileInfoList=" + mVideoFileInfoList +
                '}';
    }
}
