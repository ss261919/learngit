package vaporsome.com.vaporsome.vaporsome.moneydetail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;

public class MoneyDetailInfoActivity extends BaseActivity {

    @Bind(R.id.moneyDetailInfoLinearLayoutBack)
    LinearLayout moneyDetailInfoLinearLayoutBack;
    @Bind(R.id.moneyDetailInfoLoadRL)
    RelativeLayout moneyDetailInfoLoadRL;
    @Bind(R.id.moneyDetailInfoLL)
    LinearLayout moneyDetailInfoLL;
    @Bind(R.id.moneyDetailInfoLoadFailRL)
    RelativeLayout moneyDetailInfoLoadFailRL;
    @Bind(R.id.moneyDetailInfoDescriptionTV)
    TextView moneyDetailInfoDescriptionTV;
    @Bind(R.id.moneyDetailInfoMoneyNumberTV)
    TextView moneyDetailInfoMoneyNumberTV;
    @Bind(R.id.moneyDetailInfoTypeDescriptionTV)
    TextView moneyDetailInfoTypeDescriptionTV;
    @Bind(R.id.moneyDetailInfoTypeDescriptionDetailTV)
    TextView moneyDetailInfoTypeDescriptionDetailTV;
    @Bind(R.id.moneyDetailInfoTimeDescriptionTV)
    TextView moneyDetailInfoTimeDescriptionTV;
    @Bind(R.id.moneyDetailInfoTimeDescriptionDetailTV)
    TextView moneyDetailInfoTimeDescriptionDetailTV;
    @Bind(R.id.moneyDetailInfoRemarkDescriptionTV)
    TextView moneyDetailInfoRemarkDescriptionTV;
    @Bind(R.id.moneyDetailInfoRemarkDescriptionDetailTV)
    TextView moneyDetailInfoRemarkDescriptionDetailTV;
    @Bind(R.id.moneyDetailInfoSRL)
    SwipeRefreshLayout moneyDetailInfoSRL;
    @Bind(R.id.loadRLLoadImg)
    ImageView loadRLLoadImg;

    private Map<String, String> params;

    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    moneyDetailInfoSRLDisable();
                    moneyDetailInfoLoadRL.setVisibility(View.GONE);
                    loadRLLoadImg.clearAnimation();
                    anim.cancel();
                    isLoading = false;
                    Bundle bundle = msg.getData();
                    bundle.getString("id");
                    if (bundle.getString("type").equals("10")) {
                        moneyDetailInfoDescriptionTV.setText("提现金额");
                    } else
                        moneyDetailInfoDescriptionTV.setText("入账金额");
                    moneyDetailInfoMoneyNumberTV.setText(bundle.getString("value"));
                    moneyDetailInfoTypeDescriptionDetailTV.setText(bundle.getString("description"));
                    moneyDetailInfoTimeDescriptionDetailTV.setText(bundle.getString("created_at"));
                    moneyDetailInfoRemarkDescriptionDetailTV.setText(bundle.getString("remark"));

                    break;
                case 1:
                    moneyDetailInfoSRLDisable();
                    isLoading = false;
                    moneyDetailInfoLoadRL.setVisibility(View.GONE);
                    loadRLLoadImg.clearAnimation();
                    anim.cancel();
                    Toast.makeText(MoneyDetailInfoActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(MoneyDetailInfoActivity.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(MoneyDetailInfoActivity.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
                case 3:
                    downData();
                    isLoading = true;
                    break;
            }
        }
    };
    private Animation anim;
    private String id;
    private boolean isLoading;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_detail_info);
        id = getIntent().getStringExtra("id").toString();
        ButterKnife.bind(this);
        downData();
        initListener();
    }

    private void downData() {
        if (NetWorkInfo.isNetworkAvailable(MoneyDetailInfoActivity.this)) {
            anim = AnimationUtils.loadAnimation(this, R.anim.load);
            loadRLLoadImg.setAnimation(anim);
            anim.start();
            GetMoneyDetailInfoThread thread = new GetMoneyDetailInfoThread();
            thread.start();
            moneyDetailInfoLoadFailRL.setVisibility(View.GONE);
            isLoading = true;
        } else {
            Toast.makeText(MoneyDetail.context, "网络还没准备好,请检查后下拉刷新！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        moneyDetailInfoSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(MoneyDetailInfoActivity.this)) {
                    if (!isLoading) {
                        handler.sendEmptyMessage(3);
                    } else {
                        moneyDetailInfoSRLDisable();
                    }
                } else {
                    moneyDetailInfoSRLDisable();
                    Toast.makeText(MoneyDetail.context, "网络还没准备好,请检查后下拉重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moneyDetailInfoSRLDisable() {
        moneyDetailInfoSRL.setEnabled(false);
        moneyDetailInfoSRL.setRefreshing(false);
        moneyDetailInfoSRL.post(new Runnable() {
            @Override
            public void run() {
                moneyDetailInfoSRL.setEnabled(true);
            }
        });
    }

    @OnClick(R.id.moneyDetailInfoLinearLayoutBack)
    public void onClick() {
        finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    class GetMoneyDetailInfoThread extends Thread {
        @Override
        public void run() {
            params = new HashMap<>();
            params.put("id", id);
            String strUrlPath;
            if (PreferencesUtils.getBoolean(MoneyDetailInfoActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.moneyDetailInfo + PreferencesUtils.getString(MoneyDetailInfoActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.moneyDetailInfo + PreferencesUtils.getString(MoneyDetailInfoActivity.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.equals("-1") && !(strResult.length() == 0)) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    Map<String, String> map2;
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        map2 = JsonUtils.getMapStr(map.get("data").toString());
                        message = new Message();
                        message.what = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString("id", map2.get("id"));
                        bundle.putString("type", map2.get("type"));
                        bundle.putString("description", map2.get("description"));
                        bundle.putString("value", map2.get("value"));
                        bundle.putString("created_at", map2.get("created_at"));
                        bundle.putString("remark", map2.get("remark"));
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.moneyDetailInfo + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            map2 = JsonUtils.getMapStr(map.get("data").toString());
                            message = new Message();
                            message.what = 0;
                            Bundle bundle = new Bundle();
                            bundle.putString("id", map2.get("id"));
                            bundle.putString("type", map2.get("type"));
                            bundle.putString("description", map2.get("description"));
                            bundle.putString("value", map2.get("value"));
                            bundle.putString("created_at", map2.get("created_at"));
                            bundle.putString("remark", map2.get("remark"));
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(2);
                            isFirstGoToLogin = true;
                        }
                    } else {
                    }
                } else {
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
