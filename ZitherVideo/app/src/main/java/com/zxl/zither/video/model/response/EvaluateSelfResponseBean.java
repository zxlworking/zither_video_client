package com.zxl.zither.video.model.response;

import com.google.gson.annotations.SerializedName;
import com.zxl.zither.video.model.data.EvaluateSelfInfo;

import java.util.List;

/**
 * Created by zhangxiaolong on 19-7-5.
 */
public class EvaluateSelfResponseBean extends ResponseBaseBean {
    @SerializedName("BaseBean")
    public ResponseBaseBean mResponseBaseBean;
    @SerializedName("EvaluateSelfInfoList")
    public List<EvaluateSelfInfo> mEvaluateSelfInfoList;

    @Override
    public String toString() {
        return "EvaluateSelfResponseBean{" +
                "mResponseBaseBean=" + mResponseBaseBean +
                ", mEvaluateSelfInfoList=" + mEvaluateSelfInfoList +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
