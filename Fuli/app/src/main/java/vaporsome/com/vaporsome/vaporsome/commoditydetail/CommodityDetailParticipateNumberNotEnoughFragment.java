package vaporsome.com.vaporsome.vaporsome.commoditydetail;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.Touch;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.custom.MyScrollView;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;
import vaporsome.com.vaporsome.vaporsome.participaterecords.ParticipateRecords;
import vaporsome.com.vaporsome.vaporsome.photoandmesg.PhotoAndMesg;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class CommodityDetailParticipateNumberNotEnoughFragment extends Fragment implements MyScrollView.CustomScrollViewListener {


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setData();
                    break;
            }
        }
    };
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughViewPager)
    ViewPager commodityDetailParticipateNumberNotEnoughViewPager;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughNameTV)
    TextView commodityDetailParticipateNumberNotEnoughNameTV;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughDescription)
    TextView commodityDetailParticipateNumberNotEnoughDescription;
    @Bind(R.id.commodityDetailMoney)
    TextView commodityDetailMoney;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughLimitTimesTV)
    TextView commodityDetailParticipateNumberNotEnoughLimitTimesTV;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughPercentage)
    ProgressBar commodityDetailParticipateNumberNotEnoughPercentage;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughParticipatedPersonNumber)
    TextView commodityDetailParticipateNumberNotEnoughParticipatedPersonNumber;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughParticipatedPersonNumberTV)
    TextView commodityDetailParticipateNumberNotEnoughParticipatedPersonNumberTV;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughParticipatedDemandPersonNumber)
    TextView commodityDetailParticipateNumberNotEnoughParticipatedDemandPersonNumber;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughSurplusPersonNumber)
    TextView commodityDetailParticipateNumberNotEnoughSurplusPersonNumber;
    @Bind(R.id.commodityDetailParticipatedRL)
    RelativeLayout commodityDetailParticipatedRL;
    @Bind(R.id.commodityDetailDivider)
    View commodityDetailDivider;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughParticipateRecordsInsideImg)
    ImageView commodityDetailParticipateNumberNotEnoughParticipateRecordsInsideImg;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughParticipateRecords)
    RelativeLayout commodityDetailParticipateNumberNotEnoughParticipateRecords;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetailTV)
    TextView commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetailTV;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetailImgInside)
    ImageView commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetailImgInside;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetail)
    RelativeLayout commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetail;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughScrollView)
    MyScrollView commodityDetailParticipateNumberNotEnoughScrollView;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughImmediatelyDuoBao)
    TextView commodityDetailParticipateNumberNotEnoughImmediatelyDuoBao;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughAddToShoppingCart)
    TextView commodityDetailParticipateNumberNotEnoughAddToShoppingCart;
    @Bind(R.id.commodityDetailSRL)
    SwipeRefreshLayout commodityDetailSRL;
    @Bind(R.id.commodityDetailParticipateNumberNotEnoughGuidePointLL)
    LinearLayout commodityDetailParticipateNumberNotEnoughGuidePointLL;
    private String price = "5188.00";
    // 图片的地址，从服务器获取
    String[] urls = new String[]{
            "http://f.hiphotos.baidu.com/image/pic/item/7aec54e736d12f2ecc3d90f84dc2d56285356869.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de308a87fc96eef01f3a297969.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/f31fbe096b63f624b88f7e8e8544ebf81b4ca369.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/11385343fbf2b2117c2dc3c3c88065380cd78e38.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec5ed8456c2d2eb9389b506b38.jpg"
    };
    private LayoutInflater inflater;
    private View item;
    ArrayList<View> dataList = new ArrayList<>();
    ArrayList<ImageView> pagerTitle = new ArrayList<>();
    private MyAdapter adapter;
    private String id = "0";
    private ImageView image;
    private CommodityDetailtPartivcipateNumberNotEnoughRecycleViewAdapter recycleViewAdapter;
    private boolean[] booleanArray;
    private LinearLayoutManager linearLayoutManager;
    private ImageView view;
    private Intent intent;

    private void setData() {
        booleanArray = new boolean[urls.length];
        commodityDetailMoney.setText("价值：￥" + price + "元");
        commodityDetailParticipateNumberNotEnoughNameTV.setText("(第90751宝）苹果（Apple）iphone 6s 16G版 4G手机");
        commodityDetailParticipateNumberNotEnoughDescription.setText("有些礼物，能瞬间抓住人心，唯一的不同，是处处不同！（颜色随机发）");
        commodityDetailParticipateNumberNotEnoughLimitTimesTV.setText("限购8人次");
        commodityDetailParticipateNumberNotEnoughParticipatedPersonNumber.setText("1300");
        commodityDetailParticipateNumberNotEnoughParticipatedDemandPersonNumber.setText("5800");
        commodityDetailParticipateNumberNotEnoughSurplusPersonNumber.setText("4300");
        commodityDetailParticipateNumberNotEnoughPercentage.setProgress(56);
        /**
         * 创建多个item （每一条viewPager都是一个item）
         * 从服务器获取完数据（如文章标题、url地址） 后，再设置适配器
         */
        for (int i = 0; i < urls.length; i++) {
            view = new ImageView(getActivity());
            if (i == 0) {
                view.setBackgroundResource(R.mipmap.zhifu_pressed);
            } else {
                view.setBackgroundResource(R.mipmap.zhifu_normal);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    16, 16);
            lp.setMargins(4, 0, 4, 0);
            item = inflater.inflate(R.layout.commodity_viewpager_item, null);
            commodityDetailParticipateNumberNotEnoughGuidePointLL.addView(view, lp);
            dataList.add(item);
        }
        adapter = new MyAdapter(getActivity(), dataList, urls, IApplication.options, imageLoader);
        commodityDetailParticipateNumberNotEnoughViewPager.setAdapter(adapter);
        getActivity().onWindowFocusChanged(true);
        commodityDetailParticipateNumberNotEnoughViewPager.setPageMargin(10);
        commodityDetailParticipateNumberNotEnoughViewPager.requestFocus();
    }

    private ImageLoader imageLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commodity_detail_participate_number_not_enough_fragment, null);
        ButterKnife.bind(this, view);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        initView();
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        commodityDetailParticipateNumberNotEnoughScrollView.setScrollViewListener(this);
        commodityDetailSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!(commodityDetailParticipateNumberNotEnoughScrollView.getScrollY() == 0)) {
                    commodityDetailSRL.setEnabled(false);
                } else if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    commodityDetailSRL.setEnabled(true);

                }
            }
        });
        commodityDetailParticipateNumberNotEnoughViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                commodityDetailSRL.setEnabled(false);
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < urls.length; i++) {
                    if (i == position) {
                        commodityDetailParticipateNumberNotEnoughGuidePointLL.getChildAt(i).setBackgroundResource(R.mipmap.zhifu_pressed);
                    } else
                        commodityDetailParticipateNumberNotEnoughGuidePointLL.getChildAt(i).setBackgroundResource(R.mipmap.zhifu_normal);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void commodityDetailSRLDisable() {
        commodityDetailSRL.setEnabled(false);
        commodityDetailSRL.setRefreshing(false);
        commodityDetailSRL.post(new Runnable() {
            @Override
            public void run() {
                commodityDetailSRL.setEnabled(true);
            }
        });
    }

    private void initView() {

    }

    private void initData() {
        inflater = LayoutInflater.from(getActivity());
        downData();
    }

    private void downData() {
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.commodityDetailParticipateNumberNotEnoughParticipateRecordsInsideImg, R.id.commodityDetailParticipateNumberNotEnoughParticipateRecords,
            R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetailImgInside, R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetail,
            R.id.commodityDetailParticipateNumberNotEnoughImmediatelyDuoBao, R.id.commodityDetailParticipateNumberNotEnoughAddToShoppingCart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commodityDetailParticipateNumberNotEnoughParticipateRecordsInsideImg:
                intent = new Intent(getActivity(), ParticipateRecords.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailParticipateNumberNotEnoughParticipateRecords:
                intent = new Intent(getActivity(), ParticipateRecords.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetailImgInside:
                intent = new Intent(getActivity(), PhotoAndMesg.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailParticipateNumberNotEnoughPhotoAndMesgDetail:
                intent = new Intent(getActivity(), PhotoAndMesg.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailParticipateNumberNotEnoughImmediatelyDuoBao:
                Toast.makeText(getActivity(), "参加夺宝" + id, Toast.LENGTH_SHORT).show();
                break;
            case R.id.commodityDetailParticipateNumberNotEnoughAddToShoppingCart:
                Toast.makeText(getActivity(), "加入购物车" + id, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void startActivity(Intent intent, String id) {
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (!(scrollView.getScrollY() == 0)) {
            commodityDetailSRL.setEnabled(false);
        } else
            commodityDetailSRL.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("--Commodity", "onResume" + "notEnough");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("--Commodity", "notEnough");
    }
}
