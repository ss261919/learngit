package vaporsome.com.vaporsome.vaporsome.playads;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.main.popwindow.PopWindow;
import vaporsome.com.vaporsome.vaporsome.redresultactivity.RedResultActivity;
import vaporsome.com.vaporsome.vaporsome.shareimg.ShareImgActivity;

public class PlayerAdsActivity extends BaseActivity implements View.OnClickListener {

    ImageView adsLinearLayoutImg1;
    ImageView adsLinearLayoutImg2;
    ImageView adsLinearLayoutImg3;
    ImageView adsLinearLayoutImg4;
    ImageView adsLinearLayoutImg5;
    ImageView adsLinearLayoutImg6;
    LinearLayout adsLinearLayout;

    private Intent intent;
    private Bundle bundle;
    private String money;
    private boolean isFirstGoToLogin = true;
    private Map<String, String> params = new HashMap<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    bundle = msg.getData();
                    break;
                case 2:
                    bundle = msg.getData();
                    try {
                        switch (Integer.parseInt(JsonUtils.getMapStr(bundle.get("data").toString()).get("code"))) {
                            //code 200 是合法操作，还有剩余红包
                            case 200:
                                CODE = 200;
                                OpenRedThread thread = new OpenRedThread();
                                thread.start();
                                break;
                            //code 201 已领取过该红包
                            case 201:
                                CODE = 201;
                                intent = new Intent(getBaseContext(), RedResultActivity.class);
                                intent.putExtra("bag-id", bag_id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                                break;
                            //code 202 红包已过期
                            case 202:
                                CODE = 202;
                                initPopWindow(JsonUtils.getMapStr(bundle.get("data").toString()).get("message").toString());
                                PopWindow.showPop(popWindow, PlayerAdsActivity.this, adsLinearLayout, 10, 10, 0);
                                break;
                            //code 203 红包已派完
                            case 203:
                                CODE = 203;
                                initPopWindow(JsonUtils.getMapStr(bundle.get("data").toString()).get("message").toString());
                                PopWindow.showPop(popWindow, PlayerAdsActivity.this, adsLinearLayout, 10, 10, 0);
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    intent = new Intent(getBaseContext(), RedResultActivity.class);
                    bundle = new Bundle();
                    bundle.putString("bag-id", bag_id);
                    bundle.putString("code", "0");
                    bundle.putString("companyLogo", companyLogo);
                    bundle.putString("companyName", companyName);
                    bundle.putString("companyComment", companyComment);
                    bundle.putString("money", money);
                    intent.putExtras(bundle);
                    Log.d("--bag-id", bag_id);
                    Log.d("--code", "0");
                    Log.d("--companyLogo", companyLogo);
                    Log.d("--companyName", companyName);
                    Log.d("--companyComment", companyComment);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                    break;
                case 4:
                    bundle = msg.getData();
                    Toast.makeText(PlayerAdsActivity.this, bundle.getString("data").toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(PlayerAdsActivity.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(PlayerAdsActivity.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
            }
        }
    };
    private String bag_id;
    private String companyLogo;
    private String companyName;
    private String companyComment;
    private PopupWindow popWindow;
    private int CODE;
    private int width;
    private int height;
    private RequestQueue queue;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Message message;
    private TextView initPopWindowComment;
    private TextView initPopWindowTV;
    private LinearLayout initPopWindowCancleLL;
    private LinearLayout initPopWindowLookRedRanking;
    private ImageView initPopWindowCompanyIcon;


    @OnClick(R.id.initPopWindowCancleLL)
    public void onClick() {
        PopWindow.closePopWindow(popWindow, PlayerAdsActivity.this);
    }

    @OnClick(R.id.initPopWindowLookRedRanking)
    public void onClick2() {
        if (CODE == 202) {
            new Intent(getBaseContext(), RedResultActivity.class);
            intent.putExtra("bag_id", bag_id);
            intent.putExtra("code", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
        } else if (CODE == 203) {
            new Intent(getBaseContext(), RedResultActivity.class);
            intent.putExtra("bag_id", bag_id);
            intent.putExtra("code", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_ads);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        queue = Volley.newRequestQueue(this);
        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        options = IApplication.options;
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        adsLinearLayoutImg1 = (ImageView) findViewById(R.id.adsLinearLayoutImg1);
        adsLinearLayoutImg2 = (ImageView) findViewById(R.id.adsLinearLayoutImg2);
        adsLinearLayoutImg3 = (ImageView) findViewById(R.id.adsLinearLayoutImg3);
        adsLinearLayoutImg4 = (ImageView) findViewById(R.id.adsLinearLayoutImg4);
        adsLinearLayoutImg5 = (ImageView) findViewById(R.id.adsLinearLayoutImg5);
        adsLinearLayoutImg6 = (ImageView) findViewById(R.id.adsLinearLayoutImg6);
        adsLinearLayout = (LinearLayout) findViewById(R.id.adsLinearLayout);
    }

    @Override
    public void initData() {
        bundle = getIntent().getExtras();
        bag_id = bundle.getString("bag-id");
        companyLogo = bundle.getString("companyLogo");
        companyName = bundle.getString("companyName");
        companyComment = bundle.getString("companyComment");
    }

    @Override
    public void initListener() {
        adsLinearLayoutImg1.setOnClickListener(this);
        adsLinearLayoutImg2.setOnClickListener(this);
        adsLinearLayoutImg3.setOnClickListener(this);
        adsLinearLayoutImg4.setOnClickListener(this);
        adsLinearLayoutImg5.setOnClickListener(this);
        adsLinearLayoutImg6.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.adsLinearLayoutImg1:
                startIntent(1);
                break;
            case R.id.adsLinearLayoutImg2:
                startIntent(2);
                break;
            case R.id.adsLinearLayoutImg3:
                startIntent(3);
                break;
            case R.id.adsLinearLayoutImg4:
                startIntent(4);
                break;
            case R.id.adsLinearLayoutImg5:
                startIntent(5);
                break;
            case R.id.adsLinearLayoutImg6:
                startIntent(6);
                break;
        }
    }

    public void startIntent(int position) {
        isRedOld();
    }

    private void isRedOld() {
        if (NetWorkInfo.isNetworkAvailable(PlayerAdsActivity.this)) {
            IsRedOldedThread thread = new IsRedOldedThread();
            thread.start();
        } else {
            Toast.makeText(this, "网络未连接！", Toast.LENGTH_SHORT).show();
        }
    }

    class IsRedOldedThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(PlayerAdsActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.isRedOlded + PreferencesUtils.getString(PlayerAdsActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.isRedOlded + PreferencesUtils.getString(PlayerAdsActivity.this, "token");
            }
            params.clear();
            params.put("bag-id", bag_id);
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty()) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            message = new Message();
                            message.what = 2;
                            bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } else {
                            message = new Message();
                            message.what = 1;
                            bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.isRedOlded + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            if (!map.get("data").toString().equals("[]")) {
                                message = new Message();
                                message.what = 2;
                                bundle = new Bundle();
                                bundle.putString("data", map.get("data").toString());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            } else {
                                message = new Message();
                                message.what = 1;
                                bundle = new Bundle();
                                bundle.putString("data", map.get("data").toString());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        } else {
                            isFirstGoToLogin = true;
                            handler.sendEmptyMessage(5);
                        }
                    } else {
                        message = new Message();
                        message.what = 1;
                        bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initPopWindow(String string) {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_window, null);
        popView.setId(Integer.parseInt("110"));
        popView.setFocusable(true); // 这个很重要
        popView.setFocusableInTouchMode(true);
        // 重写onKeyListener
        popView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popWindow.isShowing()) {
                        PopWindow.closePopWindow(popWindow, PlayerAdsActivity.this);
                        return true;
                    }
                }
                return false;
            }
        });
        popWindow = new PopupWindow(popView, (int) (width * 0.8), (int) (height * 0.68));
        setAlpha(0.6f);
        //设置popwindow出现和消失动画
        popWindow.setAnimationStyle(R.style.popwindow_anim);
        initPopWindowComment = (TextView) popView.findViewById(R.id.initPopWindowComment);
        initPopWindowComment.setText(companyComment);
        initPopWindowTV = (TextView) popView.findViewById(R.id.initPopWindowTV);
        initPopWindowTV.setText(string);
        initPopWindowCancleLL = (LinearLayout) popView.findViewById(R.id.initPopWindowCancleLL);
        initPopWindowCancleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopWindow.closePopWindow(popWindow, PlayerAdsActivity.this);
            }
        });
        initPopWindowLookRedRanking = (LinearLayout) popView.findViewById(R.id.initPopWindowLookRedRanking);
        initPopWindowLookRedRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(PlayerAdsActivity.this, RedResultActivity.class);
                bundle = new Bundle();
                bundle.putString("bag-id", bag_id);
                bundle.putString("code", "1");
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                if (popWindow != null)
                    PopWindow.closePopWindow(popWindow, PlayerAdsActivity.this);
            }
        });
        initPopWindowCompanyIcon = (ImageView) popView.findViewById(R.id.initPopWindowCompanyIcon);
        imageLoader.loadImage(companyLogo, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                initPopWindowCompanyIcon.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }

    private void setAlpha(float f) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = f;
        getWindow().setAttributes(params);
    }

    class OpenRedThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(PlayerAdsActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.openRed + PreferencesUtils.getString(PlayerAdsActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.openRed + PreferencesUtils.getString(PlayerAdsActivity.this, "token");
            }
            params.clear();
            params.put("bag-id", bag_id);
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty()) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            money = map.get("data").toString();
                            message = new Message();
                            message.what = 3;
                            handler.sendMessage(message);
                        } else {
                            message = new Message();
                            message.what = 4;
                            bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.openRed + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            if (!map.get("data").toString().equals("[]")) {
                                money = map.get("data").toString();
                                message = new Message();
                                message.what = 3;
                                handler.sendMessage(message);
                            } else {
                                message = new Message();
                                message.what = 4;
                                bundle = new Bundle();
                                bundle.putString("data", map.get("data").toString());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        } else {
                            isFirstGoToLogin = true;
                            handler.sendEmptyMessage(5);
                        }
                    } else {
                        message = new Message();
                        message.what = 4;
                        bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
