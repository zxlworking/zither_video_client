package com.zxl.zither.video.model.response;

import com.google.gson.annotations.SerializedName;
import com.zxl.zither.video.model.data.UserInfo;

public class LoginResponseBean extends ResponseBaseBean {
    @SerializedName("BaseBean")
    public ResponseBaseBean mResponseBaseBean;

    @SerializedName("UserInfo")
    public UserInfo mUserInfo;

    @Override
    public String toString() {
        return "LoginResponseBean{" +
                "mResponseBaseBean=" + mResponseBaseBean +
                ", mUserInfo=" + mUserInfo +
                '}';
    }
}
