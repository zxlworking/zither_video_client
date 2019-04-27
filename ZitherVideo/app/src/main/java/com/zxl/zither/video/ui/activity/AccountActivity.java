package com.zxl.zither.video.ui.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.zxl.common.DebugUtil;
import com.zxl.zither.video.R;
import com.zxl.zither.video.event.LoginSuccessEvent;
import com.zxl.zither.video.event.LogoutSuccessEvent;
import com.zxl.zither.video.http.HttpUtils;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.data.UserInfo;
import com.zxl.zither.video.model.response.LoginResponseBean;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.utils.EventBusUtils;
import com.zxl.zither.video.utils.SharePreUtils;

public class AccountActivity extends BaseActivity {
    private static final String TAG = "AccountActivity";

    private static final int CLICK_UNKNOWN_STATE = 0;
    private static final int CLICK_REGISTER_STATE = 1;
    private static final int CLICK_LOGIN_STATE = 2;
    private static final int LOGIN_SUCCESS_STATE = 3;

    private TextInputLayout mUserNameTextInputLayout;
    private TextInputEditText mUserNameTextInputEditText;
    private TextInputLayout mPassWordTextInputLayout;
    private TextInputEditText mPassWordTextInputEditText;

    private CardView mRegisterCardView;
    private CardView mLoginCardView;
    private CardView mLogoutCardView;
    private CardView mCancelCardView;

