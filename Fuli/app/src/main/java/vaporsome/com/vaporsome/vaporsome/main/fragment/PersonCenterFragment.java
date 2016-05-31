package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.DelayUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.SaveAndRedUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.AESTest;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.AdsDetailActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.moneydetail.MoneyDetail;
import vaporsome.com.vaporsome.vaporsome.outofmoney.SmallChangeActivity;
import vaporsome.com.vaporsome.vaporsome.recharge.RechargeActivity;
import vaporsome.com.vaporsome.vaporsome.setting.SettingActivity;

/**
 * Created by Administrator on 2016/3/21.
 */
public class PersonCenterFragment extends Fragment implements View.OnClickListener {

    private TextView personFragmentUserId;
    private TextView personFragmentUserMoneyNumber;
    private ImageView personFragmentUserIcon;
    private RelativeLayout personFragmentOutputMoneyLayout;
    private final int CHANGE_USER_INFO = 0;
    private final int MONEY_DETAIL = 1;
    private final int RECHARGE = 2;
    private int width;
    private int height;
    private ImageLoader imageLoader;

    private List<Map<String, Object>> dataImgMapList;
    private RequestQueue queue;
    private RelativeLayout personFragmentMoneyDetailLayout;
    private Map<String, String> dataList;

    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    personFragmentSRL.setRefreshing(false);
                    isLoading = false;
                    setUserInfo();
                    break;
                case 1:
                    if (personFragmentSRL.isRefreshing()) {
                        personFragmentSRL.setRefreshing(false);
                        personFragmentSRL.setEnabled(false);
                        personFragmentSRL.post(new Runnable() {
                            @Override
                            public void run() {
                                personFragmentSRL.setEnabled(true);
                            }
                        });
                    }
                    isLoading = false;
                    Toast.makeText(getActivity(), "获取信息失败，请重新刷新。", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (personFragmentSRL.isRefreshing()) {
                        personFragmentSRL.setRefreshing(false);
                        personFragmentSRL.setEnabled(false);
                        personFragmentSRL.post(new Runnable() {
                            @Override
                            public void run() {
                                personFragmentSRL.setEnabled(true);
                            }
                        });
                    }
                    isLoading = false;
//                    Toast.makeText(getActivity(), "网络有问题，确认网络后重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        PreferencesUtils.putBoolean(getActivity(), "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
            }
        }
    };
    private TextView personFragmentUserPickName;
    private ImageView personFragmentAdsImg;
    private int screenWidth;
    private int personFragmentAdsImgHeight;
    private ImageView personFragmentLoadImg;
    private RelativeLayout personFragmentSetting;
    private SwipeRefreshLayout personFragmentSRL;
    private boolean isLoading = false;
    private Message message;
    private LinearLayout personFragmentUserIdLL;
    private String link_url;
    private RelativeLayout personFragmentRechargeLayout;

    private void setUserInfo() {
        link_url = (String) dataImgMapList.get(0).get("link_url");
        personFragmentUserId.setText(dataList.get("mobile"));
        personFragmentUserPickName.setText(dataList.get("nickname"));
        PreferencesUtils.putString(getActivity(), "nickname", dataList.get("nickname"));
        personFragmentUserMoneyNumber.setText(dataList.get("money") + "元");
        PreferencesUtils.putString(getActivity(), "money", dataList.get("money") + "元");
        personFragmentUserIcon.setTag(dataList.get("avatar").toString() + "?imageView2/2/w/100/h/100");
        PreferencesUtils.putString(getActivity(), "avatar", dataList.get("avatar").toString());
        PreferencesUtils.putString(getActivity(), "userHeadIcon", dataList.get("avatar").toString() + "?imageView2/2/w/100/h/100");
        imageLoader.loadImage(dataList.get("avatar").toString() + "?imageView2/2/w/100/h/100", new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (personFragmentUserIcon.getTag().equals(dataList.get("avatar").toString() + "?imageView2/2/w/100/h/100")) {
                    personFragmentUserIcon.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    SaveAndRedUtils.saveImg(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10), Constants.userHeaderUrl);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        if (!dataImgMapList.isEmpty()) {
            if (screenWidth <= 768) {
                personFragmentAdsImg.setTag(dataImgMapList.get(0).get("file_768_url").toString() + "?imageView2/2/w/" + width + "/h/" + personFragmentAdsImgHeight);
                imageLoader.loadImage(dataImgMapList.get(0).get("file_768_url").toString() + "?imageView2/2/w/" + width + "/h/" + personFragmentAdsImgHeight, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (personFragmentAdsImg.getTag().equals(imageUri)) {
                            personFragmentAdsImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                            SaveAndRedUtils.saveImg(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10), Constants.personFragmentAdsImgUrl);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            } else {
                personFragmentAdsImg.setTag(dataImgMapList.get(0).get("file_1440_url").toString() + "?imageView2/2/w/" + width + "/h/" + personFragmentAdsImgHeight);
                imageLoader.loadImage(dataImgMapList.get(0).get("file_1440_url").toString() + "?imageView2/2/w/" + width + "/h/" + personFragmentAdsImgHeight, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (personFragmentAdsImg.getTag().equals(imageUri)) {
                            personFragmentAdsImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                            SaveAndRedUtils.saveImg(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10), Constants.personFragmentAdsImgUrl);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, null);
        personFragmentUserId = (TextView) view.findViewById(R.id.personFragmentUserId);
        personFragmentUserPickName = (TextView) view.findViewById(R.id.personFragmentUserPickName);
        personFragmentUserMoneyNumber = (TextView) view.findViewById(R.id.personFragmentUserMoneyNumber);
        personFragmentUserIcon = (ImageView) view.findViewById(R.id.personFragmentUserIcon);
        personFragmentAdsImg = (ImageView) view.findViewById(R.id.personFragmentAdsImg);
        personFragmentLoadImg = (ImageView) view.findViewById(R.id.personFragmentLoadImg);
        personFragmentSRL = (SwipeRefreshLayout) view.findViewById(R.id.personFragmentSRL);
        personFragmentAdsImg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                personFragmentAdsImgHeight = personFragmentAdsImg.getHeight();
            }
        });
        personFragmentOutputMoneyLayout = (RelativeLayout) view.findViewById(R.id.personFragmentOutputMoneyLayout);
        personFragmentSetting = (RelativeLayout) view.findViewById(R.id.personFragmentSetting);
        personFragmentMoneyDetailLayout = (RelativeLayout) view.findViewById(R.id.personFragmentMoneyDetailLayout);
        personFragmentRechargeLayout = (RelativeLayout) view.findViewById(R.id.personFragmentRechargeLayout);
        personFragmentUserIdLL = (LinearLayout) view.findViewById(R.id.personFragmentUserIdLL);

        if (PreferencesUtils.getBoolean(getActivity(), "isOtherLogin")) {
            personFragmentUserIdLL.setVisibility(View.GONE);
        } else {
            personFragmentUserIdLL.setVisibility(View.VISIBLE);
        }
        queue = Volley.newRequestQueue(getActivity());
        imageLoader = ImageLoader.getInstance();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        return view;
    }

    private void downData() {
        String strUrlPath;
        if (PreferencesUtils.getBoolean(getActivity(), "isOtherLogin", false)) {
            strUrlPath = Constants.personCenterUri + PreferencesUtils.getString(getActivity(), "otherToken");
        } else {
            strUrlPath = Constants.personCenterUri + PreferencesUtils.getString(getActivity(), "token");
        }
        StringRequest request = new StringRequest(Request.Method.POST, strUrlPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Map<String, Object> map = JsonUtils.getMapObj(response);
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                dataList = JsonUtils.getMapStr(map.get("info").toString());
                                dataImgMapList = JsonUtils.getListMap(map.get("banana").toString());
                                message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                                StringRequest request2 = new StringRequest(Request.Method.POST, Constants.personCenterUri + TokenUtlils.getToken(),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Map<String, Object> map2 = null;
                                                try {
                                                    map2 = JsonUtils.getMapObj(response);
                                                    if (TextUtils.equals(map2.get("status").toString(), "true")) {
                                                        dataList = JsonUtils.getMapStr(map2.get("info").toString());
                                                        dataImgMapList = JsonUtils.getListMap(map2.get("banana").toString());
                                                        Log.d("---PersonCenter22", dataList.toString());
                                                        Log.d("---PersonCenter22", dataImgMapList.toString());
                                                        message = new Message();
                                                        message.what = 0;
                                                        handler.sendMessage(message);
                                                    } else {
                                                        message = new Message();
                                                        message.what = 3;
                                                        handler.sendMessage(message);
                                                        isFirstGoToLogin = true;
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        message = new Message();
                                        message.what = 2;
                                        handler.sendMessage(message);
                                    }
                                });
                                queue.add(request2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        });
        queue.add(request);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        personFragmentUserId.setText(PreferencesUtils.getString(getActivity(), "mobile"));
        personFragmentUserPickName.setText(PreferencesUtils.getString(getActivity(), "nickname"));
        personFragmentUserMoneyNumber.setText(PreferencesUtils.getString(getActivity(), "money"));
        if (!(SaveAndRedUtils.readSplashImg(Constants.userHeaderUrl) == null)) {
            personFragmentUserIcon.setImageBitmap(SaveAndRedUtils.readSplashImg(Constants.userHeaderUrl));
        }
        if (!(SaveAndRedUtils.readSplashImg(Constants.personFragmentAdsImgUrl) == null)) {
            personFragmentAdsImg.setImageBitmap(SaveAndRedUtils.readSplashImg(Constants.personFragmentAdsImgUrl));
        }
        initListener();
        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
            downData();
        } else Toast.makeText(getActivity(), "网络还没准备好,请检查后下拉刷新！", Toast.LENGTH_SHORT).show();
    }

    private void initListener() {
        personFragmentSetting.setOnClickListener(this);
        personFragmentMoneyDetailLayout.setOnClickListener(this);
        personFragmentOutputMoneyLayout.setOnClickListener(this);
        personFragmentRechargeLayout.setOnClickListener(this);
        personFragmentAdsImg.setOnClickListener(this);
        personFragmentSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    personFragmentSRL.setEnabled(true);
                    personFragmentSRL.setRefreshing(true);
                    if (!isLoading) {
                        downData();
                        isLoading = true;
                    }
                } else {
                    personFragmentSRL.setEnabled(false);
                    personFragmentSRL.setRefreshing(false);
                    personFragmentSRL.post(new Runnable() {
                        @Override
                        public void run() {
                            personFragmentSRL.setEnabled(true);
                        }
                    });
                    Toast.makeText(getActivity(), "网络还没准备好,请检查后下拉刷新！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.personFragmentAdsImg:
                personFragmentSRL.setRefreshing(false);
                if (!(link_url == null) && NetWorkInfo.isNetworkAvailable(getActivity())) {
                    intent = new Intent(getActivity(), AdsDetailActivity.class);
                    intent.putExtra("link_url", link_url);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                    DelayUtils.viewEnableDelay(handler, personFragmentAdsImg, 500);
                } else Toast.makeText(getActivity(), "网络有问题，请检查后重试！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.personFragmentSetting:
                personFragmentSRL.setRefreshing(false);
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(intent, CHANGE_USER_INFO);
                getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                DelayUtils.viewEnableDelay(handler, personFragmentSetting, 500);
                break;
            case R.id.personFragmentOutputMoneyLayout:
                personFragmentSRL.setRefreshing(false);
                intent = new Intent(getActivity(), SmallChangeActivity.class);
                intent.putExtra("money_number", personFragmentUserMoneyNumber.getText().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                DelayUtils.viewEnableDelay(handler, personFragmentOutputMoneyLayout, 500);
                break;
            case R.id.personFragmentMoneyDetailLayout:
                personFragmentSRL.setRefreshing(false);
                intent = new Intent(getActivity(), MoneyDetail.class);
                startActivityForResult(intent, MONEY_DETAIL);
                getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                DelayUtils.viewEnableDelay(handler, personFragmentMoneyDetailLayout, 500);
                break;
            case R.id.personFragmentRechargeLayout:
                personFragmentSRL.setRefreshing(false);
                intent = new Intent(getActivity(), RechargeActivity.class);
                startActivityForResult(intent, CHANGE_USER_INFO);
                getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                DelayUtils.viewEnableDelay(handler, personFragmentRechargeLayout, 500);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 0) {
                downData();
                personFragmentSRL.setRefreshing(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
            downData();
        }
    }
}