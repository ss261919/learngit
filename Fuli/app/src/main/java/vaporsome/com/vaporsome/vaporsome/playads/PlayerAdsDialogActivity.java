package vaporsome.com.vaporsome.vaporsome.playads;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

/**
 * Created by ${Bash} on 2016/3/29.
 */
public class PlayerAdsDialogActivity extends Activity implements View.OnClickListener {
    private LinearLayout playerAdsLL;
    private LinearLayout adsVideoLoadLL;

    private VideoView videoView;
    private int width, height;
    private TextView adsVideoWatchFullVideoView;
    private FrameLayout adsVideoViewLL;
    private ImageView adsVideoLoadImg;

    private Intent intent;
    private String bag_id;
    private String companyLogo;
    private String companyName;
    private String companyComment;
    private boolean isFirstGoToLogin = true;

    private Uri uri;
    private Animation animation;
    private int old_duration;
    private String video_url;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_ads_video);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        initView();
        loadData();
        initListener();
    }

    private void loadData() {
        bundle = getIntent().getExtras();
        video_url = bundle.getString("video_url");
        bag_id = bundle.getString("bag-id");
        companyLogo = bundle.getString("companyLogo");
        companyName = bundle.getString("companyName");
        companyComment = bundle.getString("companyComment");
        /***
         * 将播放器关联上一个音频或者视频文件
         * videoView.setVideoURI(Uri uri)
         * videoView.setVideoPath(String path)
         * 以上两个方法都可以。
         */
        if (NetWorkInfo.isNetworkAvailable(PlayerAdsDialogActivity.this)) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //land
                videoView.setLayoutParams(new FrameLayout.LayoutParams(height, width));
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //port
                videoView.setLayoutParams(new FrameLayout.LayoutParams(width, (int) (height * 0.48)));
            }
            uri = Uri.parse(video_url);
            videoView.setVideoURI(uri);
            videoView.start();
            old_duration = 0;
//            final Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    int duration = videoView.getCurrentPosition();
//                    if (old_duration == duration && videoView.isPlaying()) {
//                        adsVideoLoadLL.setVisibility(View.VISIBLE);
//                        adsVideoLoadImg.setAnimation(animation);
//                        adsVideoLoadImg.setVisibility(View.VISIBLE);
//                    } else {
//                        adsVideoLoadImg.setVisibility(View.GONE);
//                        adsVideoLoadLL.setVisibility(View.VISIBLE);
//                    }
//                    old_duration = duration;
//                }
//            };
//            Handler handler = new Handler() {
//            };
//            handler.postDelayed(runnable, 150);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            adsVideoLoadImg.setVisibility(View.VISIBLE);
                            adsVideoLoadLL.setVisibility(View.VISIBLE);
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                            if (mp.isPlaying()) {
                                adsVideoLoadImg.setVisibility(View.GONE);
                                adsVideoLoadLL.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    }
                });
            }
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    adsVideoLoadImg.setVisibility(View.GONE);
                    adsVideoLoadLL.setVisibility(View.VISIBLE);
                    //缓冲完成就隐藏
                }
            });
        } else {
            Toast.makeText(this, "网络链接有问题！", Toast.LENGTH_LONG).show();
        }
    }

    public void initView() {
        videoView = (VideoView) findViewById(R.id.adsVideoViewPlayer);
        adsVideoViewLL = (FrameLayout) findViewById(R.id.adsVideoViewLL);
        playerAdsLL = (LinearLayout) findViewById(R.id.playerAdsLL);
        adsVideoLoadLL = (LinearLayout) findViewById(R.id.adsVideoLoadLL);
        adsVideoLoadImg = (ImageView) findViewById(R.id.adsVideoLoadImg);
        adsVideoWatchFullVideoView = (TextView) findViewById(R.id.adsVideoWatchFullVideoView);
        animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.load);
        animation.start();
        adsVideoLoadImg.setAnimation(animation);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //land
            videoView.setLayoutParams(new FrameLayout.LayoutParams(height, width));
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //port
            videoView.setLayoutParams(new FrameLayout.LayoutParams(width, (int) (height * 0.48)));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void initListener() {
        videoView.setOnClickListener(this);
        /**
         * 视频或者音频到结尾时触发的方法
         */

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                Bundle bundle = new Bundle();
//                bundle.putString("bag_id", bag_id);
//                setResult(1, getIntent().putExtras(bundle));
                setResult(1);
                finish();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                uri = Uri.parse(video_url);
                videoView.setVideoURI(uri);
                videoView.start();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
    }


    /**
     * 设置videiview的全屏和窗口模式
     *
     * @param paramsType 标识 1为全屏模式 2为窗口模式
     */
    public void setVideoViewLayoutParams(int paramsType) {
        //全屏模式
        if (1 == paramsType) {
            //设置充满整个父布局
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            //设置相对于父布局四边对齐
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //为VideoView添加属性
            videoView.setLayoutParams(LayoutParams);
        } else {
            //窗口模式
            //获取整个屏幕的宽高
            DisplayMetrics DisplayMetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(DisplayMetrics);
            //设置窗口模式距离边框50
            int videoHeight = DisplayMetrics.heightPixels - 50;
            int videoWidth = DisplayMetrics.widthPixels - 50;
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
            //设置居中
            LayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            //为VideoView添加属性
            videoView.setLayoutParams(LayoutParams);
        }
    }
}
