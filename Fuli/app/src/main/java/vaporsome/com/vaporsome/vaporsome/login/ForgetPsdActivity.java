package vaporsome.com.vaporsome.vaporsome.login;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.TransactionTooLargeException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.IsMatchUtil;
import vaporsome.com.vaporsome.common.utils.MD5Utils;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TimeCountUtil;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class ForgetPsdActivity extends BaseActivity {

    private Bundle bundle;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 5:
                    bundle = msg.getData();
                    openToast(bundle.get("data").toString());
                    break;
                case 4:
                    openToast("验证码已发送。");
                    break;
                case 3:
                    bundle = msg.getData();
                    openToast(bundle.get("data").toString());
                    bundle = null;
                    break;
                case 2:
                    openToast(bundle.get("data").toString());
                    PreferencesUtils.putBoolean(getBaseContext(), "isFirstLogin", true);
                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
            }
            bundle = null;
        }
    };

    private void openToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Bind(R.id.forgetPsdUserNameET)
    EditText forgetPsdUserNameET;
    @Bind(R.id.forgetPsdVerificationNumberTT)
    TextView forgetPsdVerificationNumberTT;
    @Bind(R.id.forgetPsdUserPasswordET)
    EditText forgetPsdUserPasswordET;
    @Bind(R.id.forgetPsdUserPasswordConfirmET)
    EditText forgetPsdUserPasswordConfirmET;
    @Bind(R.id.forgetPsdVerificationNumberET)
    EditText forgetPsdVerificationNumberET;
    @Bind(R.id.forgetPasswordPostBT)
    Button forgetPasswordPostBT;
    //获取验证码
    GetVerificationNumberThread getVerificationNumberThread;
    //提交
    ForgetPsdGetVerificationNumberThread forgetPsdGetVerificationNumberThread;

    @OnClick(R.id.forgetPsdVerificationNumberTT)
    void getVerificationNumberTT() {
        showSurplusTime(forgetPsdVerificationNumberTT);
    }

    @OnClick(R.id.forgetPasswordPostBT)
    void forgetPostBT() {
        if (forgetPsdUserPasswordET.getText().length() <= 6) {
            Toast.makeText(this, "密码不能低于六位", Toast.LENGTH_SHORT).show();
        } else if (!forgetPsdUserPasswordET.getText().toString().equals(forgetPsdUserPasswordConfirmET.getText().toString())) {
            Toast.makeText(this, "两次输入的密码不一致，请重新输入密码。", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isPhoneNumberValid(forgetPsdUserNameET.getText().toString())) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isConfirmNumberValid(forgetPsdVerificationNumberET.getText().toString())
                || TextUtils.isEmpty(forgetPsdVerificationNumberET.getText())
                || !(forgetPsdVerificationNumberET.getText().length() == 6)
                ) {
            Toast.makeText(this, "验证码格式错误...", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isPsdrValid(forgetPsdUserPasswordET.getText().toString())) {
            Toast.makeText(this, "密码必须为6到12位数字、字母、下划线中的2种。", Toast.LENGTH_SHORT).show();
        } else {
            forgetPsdGetVerificationNumberThread = new ForgetPsdGetVerificationNumberThread();
            forgetPsdGetVerificationNumberThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psd);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        forgetPsdUserNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgetUserNameETEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                forgetUserNameETEnable();
                forgetButtonEnable();
            }
        });
        forgetPsdUserPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgetUserPasswordETEnable();
                forgetButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                forgetUserPasswordETEnable();
                forgetButtonEnable();
            }
        });
        forgetPsdUserPasswordConfirmET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgetVerificationNumberETEnable();
                forgetButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                forgetVerificationNumberETEnable();
                forgetButtonEnable();
            }
        });
        forgetPsdVerificationNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                forgetUserPasswordConfirmETEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                forgetButtonEnable();
            }
        });
    }

    private void forgetUserPasswordConfirmETEnable() {
        if (!TextUtils.isEmpty(forgetPsdUserPasswordConfirmET.getText())) {
            forgetPsdUserPasswordConfirmET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            forgetPsdUserPasswordConfirmET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void forgetVerificationNumberETEnable() {
        if (!TextUtils.isEmpty(forgetPsdVerificationNumberET.getText())) {
            forgetPsdVerificationNumberET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            forgetPsdVerificationNumberET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void forgetUserPasswordETEnable() {
        if (!TextUtils.isEmpty(forgetPsdUserPasswordET.getText())) {
            forgetPsdUserPasswordET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            forgetPsdUserPasswordET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void forgetUserNameETEnable() {
        if (!TextUtils.isEmpty(forgetPsdUserNameET.getText())) {
            forgetPsdUserNameET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            forgetPsdUserNameET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void forgetButtonEnable() {
        if (!TextUtils.isEmpty(forgetPsdUserNameET.getText())
                && !TextUtils.isEmpty(forgetPsdUserPasswordET.getText())
                && !TextUtils.isEmpty(forgetPsdUserPasswordConfirmET.getText())
                && !TextUtils.isEmpty(forgetPsdVerificationNumberET.getText())) {
            forgetPasswordPostBT.setBackgroundResource(R.drawable.login_button_shape_press);
            forgetPasswordPostBT.setTextColor(Color.parseColor("#ffffffff"));
            forgetPasswordPostBT.setEnabled(true);
        } else if (!TextUtils.equals(forgetPsdUserPasswordET.getText(), forgetPsdUserPasswordConfirmET.getText())) {
            openToast("两次密码输入不正确，请确认");
            forgetPasswordPostBT.setEnabled(false);
            forgetPasswordPostBT.setBackgroundResource(R.drawable.login_button_shape_nomal);
            forgetPasswordPostBT.setTextColor(Color.parseColor("#ccbfbf"));
        } else if (IsMatchUtil.isConfirmNumberValid(forgetPsdVerificationNumberET.getText().toString())) {
            openToast("验证码格式不正确。");
        } else {
            forgetPasswordPostBT.setEnabled(false);
            forgetPasswordPostBT.setBackgroundResource(R.drawable.login_button_shape_nomal);
            forgetPasswordPostBT.setTextColor(Color.parseColor("#ccbfbf"));
        }

    }


    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    private void showSurplusTime(TextView textView) {
        getVerificationNumberThread = new GetVerificationNumberThread();
        getVerificationNumberThread.start();
        TimeCountUtil timeCountUtil = new TimeCountUtil(this, 60000, 1000, textView);
        timeCountUtil.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    class ForgetPsdGetVerificationNumberThread extends Thread {
        public void run() {
            //服务器请求路径
            Map<String, String> params2 = new HashMap<>();
            params2.put("mobile", forgetPsdUserNameET.getText().toString());
            params2.put("password", MD5Utils.getMD5(forgetPsdUserPasswordET.getText().toString() + "qckj"));
            params2.put("code", forgetPsdVerificationNumberET.getText().toString());
            String strUrlPath = Constants.forgetPsdUri;
            String strResult = HttpUtils.submitPostData(strUrlPath, params2, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        Message message = new Message();
                        message.what = 2;
                        bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 3;
                        bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                        bundle = null;
                    }
                } else {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class GetVerificationNumberThread extends Thread {
        public void run() {
            //服务器请求路径
            Map<String, String> params2 = new HashMap<>();
            params2.put("mobile", forgetPsdUserNameET.getText().toString());
            String strUrlPath = Constants.forgetPsdVerificationNumberUri;
            String strResult = HttpUtils.submitPostData(strUrlPath, params2, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Message message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 5;
                    bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}