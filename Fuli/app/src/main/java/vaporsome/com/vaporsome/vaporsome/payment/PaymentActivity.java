package vaporsome.com.vaporsome.vaporsome.payment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.custominterface.CustomScrollViewListener;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.common.custom.MyScrollView;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;

public class PaymentActivity extends BaseActivity implements PaymentRecycleViewAdapter.onItemViewClickListener, View.OnClickListener, MyScrollView.CustomScrollViewListener {

    @Bind(R.id.paymentCancel)
    LinearLayout paymentCancel;
    @Bind(R.id.paymentRecycleView)
    RecyclerView paymentRecycleView;
    @Bind(R.id.paymentTotalMoneyTV)
    TextView paymentTotalMoneyTV;
    @Bind(R.id.paymentSmallChargePayImg)
    ImageView paymentSmallChargePayImg;
    @Bind(R.id.paymentSmallChargePayTV)
    TextView paymentSmallChargePayTV;
    @Bind(R.id.paymentSmallChargeNullEnough)
    TextView paymentSmallChargeNullEnough;
    @Bind(R.id.paymentSmallChargePayChooseImg)
    ImageView paymentSmallChargePayChooseImg;
    @Bind(R.id.paymentSmallChargePayRL)
    RelativeLayout paymentSmallChargePayRL;
    @Bind(R.id.paymentWXImg)
    ImageView paymentWXImg;
    @Bind(R.id.paymentWXChooseImg)
    ImageView paymentWXChooseImg;
    @Bind(R.id.paymentWXLL)
    RelativeLayout paymentWXLL;
    @Bind(R.id.paymentZFBImg)
    ImageView paymentZFBImg;
    @Bind(R.id.paymentZFBChooseImg)
    ImageView paymentZFBChooseImg;
    @Bind(R.id.paymentZFBLL)
    RelativeLayout paymentZFBLL;
    @Bind(R.id.paymentScrollView)
    MyScrollView paymentScrollView;
    @Bind(R.id.paymentBottomPostBT)
    Button paymentBottomPostBT;
    @Bind(R.id.paymentSRL)
    SwipeRefreshLayout paymentSRL;
    @Bind(R.id.paymentLookMoreLL)
    RelativeLayout paymentLookMoreLL;
    @Bind(R.id.paymentLookMoreLLImg)
    ImageView paymentLookMoreLLImg;

    private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private HashMap<String, Object> map;
    private FullyLinearLayoutManager linearLayoutManager;
    private ImageLoader imageLoader;
    private PaymentRecycleViewAdapter adapter;
    private boolean isShowMore = false;
    private boolean isLoading;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    private boolean isHaveNext;
    private int payFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        linearLayoutManager = new FullyLinearLayoutManager(getBaseContext());
        paymentRecycleView.setLayoutManager(linearLayoutManager);
        paymentRecycleView.setHasFixedSize(true);
        paymentRecycleView.addItemDecoration(new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        paymentScrollView.setScrollViewListener(this);
        initData();
        initListener();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        downData();
    }

    private void downData() {
        for (int i = 0; i < 1; i++) {
            map = new HashMap<>();
            map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
            map.put("name", "360奇酷手机");
            map.put("participateNumber", "1");
            map.put("money", "1.00");
            dataList.add(map);
        }
        setAdapter();
    }

    private void setAdapter() {
        if (!(dataList.size() == 0)) {
            adapter = new PaymentRecycleViewAdapter(getBaseContext(), dataList, IApplication.options, imageLoader);
            paymentRecycleView.setAdapter(adapter);
            if (dataList.size() <= 2) {
                paymentRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                paymentRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.2)));
            }
            adapter.setOnItemViewClickListener(this);
        }
    }

    @Override
    public void initListener() {
        paymentSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(PaymentActivity.this)) {
                    if (paymentScrollView.getScrollY() == 0) {
                        // 到顶部了
                        //TODO
                        paymentSRL.setEnabled(true);
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(3, 10);
                        } else {
                            Toast.makeText(getBaseContext(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                            paymentSRLDisable();
                        }
                    } else {
                        paymentSRL.setEnabled(false);
                        paymentSRL.setRefreshing(false);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                    paymentSRLDisable();
                    Toast.makeText(getBaseContext(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        paymentRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                paymentSRL.setEnabled(false);
                paymentSRL.setRefreshing(false);
                paymentSRL.setEnabled(linearLayoutManager.findFirstVisibleItemPosition() == 0);
                if (paymentScrollView.getScrollY() == 0) {
                    // 到顶部了
                    //TODO
                    paymentSRL.setEnabled(true);
                } else {
                    paymentSRL.setEnabled(false);
                    paymentSRL.setRefreshing(false);
                }

            }
        });
    }

    private void paymentSRLDisable() {
        paymentSRL.setEnabled(false);
        paymentSRL.setRefreshing(false);
    }

    @OnClick({R.id.paymentCancel, R.id.paymentSmallChargePayChooseImg, R.id.paymentSmallChargePayRL,
            R.id.paymentWXChooseImg, R.id.paymentWXLL, R.id.paymentZFBChooseImg, R.id.paymentZFBLL, R.id.paymentBottomPostBT, R.id.paymentLookMoreLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.paymentCancel:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.paymentSmallChargePayChooseImg:
                payFlag = 0;
                paymentZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentWXChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentSmallChargePayChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                break;
            case R.id.paymentSmallChargePayRL:
                payFlag = 0;
                paymentZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentWXChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentSmallChargePayChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                break;
            case R.id.paymentWXChooseImg:
                payFlag = 1;
                paymentZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentWXChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                paymentSmallChargePayChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
            case R.id.paymentWXLL:
                payFlag = 1;
                paymentZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentWXChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                paymentSmallChargePayChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
            case R.id.paymentZFBChooseImg:
                payFlag = 2;
                paymentZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                paymentWXChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentSmallChargePayChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
            case R.id.paymentZFBLL:
                payFlag = 2;
                paymentZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                paymentWXChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                paymentSmallChargePayChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
            case R.id.paymentBottomPostBT:
                switch (payFlag) {
                    case 0:
                        Toast.makeText(this, "零钱支付", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(this, "微信支付", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(this, "支付宝支付", Toast.LENGTH_SHORT).show();
                        break;
                }
                paymentBottomPostBT.setEnabled(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paymentBottomPostBT.setEnabled(true);
                    }
                }, 1500);
                break;
            case R.id.paymentLookMoreLL:
                if (!(dataList.size() <= 2)) {
                    if (!isShowMore) {
                        paymentRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        isShowMore = true;
                        paymentLookMoreLLImg.setBackgroundResource(R.mipmap.shangjiantou);
                    } else {
                        paymentRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.2)));
                        isShowMore = false;
                        paymentLookMoreLLImg.setBackgroundResource(R.mipmap.xiajiantou);
                    }
                } else {
                    if (!isShowMore) {
                        isShowMore = true;
                        paymentLookMoreLLImg.setBackgroundResource(R.mipmap.shangjiantou);
                    } else {
                        isShowMore = false;
                        paymentLookMoreLLImg.setBackgroundResource(R.mipmap.xiajiantou);
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (!(scrollView.getScrollY() == 0)) {
            paymentSRL.setEnabled(false);
        } else
            paymentSRL.setEnabled(true);
    }
}
