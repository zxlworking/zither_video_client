package com.zxl.zither.video.model.response;

import com.google.gson.annotations.SerializedName;
import com.zxl.zither.video.model.data.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class FileInfoResponse extends ResponseBaseBean {
    @SerializedName("BaseBean")
    public ResponseBaseBean mResponseBaseBean;

    @SerializedName("FileInfoList")
    public List<FileInfo> mFileInfoList = new ArrayList<>();

    @Override
    public String toString() {
        return "FileInfoResponse{" +
                "mResponseBaseBean=" + mResponseBaseBean +
                ", mFileInfoList=" + mFileInfoList +
                '}';
    }
}
