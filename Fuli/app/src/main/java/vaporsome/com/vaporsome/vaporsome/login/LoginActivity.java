package vaporsome.com.vaporsome.vaporsome.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.InstrumentedActivity;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.DelayUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.IsMatchUtil;
import vaporsome.com.vaporsome.common.utils.MD5Utils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.SaveAndRedUtils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.register.RegisterActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText loginUserNameET;
    private EditText loginUserPasswordET;
    private Button loginButton;
    private ImageView loginWeiXin;
    private TextView loginForgetPassword;
    private TextView loginToRegister;
    public Map<String, String> params;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PreferencesUtils.putBoolean(getBaseContext(), "isLogin", true);
                    if (PreferencesUtils.getBoolean(getBaseContext(), "isFirstLogin", true)) {
                        PreferencesUtils.putString(getBaseContext(), "mobile", loginUserNameET.getText().toString());
                        PreferencesUtils.putString(getBaseContext(), "password", MD5Utils.getMD5(loginUserPasswordET.getText().toString() + "qckj"));
                        PreferencesUtils.putBoolean(getBaseContext(), "isFirstLogin", false);
                    }
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 0:
                    loginButton.setEnabled(true);
                    PreferencesUtils.putBoolean(getBaseContext(), "isFirstLogin", true);
                    openToast(msg.getData().getString("data"));
                    break;
                case 2:
                    loginButton.setEnabled(true);
                    openToast("网络有问题，请检查后重试！");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private IWXAPI api;
    private ImageView loginUserICon;


    private void openToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    private void assignViews() {
        loginUserNameET = (EditText) findViewById(R.id.loginUserNameET);
        loginUserPasswordET = (EditText) findViewById(R.id.loginUserPasswordET);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginWeiXin = (ImageView) findViewById(R.id.loginWeiXin);
        loginUserICon = (ImageView) findViewById(R.id.loginUserICon);
        loginForgetPassword = (TextView) findViewById(R.id.loginForgetPassword);
        loginToRegister = (TextView) findViewById(R.id.loginToRegister);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assignViews();
        initView();
        initListener();
    }

    public void initView() {
        loginUserNameET.setText(PreferencesUtils.getString(LoginActivity.this, "mobile", ""));
        if (!(PreferencesUtils.getString(LoginActivity.this, "password", "").length() == 0)) {
            loginUserPasswordET.setText(PreferencesUtils.getString(LoginActivity.this, "password", "").substring(0, 6));
        }
        if (PreferencesUtils.getString(getBaseContext(), Constants.splashImgUrl, "").isEmpty()) {
            loginUserICon.setBackgroundResource(R.mipmap.person_center_head_icon);
        } else {
            if (!(SaveAndRedUtils.readSplashImg(Constants.userHeaderUrl) == null)) {
                loginUserICon.setImageBitmap(SaveAndRedUtils.readSplashImg(Constants.userHeaderUrl));
            }
        }
    }


    private void loginUserPasswordETEnable() {
        if (!TextUtils.isEmpty(loginUserPasswordET.getText())) {
            loginUserPasswordET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            loginUserPasswordET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void loginUserNameETEnable() {
        if (!TextUtils.isEmpty(loginUserNameET.getText())) {
            loginUserNameET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            loginUserNameET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }


    private void loginButtonEnable() {
        if (!TextUtils.isEmpty(loginUserNameET.getText()) && !TextUtils.isEmpty(loginUserPasswordET.getText()) && IsMatchUtil.isPhoneNumberValid(loginUserNameET.getText().toString())) {
            loginButton.setBackgroundResource(R.drawable.login_button_shape_press);
            loginButton.setTextColor(Color.parseColor("#ffffffff"));
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
            loginButton.setBackgroundResource(R.drawable.login_button_shape_nomal);
            loginButton.setTextColor(Color.parseColor("#ccbfbf"));
        }
    }

    public void initData() {

    }

    public void initListener() {
        loginButton.setOnClickListener(this);
        loginWeiXin.setOnClickListener(this);
        loginForgetPassword.setOnClickListener(this);
        loginToRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //login
            case R.id.loginForgetPassword:
                intent = new Intent(this, ForgetPsdActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.loginToRegister:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.loginButton:
                if (!IsMatchUtil.isPhoneNumberValid(loginUserNameET.getText().toString().trim())) {
                    Toast.makeText(this, "手机号码格式错误!", Toast.LENGTH_SHORT).show();
                } else {
                    loginPost();
                }
                break;
            case R.id.loginWeiXin:
                wxLogin();
                break;
        }
    }

    private void wxLogin() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        if (!IApplication.api.isWXAppInstalled()) {
            Toast.makeText(this, "还没有安装微信", Toast.LENGTH_SHORT).show();
        } else {
            if (IApplication.api == null) {
                api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
                api.registerApp(Constants.APP_ID);
                api.sendReq(req);
            } else {
                IApplication.api.sendReq(req);
            }
        }
        loginWeiXin.setEnabled(false);
        DelayUtils.viewEnableDelay(handler, loginWeiXin, 2500);
    }

    private void loginPost() {
        params = new HashMap<>();
        params.put("mobile", loginUserNameET.getText().toString().trim());
        if (PreferencesUtils.getBoolean(getBaseContext(), "isFirstLogin", true)) {
            params.put("password", MD5Utils.getMD5(loginUserPasswordET.getText().toString().trim() + "qckj"));
        } else
            params.put("password", PreferencesUtils.getString(getBaseContext(), "password").toString());

        if (NetWorkInfo.isNetworkAvailable(LoginActivity.this)) {
            PostThread thread = new PostThread();
            thread.start();
            loginButton.setEnabled(false);
        } else Toast.makeText(this, "网络有问题，请检查后重试！", Toast.LENGTH_SHORT).show();

    }

    class PostThread extends Thread {
        public void run() {
            //服务器请求路径
            String strUrlPath = Constants.phoneLoginUri;
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Map<String, Object> map2 = JsonUtils.getMapObj(map.get("data").toString());
                    PreferencesUtils.putString(getBaseContext(), "token", map2.get("token").toString());
                    handler.sendEmptyMessage(1);
                } else {
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(2);
                e.printStackTrace();
            }
        }
    }

    long firstTime = 0;
}