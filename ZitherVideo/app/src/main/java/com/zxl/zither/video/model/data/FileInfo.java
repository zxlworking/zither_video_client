package com.zxl.zither.video.model.data;

import com.google.gson.annotations.SerializedName;

public class FileInfo {
    @SerializedName("FileName")
    public String mFileName = "";
    @SerializedName("FileDesc")
    public String mFileDesc = "";
    @SerializedName("FileUrl")
    public String mFileUrl = "";

    @Override
    public String toString() {
        return "FileInfo{" +
                "mFileName='" + mFileName + '\'' +
                ", mFileDesc='" + mFileDesc + '\'' +
                ", mFilePath='" + mFileUrl + '\'' +
                '}';
    }
}
