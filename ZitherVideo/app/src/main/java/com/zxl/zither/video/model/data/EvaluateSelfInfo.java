package com.zxl.zither.video.model.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangxiaolong on 19-7-5.
 */
public class EvaluateSelfInfo {
    @SerializedName("Similarity")
    public double mSimilarity = 0;
    @SerializedName("StarInfo")
    public StarInfo mStarInfo;

    @Override
    public String toString() {
        return "EvaluateSelfInfo{" +
                "mSimilarity=" + mSimilarity +
                ", mStarInfo=" + mStarInfo +
                '}';
    }
}
