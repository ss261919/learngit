package vaporsome.com.vaporsome.vaporsome.commoditydetail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.custom.MyScrollView;
import vaporsome.com.vaporsome.common.custom.MyScrollView.CustomScrollViewListener;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.participaterecords.ParticipateRecords;
import vaporsome.com.vaporsome.vaporsome.photoandmesg.PhotoAndMesg;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class CommodityDetailWaitForAnnounceFragment extends Fragment implements CustomScrollViewListener {

    @Bind(R.id.commodityDetailWaitTimeRLTV)
    TextView commodityDetailWaitTimeRLTV;
    @Bind(R.id.commodityDetailWaitTimeSecond)
    TextView commodityDetailWaitTimeSecond;
    @Bind(R.id.commodityDetailWaitTimeSecondDividerPoint)
    ImageView commodityDetailWaitTimeSecondDividerPoint;
    @Bind(R.id.commodityDetailWaitTimeMinutePoint)
    ImageView commodityDetailWaitTimeMinutePoint;
    @Bind(R.id.commodityDetailWaitTimeMinute)
    TextView commodityDetailWaitTimeMinute;
    @Bind(R.id.commodityDetailWaitTimeHair)
    TextView commodityDetailWaitTimeHair;
    @Bind(R.id.commodityDetailWaitTimeRL)
    RelativeLayout commodityDetailWaitTimeRL;
    @Bind(R.id.commodityDetailWaitGuidePointLL)
    LinearLayout commodityDetailWaitGuidePointLL;
    @Bind(R.id.commodityDetailWaitViewPager)
    ViewPager commodityDetailWaitViewPager;
    @Bind(R.id.commodityDetailWaitNameTV)
    TextView commodityDetailWaitNameTV;
    @Bind(R.id.commodityDetailWaitDescription)
    TextView commodityDetailWaitDescription;
    @Bind(R.id.commodityDetailWaitMoney)
    TextView commodityDetailWaitMoney;
    @Bind(R.id.commodityDetailLimitTimesTV)
    TextView commodityDetailLimitTimesTV;
    @Bind(R.id.commodityDetailWaitPercentage)
    ProgressBar commodityDetailWaitPercentage;
    @Bind(R.id.commodityDetailWaitParticipateRecordsInsideImg)
    ImageView commodityDetailWaitParticipateRecordsInsideImg;
    @Bind(R.id.commodityDetailWaitParticipateRecords)
    RelativeLayout commodityDetailWaitParticipateRecords;
    @Bind(R.id.commodityDetailWaitPhotoAndMesgDetailTV)
    TextView commodityDetailWaitPhotoAndMesgDetailTV;
    @Bind(R.id.commodityDetailWaitPhotoAndMesgDetailImgInside)
    ImageView commodityDetailWaitPhotoAndMesgDetailImgInside;
    @Bind(R.id.commodityDetailWaitPhotoAndMesgDetail)
    RelativeLayout commodityDetailWaitPhotoAndMesgDetail;
    @Bind(R.id.commodityDetailWaitScrollView)
    MyScrollView commodityDetailWaitScrollView;
    @Bind(R.id.computeDetailWaitBottomRL)
    TextView computeDetailWaitBottomRL;
    @Bind(R.id.computeDetailWaitImgGoing)
    ImageView computeDetailWaitImgGoing;
    @Bind(R.id.commodityDetailWaitSRL)
    SwipeRefreshLayout commodityDetailWaitSRL;
    @Bind(R.id.commodityDetailWaitTimeRLTV2)
    TextView commodityDetailWaitTimeRLTV2;
    @Bind(R.id.commodityDetailWaitTimeLoadImg)
    ImageView commodityDetailWaitTimeLoadImg;
    @Bind(R.id.commodityDetailWaitTimeRL2)
    RelativeLayout commodityDetailWaitTimeRL2;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Intent intent;
    private String id = "0";
    private ProgressDialog dialog;
    private boolean isShow;

    /**
     * Acitivity要实现这个接口，这样Fragment和Activity就可以共享事件触发的资源了
     */
    public interface UpdateFragmentListener {
        public void updateFragment();
    }

    private UpdateFragmentListener myListener;
    // 图片的地址，从服务器获取
    String[] urls = new String[]{
            "http://f.hiphotos.baidu.com/image/pic/item/7aec54e736d12f2ecc3d90f84dc2d56285356869.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de308a87fc96eef01f3a297969.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/f31fbe096b63f624b88f7e8e8544ebf81b4ca369.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/11385343fbf2b2117c2dc3c3c88065380cd78e38.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec5ed8456c2d2eb9389b506b38.jpg"
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setData();
                    break;
                case 1:
                    dialog.dismiss();
                    dialog = null;
                    Toast.makeText(IApplication.applicationContext, "网络请求", Toast.LENGTH_SHORT).show();
                    if (!(runnable == null) && !(handler2 == null)) {
                        handler2.removeCallbacks(runnable);
                    }
                    if (!(runnable2 == null) && !(handler2 == null)) {
                        handler2.removeCallbacks(runnable2);
                    }
                    handler2 = null;
                    isRunable2Going = false;
                    isRunableGoing = false;
                    myListener.updateFragment();
                    break;
            }
        }
    };
    private ImageView view;
    private LayoutInflater inflater;
    private View item;
    ArrayList<View> dataList = new ArrayList<>();
    private MyAdapter adapter;
    private AnimationDrawable animationDrawable;
    private Runnable runnable;
    private int secondText;
    private int minuteText;
    private Runnable runnable2;
    private String str;
    private Handler handler2;
    private boolean isRunable2Going = true;
    private boolean isRunableGoing = true;

    private void setData() {
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
            commodityDetailWaitGuidePointLL.addView(view, lp);
            dataList.add(item);
        }
        adapter = new MyAdapter(getActivity(), dataList, urls, IApplication.options, imageLoader);
        commodityDetailWaitViewPager.setAdapter(adapter);

        commodityDetailWaitTimeMinute.setText("0");
        commodityDetailWaitTimeSecond.setText("15");
        commodityDetailWaitTimeHair.setText("24");
        commodityDetailWaitNameTV.setText("(第90751宝）苹果（Apple）iphone 6s 16G版 4G手机");
        commodityDetailWaitDescription.setText("有些礼物，能瞬间抓住人心，唯一的不同，是处处不同！（颜色随机发）");
        commodityDetailWaitMoney.setText("价值：￥" + "6088.00" + "元");
        commodityDetailLimitTimesTV.setText("限购" + 9 + "人次");
        commodityDetailWaitPercentage.setProgress(65);
        timeCountDown();
    }

    private void timeCountDown() {
        minuteText = Integer.parseInt(commodityDetailWaitTimeMinute.getText().toString());
        if (minuteText < 10) {
            str = "0" + String.valueOf(minuteText);
        } else {
            str = String.valueOf(minuteText);
        }
        if (!(commodityDetailWaitTimeSecond == null))
            commodityDetailWaitTimeMinute.setText(str);
        Log.d("--", "minuteText:" + minuteText);
        if (!(commodityDetailWaitTimeSecond == null))
            secondText = Integer.parseInt(commodityDetailWaitTimeSecond.getText().toString());
        Log.d("--", "secondText:" + secondText);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRunableGoing) {
                    if (!(secondText == 0)) {
                        secondText--;
                        if (!(commodityDetailWaitTimeSecond == null))
                            commodityDetailWaitTimeSecond.setText("" + secondText);
                        if (!(handler2 == null))
                            handler2.postDelayed(this, 1000);
                    } else {
                        commodityDetailWaitTimeSecond.setText("00");
                        if (minuteText == 0) {
                            if (isShow) {
                                dialog = ProgressDialog.show(getActivity(), "正在计算结果", null);
                                if (!(handler2 == null))
                                    handler.sendEmptyMessageDelayed(1, 1500);
                            }
                            commodityDetailWaitTimeHair.setText("00");
                            isRunable2Going = false;
                        } else {
                            minuteText--;
                            secondText = 59;
                            if (!(commodityDetailWaitTimeSecond == null))
                                commodityDetailWaitTimeSecond.setText("" + secondText);
                            if (!(commodityDetailWaitTimeMinute == null)) {
                                if (minuteText < 10) {
                                    str = "0" + String.valueOf(minuteText);
                                } else {
                                    str = String.valueOf(minuteText);
                                }
                                if (!(commodityDetailWaitTimeMinute == null))
                                    commodityDetailWaitTimeMinute.setText(str);
                            }
                            if (!(handler2 == null))
                                handler2.postDelayed(this, 1000);
                        }
                    }
                }

            }
        };
        runnable2 = new Runnable() {
            @Override
            public void run() {
                if (isRunable2Going) {
                    if (!(commodityDetailWaitTimeHair == null)) {
                        commodityDetailWaitTimeHair.setText(String.valueOf((Math.random() + 1) * 1000).substring(1, 3));
                        if (!(handler2 == null))
                            handler2.postDelayed(this, 100);
                    }
                }
            }
        };
        handler2.postDelayed(runnable, 1000);
        handler2.postDelayed(runnable2, 100);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myListener = (UpdateFragmentListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commodity_detail_wait_for_announce_fragment, null);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        options = IApplication.options;
        ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            downData();
            Log.d("--Commodity", "isVisibleToUser" + isVisibleToUser);
            isShow = true;
        } else {
            //相当于Fragment的onPause
            isShow = false;
        }
    }

    private void initListener() {
        commodityDetailWaitScrollView.setScrollViewListener(this);
        commodityDetailWaitSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!(commodityDetailWaitScrollView.getScrollY() == 0)) {
                    commodityDetailWaitSRL.setEnabled(false);
                } else if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    commodityDetailWaitSRL.setEnabled(true);

                }
            }
        });
        commodityDetailWaitViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                commodityDetailWaitSRL.requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < urls.length; i++) {
                    if (i == position) {
                        commodityDetailWaitGuidePointLL.getChildAt(i).setBackgroundResource(R.mipmap.zhifu_pressed);
                    } else
                        commodityDetailWaitGuidePointLL.getChildAt(i).setBackgroundResource(R.mipmap.zhifu_normal);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void commodityDetailWaitSRLDisable() {
        commodityDetailWaitSRL.setEnabled(false);
        commodityDetailWaitSRL.setRefreshing(false);
        commodityDetailWaitSRL.post(new Runnable() {
            @Override
            public void run() {
                commodityDetailWaitSRL.setEnabled(true);
            }
        });
    }

    private void initData() {
        inflater = LayoutInflater.from(getActivity());
        animationDrawable = (AnimationDrawable) computeDetailWaitImgGoing.getDrawable();
        animationDrawable.start();
        isRunable2Going = true;
        isRunableGoing = true;
        handler2 = new Handler();
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

    @OnClick({R.id.commodityDetailWaitParticipateRecordsInsideImg, R.id.commodityDetailWaitParticipateRecords, R.id.commodityDetailWaitPhotoAndMesgDetailImgInside, R.id.commodityDetailWaitPhotoAndMesgDetail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commodityDetailWaitParticipateRecordsInsideImg:
                intent = new Intent(getActivity(), ParticipateRecords.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailWaitParticipateRecords:
                intent = new Intent(getActivity(), ParticipateRecords.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailWaitPhotoAndMesgDetailImgInside:
                intent = new Intent(getActivity(), PhotoAndMesg.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailWaitPhotoAndMesgDetail:
                intent = new Intent(getActivity(), PhotoAndMesg.class);
                startActivity(intent, id);
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
            commodityDetailWaitSRL.setEnabled(false);
        } else
            commodityDetailWaitSRL.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("--Commodity", "onResume" + "waitforAnnounce" + "downData()");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!(runnable == null) && !(handler2 == null))
            handler2.removeCallbacks(runnable);
        if (!(runnable2 == null) && !(handler2 == null))
            handler2.removeCallbacks(runnable2);
        handler2 = null;
        isRunable2Going = false;
        isRunableGoing = false;
        Log.d("--Commodity", "onHiddenChanged " + "waitforAnnounce");
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
