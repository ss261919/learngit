package vaporsome.com.vaporsome.common.utils;

import com.android.volley.RequestQueue;

import java.util.Map;

import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/5/20.
 */
public class PostUtils {
    private static boolean isGetData = false;
    private static String strResult;

    public static String postData(final Map<String, String> params, String url) {
        final String strUrlPath;
        if (PreferencesUtils.getBoolean(IApplication.applicationContext, "isOtherLogin")) {
            strUrlPath = url + PreferencesUtils.getString(IApplication.applicationContext, "otherToken");
        } else {
            strUrlPath = url + PreferencesUtils.getString(IApplication.applicationContext, "token");
        }
        class PostThread extends Thread {
            @Override
            public void run() {
                super.run();
                strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            }
        }
        do {
            if (!isGetData) {
                if (!(strResult == null))
                    isGetData = true;
                return strResult;
            }
        } while (isGetData);
        return strResult;
    }
}
