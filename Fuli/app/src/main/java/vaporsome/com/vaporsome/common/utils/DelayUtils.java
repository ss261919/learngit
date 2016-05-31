package vaporsome.com.vaporsome.common.utils;

import android.os.Handler;
import android.view.View;


/**
 * Created by ${Bash} on 2016/5/9.
 */
public class DelayUtils {
    public static void viewEnableDelay(Handler handler, final View view, long f) {
        view.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, f);
    }
}
