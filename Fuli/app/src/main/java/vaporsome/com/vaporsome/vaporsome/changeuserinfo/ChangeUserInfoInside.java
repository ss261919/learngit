package vaporsome.com.vaporsome.vaporsome.changeuserinfo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.IsMatchUtil;
import vaporsome.com.vaporsome.common.utils.MD5Utils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.RandomUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;

public class ChangeUserInfoInside extends BaseActivity implements View.OnClickListener {

    private ArrayAdapter<String> cityAdapter;
    private boolean isGetLocationInfo;
    private Set<String> tagList = new HashSet<>();
    private boolean setTagTrue = false;
    private int setTagTime = 0;
    private Bundle bundle;
    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bundle = msg.getData();
            switch (msg.what) {
                case 18:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(ChangeUserInfoInside.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(ChangeUserInfoInside.this, "isFirstLogin", true);
                        isFirstGoToLogin = true;
                    }
                    break;
                case 17:
                    isGetLocationInfo = true;
                    break;
                case 16:
                    provinceAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, provinceListName);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    changeUserLocationSpinnerProvince.setAdapter(provinceAdapter);// 设置显示信息
                    if (NetWorkInfo.isNetworkAvailable(ChangeUserInfoInside.this)) {
                        changeUserLocationSpinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                provinceId = provinceListId.get(position);
                                PreferencesUtils.putString(getBaseContext(), "defaultProvince", provinceListName.get(position));
                                Log.d("---ChangeUserInfoInside", provinceId + "::" + provinceListName.get(position));
                                getCityJsonString();
                                isProvinceChange = true;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                provinceId = String.valueOf(2);
                            }
                        });
                        changeUserLocationSpinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int pos, long id) {
                                cityId = cityListId.get(pos);
                                isCityChange = true;
                                Log.d("--ChangeUserInfoInside", cityId + "::" + cityListName.get(pos));
                                try {
                                    areaMapDetil = JsonUtils.getMapStr(areaListName.get(pos).toString());
                                    PreferencesUtils.putString(getBaseContext(), "defaultCity", cityListName.get(pos));
                                    areaListDetailId.clear();
                                    areaListDetailName.clear();
                                    for (Map.Entry entry : areaMapDetil.entrySet()) {
                                        areaListDetailId.add(entry.getKey().toString());
                                        areaListDetailName.add(areaMapDetil.get(entry.getKey().toString()).toString());
                                    }
                                    areaDetailAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, areaListDetailName);
                                    changeUserLocationSpinnerArea.setAdapter(areaDetailAdapter);
                                    changeUserInfoLocationSRL.setEnabled(true);
                                    changeUserLocationSpinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view,
                                                                   int pos, long id) {
                                            areaId = areaListDetailId.get(pos);
                                            isAreaChange = true;
                                            PreferencesUtils.putString(getBaseContext(), "defaultArea", areaListDetailName.get(pos));
                                            Log.d("--ChangeUserInfoInside", areaId + "::" + areaListDetailId.get(pos));
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            // Another interface callback
                                            areaId = String.valueOf(0);
                                        }
                                    });
                                    isLoading = false;
                                    changeUserLocationSpinnerLL.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Another interface callback
                                cityId = String.valueOf(0);
                            }
                        });
                    } else {
                        Toast.makeText(ChangeUserInfoInside.this, "网络有问题，获取省市区信息失败", Toast.LENGTH_SHORT).show();
                    }
                    isLoading = false;
                    isGetLocationInfo = true;
                    changeUserInfoLocationSRL.setEnabled(true);
                    break;
                case 15:
                    openToast(bundle.getString("data"));
                    break;
                case 14:
                    if (!(provinceId.length() == 0) && !provinceId.equals("0") && !(cityId.length() == 0) && !cityId.equals("0")
                            && !(areaId.length() == 0) && !areaId.equals("0")) {
                        tagList.add(provinceId);
                        tagList.add(cityId);
                        tagList.add(areaId);
                        if (!setTagTrue) {
                            if (setTagTime < 2) {
                                JPushInterface.setTags(ChangeUserInfoInside.this, tagList, new TagAliasCallback() {
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
                    setResult(0);
                    PreferencesUtils.putString(ChangeUserInfoInside.this, "provinceId", provinceId);
                    PreferencesUtils.putString(ChangeUserInfoInside.this, "cityId", cityId);
                    PreferencesUtils.putString(ChangeUserInfoInside.this, "areaId", areaId);
                    Log.d("--ChangeUserInfoInside", provinceId + cityId + areaId);
                    openToast("地区修改成功");

                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
                case 13:
                    cityAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, cityListName);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    changeUserLocationSpinnerCity.setAdapter(cityAdapter);
                    isLoading = false;
                    changeUserLocationSpinnerLL.setVisibility(View.VISIBLE);
                    changeUserInfoLocationSRL.setEnabled(true);
                    break;
                case 12:
                    openToast(bundle.getString("data"));
                    changeUserInfoPickNamePost.setEnabled(true);
                    break;
                case 11:
                    openToast(bundle.getString("data"));
                    PreferencesUtils.putString(ChangeUserInfoInside.this, "pickName", changeUserInfoPickNameET.getText().toString());
                    setResult(0);
                    changeUserInfoPickNamePost.setEnabled(true);
                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
                case 10:
                    openToast(bundle.getString("data"));
                    changeUserInfoAgePost.setEnabled(true);
                    break;
                case 9:
                    setResult(0);
                    PreferencesUtils.putString(ChangeUserInfoInside.this, "age", String.valueOf(age));
                    openToast("年龄修改成功");
                    changeUserInfoAgePost.setEnabled(true);
                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
                case 8:
                    openToast("性别修改失败");
                    changeUserInfoGenderPost.setEnabled(true);
                    break;
                case 7:
                    setResult(0);
                    if (genderFlag == 10) {
                        PreferencesUtils.putString(ChangeUserInfoInside.this, "sex", "男");
                    } else
                        PreferencesUtils.putString(ChangeUserInfoInside.this, "sex", "女");
                    openToast("性别修改成功");
                    changeUserInfoGenderPost.setEnabled(true);

                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
                case 4:
                    bundle = msg.getData();
                    openToast(bundle.get("data").toString());
                    changeUserInfoPsdPost.setEnabled(true);
                    break;
                case 3:
                    setResult(0);
                    PreferencesUtils.putString(getBaseContext(), "password", MD5Utils.getMD5(changeUserInfoPsdET.getText().toString() + "qckj"));
                    openToast("密码修改成功");
                    changeUserInfoPsdPost.setEnabled(true);
                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
                case 2:
                    openToast("头像上传失败");
                    changeUserInfoPsdPost.setEnabled(true);
                    progressDialog.dismiss();
                    progressDialog = null;
                    break;
//                case 1:
//                    progressDialog.setMessage("以上传" + msg.getData().getString("PERCENT").toString());
//                    break;
                case 0:
                    setResult(0);
                    openToast("头像上传成功");
                    changeUserIconSave.setEnabled(true);
                    progressDialog.dismiss();
                    progressDialog = null;
                    finish();
                    overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                    break;
            }
        }
    };

    /***
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 0;
    //setResultCode
    private final int CHANGE_USER_ICON = 1;
    private final int CHANGE_USER_PICKNAME = 2;
    private final int CHANGE_USER_AREA = 3;
    private final int CHANGE_USER_AGE = 4;
    private final int CHANGE_USER_GENDER = 5;
    private final int CHANGE_USER_PSD = 6;

    private ArrayAdapter<String> provinceAdapter;
    private ArrayAdapter<String> areaDetailAdapter;

    private String imgPath;
    private View view;
    private LinearLayout changeUserInfoAgeBack;
    private EditText changeUserInfoAgeET;
    private TextView changeUserInfoAgePost;
    private LinearLayout changeUserInfoGenderBack;
    private TextView changeUserInfoGenderPost;
    private Spinner changeUserInfoGenderSpinner;
    private LinearLayout changeUserInfoLocationBack;
    private Spinner changeUserLocationSpinnerProvince;
    private Spinner changeUserLocationSpinnerCity;
    private Spinner changeUserLocationSpinnerArea;
    private LinearLayout changeUserInfoPickNameBack;
    private EditText changeUserInfoPickNameET;
    private TextView changeUserInfoPickNamePost;
    private LinearLayout changeUserInfoPsdBack;
    private EditText changeUserInfoOldPsdET;
    //    private EditText changeUserInfoPsdVerificationNumberET;
    private EditText changeUserInfoPsdET;
    private EditText changeUserInfoPsdConfirmET;
    private TextView changeUserInfoPsdPost;
    private LinearLayout changeUserIconBack;
    private TextView changeUserIconSave;
    private ImageView changeUserIconAddImg;
    private String flag;
    private TextView changeUserInfoLocationPost;
    private Spinner changeUserInfoAgeSpinner;

    private List<Integer> ageDataList;
    private ProgressDialog progressDialog;
    private Map<String, String> params = new HashMap<>();
    private int genderFlag = 10;
    private int age = 25;
    private String strResult;
    private RequestQueue queue;

    private Map<String, Object> map;
    private List<Map<String, Object>> provinceList;
    private List<String> provinceListName = new ArrayList<>();
    private List<String> provinceListId = new ArrayList<>();
    private Map<String, Object> cityMap;
    private Map<String, String> cityMapDetail;
    private String provinceId;
    private String cityId;
    private String areaId;

    private List<String> cityListId = new ArrayList<>();
    private List<String> cityListName = new ArrayList<>();

    private Map<String, String> areaMapDetil;
    private List<String> areaListId = new ArrayList<>();
    private List<Map<String, String>> areaListName = new ArrayList<>();
    private Map<String, Object> areaMap;
    private List<String> areaListDetailId = new ArrayList<>();
    private List<String> areaListDetailName = new ArrayList<>();
    private boolean isProvinceChange = false;
    private boolean isCityChange = false;
    private boolean isAreaChange = false;
    private LinearLayout changeUserLocationLoadLL;
    private ImageView changeUserLocationLoad;
    private LinearLayout changeUserLocationSpinnerLL;
    private Animation anim;
    private SwipeRefreshLayout changeUserInfoLocationSRL;
    private boolean isLoading;
    private Message message;
    private int imgHeitht;
    private int imgWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        initView();
        setContentView(view);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    public void initView() {
        flag = getIntent().getStringExtra("FLAG").toString();
        if (TextUtils.equals(flag, "ChangeUserImg")) {
            view = LayoutInflater.from(this).inflate(R.layout.activity_change_user_info_usericon_inside, null);
        } else if (TextUtils.equals(flag, "ChangeUserPickName")) {
            view = LayoutInflater.from(this).inflate(R.layout.activity_change_user_info_pickname_inside, null);
        } else if (TextUtils.equals(flag, "ChangeUserLocation")) {
            view = LayoutInflater.from(this).inflate(R.layout.activity_change_user_info_location_inside, null);
            Log.d("--ChangeUserInfoInside", "queue:" + queue);
        } else if (TextUtils.equals(flag, "ChangeUserAge")) {
            view = LayoutInflater.from(this).inflate(R.layout.activity_change_user_info_age_inside, null);
        } else if (TextUtils.equals(flag, "ChangeUserGender")) {
            view = LayoutInflater.from(this).inflate(R.layout.activity_change_user_info_gender_inside, null);
        } else if (TextUtils.equals(flag, "ChangeUserPsd")) {
            view = LayoutInflater.from(this).inflate(R.layout.activity_change_user_info_psd_inside, null);
        }
    }

    private void getProvinceJsonString() {
        if (NetWorkInfo.isNetworkAvailable(this)) {
            isLoading = true;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.getProvinceUri, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                map = JsonUtils.getMapObj(response.toString());
                                if (TextUtils.equals(map.get("status").toString(), "true")) {
                                    provinceList = JsonUtils.getListMap(map.get("data").toString());
                                    Log.d("--ChangeUserInfoInside", map.get("data").toString());
                                    provinceListName.clear();
                                    provinceListId.clear();
                                    for (Map<String, Object> map1 : provinceList
                                            ) {
                                        provinceListName.add(map1.get("name").toString());
                                        provinceListId.add(map1.get("id").toString());
                                    }
                                    Message message = new Message();
                                    message.what = 16;
                                    handler.sendMessage(message);
                                } else {
                                    Log.d("--ChangeUserInfoInside", map.get("data").toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Message message = Message.obtain();
                    message.what = 17;
                    handler.sendMessage(message);
                    Log.e("TAG", error.getMessage(), error);
                }
            });
            queue.add(jsonObjectRequest);
        } else openToast("网络链接有问题，请检查网络重试！");
    }

    @Override
    public void initData() {
        provinceListName.add(PreferencesUtils.getString(getBaseContext(), "defaultProvince", "北京"));
        cityListName.add(PreferencesUtils.getString(getBaseContext(), "defaultCity", "北京"));
        areaListDetailName.add(PreferencesUtils.getString(getBaseContext(), "defaultArea", "昌平区"));
        ageDataList = new ArrayList<>();
        for (Integer i = 10; i <= 99; i++) {
            ageDataList.add(i);
        }
        if (TextUtils.equals(flag, "ChangeUserImg")) {
            //修改头像
            changeUserIconBack = (LinearLayout) view.findViewById(R.id.changeUserIconBack);
            changeUserIconSave = (TextView) view.findViewById(R.id.changeUserIconSave);
            changeUserIconAddImg = (ImageView) view.findViewById(R.id.changeUserIconAddImg);
        } else if (TextUtils.equals(flag, "ChangeUserPickName")) {
            //修改昵称
            changeUserInfoPickNameBack = (LinearLayout) view.findViewById(R.id.changeUserInfoPickNameBack);
            changeUserInfoPickNameET = (EditText) view.findViewById(R.id.changeUserInfoPickNameET);
            changeUserInfoPickNameET.setText(getIntent().getStringExtra("string"));
            changeUserInfoPickNamePost = (TextView) view.findViewById(R.id.changeUserInfoPickNamePost);
        } else if (TextUtils.equals(flag, "ChangeUserLocation")) {
            getProvinceJsonString();
            //修改地区
            changeUserInfoLocationBack = (LinearLayout) view.findViewById(R.id.changeUserInfoLocationBack);
            changeUserLocationSpinnerProvince = (Spinner) view.findViewById(R.id.changeUserLocationSpinnerProvince);
            changeUserLocationSpinnerCity = (Spinner) view.findViewById(R.id.changeUserLocationSpinnerCity);
            changeUserLocationSpinnerArea = (Spinner) view.findViewById(R.id.changeUserLocationSpinnerArea);
            changeUserInfoLocationPost = (TextView) view.findViewById(R.id.changeUserInfoLocationPost);
            changeUserLocationSpinnerLL = (LinearLayout) view.findViewById(R.id.changeUserLocationSpinnerLL);
            changeUserInfoLocationSRL = (SwipeRefreshLayout) view.findViewById(R.id.changeUserInfoLocationSRL);
        } else if (TextUtils.equals(flag, "ChangeUserAge")) {
            //修改年龄
            changeUserInfoAgeBack = (LinearLayout) view.findViewById(R.id.changeUserInfoAgeBack);
            changeUserInfoAgeSpinner = (Spinner) view.findViewById(R.id.changeUserInfoAgeSpinner);
            changeUserInfoAgePost = (TextView) view.findViewById(R.id.changeUserInfoAgePost);

        } else if (TextUtils.equals(flag, "ChangeUserGender")) {
            //修改性别
            changeUserInfoGenderBack = (LinearLayout) view.findViewById(R.id.changeUserInfoGenderBack);
            changeUserInfoGenderSpinner = (Spinner) view.findViewById(R.id.changeUserInfoGenderSpinner);
            changeUserInfoGenderSpinner.setPrompt("请选择性别");
            changeUserInfoGenderPost = (TextView) view.findViewById(R.id.changeUserInfoGenderPost);
        } else if (TextUtils.equals(flag, "ChangeUserPsd")) {
            //修改密码
            changeUserInfoPsdBack = (LinearLayout) view.findViewById(R.id.changeUserInfoPsdBack);
            changeUserInfoOldPsdET = (EditText) view.findViewById(R.id.changeUserInfoOldPsdET);
            changeUserInfoPsdET = (EditText) view.findViewById(R.id.changeUserInfoPsdET);
            changeUserInfoPsdConfirmET = (EditText) view.findViewById(R.id.changeUserInfoPsdConfirmET);
            changeUserInfoPsdPost = (TextView) view.findViewById(R.id.changeUserInfoPsdPost);
        }
    }

    @Override
    public void initListener() {
        ArrayAdapter<Integer> ageAdapter;
        if (TextUtils.equals(flag, "ChangeUserImg")) {
            //修改头像
            changeUserIconBack.setOnClickListener(this);
            changeUserIconSave.setOnClickListener(this);
            changeUserIconAddImg.setOnClickListener(this);
        } else if (TextUtils.equals(flag, "ChangeUserPickName")) {
            //修改昵称
            changeUserInfoPickNameBack.setOnClickListener(this);
            changeUserInfoPickNamePost.setOnClickListener(this);
        } else if (TextUtils.equals(flag, "ChangeUserLocation")) {
            //修改地区
            changeUserInfoLocationBack.setOnClickListener(this);
            changeUserInfoLocationPost.setOnClickListener(this);
            changeUserInfoLocationSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (NetWorkInfo.isNetworkAvailable(ChangeUserInfoInside.this)) {
                        changeUserInfoLocationSRL.setEnabled(true);
                        if (!isLoading) {
                            getProvinceJsonString();
                        }
                    } else {
                        changeUserInfoLocationSRL.setEnabled(false);
                        changeUserInfoLocationSRL.setRefreshing(false);
                        changeUserInfoLocationSRL.post(new Runnable() {
                            @Override
                            public void run() {
                                changeUserInfoLocationSRL.setEnabled(true);
                            }
                        });
                        Toast.makeText(ChangeUserInfoInside.this, "网络未连接，请检查后重试！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (TextUtils.equals(flag, "ChangeUserAge")) {
            //修改年龄
            changeUserInfoAgeBack.setOnClickListener(this);
            changeUserInfoAgePost.setOnClickListener(this);
            ageAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, ageDataList);
            ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            changeUserInfoAgeSpinner.setAdapter(ageAdapter);// 设置显示信息
            changeUserInfoAgeSpinner.setSelection(14, true);
            changeUserInfoAgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    age = ageDataList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else if (TextUtils.equals(flag, "ChangeUserGender")) {
            //修改性别
            changeUserInfoGenderBack.setOnClickListener(this);
            changeUserInfoGenderPost.setOnClickListener(this);
            changeUserInfoGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int pos, long id) {
                    if (pos == 0) {
                        genderFlag = 10;
                    } else genderFlag = 20;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Another interface callback
                }
            });
        } else if (TextUtils.equals(flag, "ChangeUserPsd")) {
            //修改密码
            changeUserInfoPsdBack.setOnClickListener(this);
            changeUserInfoPsdPost.setOnClickListener(this);
        }
    }

    private void openToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private void getCityJsonString() {
        GetCityThread thread = new GetCityThread();
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            Log.e("TAG->onresult", "ActivityResult resultCode error");
            return;
        }
        Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            try {
                Uri originalUri = data.getData();        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
                changeUserIconAddImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(bm, 10, 10));
                //  这里开始的第二部分，获取图片的路径：
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(originalUri, null, null, null, null);

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                String imgPath1 = cursor.getString(1); // 图片文件路径
                String imgSize = cursor.getString(2); // 图片大小
                String imgName = cursor.getString(3); // 图片文件名
                imgPath = imgPath1;

                BitmapFactory.Options options = new BitmapFactory.Options();

                // 此时把options.inJustDecodeBounds 设回true，即只读边不读内容
                options.inJustDecodeBounds = true;
                // 默认是Bitmap.Config.ARGB_8888
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                //此时不会把图片读入内存，只会获取图片宽高等信息
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(originalUri), null, options);
                //上面一句和下面的类似
                //Bitmap bitmap = BitmapFactory.decodeFile(imgPath,options);
                imgHeitht = options.outHeight;
                imgWidth = options.outWidth;
            } catch (Exception e) {
                Log.e("TAG-->Error", e.toString());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //昵称
            case R.id.changeUserInfoPickNameBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.changeUserInfoPickNamePost:
                saveChangeUserInfoPickNamePost();
                changeUserInfoPickNamePost.setEnabled(false);
                break;
            //年龄
            case R.id.changeUserInfoAgeBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.changeUserInfoAgePost:
                saveChangeUserInfoAgePost();
                break;
            //性别
            case R.id.changeUserInfoGenderBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.changeUserInfoGenderPost:
                saveChangeUserInfoGenderPost();
                changeUserInfoGenderPost.setEnabled(false);
                break;
            //位置
            case R.id.changeUserInfoLocationBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.changeUserInfoLocationPost:
                saveChangeUserInfoLocation();
                changeUserInfoLocationPost.setEnabled(false);
                break;
            //密码
            case R.id.changeUserInfoPsdBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.changeUserInfoPsdPost:
                saveChangeUserInfoPsd();
                break;
            //头像
            case R.id.changeUserIconBack:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.changeUserIconSave:
                if (imgHeitht > 2560 || imgWidth > 1440) {
                    Toast.makeText(this, "图片过大,请重新选择！", Toast.LENGTH_SHORT).show();
                } else if (imgPath == null) {
                    Toast.makeText(this, "请选择图片后上传", Toast.LENGTH_SHORT).show();
                } else
                    saveChangeUserIconSave();
                break;
            case R.id.changeUserIconAddImg:
                pickPhoto();
                break;
        }
    }

    private void saveChangeUserInfoPsd() {
        if (!TextUtils.isEmpty(changeUserInfoOldPsdET.getText())
                && !TextUtils.isEmpty(changeUserInfoPsdET.getText())
                && !TextUtils.isEmpty(changeUserInfoPsdConfirmET.getText())
                && IsMatchUtil.isPsdrValid(changeUserInfoPsdET.getText().toString())
                && IsMatchUtil.isPsdrValid(changeUserInfoPsdConfirmET.getText().toString())
                && TextUtils.equals(changeUserInfoPsdET.getText().toString(), changeUserInfoPsdConfirmET.getText().toString())) {
            if (TextUtils.equals(changeUserInfoOldPsdET.getText().toString(), changeUserInfoPsdET.getText().toString())) {
                Toast.makeText(this, "新密码和原密码不能相同", Toast.LENGTH_SHORT).show();
            } else {
                changeUserInfoPsdPost.setEnabled(false);
                ChangePsdThread thread = new ChangePsdThread();
                thread.start();
            }

        } else if (TextUtils.isEmpty(changeUserInfoOldPsdET.getText())) {
            Toast.makeText(this, "原始密码不能为空", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(changeUserInfoPsdET.getText())
                || TextUtils.isEmpty(changeUserInfoPsdConfirmET.getText())) {
            Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.equals(changeUserInfoPsdET.getText().toString(), changeUserInfoPsdConfirmET.getText().toString())) {
            Toast.makeText(this, "两次输入密码不一致，请确认后提交", Toast.LENGTH_SHORT).show();
        } else if (!IsMatchUtil.isPsdrValid(changeUserInfoPsdET.getText().toString())
                || !IsMatchUtil.isPsdrValid(changeUserInfoPsdConfirmET.getText().toString())) {
            Toast.makeText(this, "密码为6-12位的数字、字母、下划线其中两种", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChangeUserInfoLocation() {
        if (PreferencesUtils.getBoolean(getBaseContext(), "isFirstChangeLocation", true) && isProvinceChange && isAreaChange && isCityChange) {
            ChangeLocationThread thread = new ChangeLocationThread();
            thread.start();
        } else {
            openToast("还没有没有改变地区哦...");
        }

    }

    private void saveChangeUserInfoGenderPost() {
        ChangeGenderThread thread = new ChangeGenderThread();
        thread.start();
    }

    private void saveChangeUserInfoAgePost() {
        ChangeAgeThread thread = new ChangeAgeThread();
        thread.start();
    }

    private void saveChangeUserInfoPickNamePost() {
        ChangePickNameThread thread = new ChangePickNameThread();
        thread.start();
    }

    private void saveChangeUserIconSave() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("等待");
        progressDialog.setMessage("正在上传...");
        progressDialog.show();
        changeUserIconSave.setEnabled(false);
        String tokenStr;
        if (PreferencesUtils.getBoolean(ChangeUserInfoInside.this, "isOtherLogin")) {
            tokenStr = PreferencesUtils.getString(ChangeUserInfoInside.this, "otherToken");
        } else {
            tokenStr = PreferencesUtils.getString(ChangeUserInfoInside.this, "token");
        }
        params.clear();
        params.put("x:token", tokenStr);
       /*
         * 上传图片到七牛
	     */
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获得七牛上传凭证uploadToken
                final String token = TokenUtlils.changeUserImgQiNiuToken();
                try {
                    if (token != null) {
                        Log.d("qiniu----", token);
                        String data = imgPath;
                        //图片名称为当前日期+随机数生成
                        String key = String.valueOf(System.currentTimeMillis());
                        key = key + imgPath.hashCode() + RandomUtils.getRandomIntString();
                        Log.d("---ChangeUserInfoInside", key);
                        UploadManager uploadManager = new UploadManager();
                        uploadManager.put(data, key, token,
                                new UpCompletionHandler() {
                                    @Override
                                    public void complete(String arg0, ResponseInfo info, JSONObject response) {
                                        // TODO Auto-generated method stub
                                        try {
                                            Log.d("1qiniu--info", info.toString());
                                            Log.d("1qiniu--response", response.toString());
                                            Map<String, String> map1 = JsonUtils.getMapStr(response.toString());
                                            if (TextUtils.equals(map1.get("status").toString(), "true")) {
                                                message = new Message();
                                                message.what = 0;
                                                handler.sendMessage(message);
                                            } else {
                                                message = new Message();
                                                message.what = 2;
                                                handler.sendMessage(message);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new UploadOptions(params, null, false,
                                        new UpProgressHandler() {
                                            Bundle bundle;

                                            @Override
                                            public void progress(String s, double v) {
//                                                    Message message = new Message();
//                                                    message.what = 1;
//                                                    bundle = new Bundle();
//                                                    bundle.putString("PERCENT", String.valueOf(v));
                                                Log.d("qiniu--", s + ":" + v);
//                                                    handler.sendMessage(message);
                                            }
                                        }, null));
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(2);
                    Log.d("--e", "e:" + e);
                }
            }
        }).start();
    }

    class ChangePsdThread extends Thread {
        @Override
        public void run() {
            String strUrlPath;
            if (PreferencesUtils.getBoolean(ChangeUserInfoInside.this, "isOtherLogin")) {
                strUrlPath = Constants.changePsdUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "otherToken");
            } else {
                strUrlPath = Constants.changePsdUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "token");
            }
            try {
                params.clear();
                params.put("old-password", MD5Utils.getMD5(changeUserInfoOldPsdET.getText().toString() + "qckj"));
                Log.d("--old-password", MD5Utils.getMD5(changeUserInfoOldPsdET.getText().toString() + "qckj"));
                params.put("new-password", MD5Utils.getMD5(changeUserInfoPsdET.getText().toString() + "qckj"));
                Log.d("--new-password", MD5Utils.getMD5(changeUserInfoPsdET.getText().toString() + "qckj"));
                String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                Log.d("--ChangePsdThread", strResult);
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Log.d("--ChangePsdThread", map.get("data").toString());
                    message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                    strUrlPath = Constants.changePsdUri + TokenUtlils.getToken();
                    strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(18);
                    }
                } else {
                    message = new Message();
                    message.what = 4;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("ChangeUserInfoInside", map.get("data").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ChangeGenderThread extends Thread {
        @Override
        public void run() {
            params = new HashMap<>();
            params.put("sex", String.valueOf(genderFlag));
            String strUrlPath;
            if (PreferencesUtils.getBoolean(ChangeUserInfoInside.this, "isOtherLogin")) {
                strUrlPath = Constants.changeGenderUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "otherToken");
            } else {
                strUrlPath = Constants.changeGenderUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Log.d("ChangeGenderThread", map.get("data").toString());
                    Message message = new Message();
                    message.what = 7;
                    handler.sendMessage(message);
                } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                    strUrlPath = Constants.changeGenderUri + TokenUtlils.getToken();
                    strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        handler.sendEmptyMessage(7);
                    } else {
                        handler.sendEmptyMessage(18);
                        isFirstGoToLogin = true;
                    }
                } else {
                    Log.d("ChangeUserInfoInside", map.get("data").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ChangeAgeThread extends Thread {
        @Override
        public void run() {
            params = new HashMap<>();
            params.put("age", String.valueOf(age));
            String strUrlPath;
            if (PreferencesUtils.getBoolean(ChangeUserInfoInside.this, "isOtherLogin")) {
                strUrlPath = Constants.changeAgeUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "otherToken");
            } else {
                strUrlPath = Constants.changeAgeUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Message message = new Message();
                    message.what = 9;
                    handler.sendMessage(message);
                } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                    strUrlPath = Constants.changeGenderUri + TokenUtlils.getToken();
                    strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        handler.sendEmptyMessage(9);
                    } else {
                        handler.sendEmptyMessage(18);
                        isFirstGoToLogin = true;
                    }
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ChangePickNameThread extends Thread {
        @Override
        public void run() {
            params = new HashMap<>();
            params.put("nickname", changeUserInfoPickNameET.getText().toString());
            String strUrlPath;
            if (PreferencesUtils.getBoolean(ChangeUserInfoInside.this, "isOtherLogin")) {
                strUrlPath = Constants.changePickNameUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "otherToken");
            } else {
                strUrlPath = Constants.changePickNameUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    message = new Message();
                    message.what = 11;
                    bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                    strUrlPath = Constants.changeGenderUri + TokenUtlils.getToken();
                    strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        message = new Message();
                        message.what = 11;
                        bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(18);
                        isFirstGoToLogin = true;
                    }
                } else {
                    message = new Message();
                    message.what = 12;
                    bundle = new Bundle();
                    bundle.putString("data", map.get("data").toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class ChangeLocationThread extends Thread {
        @Override
        public void run() {
            params.clear();
            params.put("province-id", provinceId);
            params.put("city-id", cityId);
            params.put("district-id", areaId);
            String strUrlPath;
            if (PreferencesUtils.getBoolean(ChangeUserInfoInside.this, "isOtherLogin")) {
                strUrlPath = Constants.changeAreaUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "otherToken");
            } else {
                strUrlPath = Constants.changeAreaUri + PreferencesUtils.getString(ChangeUserInfoInside.this, "token");
            }
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!TextUtils.equals(strResult, "-1") && !TextUtils.equals(strResult, "[]")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        message = new Message();
                        message.what = 14;
                        handler.sendMessage(message);
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.changeAreaUri + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            message = new Message();
                            message.what = 14;
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(18);
                            isFirstGoToLogin = true;
                        }
                    } else {
                        message = new Message();
                        message.what = 15;
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

    class GetCityThread extends Thread {
        @Override
        public void run() {
            super.run();
            isLoading = true;
            params = new HashMap<>();
            params.put("id", provinceId);
            strResult = HttpUtils.submitPostData(Constants.getProvinceCityUri, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        cityMap = JsonUtils.getMapObj(map.get("data").toString());
                        //取得城市
                        cityMapDetail = JsonUtils.getMapStr(cityMap.get("city").toString());
                        cityListId.clear();
                        cityListName.clear();
                        for (Map.Entry entry : cityMapDetail.entrySet()) {
                            cityListId.add(entry.getKey().toString());
                            cityListName.add(cityMapDetail.get(entry.getKey().toString()).toString());
                        }
                        Message message = new Message();
                        message.what = 13;
                        handler.sendMessage(message);
                        //取得区县
                        areaMap = JsonUtils.getMapObj(cityMap.get("district").toString());
                        areaListId.clear();
                        areaListName.clear();
                        for (Map.Entry entry : areaMap.entrySet()) {
                            areaListId.add(entry.getKey().toString());
                            areaListName.add((Map<String, String>) areaMap.get(entry.getKey().toString()));
                        }
                    }
                } else if (!strResult.equals("-1")) {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void finish() {
//        if (!(progressDialog == null)) {
//            if (TextUtils.equals(flag, "ChangeUserImg") && progressDialog.isShowing()) {
//            }
//        } else
//        {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
//        }
    }
}