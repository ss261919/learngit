package vaporsome.com.vaporsome.vaporsome.register;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.registerGuideTV)
    TextView registerGuideTV;
    @Bind(R.id.registerUserNameTV)
    TextView registerUserNameTV;
    @Bind(R.id.registerUserNameET)
    EditText registerUserNameET;
    @Bind(R.id.registerVerificationNumberBT)
    TextView registerVerificationNumberBT;
    @Bind(R.id.registerPasswordTV)
    TextView registerPasswordTV;
    @Bind(R.id.registerUserPasswordET)
    EditText registerUserPasswordET;
    @Bind(R.id.registerUserPasswordConfirmTV)
    TextView registerUserPasswordConfirmTV;
    @Bind(R.id.registerUserPasswordConfirmET)
    EditText registerUserPasswordConfirmET;
    @Bind(R.id.registerVerificationLayout)
    RelativeLayout registerVerificationLayout;
    @Bind(R.id.registerLayout)
    LinearLayout registerLayout;
    @Bind(R.id.registerVerificationNumberET)
    EditText registerVerificationNumberET;
    @Bind(R.id.registerButton)
    Button registerButton;
    private HashMap<String, String> params;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 4:
                    registerButton.setEnabled(true);
                    openToast("网络链接失败,请重试！");
                    break;
                case 3:
                    openToast(bundle.get("data").toString());
                    break;
                case 2:
                    openToast("验证码已发送，60秒后可重新获取。");
                    break;
                case 1:
                    openToast(bundle.get("data").toString());
                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
                case 0:
                    registerButton.setEnabled(true);
                    openToast(bundle.get("data").toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @OnClick(R.id.registerVerificationNumberBT)
    void getVerificationNumberBT() {
        showSurplusTime(registerVerificationNumberBT);
        Toast.makeText(this, "获取验证码", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.registerButton)
    void registerPost() {
        if (registerUserPasswordET.getText().length() <= 6) {
            Toast.makeText(this, "密码不能低于六位", Toast.LENGTH_SHORT).show();
        } else if (!registerUserPasswordET.getText().toString().equals(registerUserPasswordConfirmET.getText().toString())) {
            Toast.makeText(this, "两次输入的密码不一致，请重新输入密码。", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isPhoneNumberValid(registerUserNameET.getText().toString())) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isConfirmNumberValid(registerVerificationNumberET.getText().toString())
                || TextUtils.isEmpty(registerVerificationNumberET.getText())
                || !(registerVerificationNumberET.getText().length() == 6)
                ) {
            Toast.makeText(this, "验证码格式错误...", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isPsdrValid(registerUserPasswordET.getText().toString())) {
            Toast.makeText(this, "密码必须为6到12位数字、字母、下划线中的2种。", Toast.LENGTH_SHORT).show();
        } else {
            //参数
            params = new HashMap<>();
            params.put("mobile", registerUserNameET.getText().toString());
            params.put("password", MD5Utils.getMD5(registerUserPasswordET.getText().toString() + "qckj"));
            params.put("code", registerVerificationNumberET.getText().toString());
            params.put("source", "20");
            PostThread thread = new PostThread();
            thread.start();
            registerButton.setEnabled(false);
        }
    }

    class PostThread extends Thread {
        public void run() {
            //服务器请求路径
            String strUrlPath = Constants.registerUri;
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !(strResult.equals("-1"))) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                } else {
                    handler.sendEmptyMessage(4);
                }

            } catch (Exception e) {
                handler.sendEmptyMessage(4);
                e.printStackTrace();
            }
        }
    }

    class GetVerificationNumberThread extends Thread {
        public void run() {
            //服务器请求路径
            Map<String, String> params2 = new HashMap<>();
            params2.put("mobile", registerUserNameET.getText().toString());
            String strUrlPath = Constants.verificationNumberUri;
            String strResult = HttpUtils.submitPostData(strUrlPath, params2, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 3;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        registerUserNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerUserNameETEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerUserNameETEnable();
                registerButtonEnable();
            }
        });
        registerUserPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerUserPasswordETEnable();
                registerButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerUserPasswordETEnable();
                registerButtonEnable();
            }
        });
        registerVerificationNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerVerificationNumberETEnable();
                registerButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerVerificationNumberETEnable();
                registerButtonEnable();
            }
        });
        registerUserPasswordConfirmET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                registerUserPasswordConfirmETEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                registerButtonEnable();
            }
        });
    }

    private void registerUserPasswordConfirmETEnable() {
        if (!TextUtils.isEmpty(registerUserPasswordConfirmET.getText())) {
            registerUserPasswordConfirmET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            registerUserPasswordConfirmET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void registerVerificationNumberETEnable() {
        if (!TextUtils.isEmpty(registerVerificationNumberET.getText())) {
            registerVerificationNumberET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            registerVerificationNumberET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void registerUserPasswordETEnable() {
        if (!TextUtils.isEmpty(registerUserPasswordET.getText())) {
            registerUserPasswordET.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            registerUserPasswordET.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void registerUserNameETEnable() {
        if (!TextUtils.isEmpty(registerUserNameET.getText())) {
            registerUserNameET.setBackgroundResource(R.drawable.login_edittext_focusable);
            registerVerificationNumberBT.setBackgroundResource(R.drawable.login_edittext_focusable);
        } else {
            registerUserNameET.setBackgroundResource(R.drawable.login_button_shape_nomal);
            registerVerificationNumberBT.setBackgroundResource(R.drawable.login_button_shape_nomal);
        }
    }

    private void registerButtonEnable() {
        if (!TextUtils.isEmpty(registerUserPasswordET.getText())
                && !TextUtils.isEmpty(registerUserNameET.getText())
                && !TextUtils.isEmpty(registerVerificationNumberET.getText())
                && !TextUtils.isEmpty(registerUserPasswordConfirmET.getText())) {
            registerButton.setBackgroundResource(R.drawable.login_button_shape_press);
            registerButton.setTextColor(Color.parseColor("#ffffffff"));
            registerButton.setEnabled(true);
        } else {
            registerButton.setEnabled(false);
            registerButton.setBackgroundResource(R.drawable.login_button_shape_nomal);
            registerButton.setTextColor(Color.parseColor("#ccbfbf"));
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    //重新获取验证码倒计时
    private void showSurplusTime(TextView textView) {
        GetVerificationNumberThread thread2 = new GetVerificationNumberThread();
        thread2.start();
        TimeCountUtil timeCountUtil = new TimeCountUtil(this, 60000, 1000, textView);
        timeCountUtil.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
