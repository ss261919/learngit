package vaporsome.com.vaporsome.vaporsome.changeuserinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.SaveAndRedUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;

public class ChangeUserInfo extends BaseActivity {

    @Bind(R.id.changeUserImgBack)
    LinearLayout changeUserImgBack;
    @Bind(R.id.changeUserInfoSRL)
    SwipeRefreshLayout changeUserInfoSRL;
    @Bind(R.id.addUserImg)
    ImageView addUserImg;
    @Bind(R.id.changeUserImgInside)
    ImageView changeUserImgInside;
    @Bind(R.id.changeUserInfoPickNameInside)
    RelativeLayout changeUserInfoPickNameInside;
    @Bind(R.id.changeUserInfoLocationInside)
    RelativeLayout changeUserInfoLocatinoInside;
    @Bind(R.id.changeUserInfoAgeInside)
    RelativeLayout changeUserInfoAgeInside;
    @Bind(R.id.changeUserInfoGenderInside)
    RelativeLayout changeUserInfoGenderInside;
    @Bind(R.id.changeUserInfoPsdInside)
    RelativeLayout changeUserInfoPsdInside;

    @Bind(R.id.changeUserInfoLocationTV)
    TextView changeUserInfoLocationTV;
    @Bind(R.id.changeUserInfoPickNameTV)
    TextView changeUserInfoPickNameTV;
    @Bind(R.id.changeUserInfoAgeTV)
    TextView changeUserInfoAgeTV;
    @Bind(R.id.changeUserInfoGenderTV)
    TextView changeUserInfoGenderTV;
    @Bind(R.id.changeUserInfoPhoneNumber)
    TextView changeUserInfoPhoneNumber;

    private final int CHANGE_USER_ICON = 1;
    private final int CHANGE_USER_PICKNAME = 2;
    private final int CHANGE_USER_AREA = 3;
    private final int CHANGE_USER_AGE = 4;
    private final int CHANGE_USER_GENDER = 5;
    private final int CHANGE_USER_PSD = 6;

