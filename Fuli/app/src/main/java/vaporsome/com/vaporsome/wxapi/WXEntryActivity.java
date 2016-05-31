package vaporsome.com.vaporsome.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.redresultactivity.RedResultActivity;

/**
 * Created by ${Bash} on 2016/5/5.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public IWXAPI api;
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    PreferencesUtils.putBoolean(WXEntryActivity.this, "isOtherLogin", true);
                    PreferencesUtils.putBoolean(WXEntryActivity.this, "isLogin", true);
                    startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                    finish();
                    break;
                case 1:
                    Toast.makeText(WXEntryActivity.this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:

                    break;

            }
        }
    };
    private String code;
    private Map<String, String> params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);
        if (!(IApplication.api == null)) {
            IApplication.api.handleIntent(getIntent(), this);
        } else {
            api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
            api.handleIntent(getIntent(), this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED://拒绝授权
            case BaseResp.ErrCode.ERR_USER_CANCEL://取消授权
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case RETURN_MSG_TYPE_SHARE:// 用于分享
                        finish();
                        overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                        break;
                    case RETURN_MSG_TYPE_LOGIN://用于登录
                        code = ((SendAuth.Resp) baseResp).code; //即为所需的code
//                        AlertDialog.Builder builder = new AlertDialog.Builder(WXEntryActivity.this);
//                        builder.setTitle(code);
//                        builder.show();
                        params = new HashMap<>();
                        params.put("code", code);
                        WXLoginThread thread = new WXLoginThread();
                        thread.start();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!(IApplication.api == null)) {
            IApplication.api.handleIntent(intent, this);
        }
    }

    private Message message;

    class WXLoginThread extends Thread {

        @Override
        public void run() {
            super.run();
            String strResult = HttpUtils.submitPostData(Constants.wxLoginUrl, params, "utf-8");
            if (!strResult.equals("-1") && !(strResult.length() == 0)) {
                try {
                    Map<String, String> map = JsonUtils.getMapStr(strResult);
                    if (map.get("status").toString().equals("true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            PreferencesUtils.putString(getBaseContext(), "otherToken", map.get("data").toString());
                            message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } else {
                            message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    } else {
                        message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }
}
