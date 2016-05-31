package vaporsome.com.vaporsome.vaporsome.adsdetailactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.IsMatchUtil;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.outofmoney.OutofMoneyActivity;

public class AdsDetailActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.adsActivityBack)
    LinearLayout adsActivityBack;
    @Bind(R.id.adsActivityColorLL)
    RelativeLayout adsActivityColorLL;
    @Bind(R.id.adsActivitySizeLL)
    RelativeLayout adsActivitySizeLL;
    @Bind(R.id.adsActivityGoToTop)
    TextView adsActivityGoToTop;
    @Bind(R.id.adsActivityOnLineOrder)
    LinearLayout adsActivityOnLineOrder;
    @Bind(R.id.adsActivityPhoneInformation)
    LinearLayout adsActivityPhoneInformation;
    @Bind(R.id.adsActivitySmsInformation)
    LinearLayout adsActivitySmsInformation;
    @Bind(R.id.adsActivityFooter)
    LinearLayout adsActivityFooter;
    @Bind(R.id.adsActivityRecycleView)
    RecyclerView adsActivityRecycleView;
    @Bind(R.id.adsActivityScrollView)
    ScrollView adsActivityScrollView;
    @Bind(R.id.adsActivityListViewFooterColorSpinner)
    Spinner adsActivityListViewFooterColorSpinner;
    @Bind(R.id.adsActivityListViewFooterSizeSpinner)
    Spinner adsActivityListViewFooterSizeSpinner;
    @Bind(R.id.adsActivityListViewFooterLocationProvinceSpinner)
    Spinner adsActivityListViewFooterLocationProvinceSpinner;
    @Bind(R.id.adsActivityListViewFooterLocationCitySpinner)
    Spinner adsActivityListViewFooterLocationCitySpinner;
    @Bind(R.id.adsActivityListViewFooterLocationAreaSpinner)
    Spinner adsActivityListViewFooterLocationAreaSpinner;
    @Bind(R.id.decreaseCountBT)
    Button decreaseCountBT;
    @Bind(R.id.increaseCountBT)
    Button increaseCountBT;
    @Bind(R.id.adsActivityListViewFooterNameET)
    EditText adsActivityListViewFooterNameET;
    @Bind(R.id.adsActivityListViewFooterPhoneET)
    EditText adsActivityListViewFooterPhoneET;
    @Bind(R.id.adsActivityListViewFooterCountET)
    EditText adsActivityListViewFooterCountET;
    @Bind(R.id.adsActivityListViewFooterLocationDetailET)
    EditText adsActivityListViewFooterLocationDetailET;
    @Bind(R.id.adsActivityListViewFooterMoneyAccountTV)
    TextView adsActivityListViewFooterMoneyAccountTV;
    @Bind(R.id.adsActivityListViewFooterMessageET)
    EditText adsActivityListViewFooterMessageET;
    @Bind(R.id.adsActivityListViewFooterPostBT)
    Button adsActivityListViewFooterPostBT;
    //header
    @Bind(R.id.adsActivityHeaderBGIMG)
    ImageView adsActivityHeaderBGIMG;
    @Bind(R.id.adsActivityHeaderLoadImg)
    ImageView adsActivityHeaderLoadImg;
    @Bind(R.id.adsActivityHeaderNewPrice)
    TextView adsActivityHeaderNewPrice;
    @Bind(R.id.adsActivityHeaderOldPrice)
    TextView adsActivityHeaderOldPrice;
    @Bind(R.id.adsActivityHeaderDiscount)
    TextView adsActivityHeaderDiscount;
    @Bind(R.id.adsActivityHeaderSaveTV)
    TextView adsActivityHeaderSaveTV;
    @Bind(R.id.adsActivityHeaderBuyBT)
    Button adsActivityHeaderBuyBT;
    @Bind(R.id.adsActivityHeader)
    LinearLayout adsActivityHeader;
    @Bind(R.id.adsActivityHeaderLoadLL)
    LinearLayout adsActivityHeaderLoadLL;
    @Bind(R.id.adsActivityHeaderCompanyNmaeTV)
    TextView adsActivityHeaderCompanyNmaeTV;
    @Bind(R.id.adsActivitySRL)
    SwipeRefreshLayout adsActivitySRL;
    @Bind(R.id.adsActivityScrollViewLL)
    LinearLayout adsActivityScrollViewLL;

    private int width;
    private int height;
    private Map<String, Object> dataMap;
    private List<Map<String, Object>> sizeMap;
    private List<Map<String, Object>> adsList;
    private List<String> adsImgMap;
    private List<Map<String, Object>> colorList;
    private ImageLoader imageLoader;
    RequestQueue queue;

    private ArrayAdapter<String> cityAdapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setInfo();
                    adsActivitySRLDisable();
                    break;
                case 1:
                    adsActivitySRLDisable();
                    Toast.makeText(AdsDetailActivity.this, "获取商品信息失败，刷新重试", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    cityAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, cityListName);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adsActivityListViewFooterLocationCitySpinner.setAdapter(cityAdapter);
                    //city
                    adsActivityListViewFooterLocationCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int pos, long id) {
                            city_id = cityListId.get(pos);
                            city_name = cityListName.get(pos);
//                            Log.d("--ChangeUserInfoInside", city_id + "::" + cityListName.get(pos));
                            try {
                                //取得区县
                                areaMap = JsonUtils.getMapObj(cityMap.get("district").toString());
                                areaMapDetail = (Map<String, String>) areaMap.get(city_id);
//                                Log.d("----areaMapDetail", areaMapDetail.toString());
                                areaListId.clear();
                                areaListName.clear();
                                for (Map.Entry entry : areaMapDetail.entrySet()) {
                                    areaListId.add(entry.getKey().toString());
                                    areaListName.add(areaMapDetail.get(entry.getKey().toString()).toString());
                                }
//                                Log.d("----areaListId", areaListId.toString());
//                                Log.d("----areaListName", areaListName.toString());
                                areaDetailAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, areaListName);
                                adsActivityListViewFooterLocationAreaSpinner.setAdapter(areaDetailAdapter);
                                adsActivityListViewFooterLocationAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int pos, long id) {
                                        district_id = areaListId.get(pos);
                                        district_name = areaListName.get(pos);
//                                        Log.d("--ChangeUserInfoInside", district_id + "::" + areaListName.get(pos));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Another interface callback
                                        district_id = String.valueOf(0);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Another interface callback
                            city_id = String.valueOf(0);
                        }
                    });
                    break;
                case 4:
                    adsActivitySRLDisable();
                    Toast.makeText(AdsDetailActivity.this, "获取省市信息失败，下拉刷新重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    openToast(msg.getData().get("data").toString());
                    adsActivityListViewFooterPostBT.setEnabled(true);
                    break;
                case 6:
                    openToast(msg.getData().get("data").toString());
                    adsActivityListViewFooterPostBT.setEnabled(true);
                    break;
                case 7:
                    num--;
                    break;
                case 8:
                    num++;
                    break;
                case 9:
                    openToast("数量不能小于1");
                    break;
                case 10:
                    adsActivityListViewFooterMoneyAccountTV.setText(String.valueOf(num * Integer.parseInt(adsInfoMap.get("price").toString())));
                    break;
                case 11:
                    timer.cancel();
                    break;
                case 12:
                    downData();
                    break;
                case 13:
                    adsActivitySRL.setEnabled(true);
                    break;
                case 14:
                    adsActivitySRLDisable();
                    break;
                case 15:
                    break;

            }
        }
    };

    private void adsActivitySRLDisable() {
        adsActivitySRL.setEnabled(false);
        adsActivitySRL.setRefreshing(false);
        adsActivitySRL.post(new Runnable() {
            @Override
            public void run() {
                adsActivitySRL.setEnabled(true);
            }
        });
    }

    private Map<String, String> adsDetailInfo = new HashMap<>();
    private ArrayList<Object> adsImgList;
    private AdsDetailActivityAdapter adsDetailActivityAdapter;
    private List<String> colourListId;
    private List<String> colourListName;
    private List<String> sizeListId;
    private List<String> sizeListName;
    private String color_id = "0";
    private String size_id = "0";
    private Message message;
    private String province_name;
    private String city_name;
    private String district_name;
    private boolean isScroll;
    private Timer timer;
    private DisplayImageOptions options;
    private Animation anim;
    private int adsActivityHeaderHeight;
    private boolean isLoadComplete;
    private String link_url;
    private boolean isLoading = false;

    private void setInfo() {
        dataMap = new HashMap<>();
        if (!adsInfoMap.toString().equals("{}")) {
            adsActivityListViewFooterMoneyAccountTV.setText(adsInfoMap.get("price").toString());
            try {
                adsList = JsonUtils.getListMap(adsInfoMap.get("file").toString());
                if (!TextUtils.equals(adsList.toString(), "[]")) {
                    adsImgList = new ArrayList<>();
                    for (Map<String, Object> map : adsList
                            ) {
                        adsImgList.add(map.get("file_url").toString());
                    }
                    dataMap.put("adsImgList", adsImgList);
                } else {
                    Toast.makeText(this, "获取图片信息失败，下拉刷新重试！", Toast.LENGTH_SHORT).show();
                }
                if (adsInfoMap.get("color").toString().equals("[]")) {
                    adsActivityColorLL.setVisibility(View.GONE);
                }
                if (adsInfoMap.get("model").toString().equals("[]")) {
                    adsActivitySizeLL.setVisibility(View.GONE);
                }
                adsActivityHeaderBGIMG.setTag(adsImgList.get(0).toString() + "?imageView2/2/w/" + width + "/");
//                Log.d("----AdsDetailActivity", adsImgList.get(0).toString() + "?imageView2/2/w/" + width + "/");
                imageLoader.loadImage(adsImgList.get(0).toString() + "?imageView2/2/w/" + width + "/", options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        adsActivityHeaderLoadLL.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(AdsDetailActivity.this, R.anim.load);
                        anim.start();
                        adsActivityHeaderLoadImg.setAnimation(anim);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        adsActivityHeaderLoadImg.clearAnimation();
                        adsActivityHeaderLoadImg.setBackgroundResource(R.drawable.imagedownload_fail);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (adsActivityHeaderBGIMG.getTag().equals(imageUri)) {
                            adsActivityHeaderBGIMG.setImageBitmap(loadedImage);
                            adsActivityHeaderBGIMG.setVisibility(View.VISIBLE);
                            adsActivityHeaderLoadLL.setVisibility(View.GONE);
                            adsActivityHeaderLoadImg.clearAnimation();
                        }

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
                adsActivityHeaderNewPrice.setText(adsInfoMap.get("price").toString());
                adsActivityHeaderOldPrice.setText(adsInfoMap.get("old_price").toString());
                adsActivityHeaderSaveTV.setText(adsInfoMap.get("save").toString());
                adsActivityHeaderDiscount.setText(adsInfoMap.get("sale").toString());
                adsActivityHeaderCompanyNmaeTV.setText(adsInfoMap.get("company_name").toString());
                phoneNumber = adsInfoMap.get("tel").toString();
                if (!adsInfoMap.get("color").toString().equals("[]")) {
                    colorList = JsonUtils.getListMap(adsInfoMap.get("color").toString());
                    colourListId = new ArrayList<>();
                    colourListName = new ArrayList<>();
                    for (Map<String, Object> map : colorList
                            ) {
                        colourListId.add(map.get("id").toString());
                        colourListName.add(map.get("color").toString());
                    }
//                    Log.d("--AdsDetailActivity", "colourListName:" + colourListName);
                    adsActivityListViewFooterColorSpinner.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, colourListName));

                }
                if (!adsInfoMap.get("model").toString().equals("[]")) {
                    sizeMap = JsonUtils.getListMap(adsInfoMap.get("model").toString());
                    sizeListId = new ArrayList<>();
                    sizeListName = new ArrayList<>();
                    for (Map<String, Object> map : sizeMap
                            ) {
                        sizeListId.add(map.get("id").toString());
                        sizeListName.add(map.get("model").toString());
                    }
//                    Log.d("--AdsDetailActivity", "sizeListName:" + sizeListName);
                    adsActivityListViewFooterSizeSpinner.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, sizeListName));
                }

            } catch (Exception e) {
//                Log.d("-------eeeee", "e:" + e);
            }
            adsDetailActivityAdapter = new AdsDetailActivityAdapter(this, dataMap, IApplication.options, imageLoader, width, height);
            adsActivityRecycleView.setAdapter(adsDetailActivityAdapter);
            adsActivityScrollView.smoothScrollTo(0, 0);
            adsActivityScrollView.post(new Runnable() {
                @Override
                public void run() {
                    adsActivityScrollView.smoothScrollTo(0, 0);
                }
            });
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    int old_adsActivityRecycleViewHeight = adsActivityRecycleView.getHeight();
                    if (!(adsActivityRecycleViewHeight == old_adsActivityRecycleViewHeight)) {
                        adsActivityRecycleViewHeight = adsActivityRecycleView.getHeight();
                    } else {
                        message = new Message();
                        message.what = 11;
                        handler.sendMessage(message);
                        isLoadComplete = true;
//                        Log.d("-----AdsDetailActivity", "adsActivityRecycleViewHeight:" + adsActivityRecycleViewHeight + adsActivityHeaderHeight);
                    }
                }
            };
            timer.schedule(timerTask, 300, 150);
        } else {
            Toast.makeText(this, "获取数据失败，下拉刷新重试！", Toast.LENGTH_SHORT).show();
        }
    }

    private String phoneNumber;
    private ArrayAdapter<String> provinceAdapter;
    private Map<String, Object> map;
    private List<Map<String, Object>> provinceList;
    private List<String> provinceListName = new ArrayList<>();
    private List<String> provinceListId = new ArrayList<>();
    private List<String> cityListId = new ArrayList<>();
    private List<String> cityListName = new ArrayList<>();
    private Map<String, String> params;
    private Map<String, Object> cityMap;
    private Map<String, String> cityMapDetail;
    private Map<String, Object> areaMap;
    private List<String> areaListId = new ArrayList<>();
    private List<String> areaListName = new ArrayList<>();
    private String province_id;
    private String city_id;
    private String district_id;
    private Map<String, String> areaMapDetail;
    private ArrayAdapter<String> areaDetailAdapter;
    private int num = 1;
    private int adsActivityRecycleViewHeight = 0;
    private Map<String, Object> adsInfoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_detail);
        ButterKnife.bind(this);
        link_url = getIntent().getStringExtra("link_url");
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(AdsDetailActivity.context));
        options = IApplication.options;
        queue = Volley.newRequestQueue(this);
        adsActivityRecycleView.setLayoutManager(new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        initData();
        initView();
        initListener();
    }

    @Override
    public void initView() {
        adsActivityFooter.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        increaseCountBT.setTag("+");
        decreaseCountBT.setTag("-");
        adsActivityListViewFooterCountET.setInputType(InputType.TYPE_CLASS_NUMBER);
        adsActivityListViewFooterCountET.setText(String.valueOf(num));
        increaseCountBT.setOnClickListener(new OnButtonClickListener());
        decreaseCountBT.setOnClickListener(new OnButtonClickListener());
        adsActivityListViewFooterCountET.addTextChangedListener(new OnTextChangeListener());

    }


    /**
     * EditText输入变化事件监听器
     */
    class OnTextChangeListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            String numString = s.toString();
            if (numString == null || numString.equals("")) {
                num = 1;
            } else {
                int numInt = Integer.parseInt(numString);
                if (numInt < 1) {
                    message = new Message();
                    message.what = 9;
                    handler.sendMessage(message);
                } else {
                    //设置EditText光标位置 为文本末端
                    adsActivityListViewFooterCountET.setSelection(adsActivityListViewFooterCountET.getText().toString().length());
                    num = numInt;
                    message = new Message();
                    message.what = 10;
                    handler.sendMessage(message);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

    }

    /**
     * 加减按钮事件监听器
     */
    class OnButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String numString = adsActivityListViewFooterCountET.getText().toString();
            if (numString == null || numString.equals("")) {
                num = 0;
                adsActivityListViewFooterCountET.setText("0");
            } else {
                if (v.getTag().equals("+")) {
                    if (++num < 1)  //先加，再判断
                    {
                        num--;
//                        Log.d("----", "num:" + num);
                        Message message = new Message();
                        message.what = 9;
                        handler.sendMessage(message);
                    } else {
                        adsActivityListViewFooterCountET.setText(String.valueOf(num));
                    }
                } else if (v.getTag().equals("-")) {
                    if (--num < 1)  //先减，再判断
                    {
                        num++;
//                        Log.d("----", "num:" + num);
                        Message message = new Message();
                        message.what = 9;
                        handler.sendMessage(message);
                    } else {
                        adsActivityListViewFooterCountET.setText(String.valueOf(num));
                    }
                }
            }
        }
    }

    @Override
    public void initData() {
        downData();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

    }

    private void downData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adsActivityScrollView.setNestedScrollingEnabled(false);
        }
        if (NetWorkInfo.isNetworkAvailable(this)) {
            getAdsDetailInfoString();
            getProvinceJsonString();
        } else Toast.makeText(this, "网络链接有问题，下拉刷新重试", Toast.LENGTH_SHORT).show();
    }

    private void getAdsDetailInfoString() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(link_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            map = JsonUtils.getMapObj(response.toString());
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                if (!map.get("data").toString().equals("{}")) {
                                    adsInfoMap = JsonUtils.getMapObj(map.get("data").toString());
//                                    Log.d("----adsInfoMap", map.get("data").toString());
                                    Message message = new Message();
                                    message.what = 0;
                                    handler.sendMessage(message);
                                } else {
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
//                                    Log.d("--ChangeUserInfoInside", map.get("data").toString());
                                }

                            } else {
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
//                                Log.d("--ChangeUserInfoInside", map.get("data").toString());
                            }
                            map = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", error.getMessage(), error);
                message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void getProvinceJsonString() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.getProvinceUri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.toString().equals("")) {
                                map = JsonUtils.getMapObj(response.toString());
                                if (TextUtils.equals(map.get("status").toString(), "true")) {
                                    if (!map.get("data").toString().equals("{}")) {
                                        provinceList = JsonUtils.getListMap(map.get("data").toString());
//                                        Log.d("----ProvinceJsonString", map.get("data").toString());
                                        if (!map.get("data").toString().equals("[]")) {
                                            provinceListName.clear();
                                            provinceListId.clear();
                                            for (Map<String, Object> map1 : provinceList
                                                    ) {
                                                provinceListName.add(map1.get("name").toString());
                                                provinceListId.add(map1.get("id").toString());
                                            }
//                                            Log.d("---ProvinceJsonString", provinceListName.toString());
//                                            Log.d("---ProvinceJsonString", provinceListId.toString());
                                            provinceAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, provinceListName);
                                            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            adsActivityListViewFooterLocationProvinceSpinner.setAdapter(provinceAdapter);// 设置显示信息
                                            //province
                                            adsActivityListViewFooterLocationProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    province_id = provinceListId.get(i);
                                                    province_name = provinceListName.get(i);
//                                                    Log.d("---ChangeUserInfoInside", province_id + "::" + provinceListName.get(i));
                                                    getCityJsonString();
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });
                                        } else {
                                            Message message = new Message();
                                            message.what = 4;
                                            handler.sendMessage(message);
                                        }
                                    } else {
                                        Message message = new Message();
                                        message.what = 4;
                                        handler.sendMessage(message);
//                                        Log.d("--ChangeUserInfoInside", map.get("data").toString());
                                    }
                                } else {
                                    Message message = new Message();
                                    message.what = 4;
                                    handler.sendMessage(message);
                                }
                            } else {
                                Message message = new Message();
                                message.what = 4;
                                handler.sendMessage(message);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", error.getMessage(), error);
            }
        });
        queue.add(jsonObjectRequest);
        jsonObjectRequest = null;
    }

    class GetCityThread extends Thread {
        @Override
        public void run() {
            super.run();
            params = new HashMap<>();
            params.put("id", province_id);
//            Log.d("--province_id", province_id);
            String strResult = HttpUtils.submitPostData(Constants.getProvinceCityUri, params, "utf-8");
            try {
                if (!strResult.isEmpty()) {
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
//                        Log.d("---GetCityThread", map.get("data").toString());
                        if (!TextUtils.equals(map.get("data").toString(), "{}")) {
                            cityMap = JsonUtils.getMapObj(map.get("data").toString());
                            if (!TextUtils.equals(cityMap.get("city").toString(), "{}") && !TextUtils.equals(cityMap.get("district").toString(), "{}")) {
                                //取得城市
                                cityMapDetail = JsonUtils.getMapStr(cityMap.get("city").toString());
                                areaMapDetail = JsonUtils.getMapStr(cityMap.get("district").toString());
//                                Log.d("--cityMapDetail", "cityMapDetail:" + cityMapDetail.toString());
//                                Log.d("--areaMapDetail", "areaMapDetail:" + cityMap.get("district").toString());
                                cityListId.clear();
                                cityListName.clear();
                                for (Map.Entry entry : cityMapDetail.entrySet()) {
                                    cityListId.add(entry.getKey().toString());
                                    cityListName.add(cityMapDetail.get(entry.getKey().toString()).toString());
                                }
//                                Log.d("----cityListId", cityListId.toString());
//                                Log.d("----cityListName", cityListName.toString());
                                Message message = new Message();
                                message.what = 2;
                                handler.sendMessage(message);
                            } else {
                                Message message = new Message();
                                message.what = 4;
                                handler.sendMessage(message);
                            }
                        } else {
                            Message message = new Message();
                            message.what = 4;
                            handler.sendMessage(message);
                        }
                    } else {
                        Message message = new Message();
                        message.what = 4;
                        handler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initListener() {
        //颜色
        adsActivityListViewFooterColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                size_id = sizeListId.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //size
        adsActivityListViewFooterSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                color_id = colourListId.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        adsActivityHeaderBuyBT.setOnClickListener(this);
        adsActivitySRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(AdsDetailActivity.this)) {
                    if (!isLoading) {
                        downData();
//                        Log.d("--AdsDetailActivity", "-----------------");
                    }
                } else {
                    adsActivitySRL.setEnabled(false);
                    adsActivitySRL.setRefreshing(false);
                    adsActivitySRL.post(new Runnable() {
                        @Override
                        public void run() {
                            adsActivitySRL.setEnabled(true);
                        }
                    });
                    Toast.makeText(AdsDetailActivity.this, "网络未连接，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adsActivityScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adsActivityScrollView.getScrollY() <= 20) {
                    adsActivitySRL.setEnabled(true);
                    return false;
                } else {
                    adsActivitySRL.setEnabled(false);
                    adsActivitySRL.setRefreshing(false);
                    return false;
                }
            }
        });
    }

    private void getCityJsonString() {
        GetCityThread thread = new GetCityThread();
        thread.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        if (hasFocus) {
            adsActivityHeader.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.82)));
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @OnClick({R.id.adsActivityBack, R.id.adsActivityGoToTop, R.id.adsActivityOnLineOrder, R.id.adsActivityPhoneInformation,
            R.id.adsActivitySmsInformation, R.id.adsActivityListViewFooterPostBT})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.adsActivityBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.adsActivityGoToTop:
                adsActivityScrollView.setScrollY(0);
                break;
            case R.id.adsActivityOnLineOrder:
                adsActivityScrollView.setScrollY((adsActivityHeader.getHeight() + adsActivityRecycleView.getHeight()));
                break;
            case R.id.adsActivityPhoneInformation:
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                } catch (Exception e) {
//                    Log.d("----AdsDetail", "e:" + e);
                }
                break;
            case R.id.adsActivitySmsInformation:
                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + phoneNumber));
                    startActivity(sendIntent);
                } catch (Exception e) {
//                    Log.d("----AdsDetail", "e:" + e);
                }
                break;
            case R.id.adsActivityListViewFooterPostBT:
                saveAdsPost();
                break;
            case R.id.adsActivityHeaderBuyBT:
                adsActivityScrollView.setScrollY((adsActivityHeader.getHeight() + adsActivityRecycleView.getHeight()));