    private int mClickState = CLICK_UNKNOWN_STATE;
    private boolean isRegistering = false;
    private boolean isLogining = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DebugUtil.d(TAG,"mOnClickListener::mClickState = " + mClickState);
            switch (v.getId()){
                case R.id.register_card_view:
                    if(mClickState == CLICK_REGISTER_STATE){
                        register();
                    }else{
                        mClickState = CLICK_REGISTER_STATE;
                    }
                    break;
                case R.id.login_card_view:
                    if(mClickState == CLICK_LOGIN_STATE){
                        login();
                    }else{
                        mClickState = CLICK_LOGIN_STATE;
                    }
                    break;
                case R.id.logout_card_view:
                    mClickState = CLICK_UNKNOWN_STATE;
                    SharePreUtils.getInstance(mActivity).saveUserInfo(null);
                    EventBusUtils.post(new LogoutSuccessEvent());
                    break;
                case R.id.cancel_card_view:
                    mClickState = CLICK_UNKNOWN_STATE;
                    break;
            }
            doForkState(mClickState);
        }
    };

    @Override
    public int getResLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mUserNameTextInputLayout = findViewById(R.id.user_name_input_l);
        mUserNameTextInputEditText = findViewById(R.id.user_name_input_et);

        mPassWordTextInputLayout = findViewById(R.id.pass_word_input_l);
        mPassWordTextInputEditText = findViewById(R.id.pass_word_input_et);
        mPassWordTextInputLayout.setPasswordVisibilityToggleEnabled(true);

        mRegisterCardView = findViewById(R.id.register_card_view);
        mLoginCardView = findViewById(R.id.login_card_view);
        mLogoutCardView = findViewById(R.id.logout_card_view);
        mCancelCardView = findViewById(R.id.cancel_card_view);

        mRegisterCardView.setOnClickListener(mOnClickListener);
        mLoginCardView.setOnClickListener(mOnClickListener);
        mLogoutCardView.setOnClickListener(mOnClickListener);
        mCancelCardView.setOnClickListener(mOnClickListener);

        UserInfo userInfo = SharePreUtils.getInstance(mActivity).getUserInfo();
        if(userInfo != null){
            doForkState(LOGIN_SUCCESS_STATE);
            onLoginSuccess(userInfo);
        }else{
            mRegisterCardView.setVisibility(View.VISIBLE);
            mLoginCardView.setVisibility(View.VISIBLE);
            mLogoutCardView.setVisibility(View.GONE);
        }
    }

    public void doForkState(int state){
        DebugUtil.d(TAG,"doForkState::state = " + state);
        switch (state){
            case CLICK_UNKNOWN_STATE:
                mUserNameTextInputLayout.setVisibility(View.GONE);
                mPassWordTextInputLayout.setVisibility(View.GONE);

                initInputContent(state);

                mRegisterCardView.setVisibility(View.VISIBLE);
                mLoginCardView.setVisibility(View.VISIBLE);
                mLogoutCardView.setVisibility(View.GONE);
                mCancelCardView.setVisibility(View.GONE);
                break;
            case CLICK_REGISTER_STATE:
                mUserNameTextInputLayout.setVisibility(View.VISIBLE);
                mPassWordTextInputLayout.setVisibility(View.VISIBLE);

                initInputContent(state);

                mRegisterCardView.setVisibility(View.VISIBLE);
                mLoginCardView.setVisibility(View.GONE);
                mLogoutCardView.setVisibility(View.GONE);
                mCancelCardView.setVisibility(View.VISIBLE);
                break;
            case CLICK_LOGIN_STATE:
                mUserNameTextInputLayout.setVisibility(View.VISIBLE);
                mPassWordTextInputLayout.setVisibility(View.VISIBLE);

                mRegisterCardView.setVisibility(View.GONE);
                mLoginCardView.setVisibility(View.VISIBLE);
                mLogoutCardView.setVisibility(View.GONE);
                mCancelCardView.setVisibility(View.VISIBLE);
                break;
            case LOGIN_SUCCESS_STATE:
                mUserNameTextInputLayout.setVisibility(View.VISIBLE);
                mPassWordTextInputLayout.setVisibility(View.GONE);

                initInputContent(state);

                mRegisterCardView.setVisibility(View.GONE);
                mLoginCardView.setVisibility(View.GONE);
                mLogoutCardView.setVisibility(View.VISIBLE);
                mCancelCardView.setVisibility(View.GONE);
                break;
        }
    }

    private void initInputContent(int state) {
        switch (state){
            case CLICK_UNKNOWN_STATE:
            case CLICK_REGISTER_STATE:
            case CLICK_LOGIN_STATE:
                mUserNameTextInputEditText.setEnabled(true);
                mPassWordTextInputEditText.setEnabled(true);

                mUserNameTextInputEditText.setText("");
                mPassWordTextInputEditText.setText("");
                mUserNameTextInputEditText.requestFocus();
                break;
            case LOGIN_SUCCESS_STATE:

                mUserNameTextInputEditText.setEnabled(false);
                mPassWordTextInputEditText.setEnabled(false);

                UserInfo userInfo = SharePreUtils.getInstance(mActivity).getUserInfo();

                mUserNameTextInputEditText.setText(userInfo.mUserName);
                mPassWordTextInputEditText.setText("");
                break;
        }
    }

    public UserInfo createUserInfo(){
        String userName = mUserNameTextInputEditText.getText().toString();
        String passWord = mPassWordTextInputEditText.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(mActivity,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }
        if(TextUtils.isEmpty(passWord)){
            Toast.makeText(mActivity,"密码不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.mUserName = userName;
        userInfo.mPassWord = passWord;
        return userInfo;
    }

    public void register(){
        UserInfo userInfo = createUserInfo();
        if(null == userInfo){
            return;
        }

        if(isRegistering){
            return;
        }
        isRegistering = true;

        showLoading();

        HttpUtils.getInstance().register(mActivity, userInfo, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {

                hideLoading(false);

                mClickState = CLICK_UNKNOWN_STATE;
                doForkState(mClickState);

                isRegistering = false;
                Toast.makeText(mActivity,"注册成功!",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.no_network_tip)),Toast.LENGTH_SHORT).show();
                hideLoading(false);
                isRegistering = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.network_error_tip)," 请检查网络"),Toast.LENGTH_SHORT).show();

                hideLoading(false);
                isRegistering = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                if(responseBaseBean.code == -1){
                    Toast.makeText(mActivity,"该用户已存在",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.server_error_tip),responseBaseBean.desc),Toast.LENGTH_SHORT).show();
                }

                hideLoading(false);
                isRegistering = false;
            }
        });
    }

    public void login(){
        DebugUtil.d(TAG,"login::isLogining = " + isLogining);
        final UserInfo userInfo = createUserInfo();
        if(null == userInfo){
            return;
        }

        if(isLogining){
            return;
        }
        isLogining = true;

        showLoading();

        HttpUtils.getInstance().login(mActivity, userInfo, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                LoginResponseBean loginResponseBean = (LoginResponseBean) responseBaseBean;

                SharePreUtils.getInstance(mActivity).saveUserInfo(loginResponseBean.mUserInfo);

                hideLoading(false);

                mClickState = LOGIN_SUCCESS_STATE;
                doForkState(mClickState);

                EventBusUtils.post(new LoginSuccessEvent());

                isLogining = false;
                Toast.makeText(mActivity,"登录成功!",Toast.LENGTH_LONG).show();

                onLoginSuccess(loginResponseBean.mUserInfo);
            }

            @Override
            public void onNetError() {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.no_network_tip)),Toast.LENGTH_SHORT).show();
                hideLoading(false);
                isLogining = false;
            }

            @Override
            public void onNetError(Throwable e) {
                Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.network_error_tip)," 请检查网络"),Toast.LENGTH_SHORT).show();

                hideLoading(false);
                isLogining = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                if(responseBaseBean.code == -1){
                    Toast.makeText(mActivity,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mActivity,String.format(mActivity.getResources().getString(R.string.server_error_tip),responseBaseBean.desc),Toast.LENGTH_SHORT).show();
                }

                hideLoading(false);
                isLogining = false;
            }
        });
    }

    public void onLoginSuccess(UserInfo userInfo){

    }
}
