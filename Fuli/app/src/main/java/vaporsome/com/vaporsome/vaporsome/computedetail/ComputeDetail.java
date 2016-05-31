package vaporsome.com.vaporsome.vaporsome.computedetail;

import android.app.AlertDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.common.custom.MyScrollView;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

public class ComputeDetail extends BaseActivity implements MyScrollView.CustomScrollViewListener {

    @Bind(R.id.computeDetailCancel)
    LinearLayout computeDetailCancel;
    @Bind(R.id.computeDetailWinNumberTV)
    TextView computeDetailWinNumberTV;
    @Bind(R.id.computeDetailWinNumberLL)
    LinearLayout computeDetailWinNumberLL;
    @Bind(R.id.computeDetailWinNumberEqualTV)
    TextView computeDetailWinNumberEqualTV;
    @Bind(R.id.computeDetailCommodityTV)
    TextView computeDetailCommodityTV;
    @Bind(R.id.computeDetailCommodityLL)
    LinearLayout computeDetailCommodityLL;
    @Bind(R.id.computeDetailRemainder)
    TextView computeDetailRemainder;
    @Bind(R.id.computeDetailTimeAddingValuesTV)
    TextView computeDetailTimeAddingValuesTV;
    @Bind(R.id.computeDetailTimeAddingValuesLL)
    LinearLayout computeDetailTimeAddingValuesLL;
    @Bind(R.id.computeDetailRightParenthesisTV)
    TextView computeDetailRightParenthesisTV;
    @Bind(R.id.computeDetailFixedNumberTV)
    TextView computeDetailFixedNumberTV;
    @Bind(R.id.computeDetailFixedNumberLL)
    LinearLayout computeDetailFixedNumberLL;
    @Bind(R.id.computeDetailComputeRL)
    RelativeLayout computeDetailComputeRL;
    @Bind(R.id.computeDetailTheLastBoughtTimeTV)
    TextView computeDetailTheLastBoughtTimeTV;
    @Bind(R.id.computeDetailTheLastBoughtTime)
    TextView computeDetailTheLastBoughtTime;
    @Bind(R.id.computeDetailComputeWayLL)
    LinearLayout computeDetailComputeWayLL;
    @Bind(R.id.computeDetailRecycleView)
    RecyclerView computeDetailRecycleView;
    @Bind(R.id.computeDetailLookMoreTV)
    TextView computeDetailLookMoreTV;
    @Bind(R.id.computeDetailLookMoreLLImg)
    ImageView computeDetailLookMoreLLImg;
    @Bind(R.id.computeDetailLookMoreLL)
    LinearLayout computeDetailLookMoreLL;
    @Bind(R.id.paymentScrollView)
    MyScrollView paymentScrollView;
    @Bind(R.id.computeDetailBottomRL)
    TextView computeDetailBottomRL;
    @Bind(R.id.computeDetailImgGoing)
    ImageView computeDetailImgGoing;
    @Bind(R.id.computeDetailSRL)
    SwipeRefreshLayout computeDetailSRL;
    private HashMap<String, Object> map;
    private ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
    private FullyLinearLayoutManager linearLayoutManager;
    private ImageLoader imageLoader;
    private ComputeDetailRecycleViewAdapter adapter;
    private boolean isShowMore = false;
    private boolean isLoading;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setAdapter();
                    break;
                case 1:
                    setAdapter();
                    break;
                case 2:
                    setAdapter();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compute_detail);
        ButterKnife.bind(this);
        linearLayoutManager = new FullyLinearLayoutManager(getBaseContext());
        computeDetailRecycleView.setLayoutManager(linearLayoutManager);
        computeDetailRecycleView.setHasFixedSize(true);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        initView();
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

    private void setAdapter() {
        if (!(dataList.size() == 0)) {
            adapter = new ComputeDetailRecycleViewAdapter(getBaseContext(), dataList, IApplication.options, imageLoader);
            computeDetailRecycleView.setAdapter(adapter);
            computeDetailRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.8)));
        }
        AnimationDrawable anim = (AnimationDrawable) computeDetailImgGoing.getBackground();
        anim.start();
    }

    private void downData() {
        for (int i = 0; i < 15; i++) {
            map = new HashMap<>();
            map.put("dayTime", "2016-05-07");
            map.put("secondTime", "18:36:16:216");
            map.put("number", "10004856");
            map.put("name", "自强不息八方");
            dataList.add(map);
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    public void initListener() {
        paymentScrollView.setScrollViewListener(this);
    }

    @OnClick({R.id.computeDetailCancel, R.id.computeDetailComputeWayLL, R.id.computeDetailLookMoreTV, R.id.computeDetailLookMoreLLImg, R.id.computeDetailLookMoreLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.computeDetailCancel:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.computeDetailComputeWayLL:
                showDialog();
                break;
            case R.id.computeDetailLookMoreTV:
                lookMoreOrFold();
                break;
            case R.id.computeDetailLookMoreLLImg:
                lookMoreOrFold();
                break;
            case R.id.computeDetailLookMoreLL:
                lookMoreOrFold();
                break;
        }
    }

    private void showDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.compute_detail_dialog);
        LinearLayout okTV = (LinearLayout) window.findViewById(R.id.computeDetailDialogCancelLL);
        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void lookMoreOrFold() {
        if (!isShowMore) {
            computeDetailRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            isShowMore = true;
            computeDetailLookMoreLLImg.setBackgroundResource(R.mipmap.shangjiantou);
            computeDetailLookMoreTV.setText("收起");
        } else {
            computeDetailRecycleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.8)));
            isShowMore = false;
            computeDetailLookMoreLLImg.setBackgroundResource(R.mipmap.xiajiantou);
            computeDetailLookMoreTV.setText("查看所有");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (!(paymentScrollView.getScrollY() == 0)) {
            computeDetailSRL.setEnabled(false);
        } else computeDetailSRL.setEnabled(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        paymentScrollView.scrollBy(0, 0);
    }
}
