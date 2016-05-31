package vaporsome.com.vaporsome.vaporsome.main.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.logging.LogRecord;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/3/30.
 */
public class ShareImageDialogActivity extends Activity {
    private int width;
    private int height;
    private ImageView shareImageDialogActivityImg;
    private String file_url_small;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    long firstTime = 0;
    private ImageView shareImageDialogActivityImgLoad;
    private String file_url_large;

    private Animation animation;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    shareImageDialogActivityImg.setTag(file_url_large);
                    imageLoader.loadImage(file_url_large, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            shareImageDialogActivityImgLoad.setVisibility(View.GONE);
                            shareImageDialogActivityImg.setBackgroundResource(R.drawable.imagedownload_fail);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (shareImageDialogActivityImg.getTag().equals(imageUri)) {
                                shareImageDialogActivityImg.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                                shareImageDialogActivityImg.setImageBitmap(loadedImage);
                                shareImageDialogActivityImgLoad.setVisibility(View.GONE);
                                shareImageDialogActivityImgLoad.clearAnimation();
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    });
                    break;
            }
        }
    };
    private RelativeLayout shareImageDialogActivityLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_img_dialog_activity);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(ShareImageDialogActivity.this));
        options = IApplication.options;
        initData();
        initView();
    }

    private void initData() {
        file_url_small = getIntent().getStringExtra("file_url_small");
        file_url_large = getIntent().getStringExtra("file_url_large");
    }

    private void initView() {
        shareImageDialogActivityImg = (ImageView) findViewById(R.id.shareImageDialogActivityImg);
        shareImageDialogActivityImgLoad = (ImageView) findViewById(R.id.shareImageDialogActivityImgLoad);
        shareImageDialogActivityLL = (RelativeLayout) findViewById(R.id.shareImageDialogActivityLL);
        shareImageDialogActivityImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in_main, R.anim.fade_out_player_ads_video);
            }
        });
        shareImageDialogActivityImg.setTag(file_url_small);
        imageLoader.loadImage(file_url_small, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                shareImageDialogActivityImgLoad.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(ShareImageDialogActivity.this, R.anim.load);
                shareImageDialogActivityImgLoad.startAnimation(animation);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (shareImageDialogActivityImg.getTag().equals(imageUri)) {
                    shareImageDialogActivityImg.setImageBitmap(loadedImage);
                    Message message = new Message();
                    handler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_main, R.anim.fade_out_player_ads_video);
    }
}
