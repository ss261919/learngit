package vaporsome.com.vaporsome.vaporsome.outofmoney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;

public class SmallChangeActivity extends BaseActivity {

    @Bind(R.id.smallChangeCancle)
    LinearLayout smallChangeCancle;
    @Bind(R.id.smallChangeLL)
    RelativeLayout smallChangeLL;
    @Bind(R.id.smallChangePost)
    Button smallChangePost;
    @Bind(R.id.smallChangMoneyNumberTV)
    TextView smallChangMoneyNumberTV;
    @Bind(R.id.smallChangeSRL)
    SwipeRefreshLayout smallChangeSRL;
    private final int OUTOF_MONEY_ACTIVITY = 0;
    private RequestQueue queue;

    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    smallChangeSRL.setEnabled(false);
                    smallChangeSRL.setRefreshing(false);
                    smallChangeSRL.post(new Runnable() {
                        @Override
                        public void run() {
                            smallChangeSRL.setEnabled(true);
                        }
                    });
                    smallChangMoneyNumberTV.setText("￥" + dataList.get("money").toString());
                    isLoading = false;
                    break;
                case 1:
                    smallChangeSRL.setEnabled(false);
                    smallChangeSRL.setRefreshing(false);
                    smallChangeSRL.post(new Runnable() {
                        @Override
                        public void run() {
                            smallChangeSRL.setEnabled(true);
                        }
                    });
                    isLoading = false;
                    Toast.makeText(SmallChangeActivity.context, "网络有问题,请检查后下拉刷新重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (NetWorkInfo.isNetworkAvailable(SmallChangeActivity.this)) {
                        downData();
                        isLoading = true;
                    } else {
                        Toast.makeText(SmallChangeActivity.context, "网络有问题,请检查后重试！", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }
                    break;
                case 3:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(SmallChangeActivity.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(SmallChangeActivity.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }

                    break;
            }
        }
    };
    private Map<String, String> dataList;
    private boolean isLoading;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_change);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void initData() {
        downData();
    }

    @Override
    public void initListener() {
        smallChangeSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(SmallChangeActivity.this)) {
                    smallChangeSRL.setEnabled(true);
                    if (!isLoading) {
                        handler.sendEmptyMessageAtTime(2, 10);
                    }
                } else {
                    smallChangeSRL.setEnabled(false);
                    smallChangeSRL.setRefreshing(false);
                    smallChangeSRL.post(new Runnable() {
                        @Override
                        public void run() {
                            smallChangeSRL.setEnabled(true);
                        }
                    });
                    Toast.makeText(SmallChangeActivity.this, "网络未连接，请检查无误后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.smallChangeCancle, R.id.smallChangePost})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smallChangeCancle:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.smallChangePost:
                Intent intent = new Intent(this, OutofMoneyActivity.class);
                startActivityForResult(intent, OUTOF_MONEY_ACTIVITY);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            if (NetWorkInfo.isNetworkAvailable(SmallChangeActivity.this)) {
                downData();
            } else Toast.makeText(this, "网络有问题,数据更新失败！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    private void downData() {
        String strUrlPath;
        if (PreferencesUtils.getBoolean(SmallChangeActivity.this, "isOtherLogin")) {
            strUrlPath = Constants.personCenterUri + PreferencesUtils.getString(SmallChangeActivity.this, "otherToken");
        } else {
            strUrlPath = Constants.personCenterUri + PreferencesUtils.getString(SmallChangeActivity.this, "token");
        }
        StringRequest request = new StringRequest(Request.Method.POST, strUrlPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Map<String, Object> map = JsonUtils.getMapObj(response);
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                if (!TextUtils.equals(map.get("info").toString(), "[]")) {
                                    dataList = JsonUtils.getMapStr(map.get("info").toString());
                                    message = new Message();
                                    message.what = 0;
                                    handler.sendMessage(message);
                                } else {
                                    message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }

                            } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                                String strUrlPath = Constants.personCenterUri + TokenUtlils.getToken();
                                StringRequest request2 = new StringRequest(Request.Method.POST, strUrlPath,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Map<String, Object> map2 = null;
                                                try {
                                                    map2 = JsonUtils.getMapObj(response);
                                                    if (TextUtils.equals(map2.get("status").toString(), "true")) {
                                                        if (!TextUtils.equals(map2.get("info").toString(), "[]")) {
                                                            dataList = JsonUtils.getMapStr(map2.get("info").toString());
                                                            message = new Message();
                                                            message.what = 0;
                                                            handler.sendMessage(message);
                                                        }
                                                    } else {
                                                        message = new Message();
                                                        message.what = 3;
                                                        handler.sendMessage(message);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                                queue.add(request2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
        queue.add(request);
    }
}
