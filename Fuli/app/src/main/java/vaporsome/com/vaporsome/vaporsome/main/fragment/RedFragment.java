package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.AdsDetailActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.main.adapter.RedFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.popwindow.PopWindow;
import vaporsome.com.vaporsome.vaporsome.outadsactivity.OutAdsActivity;
import vaporsome.com.vaporsome.vaporsome.playads.PlayerAdsActivity;
import vaporsome.com.vaporsome.vaporsome.playads.PlayerAdsDialogActivity;
import vaporsome.com.vaporsome.vaporsome.redresultactivity.RedResultActivity;

/**
 * Created by Administrator on 2016/3/21.
 */
public class RedFragment extends Fragment implements RedFragmentAdapter.onItemViewClickListener {
    private final int RED_OPEN_BACK = 1;
    RecyclerView recyclerView;
    Map<String, Object> dataList = new HashMap<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    RedFragmentAdapter redFragmentAdapter;
    PopupWindow popWindow;
    private ImageView popWindowCancle;
    private TextView popWindowShare;
    private int width;
    private int height;
    private RelativeLayout popWindowRedRalativeLayout;
    private LinearLayout popWindowCompanyRedNumberRanking;
    private Intent intent;
    private Animation anim;
    private ImageView redLoadImg;
    private Map<String, String> params;
    private int page = 1;
    private Map<String, List<String>> dataMap;

    private String bag_id;
    private boolean isFirstGoToLogin = true;
    private int lastVisibleItemPosition;
    private int setTagTime = 0;
    private boolean setTagTrue = false;

    private Set<String> tagList = new HashSet<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (!dataList.get("list").toString().equals("[]")) {
                        if (page == 1) {
                            redInfoList.clear();
                            isRedListIdOpen.clear();
                            redAdsImgList.clear();
                            redListId.clear();
                            redListCompanyName.clear();
                            redListCompanyLogo.clear();
                            redListRedMoney.clear();
                            redListRedCount.clear();
                            redListComment.clear();
                            redListVideoUrl.clear();
                            redAdsImgUrlList.clear();
                            redAdsImgTypeList.clear();
                            redAdsImgTypePhoneList.clear();
                            redAdsImgLinkUrlList.clear();
                            red_fragment_SRWDisable();
                        } else {
                            red_fragment_SRW.setEnabled(false);
                        }
                        setDataInfo();
                        redLoadImg.setVisibility(View.GONE);
                        redLoadImg.clearAnimation();
                        redLoadTV.setVisibility(View.GONE);
                        lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                    } else {
                        if (page == 1) {
                            red_fragment_SRWDisable();
                            redLoadImg.setVisibility(View.GONE);
                            redLoadImg.clearAnimation();
                            isHaveNext = true;
                            redLoadTV.setVisibility(View.VISIBLE);
                            redLoadTV.setText("没有数据！");
                        } else {
                            red_fragment_SRW.setEnabled(false);
                            isHaveNext = false;
                        }
                    }
                    isLoading = false;

//                    if (lastVisibleItemPosition + 1 == redFragmentAdapter.getItemCount()) {
//                        red_fragment_SRW.setEnabled(false);
//                        red_fragment_SRW.setRefreshing(false);
//                    } else {
//                        red_fragment_SRWDisable();
//
//                        redLoadImg.setVisibility(View.GONE);
//                    }

