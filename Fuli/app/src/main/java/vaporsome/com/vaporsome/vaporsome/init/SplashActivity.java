package vaporsome.com.vaporsome.vaporsome.init;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;

import cn.jpush.android.api.JPushInterface;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;

public class SplashActivity extends BaseActivity {
    private ImageView splashImage;
    private int screenWidth;
    private int screenHeight;
    SoundPool soundPool;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent it = null;
            switch (msg.what) {
                case 0:
                    it = new Intent(getBaseContext(), GuideActivity.class);
                    break;
                case 1:
                    if (PreferencesUtils.getBoolean(SplashActivity.this, "isLogin", false)) {
                        it = new Intent(getBaseContext(), MainActivity.class);
                    } else it = new Intent(getBaseContext(), LoginActivity.class);
                    break;
                default:
                    break;
            }
            startActivity(it);
            finish();
        }

    };


    //    private WebView webView;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();

        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        PreferencesUtils.putString(getBaseContext(), "screenWidth", String.valueOf(screenWidth));
        PreferencesUtils.putString(getBaseContext(), "screenHeight", String.valueOf(screenHeight));
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private void readSplashImg() {
        FileInputStream phone_inputStream = null;
        try {
            byte[] bytes = new byte[1024 * 1024 * 8];
            phone_inputStream = this.openFileInput(Constants.splashImgUrl);
            phone_inputStream.read(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                splashImage.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void anim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(splashImage, "alpha", 0.2f, 1f, 0.8f, 1f);
        animator.setDuration(1500);
        animator.start();
    }

    @Override
    public void initView() {
        splashImage = (ImageView) findViewById(R.id.splashImage);
        if (PreferencesUtils.getString(getBaseContext(), Constants.splashImgUrl, "").isEmpty()) {
            splashImage.setBackgroundResource(R.mipmap.splash_img);
        } else {
            readSplashImg();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 10);
        }
        anim();
        soundPool.load(getBaseContext(), R.raw.classic, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1, 1, 1, 0, 0, 14);
                splash();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    private void splash() {
        boolean isFirst = PreferencesUtils.getBoolean(getBaseContext(), "isFirst", true);
        if (isFirst) {
//            if (isVideoFinsh())
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
//            if (isVideoFinsh())
            //判断登录状态
            handler.sendEmptyMessageDelayed(1, 2000);
        }
    }

    private boolean isVideoFinsh() {
        return true;
    }

    @Override
    public void onDestroy() {
        soundPool.release();
        super.onDestroy();
    }
}

