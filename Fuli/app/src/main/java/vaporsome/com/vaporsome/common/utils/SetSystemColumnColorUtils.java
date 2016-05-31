package vaporsome.com.vaporsome.common.utils;

import android.app.Activity;
import android.os.Build;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.SystemBarTintManager;

/**
 * Created by ${Bash} on 2016/5/25.
 */
public class SetSystemColumnColorUtils {
    /**
     * 设置状态栏背景状态
     */
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
        }
    }
}
