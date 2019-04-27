package com.zxl.zither.video.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zxl.zither.video.model.data.UserInfo;

public class LoginActivity extends AccountActivity {

    @Override
    public void onLoginSuccess(UserInfo userInfo) {
        super.onLoginSuccess(userInfo);
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