                    break;
                case 1:
                    if (page == 1) {
                        openToast("网络有问题，请检查无误后重试！");
                        redLoadImg.clearAnimation();
                        redLoadImg.setVisibility(View.GONE);
                        redLoadTV.setVisibility(View.GONE);
                    }
                    red_fragment_SRWDisable();
                    break;
                case 2:
                    bundle = msg.getData();
                    try {
                        switch (Integer.parseInt(JsonUtils.getMapStr(bundle.get("data").toString()).get("code"))) {
                            //code 200 是合法操作，还有剩余红包
                            case 200:
                                isRedListIdOpen.set(redListId.indexOf(bag_id), false);
                                intent = null;
                                intent = new Intent(getActivity(), PlayerAdsDialogActivity.class);
                                Log.d("--RedFragment", video_url);
                                bundle = new Bundle();
                                bundle.putString("video_url", video_url);
                                bundle.putString("bag-id", bag_id);
                                bundle.putString("code", "01");
                                bundle.putString("companyLogo", companyLogo);
                                bundle.putString("companyName", companyName);
                                bundle.putString("companyComment", companyComment);
                                Log.d("--bag_id", bag_id);
                                Log.d("--video_url", video_url);
                                Log.d("--code", "01");
                                Log.d("--companyLogo", companyLogo);
                                Log.d("--companyName", companyName);
                                Log.d("--companyComment", companyComment);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, RED_OPEN_BACK);
                                getActivity().overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out_disappear);
                                break;
                            //code 201 已领取过该红包
                            case 201:
                                isRedListIdOpen.set(redListId.indexOf(bag_id), false);
                                intent = null;
                                intent = new Intent(getActivity(), RedResultActivity.class);
                                bundle = new Bundle();
                                bundle.putString("bag-id", bag_id);
                                bundle.putString("code", "01");
                                bundle.putString("companyLogo", companyLogo);
                                bundle.putString("companyName", companyName);
                                bundle.putString("companyComment", companyComment);
                                Log.d("--bag-id", bag_id);
                                Log.d("--code", "01");
                                Log.d("--companyLogo", companyLogo);
                                Log.d("--companyName", companyName);
                                Log.d("--companyComment", companyComment);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                                break;
                            //code 202 红包已过期
                            case 202:
                                isRedListIdOpen.set(redListId.indexOf(bag_id), false);
                                initPopWindow(JsonUtils.getMapStr(bundle.get("data").toString()).get("message").toString());
                                if (!popWindow.isShowing()) {
                                    PopWindow.showPop(popWindow, getActivity(), red_fragment_SRW, 10, 10, 0);
                                }
                                break;
                            //code 203 红包已派完
                            case 203:
                                isRedListIdOpen.set(redListId.indexOf(bag_id), false);
                                initPopWindow(JsonUtils.getMapStr(bundle.get("data").toString()).get("message").toString());
                                if (!popWindow.isShowing())
                                    PopWindow.showPop(popWindow, getActivity(), getView(), 10, 10, 0);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    isRedListIdOpen.set(redListId.indexOf(bag_id), false);
                    Log.d("--RedFragment", bag_id);
                    openToast("网络有问题，请检查无误后重试！");
                    break;
                case 4:
                    page = 1;
//                    red_fragment_SRW.setRefreshing(true);
                    isLoading = true;
                    isHaveNext = true;
                    firstToast = true;
                    redLoadTV.setText("");
                    redLoadTV.setVisibility(View.GONE);
                    loadData();
                    break;
                case 5:
                    page++;
                    loadData();
                    isLoading = true;
                    break;
                case 6:
                    redLoadImg.clearAnimation();
                    redLoadImg.setVisibility(View.GONE);
                    isLoading = false;
                    if (page == 1) {
                        redLoadTV.setVisibility(View.VISIBLE);
                        redLoadTV.setText("网络错误，请刷新重试！");
                        red_fragment_SRW.setEnabled(false);
                    } else {
                        redLoadTV.setVisibility(View.GONE);
                        red_fragment_SRWDisable();
                    }
                    break;
                case 7:
                    isLoading = false;
                    openToast("网络有问题，请检查无误后重试！");
                    break;
                case 8:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        PreferencesUtils.putBoolean(getActivity(), "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
                case 9:
                    isRedListIdOpen.set(redListId.indexOf(bag_id), false);
                    break;
                case 10:
                    if (!(province_id.length() == 0) && !province_id.equals("0") && !(city_id.length() == 0) && !city_id.equals("0")
                            && !(district_id.length() == 0) && !district_id.equals("0")) {
                        tagList.add(province_id);
                        tagList.add(city_id);
                        tagList.add(district_id);
                        if (!setTagTrue) {
                            if (setTagTime < 2) {
                                JPushInterface.setTags(getActivity(), tagList, new TagAliasCallback() {
                                            @Override
                                            public void gotResult(int i, String s, Set<String> set) {
                                                Log.d("--ChangeUserInfoInside", " i " + i + " ｓ " + s + set.toString());
                                                if (i == 0) {//表示调用成功
                                                    Log.d("--ChangeUserInfoInside", "调用成功");
                                                    setTagTime = 2;
                                                    setTagTrue = true;
                                                } else if (i == 6002) {//设置超时
                                                    setTagTime++;
                                                    setTagTrue = false;
                                                } else {
                                                    setTagTime++;
                                                    setTagTrue = false;
                                                }
                                            }
                                        }
                                );
                            }
                        }
                    }
                    mLocationClient.stopLocation();//停止定位
                    mLocationClient.onDestroy();//销毁定位客户端。}
                    break;
                case 11:
                    mLocationClient.stopLocation();//停止定位
                    mLocationClient.onDestroy();//销毁定位客户端。}
                    break;
            }
        }
    };
    private TextView redLoadTV;


    private double longitude;
    private double latitude;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    aMapLocation.getLatitude();//获取纬度
                    aMapLocation.getLongitude();//获取经度
                    aMapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    df.format(date);//定位时间
                    aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。

                    //经度
                    longitude = aMapLocation.getLongitude();
                    //维度
                    latitude = aMapLocation.getLatitude();
                    params.clear();
                    params.put("longitude", String.valueOf(longitude));
                    params.put("latitude", String.valueOf(latitude));
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("--AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private String province_id;
    private String city_id;
    private String district_id;

    private void red_fragment_SRWDisable() {
        red_fragment_SRW.setRefreshing(false);
        red_fragment_SRW.setEnabled(false);
        red_fragment_SRW.post(new Runnable() {
            @Override
            public void run() {
                red_fragment_SRW.setEnabled(true);
            }
        });
    }

    private String video_url;
    private String companyLogo;
    private String companyName;
    private String companyComment;
    private ImageView initPopWindowCompanyIcon;
    private TextView initPopWindowComment;
    private LinearLayout initPopWindowCancleLL;
    private TextView initPopWindowTV;
    private SwipeRefreshLayout red_fragment_SRW;
    private boolean isLoading = false;
    private LinearLayoutManager linearLayoutManager;
    private List<String> redAdsImgTypeList;
    private List<String> redAdsImgTypePhoneList;
    private List<String> redAdsImgLinkUrlList;
    private boolean firstToast = true;

    private void openToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    private List<String> redListId;
    private List<Boolean> isRedListIdOpen = new ArrayList<>();
    private List<Map<String, Object>> redInfoList = new ArrayList<>();
    private List<String> redListCompanyName;
    private List<String> redListCompanyLogo;
    private List<String> redListRedMoney;
    private List<String> redListRedCount;
    private List<String> redListComment;
    private List<String> redListVideoUrl;
    private List<Map<String, Object>> redAdsImgList = new ArrayList<>();
    private List<String> redAdsImgUrlList;

    private void setDataInfo() {
        if (!dataList.get("list").toString().equals("[]")) {
            redInfoList.addAll((Collection<? extends Map<String, Object>>) dataList.get("list"));
            if (!(page == 1)) {
                for (int i = redListId.size(); i < redInfoList.size(); i++) {
                    isRedListIdOpen.add(false);
                    redListId.add(redInfoList.get(i).get("id").toString());
                    redListCompanyName.add(redInfoList.get(i).get("company_name").toString());
                    redListCompanyLogo.add(redInfoList.get(i).get("company_logo").toString());
                    redListRedMoney.add(redInfoList.get(i).get("money").toString());
                    redListRedCount.add(redInfoList.get(i).get("total").toString());
                    redListComment.add(redInfoList.get(i).get("comment").toString());
                    redListVideoUrl.add(redInfoList.get(i).get("video_url").toString());
                }
            } else {
                for (Map<String, Object> map : redInfoList
                        ) {
                    isRedListIdOpen.add(false);
                    redListId.add(map.get("id").toString());
                    redListCompanyName.add(map.get("company_name").toString());
                    redListCompanyLogo.add(map.get("company_logo").toString());
                    redListRedMoney.add(map.get("money").toString());
                    redListRedCount.add(map.get("total").toString());
                    redListComment.add(map.get("comment").toString());
                    redListVideoUrl.add(map.get("video_url").toString());
                }
            }
            Log.d("--redListId", "redListId:" + redListId);
            Log.d("--company_logo", "company_logo:" + redListCompanyLogo);
            Log.d("--redListCompanyName", "redListCompanyName:" + redListCompanyName);
            dataMap.put("money", redListRedMoney);
            dataMap.put("company_logo", redListCompanyLogo);
            dataMap.put("company_name", redListCompanyName);
            dataMap.put("comment", redListComment);
        }
        if (!dataList.get("banana").toString().equals("[]")) {
            redAdsImgList = (List<Map<String, Object>>) dataList.get("banana");
            for (Map<String, Object> map : redAdsImgList
                    ) {
                if (width <= 768) {
                    redAdsImgUrlList.add(map.get("file_768_url").toString());
                } else
                    redAdsImgUrlList.add(map.get("file_1440_url").toString());

                redAdsImgTypeList.add(map.get("type").toString());
                redAdsImgTypePhoneList.add(map.get("android").toString());
                redAdsImgLinkUrlList.add(map.get("link_url").toString());
            }
            dataMap.put("redAdsImgUrlList", redAdsImgUrlList);
        }
        if (TextUtils.equals(String.valueOf(page), "1")) {
            redFragmentAdapter = new RedFragmentAdapter(getActivity(), dataMap, options, imageLoader, width, height);
            recyclerView.setAdapter(redFragmentAdapter);
            //实现item和item子控件监听回调
            redFragmentAdapter.setOnItemViewClickListener(this);
        } else {
            redFragmentAdapter.notifyDataSetChanged();
        }
        if (dataList.get("list").toString().equals("[]") && dataList.get("banana").toString().equals("[]")) {
            isHaveNext = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.redRecycleView);
        red_fragment_SRW = (SwipeRefreshLayout) view.findViewById(R.id.red_fragment_SRW);
        redLoadImg = (ImageView) view.findViewById(R.id.redLoadImg);
        redLoadTV = (TextView) view.findViewById(R.id.redLoadTV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        options = IApplication.options;
        initView();
        initDate();
        initListener();
        gaoDe();
        location();
        return view;
    }

    private void location() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final LocationThread thread = new LocationThread();
                thread.start();
            }
        }, 8000);
    }

    private void gaoDe() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式,Hight_Accuracy高精度
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(500);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(recyclerView == null))
            recyclerView.requestFocus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
// TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {// 不在最前端界面显示
        } else {// 重新显示到最前端中
            if (!(recyclerView == null))
                recyclerView.requestFocus();
        }
    }

    private void initListener() {
        red_fragment_SRW.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    if (topRowVerticalPosition >= 0) {
                        red_fragment_SRW.setEnabled(true);
                        if (redLoadImg.getVisibility() == View.VISIBLE) {
                            redLoadImg.clearAnimation();
                            redLoadImg.setVisibility(View.GONE);
                        }
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(4, 10);
                        } else Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    } else red_fragment_SRW.setEnabled(false);
                } else {
                    red_fragment_SRW.setEnabled(false);
                    red_fragment_SRW.setRefreshing(false);
                    red_fragment_SRW.post(new Runnable() {
                        @Override
                        public void run() {
                            red_fragment_SRW.setEnabled(true);
                        }
                    });
                    openToast("网络还没准备好，请检查后重试！");
                }
            }
        });
        //此处我们是对recyclerview添加scrollListener ,监听滑倒最后一个可见的条目的时候，刷新加载数据
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case 1:
                        //正在滑动
                        imageLoader.pause();
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //滑动停止
                        imageLoader.resume();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (red_fragment_SRW.isRefreshing())
                    red_fragment_SRW.setRefreshing(false);
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                red_fragment_SRW.setEnabled(topRowVerticalPosition >= 0);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == redFragmentAdapter.getItemCount()) {
                    if (!isLoading) {
                        if (isHaveNext) {
                            message = new Message();
                            message.what = 5;
                            handler.sendMessage(message);
                        } else {
                            if (firstToast) {
                                firstToast = false;
                            }
                        }
                    }
                }
            }
        });
    }

    private void initDate() {
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.load);
        anim.start();
        redLoadImg.setAnimation(anim);
        redLoadImg.setVisibility(View.VISIBLE);
        loadData();
    }

    private void loadData() {
        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
            GetRedListDataThread thread = new GetRedListDataThread();
            thread.start();
            isLoading = true;
        } else {
            openToast("网络有问题，请检查无误后重试！");
            redLoadImg.setVisibility(View.GONE);
            message = new Message();
            message.what = 7;
            handler.sendMessage(message);
        }
    }

    private void initView() {
        dataMap = new HashMap<>();
        redListId = new ArrayList<>();
        redListCompanyName = new ArrayList<>();
        redListCompanyLogo = new ArrayList<>();
        redListRedMoney = new ArrayList<>();
        redListRedCount = new ArrayList<>();
        redListComment = new ArrayList<>();
        redAdsImgUrlList = new ArrayList<>();
        redAdsImgTypeList = new ArrayList<>();
        redAdsImgTypePhoneList = new ArrayList<>();
        redListVideoUrl = new ArrayList<>();
        redAdsImgLinkUrlList = new ArrayList<>();
        redInfoList = new ArrayList<>();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (redAdsImgUrlList.size() <= redListCompanyName.size()) {
            if (position < (2 * redAdsImgUrlList.size()) && !(position % 2 == 0)) {
                switch (redAdsImgTypeList.get(position - ((position + 1) / 2))) {
                    case "10":
                        goToAPPMarket(redAdsImgLinkUrlList.get(position));
                        break;
                    case "20":
                        intent = new Intent(getActivity(), OutAdsActivity.class);
                        intent.putExtra("link_url", redAdsImgLinkUrlList.get(position - ((position + 1) / 2)));
                        startActivity(intent);
                        break;
                    case "30":
                        intent = new Intent(getActivity(), AdsDetailActivity.class);
                        intent.putExtra("link_url", redAdsImgLinkUrlList.get(position - ((position + 1) / 2)));
                        startActivity(intent);
                        break;
                }
            } else if (position < (2 * redAdsImgUrlList.size()) && position % 2 == 0) {
                if (position == 0) {
                    video_url = redListVideoUrl.get(position);
                    bag_id = redListId.get(position);
                    companyLogo = redListCompanyLogo.get(position);
                    companyName = redListCompanyName.get(position);
                    companyComment = redListComment.get(position);
                    if (!isRedListIdOpen.get(position)) {
                        isRedListIdOpen.set(position, true);
                        startIntent(bag_id);
                    }
                } else {
                    video_url = redListVideoUrl.get(position - ((position + 1) / 2));
                    bag_id = redListId.get(position - ((position + 1) / 2));
                    companyLogo = redListCompanyLogo.get(position - ((position + 1) / 2));
                    companyName = redListCompanyName.get(position - ((position + 1) / 2));
                    companyComment = redListComment.get(position - ((position + 1) / 2));
                    if (!isRedListIdOpen.get(position - ((position + 1) / 2))) {
                        isRedListIdOpen.set(position - ((position + 1) / 2), true);
                        startIntent(bag_id);
                    }
                }
            } else {
                video_url = redListVideoUrl.get(position - redAdsImgUrlList.size());
                bag_id = redListId.get(position - redAdsImgUrlList.size());
                companyLogo = redListCompanyLogo.get(position - redAdsImgUrlList.size());
                companyName = redListCompanyName.get(position - redAdsImgUrlList.size());
                companyComment = redListComment.get(position - redAdsImgUrlList.size());
                if (!isRedListIdOpen.get(position - redAdsImgUrlList.size())) {
                    isRedListIdOpen.set(position - redAdsImgUrlList.size(), true);
                    startIntent(bag_id);
                }
            }
        } else {
            if (position < 2 * redListCompanyName.size()) {
                if (!(position % 2 == 0)) {
                    switch (redAdsImgTypeList.get(position - ((position + 1) / 2))) {
                        case "10":
                            goToAPPMarket(redAdsImgLinkUrlList.get(position));
                            break;
                        case "20":
                            intent = new Intent(getActivity(), OutAdsActivity.class);
                            intent.putExtra("link_url", redAdsImgLinkUrlList.get(position - ((position + 1) / 2)));
                            startActivity(intent);
                            break;
                        case "30":
                            intent = new Intent(getActivity(), AdsDetailActivity.class);
                            intent.putExtra("link_url", redAdsImgLinkUrlList.get(position - ((position + 1) / 2)));
                            startActivity(intent);
                            break;
                    }
                } else {
                    video_url = redListVideoUrl.get(position - ((position + 1) / 2));
                    bag_id = redListId.get(position - ((position + 1) / 2));
                    companyLogo = redListCompanyLogo.get(position - ((position + 1) / 2));
                    companyName = redListCompanyName.get(position - ((position + 1) / 2));
                    companyComment = redListComment.get(position - ((position + 1) / 2));
                    if (!isRedListIdOpen.get(position - ((position + 1) / 2))) {
                        isRedListIdOpen.set(position - ((position + 1) / 2), true);
                        startIntent(bag_id);
                    }
                }
            } else {
                switch (redAdsImgTypeList.get(position - redListCompanyName.size())) {
                    case "10":
                        goToAPPMarket(redAdsImgLinkUrlList.get(position));
                        break;
                    case "20":
                        intent = new Intent(getActivity(), OutAdsActivity.class);
                        intent.putExtra("link_url", redAdsImgLinkUrlList.get(position - redListCompanyName.size()));
                        startActivity(intent);
                        break;
                    case "30":
                        intent = new Intent(getActivity(), AdsDetailActivity.class);
                        intent.putExtra("link_url", redAdsImgLinkUrlList.get(position - redListCompanyName.size()));
                        startActivity(intent);
                        break;
                }
            }
        }
    }

    private void goToAPPMarket(String string) {
        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + string)); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else { //没有应用市场，我们通过浏览器跳转到应用宝
            // com.tencent.android.qqdownloader
            intent.setData(Uri.parse("http://www.wandoujia.com/apps/" + string));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) { //有浏览器
                startActivity(intent);
            } else { //天哪，这还是智能手机吗？
                Toast.makeText(getActivity(), "天啊，您没安装应用市场，连浏览器也没有，您买个手机干啥？", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startIntent(String bag_id) {
        params.clear();
        params.put("bag-id", bag_id);
        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
            JudgeIsRedOldedThread thread = new JudgeIsRedOldedThread();
            thread.start();
        } else Toast.makeText(getActivity(), "网络还没准备好，请检查网络连接！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化popWindow
     */

    private void initPopWindow(String string) {
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_window, null);
        popView.setId(Integer.parseInt("110"));
        popView.setFocusable(true); // 这个很重要
        popView.setFocusableInTouchMode(true);
        // 重写onKeyListener
        popView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popWindow.isShowing()) {
                        PopWindow.closePopWindow(popWindow, getActivity());
                        return true;
                    }
                }
                return false;
            }
        });
        popWindow = new PopupWindow(popView, (int) (width * 0.8), (int) (height * 0.68));
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.6f;
        getActivity().getWindow().setAttributes(params);
        //设置popwindow出现和消失动画
        popWindow.setAnimationStyle(R.style.popwindow_anim);
        initPopWindowCancleLL = (LinearLayout) popView.findViewById(R.id.initPopWindowCancleLL);
        popWindowCompanyRedNumberRanking = (LinearLayout) popView.findViewById(R.id.initPopWindowLookRedRanking);
        initPopWindowCompanyIcon = (ImageView) popView.findViewById(R.id.initPopWindowCompanyIcon);
        imageLoader.loadImage(companyLogo, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                initPopWindowCompanyIcon.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 15, 15));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        initPopWindowComment = (TextView) popView.findViewById(R.id.initPopWindowComment);
        initPopWindowComment.setText(companyComment);
        initPopWindowTV = (TextView) popView.findViewById(R.id.initPopWindowTV);
        initPopWindowTV.setText(string);
        initPopWindowCancleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopWindow.closePopWindow(popWindow, getActivity());
            }
        });
        popWindowCompanyRedNumberRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), RedResultActivity.class);
                bundle = new Bundle();
                bundle.putString("bag-id", bag_id);
                bundle.putString("code", "1");
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                if (popWindow != null)
                    PopWindow.closePopWindow(popWindow, getActivity());
            }
        });
    }

    boolean isHaveNext = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RED_OPEN_BACK) {
            if (resultCode == 1) {
                Intent i = new Intent(getActivity(), PlayerAdsActivity.class);
                bundle = new Bundle();
                bundle.putString("bag-id", bag_id);
                bundle.putString("companyLogo", companyLogo);
                bundle.putString("companyName", companyName);
                bundle.putString("companyComment", companyComment);
                i.putExtras(bundle);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
            } else if (requestCode == 0) {
            }
        }
    }

    Message message;
    Bundle bundle;

    class GetRedListDataThread extends Thread {
        @Override
        public void run() {
            super.run();
            params = new HashMap<>();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(getActivity(), "isOtherLogin")) {
                strUrlPath = Constants.redListUrl + PreferencesUtils.getString(getActivity(), "otherToken");
            } else {
                strUrlPath = Constants.redListUrl + PreferencesUtils.getString(getActivity(), "token");
            }
            params.put("page", String.valueOf(page));
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            if (TextUtils.equals(String.valueOf(page), "1")) {
                                dataList = JsonUtils.getMapObj(map.get("data").toString());
                            } else dataList.putAll(JsonUtils.getMapObj(map.get("data").toString()));
                            Log.d("--GetRedListDataThread", "dataList:" + dataList);
                            if (!(dataList.get("list") == null)) {
                                if (!(handler == null))
                                    handler.sendEmptyMessage(0);
                            } else {
                                if (!(handler == null))
                                    handler.sendEmptyMessage(1);
                            }

                        } else {
                            if (!(handler == null))
                                handler.sendEmptyMessage(1);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") &&
                            TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        if (!(TokenUtlils.getToken() == null)) {
                            strUrlPath = Constants.redListUrl + TokenUtlils.getToken();
                            strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                            map = JsonUtils.getMapObj(strResult);
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                if (!map.get("data").toString().equals("[]")) {
                                    if (TextUtils.equals(String.valueOf(page), "1")) {
                                        dataList = JsonUtils.getMapObj(map.get("data").toString());
                                    } else
                                        dataList.putAll(JsonUtils.getMapObj(map.get("data").toString()));
                                    if (!(dataList.get("list") == null)) {
                                        if (!(handler == null))
                                            handler.sendEmptyMessage(0);
                                    } else {
                                        if (!(handler == null))
                                            handler.sendEmptyMessage(1);
                                    }
                                } else {
                                    if (!(handler == null))
                                        handler.sendEmptyMessage(1);
                                }
                            } else {
                                if (!(handler == null))
                                    handler.sendEmptyMessage(8);
                            }
                        }
                    } else {
                        handler.sendEmptyMessage(6);
                    }
                } else if (strResult.equals("-1")) {
                    handler.sendEmptyMessage(6);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(6);
                e.printStackTrace();
            }
        }
    }

    class JudgeIsRedOldedThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(getActivity(), "isOtherLogin")) {
                strUrlPath = Constants.isRedOlded + PreferencesUtils.getString(getActivity(), "otherToken");
            } else {
                strUrlPath = Constants.isRedOlded + PreferencesUtils.getString(getActivity(), "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            message = new Message();
                            message.what = 2;
                            bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            if (!(handler == null))
                                handler.sendMessage(message);
                        } else {
                            if (!(handler == null))
                                handler.sendEmptyMessage(3);
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
                                if (!(handler == null))
                                    handler.sendMessage(message);
                            } else {
                                if (!(handler == null))
                                    handler.sendEmptyMessage(3);
                            }
                        } else {
                            if (!(handler == null))
                                handler.sendEmptyMessage(8);
                            isFirstGoToLogin = true;
                        }
                    } else {
                    }
                } else if (strResult.equals("-1")) {
                    if (!(handler == null))
                        handler.sendEmptyMessage(3);
                }
            } catch (Exception e) {
                if (!(handler == null))
                    handler.sendEmptyMessage(3);
                e.printStackTrace();
            }
        }
    }

    class LocationThread extends Thread {
        private String token;

        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(IApplication.applicationContext, "isOtherLogin")) {
                strUrlPath = Constants.postLocation + PreferencesUtils.getString(IApplication.applicationContext, "otherToken");
            } else {
                strUrlPath = Constants.postLocation + PreferencesUtils.getString(IApplication.applicationContext, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!TextUtils.equals(strResult, "[]") && !TextUtils.equals(strResult, "-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        map = JsonUtils.getMapObj(map.get("data").toString());
                        province_id = map.get("province_id").toString();
                        city_id = map.get("city_id").toString();
                        district_id = map.get("district_id").toString();
                        if (!(handler == null))
                            handler.sendEmptyMessage(10);
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {

                        strUrlPath = Constants.postLocation + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostDataGet(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            map = JsonUtils.getMapObj(map.get("data").toString());
                            province_id = map.get("province_id").toString();
                            city_id = map.get("city_id").toString();
                            district_id = map.get("district_id").toString();
                            if (!(handler == null))
                                handler.sendEmptyMessage(10);
                        } else {
                            if (!(handler == null))
                                handler.sendEmptyMessage(8);
                            isFirstGoToLogin = true;
                        }
                    } else {
                        if (!(handler == null))
                            handler.sendEmptyMessage(11);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        if (!(mLocationClient == null)) {
            mLocationClient.stopLocation();//停止定位
            mLocationClient.onDestroy();//销毁定位客户端。}
        }
        if (!(handler == null)) {
            handler = null;
        }
    }
}
