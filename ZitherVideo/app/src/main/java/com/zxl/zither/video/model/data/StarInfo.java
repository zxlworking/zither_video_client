package com.zxl.zither.video.model.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangxiaolong on 19-7-5.
 */
public class StarInfo {
    @SerializedName("StarId")
    public String mStarId = "";
    @SerializedName("StarName")
    public String mStarName = "";
    @SerializedName("StarImgUrl")
    public String mStarImgUrl = "";
    @SerializedName("StarDetailUrl")
    public String mStarDetailUrl = "";
    @SerializedName("FaceId")
    public String mFaceId = "";

    @Override
    public String toString() {
        return "StarInfo{" +
                "mStarId='" + mStarId + '\'' +
                ", mStarName='" + mStarName + '\'' +
                ", mStarImgUrl='" + mStarImgUrl + '\'' +
                ", mStarDetailUrl='" + mStarDetailUrl + '\'' +
                ", mFaceId='" + mFaceId + '\'' +
                '}';
    }
}