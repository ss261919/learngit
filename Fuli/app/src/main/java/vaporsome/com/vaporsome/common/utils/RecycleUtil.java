package vaporsome.com.vaporsome.common.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;


public class RecycleUtil {

    /**
     * 递归将视图布局里面的界面VIEW回收
     *
     * @param root
     */
    public static void recycleView(ViewGroup root) {
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = root.getChildAt(i);
            if (v instanceof ViewGroup) {
                recycleView((ViewGroup) v);
            } else {
                v = null;
            }
        }
    }

    /**
     * 程序退出时，关闭所有Activity
     */
    public static void closeAllActivitys(){
        for(Activity activity : BaseActivity.activityList){
            activity.finish();
        }
    }

}
