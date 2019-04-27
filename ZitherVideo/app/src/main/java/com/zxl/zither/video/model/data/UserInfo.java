package com.zxl.zither.video.model.data;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("UserId")
    public String mUserId = "";
    @SerializedName("UserName")
    public String mUserName = "";
    @SerializedName("PassWord")
    public String mPassWord = "";
    @SerializedName("UserType")
    public int mUserType = 0;

    @Override
    public String toString() {
        return "UserInfo{" +
                "mUserId='" + mUserId + '\'' +
                ", mUserName='" + mUserName + '\'' +
                ", mPassWord='" + mPassWord + '\'' +
                ", mUserType=" + mUserType +
                '}';
    }
}