//                Log.d("--adsActivityScrollView", "----------" + adsActivityHeaderHeight + adsActivityRecycleViewHeight);
                break;
        }
    }

    private void saveAdsPost() {
        if (adsActivityListViewFooterCountET.getText().toString().isEmpty() || Integer.parseInt(adsActivityListViewFooterCountET.getText().toString()) <= 0) {
            openToast("请填写正确商品数量信息！");
        } else if (!IsMatchUtil.isPhoneNumberValid(adsActivityListViewFooterPhoneET.getText().toString()) || TextUtils.isEmpty(adsActivityListViewFooterPhoneET.getText())) {
            openToast("手机号格式错误！");
        } else if (TextUtils.isEmpty(adsActivityListViewFooterLocationDetailET.getText())) {
            openToast("地址信息不能为空");
        } else if (TextUtils.isEmpty(adsActivityListViewFooterNameET.getText())) {
            openToast("收件人姓名不能为空");
        } else {
            SaveAdsPostThread thread = new SaveAdsPostThread();
            thread.start();
            adsActivityListViewFooterPostBT.setEnabled(false);
        }
    }

    private void openToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    class SaveAdsPostThread extends Thread {
        @Override
        public void run() {
            super.run();
            params = new HashMap<>();
            params.put("shop-id", Constants.getAdsDetailInfo.substring(Constants.getAdsDetailInfo.lastIndexOf("=") + 1, Constants.getAdsDetailInfo.length()));
            params.put("num", adsActivityListViewFooterCountET.getText().toString());
            params.put("realname", adsActivityListViewFooterNameET.getText().toString());
            params.put("mobile", adsActivityListViewFooterPhoneET.getText().toString());
            params.put("address", province_name + city_name + district_name + adsActivityListViewFooterLocationDetailET.getText().toString());
            params.put("comment", adsActivityListViewFooterMessageET.getText().toString());

            if (adsActivityColorLL.getVisibility() == View.VISIBLE) {
                params.put("color-id", color_id);
//                Log.d("--color-idThread", "model-id");
            }
            if (adsActivitySizeLL.getVisibility() == View.VISIBLE) {
                params.put("model-id", size_id);
//                Log.d("--model-idThread", "model-id");
            }
//            Log.d("---SaveAdsPostThread", "params:" + params);
            String strUrlPath = Constants.adsDetailInfoPost;
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
//            Log.d("-----strUrlPath:", strUrlPath);
//            Log.d("-----strResult:", strResult);
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Log.d("--adsDetail", map.get("data").toString());
                    message = new Message();
                    message.what = 5;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    message = new Message();
                    message.what = 6;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
//                    Log.d("----adsDetail", map.get("data").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
