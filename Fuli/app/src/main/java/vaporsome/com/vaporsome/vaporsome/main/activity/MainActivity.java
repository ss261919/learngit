package vaporsome.com.vaporsome.vaporsome.main.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.SetSystemColumnColorUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.fragment.DuoBaoFragment;
import vaporsome.com.vaporsome.vaporsome.main.fragment.PersonCenterFragment;
import vaporsome.com.vaporsome.vaporsome.main.fragment.RedFragment;
import vaporsome.com.vaporsome.vaporsome.main.fragment.ShareFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
//                    thread.destroy();
                    isSuccess = true;
                    break;
                case 3:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        PreferencesUtils.putBoolean(getBaseContext(), "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
                case 6:
                    bundle = msg.getData();
                    try {
                        if (!TextUtils.equals(getVersionName(), bundle.get("version").toString())) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.show();
                            Window window = alertDialog.getWindow();
                            window.setContentView(R.layout.version_update);
                            TextView okTV = (TextView) window.findViewById(R.id.quitOk);
                            TextView cancelTV = (TextView) window.findViewById(R.id.quitCancel);
                            okTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    goToAPPMarket(Constants.yybAppID);
                                    goToAPPMarket(bundle.getString("url"));
                                    alertDialog.cancel();
                                }
                            });
                            cancelTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.cancel();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    break;
            }
        }
    };
    private RedFragment redFragment;
    private ShareFragment shareFragment;
    private PersonCenterFragment personCenterFragment;
    private LinearLayout redFragmentLinearLayout;
    private LinearLayout shareFragmentLinearLayout;
    private LinearLayout personFragmentLinearLayout;
    private boolean isFirstClickShareFragment = true;
    private boolean isFirstClickPersonFragment = true;
    private boolean isFirstClickRedFragment = true;
    private boolean isFirstClickDuoBaoFragment = true;
    private ImageView img_Red;
    private ImageView img_Share;
    private ImageView img_PersonCenter;
    private FrameLayout frameLayout;

    private double longitude;
    private double latitude;
    private Map<String, String> params = new HashMap<>();
    private int screenWidth;
    private int screenHeigh;
    //SplashImg
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String imgUrl;
    private boolean isSuccess = false;
    private DownSplashStringThread thread;
    private String imgUrlNew;

    private String province_id;
    private String city_id;
    private String district_id;
    private Message message;
    private Calendar calendar;
    private Intent intent;
    private Bundle bundle;
    private LinearLayout duoBaoFragmentLinearlayout;
    private ImageView img_DuoBao;
    private DuoBaoFragment duoBaoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetSystemColumnColorUtils.setTranslucentStatus(this);
        setContentView(R.layout.activity_main);
        BaseActivity.activityList.add(MainActivity.this);
        initView();
        initData();
        initListener();
        downImg();
    }


    public void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        redFragmentLinearLayout = (LinearLayout) findViewById(R.id.redFragment);
        img_Red = (ImageView) findViewById(R.id.img_Red);
        img_Red.setBackgroundResource(R.mipmap.grab_red_focus);
        shareFragmentLinearLayout = (LinearLayout) findViewById(R.id.shareFragment);
        img_Share = (ImageView) findViewById(R.id.img_Share);
        img_Share.setBackgroundResource(R.mipmap.share_img_normal);
        personFragmentLinearLayout = (LinearLayout) findViewById(R.id.personFragment);
        img_PersonCenter = (ImageView) findViewById(R.id.img_PersonCenter);
        img_PersonCenter.setBackgroundResource(R.mipmap.person_center_normal);
        duoBaoFragmentLinearlayout = (LinearLayout) findViewById(R.id.duoBaoFragment);
        img_DuoBao = (ImageView) findViewById(R.id.img_DuoBao);
        img_DuoBao.setBackgroundResource(R.mipmap.yiyuanduobao_normal);
    }

    public void initData() {
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        frameLayout.measure(w, h);
        int height = frameLayout.getMeasuredHeight();
        int width = frameLayout.getMeasuredWidth();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(MainActivity.this));
        options = IApplication.options;
        redFragment = new RedFragment();
        shareFragment = new ShareFragment();
        personCenterFragment = new PersonCenterFragment();
        duoBaoFragment = new DuoBaoFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, redFragment, "redFragment").show(redFragment).commit();
    }

    public void initListener() {
        redFragmentLinearLayout.setOnClickListener(this);
        shareFragmentLinearLayout.setOnClickListener(this);
        personFragmentLinearLayout.setOnClickListener(this);
        duoBaoFragmentLinearlayout.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redFragment:
                getSupportFragmentManager().beginTransaction().hide(personCenterFragment).hide(shareFragment).hide(duoBaoFragment).show(redFragment).commit();
                redFragment.onResume();
                img_Share.setBackgroundResource(R.mipmap.share_img_normal);
                img_Red.setBackgroundResource(R.mipmap.grab_red_focus);
                img_PersonCenter.setBackgroundResource(R.mipmap.person_center_normal);
                img_DuoBao.setBackgroundResource(R.mipmap.yiyuanduobao_normal);
                break;
            case R.id.shareFragment:
                if (isFirstClickShareFragment) {
//                    if (isFirstClickPersonFragment) {
//                        if (isFirstClickDuoBaoFragment) {
//                            getFragmentManager().beginTransaction().hide(redFragment).commit();
//                        } else
//                            getFragmentManager().beginTransaction().hide(redFragment).hide(duoBaoFragment).commit();
//                    } else {
//                        if (isFirstClickDuoBaoFragment) {
//                            getFragmentManager().beginTransaction().hide(redFragment).hide(personCenterFragment).commit();
//                        } else
//                            getFragmentManager().beginTransaction().hide(redFragment).hide(personCenterFragment).hide(duoBaoFragment).commit();
//                    }
                    getSupportFragmentManager().beginTransaction().hide(redFragment).hide(personCenterFragment).hide(duoBaoFragment).commit();
                    getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, shareFragment, "shareFragment").show(shareFragment).commit();
                    isFirstClickShareFragment = false;
                } else {
//                    if (isFirstClickDuoBaoFragment) {
//                        getFragmentManager().beginTransaction().hide(redFragment).hide(personCenterFragment).show(shareFragment).commit();
//                    } else
                    getSupportFragmentManager().beginTransaction().hide(redFragment).hide(personCenterFragment).hide(duoBaoFragment).show(shareFragment).commit();
                }
                img_Share.setBackgroundResource(R.mipmap.share_img_focus);
                img_Red.setBackgroundResource(R.mipmap.grab_red_normal);
                img_PersonCenter.setBackgroundResource(R.mipmap.person_center_normal);
                img_DuoBao.setBackgroundResource(R.mipmap.yiyuanduobao_normal);
                break;
            case R.id.personFragment:
                if (isFirstClickPersonFragment) {
//                    if (isFirstClickShareFragment) {
//                        if (isFirstClickDuoBaoFragment) {
//                            getFragmentManager().beginTransaction().hide(redFragment).commit();
//                        } else
//                            getFragmentManager().beginTransaction().hide(redFragment).hide(duoBaoFragment).commit();
//                    } else {
//                        if (isFirstClickDuoBaoFragment) {
//                            getFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).commit();
//                        } else
//                            getFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).hide(duoBaoFragment).commit();
//                    }
                    isFirstClickPersonFragment = false;
                    getSupportFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).hide(duoBaoFragment).commit();
                    getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, personCenterFragment, "personFragment").show(personCenterFragment).commit();
                } else {
//                    if (isFirstClickDuoBaoFragment) {
//                        getFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).show(personCenterFragment).commit();
//                    } else
                    getSupportFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).hide(duoBaoFragment).show(personCenterFragment).commit();
                }
                img_Share.setBackgroundResource(R.mipmap.share_img_normal);
                img_Red.setBackgroundResource(R.mipmap.grab_red_normal);
                img_PersonCenter.setBackgroundResource(R.mipmap.person_center_focus);
                img_DuoBao.setBackgroundResource(R.mipmap.yiyuanduobao_normal);
                break;
            case R.id.duoBaoFragment:
                if (isFirstClickDuoBaoFragment) {
                    getSupportFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).hide(personCenterFragment).commit();
                    getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, duoBaoFragment, "duoBaoFragment").show(duoBaoFragment).commit();
                    isFirstClickDuoBaoFragment = false;
                } else {
                    getSupportFragmentManager().beginTransaction().hide(redFragment).hide(shareFragment).hide(personCenterFragment).show(duoBaoFragment).commit();
                }
                img_Share.setBackgroundResource(R.mipmap.share_img_normal);
                img_Red.setBackgroundResource(R.mipmap.grab_red_normal);
                img_PersonCenter.setBackgroundResource(R.mipmap.person_center_normal);
                img_DuoBao.setBackgroundResource(R.mipmap.yiyuanduobao_pressed);
                break;

        }
    }

    private void downImg() {
        if (NetWorkInfo.isNetworkAvailable(MainActivity.this)) {
            imgUrl = PreferencesUtils.getString(getBaseContext(), Constants.splashImgUrl, "");
            thread = new DownSplashStringThread();
            thread.start();
            calendar = Calendar.getInstance();
            final int month = calendar.get(Calendar.MONTH) + 1;
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (TextUtils.equals(PreferencesUtils.getString(MainActivity.this, "data", "511").toString(), month + "" + day)) {
            } else {
                IsUpdateThread thread = new IsUpdateThread();
                thread.start();
                PreferencesUtils.putString(MainActivity.this, "data", month + "" + day);
            }
        }
    }

    FileOutputStream phone_outStream = null;

    /**
     * 保存方法
     */
    private void saveSplashImg(byte[] logoBuf) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.qingcheng.crzay/");
            if (!file.exists()) {
                file.mkdirs();
            }
            phone_outStream = getBaseContext().openFileOutput(Constants.splashImgUrl, Context.MODE_PRIVATE);
            phone_outStream.write(logoBuf);
            phone_outStream.flush();
            PreferencesUtils.putString(getBaseContext(), Constants.splashImgUrl, imgUrlNew);
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (phone_outStream != null) {
                try {
                    phone_outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!(phone_outStream == null))
            if (phone_outStream != null) {
                try {
                    phone_outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    class DownSplashStringThread extends Thread {
        @Override
        public void run() {
            super.run();
            String str = HttpUtils.submitGet(Constants.splashActivityUri);
            try {
                if (!str.isEmpty() && !(str.equals("-1"))) {
                    Map<String, Object> map = JsonUtils.getMapObj(str);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            Map<String, String> strMap = JsonUtils.getMapStr(map.get("data").toString());
                            if (screenWidth <= 768) {
                                imgUrlNew = strMap.get("file_768_url").toString();
                            } else if (screenWidth > 768) {
                                imgUrlNew = strMap.get("file_1440_url").toString();
                            }
                            if (!TextUtils.equals(imgUrl, imgUrlNew)) {
                                imageLoader.loadImage(imgUrlNew, options, new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String imageUri, View view) {

                                    }

                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                    }

                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        PreferencesUtils.putString(getBaseContext(), "splashImgUrl", imageUri);
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        boolean res = loadedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);//将图像读取到logoStream中
                                        byte[] logoBuf = byteArrayOutputStream.toByteArray();//将图像保存到byte[]中
                                        saveSplashImg(logoBuf);
                                    }

                                    @Override
                                    public void onLoadingCancelled(String imageUri, View view) {
                                    }
                                });
                            }
                        }
                    } else {
                    }
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(5);
                e.printStackTrace();
            }
        }
    }

    class IsUpdateThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            strUrlPath = Constants.getVersionUrl;
            String strResult = HttpUtils.submitGet(strUrlPath);
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!TextUtils.equals(map.get("data").toString(), "[]")) {
                            Map<String, String> map1 = JsonUtils.getMapStr(map.get("data").toString());
                            message = new Message();
                            message.what = 6;
                            bundle = new Bundle();
                            bundle.putString("version", map1.get("version"));
                            bundle.putString("url", map1.get("url"));
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } else {
                            message = new Message();
                            message.what = 7;
                            handler.sendMessage(message);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.getVersionUrl + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            if (!TextUtils.equals(map.get("data").toString(), "[]")) {
                                Map<String, String> map1 = JsonUtils.getMapStr(map.get("data").toString());
                                message = new Message();
                                message.what = 6;
                                bundle = new Bundle();
                                bundle.putString("version", map1.get("version"));
                                message.setData(bundle);
                                handler.sendMessage(message);
                            } else if (TextUtils.equals(map.get("data").toString(), "[]")) {
                                handler.sendEmptyMessage(7);
                            }
                        } else {
                            handler.sendEmptyMessage(7);
                        }
                    } else {
                        handler.sendEmptyMessage(7);
                    }
                } else if (strResult.equals("-1")) {
                    handler.sendEmptyMessage(7);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(7);
                e.printStackTrace();
            }
        }
    }

    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String versionName = packInfo.versionName;
        int versionCode = packInfo.versionCode;
        return versionName;
    }

    private void goToAPPMarket(String url) {
        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        //打开浏览器
        if (intent.resolveActivity(getPackageManager()) != null) { //有浏览器
            startActivity(intent);
        } else { //天哪，这还是智能手机吗？
            Toast.makeText(MainActivity.this, "天啊，您连浏览器都没有，您买个手机干啥？", Toast.LENGTH_SHORT).show();
        }
    }

}