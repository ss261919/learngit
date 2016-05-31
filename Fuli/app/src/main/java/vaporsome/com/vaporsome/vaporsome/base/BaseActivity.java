package vaporsome.com.vaporsome.vaporsome.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.RecycleUtil;


public abstract class BaseActivity extends Activity {
    public View rootView;
    public static ArrayList<Activity> activityList = new ArrayList<Activity>();
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activityList.add(this);
        setTranslucentStatus();
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
        }
    }

    /**
     * 初始化视图
     * 注：只做控件的获取不做数据绑定等处理
     */
    public abstract void initView();

    /**
     * 初始化数据
     * 注：数据处理相关的
     */
    public abstract void initData();

    /**
     * 初始化监听器
     */
    public abstract void initListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
        if (rootView != null) {
            RecycleUtil.recycleView((ViewGroup) rootView);
        }
    }

    /**
     * 程序退出
     */
    public static void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        activityList.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.gc();
        System.exit(0);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }
}