    private Map<String, String> dataList = new HashMap<>();

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    RequestQueue queue;
    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setUserInfo();
                    getLocationData();
//                    changeUserInfoSRLDisable();
                    break;
                case 1:
//                    changeUserInfoSRLDisable();
//                    Toast.makeText(ChangeUserInfo.this, "数据更新失败，下拉重新获取数据", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    loadUserInfo();
                    isLoading = true;
                    break;
                case 3:
                    changeUserInfoLocationTV.setText("");
                    changeUserInfoLocationTV.setText(provinceListName.get(provinceListId.indexOf(province_id))
                            + cityListName.get(cityListId.indexOf(city_id))
                            + areaListName.get(areaListId.indexOf(district_id)));
                    break;
                case 4:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(ChangeUserInfo.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(getBaseContext(), "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;

            }
        }
    };
    private GetUserInfoThread thread;
    private Map<String, Object> map;
    private List<Map<String, Object>> provinceList = new ArrayList<>();
    private List<String> provinceListName = new ArrayList<>();
    private List<String> provinceListId = new ArrayList<>();
    private List<String> cityListId = new ArrayList<>();
    private List<String> cityListName = new ArrayList<>();
    private Map<String, String> params = new HashMap<>();
    private Map<String, Object> cityMap = new HashMap<>();
    private Map<String, String> cityMapDetail = new HashMap<>();
    private Map<String, Object> areaMap = new HashMap<>();
    private List<String> areaListId = new ArrayList<>();
    private List<String> areaListName = new ArrayList<>();
    private String province_id = "0";
    private String city_id;
    private String district_id;
    private Map<String, Object> areaMapDetail = new HashMap<>();
    private boolean isLoading;

    private void setUserInfo() {
        changeUserInfoPhoneNumber.setText(dataList.get("mobile").toString());
        changeUserInfoGenderTV.setText(Integer.parseInt(dataList.get("sex").toString()) == 10 ? "男" : "女");
        if (Integer.parseInt(dataList.get("age").toString()) == 0) {
            changeUserInfoAgeTV.setText("-");
        } else changeUserInfoAgeTV.setText(dataList.get("age").toString());
        changeUserInfoPickNameTV.setText(dataList.get("nickname").toString());
        changeUserInfoLocationTV.setText(PreferencesUtils.getString(getBaseContext(), "defaultProvince", "").toString() +
                PreferencesUtils.getString(getBaseContext(), "defaultCity", "") +
                PreferencesUtils.getString(getBaseContext(), "defaultArea", ""));
        changeUserInfoLocationTV.setText("");
        if (!dataList.get("avatar").toString().equals("[]")) {
            addUserImg.setTag(dataList.get("avatar").toString() + "?imageView2/2/w/200/h/200");
            imageLoader.loadImage(dataList.get("avatar").toString() + "?imageView2/2/w/200/h/200", options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    addUserImg.setBackgroundResource(R.drawable.imagedownload_fail);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (addUserImg.getTag().equals(imageUri)) {
                        addUserImg.setBackgroundColor(Color.parseColor("#00000000"));
                        addUserImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                        //保存头像
                        SaveAndRedUtils.saveImg(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10), Constants.userHeaderUrl);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);
        ButterKnife.bind(this);
        initView();
        initData();
        loadUserInfo();
        initListener();
    }

    private void loadUserInfo() {
        if (NetWorkInfo.isNetworkAvailable(this)) {
            thread = new GetUserInfoThread();
            thread.start();
        } else Toast.makeText(this, "网络还没准备好，请检查网络链接！", Toast.LENGTH_SHORT).show();

    }

    private Message message;

    class GetUserInfoThread extends Thread {

        @Override
        public void run() {
            String strUrlPath;
            if (PreferencesUtils.getBoolean(ChangeUserInfo.this, "isOtherLogin")) {
                strUrlPath = Constants.userInfoChange + PreferencesUtils.getString(ChangeUserInfo.this, "otherToken");
            } else {
                strUrlPath = Constants.userInfoChange + PreferencesUtils.getString(ChangeUserInfo.this, "token");
            }
            String strResult = HttpUtils.submitPost(strUrlPath);
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        dataList = JsonUtils.getMapStr(map.get("data").toString());
                        province_id = dataList.get("province_id");
                        city_id = dataList.get("city_id");
                        district_id = dataList.get("district_id");
                        message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.userInfoChange + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPost(strUrlPath);
                        map = JsonUtils.getMapObj(strResult);
                        if (!strResult.isEmpty() && !strResult.equals("-1")) {
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                dataList = JsonUtils.getMapStr(map.get("data").toString());
                                province_id = dataList.get("province_id");
                                city_id = dataList.get("city_id");
                                district_id = dataList.get("district_id");
                                message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(4);
                                isFirstGoToLogin = true;
                            }
                        } else if (strResult.equals("-1")) {
                            message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
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

    @Override
    public void initView() {
        if (!PreferencesUtils.getBoolean(ChangeUserInfo.this, "isOtherLogin")) {
            changeUserInfoPhoneNumber.setText(PreferencesUtils.getString(this, "mobile"));
        }
        if (!(SaveAndRedUtils.readSplashImg(Constants.userHeaderUrl) == null)) {
            addUserImg.setImageBitmap(SaveAndRedUtils.readSplashImg(Constants.userHeaderUrl));
        }
        changeUserInfoGenderTV.setText(PreferencesUtils.getString(this, "sex"));
        changeUserInfoAgeTV.setText(PreferencesUtils.getString(this, "age"));
        changeUserInfoPickNameTV.setText(PreferencesUtils.getString(this, "pickName"));
        changeUserInfoLocationTV.setText(PreferencesUtils.getString(getBaseContext(), "defaultProvince", "北京").toString() +
                PreferencesUtils.getString(getBaseContext(), "defaultCity", "北京") +
                PreferencesUtils.getString(getBaseContext(), "defaultArea", "昌平区"));
    }

    @Override
    public void initData() {
        imageLoader = ImageLoader.getInstance();
        options = IApplication.options;
        queue = Volley.newRequestQueue(getBaseContext());
    }

    private void getLocationData() {
        getProvinceJsonString();
    }

    private void getProvinceJsonString() {
        if (NetWorkInfo.isNetworkAvailable(this)) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.getProvinceUri, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                map = JsonUtils.getMapObj(response.toString());
                                if (TextUtils.equals(map.get("status").toString(), "true")) {
                                    provinceList.clear();
                                    provinceList = JsonUtils.getListMap(map.get("data").toString());
                                    provinceListName.clear();
                                    provinceListId.clear();
                                    for (Map<String, Object> map1 : provinceList
                                            ) {
                                        provinceListName.add(map1.get("name").toString());
                                        provinceListId.add(map1.get("id").toString());
                                    }

                                    GetCityThread thread = new GetCityThread();
                                    thread.start();
                                } else {
                                    handler.sendEmptyMessageAtTime(1, 10);
                                }
                            } catch (Exception e) {
                                handler.sendEmptyMessageAtTime(1, 10);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    handler.sendEmptyMessageAtTime(1, 10);
                }
            });
            queue.add(jsonObjectRequest);
        } else openToast("网络还没准备好，请检查后下拉刷新！");
    }

    private void openToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initListener() {
//        changeUserInfoSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (NetWorkInfo.isNetworkAvailable(ChangeUserInfo.this)) {
//                    changeUserInfoSRL.setEnabled(true);
//                    if (!isLoading) {
//                        handler.sendEmptyMessageAtTime(2, 10);
//                    }
//                } else {
//                    changeUserInfoSRLDisable();
//                    Toast.makeText(ChangeUserInfo.this, "网络未连接，请检查无误后重试！", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void changeUserInfoSRLDisable() {
        changeUserInfoSRL.setEnabled(false);
        changeUserInfoSRL.setRefreshing(false);
        changeUserInfoSRL.post(new Runnable() {
            @Override
            public void run() {
                changeUserInfoSRL.setEnabled(true);
            }
        });
        isLoading = false;
    }

    @OnClick({R.id.changeUserImgBack, R.id.addUserImg, R.id.changeUserImgInside, R.id.changeUserInfoPickNameInside,
            R.id.changeUserInfoLocationInside, R.id.changeUserInfoAgeInside, R.id.changeUserInfoGenderInside,
            R.id.changeUserInfoPsdInside, R.id.changeUserImgInsideLayout})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            //头像
            case R.id.changeUserImgBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.addUserImg:
                startActivityForResult(initIntent("ChangeUserImg", ""), CHANGE_USER_ICON);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.changeUserImgInside:
                startActivityForResult(initIntent("ChangeUserImg", ""), CHANGE_USER_ICON);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.changeUserImgInsideLayout:
                startActivityForResult(initIntent("ChangeUserImg", ""), CHANGE_USER_ICON);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            //昵称
            case R.id.changeUserInfoPickNameInside:
                startActivityForResult(initIntent("ChangeUserPickName", changeUserInfoPickNameTV.getText().toString()), CHANGE_USER_PICKNAME);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            //地区
            case R.id.changeUserInfoLocationInside:
                startActivityForResult(initIntent("ChangeUserLocation", ""), CHANGE_USER_AREA);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            //年龄
            case R.id.changeUserInfoAgeInside:
                startActivityForResult(initIntent("ChangeUserAge", ""), CHANGE_USER_AGE);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            //性别
            case R.id.changeUserInfoGenderInside:
                startActivityForResult(initIntent("ChangeUserGender", ""), CHANGE_USER_GENDER);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            //密码
            case R.id.changeUserInfoPsdInside:
                startActivityForResult(initIntent("ChangeUserPsd", ""), CHANGE_USER_PSD);
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
        }
    }

    private Intent initIntent(String falg, String string) {
        Intent intent = new Intent(this, ChangeUserInfoInside.class);
        intent.putExtra("FLAG", falg);
        intent.putExtra("string", string);
        return intent;
    }

    @Override
    public void finish() {
        setResult(0);
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            loadUserInfo();
        }
    }

    class GetCityThread extends Thread {
        @Override
        public void run() {
            super.run();
            params.clear();
            params.put("id", province_id);
            String strResult = HttpUtils.submitPostData(Constants.getProvinceCityUri, params, "utf-8");
            try {
                map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    cityMap.clear();
                    cityMap = JsonUtils.getMapObj(map.get("data").toString());
                    //取得城市
                    cityMapDetail.clear();
                    cityMapDetail = JsonUtils.getMapStr(cityMap.get("city").toString());
                    cityListId.clear();
                    cityListName.clear();
                    for (Map.Entry entry : cityMapDetail.entrySet()) {
                        cityListId.add(entry.getKey().toString());
                        cityListName.add(cityMapDetail.get(entry.getKey().toString()).toString());
                    }
                    //取得区县
                    areaMap.clear();
                    areaMap = JsonUtils.getMapObj(cityMap.get("district").toString());
                    areaMapDetail.clear();
                    areaMapDetail = (Map<String, Object>) areaMap.get(city_id);
                    areaListId.clear();
                    areaListName.clear();
                    for (Map.Entry entry : areaMapDetail.entrySet()) {
                        areaListId.add(entry.getKey().toString());
                        areaListName.add(areaMapDetail.get(entry.getKey().toString()).toString());
                    }
                    message = new Message();
                    message.what = 3;
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