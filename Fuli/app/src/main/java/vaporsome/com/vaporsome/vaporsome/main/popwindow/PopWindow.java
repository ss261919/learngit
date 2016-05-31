package vaporsome.com.vaporsome.vaporsome.main.popwindow;

import android.app.Activity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2016/3/25.
 */
public class PopWindow {
    /**
     * 显示popWindow
     */
    public static void showPop(final PopupWindow popWindow, final Activity activity,View parent, int x, int y, int postion) {
        popWindow.showAtLocation(parent, Gravity.CENTER, x, y);
        popWindow.setFocusable(true);
        //点击其他地方消失
        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closePopWindow(popWindow,activity);
                return false;
            }
        });
        popWindow.update();
    }
    public static void closePopWindow(PopupWindow popWindow, Activity activity) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.alpha = 1f;
            activity.getWindow().setAttributes(params);
        }
    }
}
