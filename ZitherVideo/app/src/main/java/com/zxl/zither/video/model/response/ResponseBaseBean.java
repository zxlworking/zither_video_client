package com.zxl.zither.video.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zxl on 2018/9/5.
 */

public class ResponseBaseBean {
    @SerializedName("Code")
    public int code;
    @SerializedName("Desc")
    public String desc = "";

    @Override
    public String toString() {
        return "ResponseBaseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
