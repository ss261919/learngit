package vaporsome.com.vaporsome.vaporsome.moneydetail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;

public class MoneyDetail extends BaseActivity implements MoneyDetailAdapter.OnItemViewClickListener {
    RecyclerView recyclerView;
    List<Map<String, Object>> dataList = new ArrayList<>();
    RequestQueue queue;
    private LinearLayout moneyDetailLinearLayoutBack;
    private int screenWidth;
    private int screenHeight;
    private MoneyDetailAdapter moneyDetailAdapter;
    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    moneyDetailLoadRL.setVisibility(View.GONE);
                    setData();
                    moneyDetailSRFEnable();
                    break;
                case 1:
                    moneyDetailSRFEnable();
                    loadRLLoadImg.setVisibility(View.GONE);
                    loadRLLoadImg.clearAnimation();
                    loadRLLoadTV.setText("没有数据！");
                    isHaveNext = false;
                    break;
                case 4:
                    page = 1;
                    dataList.clear();
                    loadData();
                    isLoading = true;
                    firstToast = true;
                    break;
                case 5:
                    page++;
                    loadData();
                    isLoading = true;
                    break;
                case 2:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(MoneyDetail.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(MoneyDetail.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
            }
        }
    };
    private ImageView loadRLLoadImg;
    private TextView loadRLLoadTV;

    private void moneyDetailSRFEnable() {
        moneyDetailSRF.setRefreshing(false);
        moneyDetailSRF.setEnabled(false);
        moneyDetailSRF.post(new Runnable() {
            @Override
            public void run() {
                moneyDetailSRF.setEnabled(true);
            }
        });
        isLoading = false;
    }

    private boolean isLoading;
    private boolean isHaveNext = true;
    private Message message;
    private boolean firstToast = true;
    private int page = 1;

    private void setData() {
        if (!(dataList.size() == 0) && page == 1) {
            moneyDetailAdapter = new MoneyDetailAdapter(getBaseContext(), dataList, screenWidth, screenHeight);
            recyclerView.setAdapter(moneyDetailAdapter);
            moneyDetailAdapter.setOnItemViewClickListener(this);
        } else if (!(dataList.size() == 0)) {
            moneyDetailAdapter.notifyDataSetChanged();
        }
    }

    private RelativeLayout moneyDetailLoadRL;
    private SwipeRefreshLayout moneyDetailSRF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_detail);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        initView();
        loadData();
        initListener();
    }

    public void loadData() {
        Animation anim = AnimationUtils.loadAnimation(MoneyDetail.this, R.anim.load);
        anim.start();
        loadRLLoadImg.setAnimation(anim);
        if (NetWorkInfo.isNetworkAvailable(MoneyDetail.this)) {
            initData();
        } else Toast.makeText(MoneyDetail.context, "网络还没准备好,请检查后下拉刷新！", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void initView() {
        moneyDetailLinearLayoutBack = (LinearLayout) findViewById(R.id.moneyDetailLinearLayoutBack);
        moneyDetailSRF = (SwipeRefreshLayout) findViewById(R.id.moneyDetailSRF);
        moneyDetailLoadRL = (RelativeLayout) findViewById(R.id.moneyDetailLoadRL);
        loadRLLoadImg = (ImageView) findViewById(R.id.loadRLLoadImg);
        loadRLLoadTV = (TextView) findViewById(R.id.loadRLLoadTV);

        recyclerView = (RecyclerView) findViewById(R.id.moneyDetailRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        queue = Volley.newRequestQueue(this);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
    }

    @Override
    public void initData() {
        GetMoneyDetailThread thread = new GetMoneyDetailThread();
        thread.start();
    }

    @Override
    public void initListener() {
        moneyDetailLinearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
            }
        });
        moneyDetailSRF.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(MoneyDetail.this)) {
                    int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    if (topRowVerticalPosition >= 0) {
                        if (loadRLLoadImg.getVisibility() == View.VISIBLE)
                            moneyDetailLoadRL.setVisibility(View.GONE);
                        moneyDetailSRF.setEnabled(true);
                        if (!isLoading) {
                            if (isHaveNext)
                                handler.sendEmptyMessageAtTime(4, 10);
                            else moneyDetailSRFEnable();
                        } else
                            Toast.makeText(MoneyDetail.this, "正在刷新", Toast.LENGTH_SHORT).show();
                    } else moneyDetailSRF.setEnabled(false);
                } else {
                    moneyDetailSRF.setEnabled(true);
                    moneyDetailSRF.setRefreshing(false);
                    moneyDetailSRF.post(new Runnable() {
                        @Override
                        public void run() {
                            moneyDetailSRF.setEnabled(true);
                        }
                    });
                    Toast.makeText(MoneyDetail.this, "网络有问题，请请检查无误后下拉！", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //此处我们是对recyclerview添加scrollListener ,监听滑倒最后一个可见的条目的时候，刷新加载数据
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                moneyDetailSRF.setEnabled(topRowVerticalPosition >= 0);
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == moneyDetailAdapter.getItemCount()) {
                    boolean isRefreshing = moneyDetailSRF.isRefreshing();
                    if (isRefreshing) {
                        return;
                    }
                    if (!isLoading) {
                        if (isHaveNext) {
                            message = new Message();
                            message.what = 5;
                            handler.sendMessage(message);
                        } else {
                            if (firstToast) {
                                Toast.makeText(MoneyDetail.this, "没有更多记录！", Toast.LENGTH_SHORT).show();
                                firstToast = false;
                            }
                        }
                    }
                }
            }
        });

    }


    private void loadMore() {
        Toast.makeText(this, "加载更多", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    private HashMap<String, String> params = new HashMap<>();

    @Override
    public void onItemClick(RelativeLayout linearLayout, int position) {
        Intent intent = new Intent(this, MoneyDetailInfoActivity.class);
        intent.putExtra("id", dataList.get(position).get("id").toString());
        startActivity(intent);
        overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
    }

    class GetMoneyDetailThread extends Thread {
        @Override
        public void run() {
            params.clear();
            params.put("page", String.valueOf(page));
            String strUrlPath;
            if (PreferencesUtils.getBoolean(MoneyDetail.this, "isOtherLogin")) {
                strUrlPath = Constants.moneyDetail + PreferencesUtils.getString(MoneyDetail.this, "otherToken");
            } else {
                strUrlPath = Constants.moneyDetail + PreferencesUtils.getString(MoneyDetail.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!TextUtils.equals(map.get("data").toString(), "[]")) {
                            if (TextUtils.equals(String.valueOf(page), "1")) {
                                dataList = JsonUtils.getListMap(map.get("data").toString());
                            } else
                                dataList.addAll(JsonUtils.getListMap(map.get("data").toString()));
                            message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } else {
                            message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.moneyDetail + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            if (!TextUtils.equals(map.get("data").toString(), "[]")) {
                                if (TextUtils.equals(String.valueOf(page), "1")) {
                                    dataList = JsonUtils.getListMap(map.get("data").toString());
                                } else
                                    dataList.addAll(JsonUtils.getListMap(map.get("data").toString()));
                                message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            } else if (TextUtils.equals(map.get("data").toString(), "[]")) {
                                message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        } else {
                            message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                            isFirstGoToLogin = true;
                        }
                    } else {
                        message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } else if (strResult.equals("-1")) {
                    message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }

}