package vaporsome.com.vaporsome.common.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/4/9.
 */
public class TokenUtlils {
    private static boolean isEnable = false;
    private static Map<String, String> params;
    private static boolean isGetToken = false;
    public static String token;

    public static String getToken() {
        params = new HashMap<>();
        params.put("mobile", PreferencesUtils.getString(IApplication.applicationContext, "mobile"));
//        Log.d("--getToken", PreferencesUtils.getString(IApplication.applicationContext, "mobile"));
        params.put("password", PreferencesUtils.getString(IApplication.applicationContext, "password"));
//        Log.d("--getToken", PreferencesUtils.getString(IApplication.applicationContext, "password"));
        GetTokenThread getTokenThread = new GetTokenThread();
        getTokenThread.start();
        do {
            if (isGetToken) {
                if (!PreferencesUtils.getBoolean(IApplication.applicationContext, "isOtherLogin")) {
                    return token;
                } else {
                    return PreferencesUtils.getString(IApplication.applicationContext, "isOtherLogin");
                }
            }
        } while (!isGetToken);
        return token;
    }

    static class GetTokenThread extends Thread {
        public void run() {
            //服务器请求路径
            String strUrlPath = Constants.phoneLoginUri;
            try {
                String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                Map<String, Object> map = JsonUtils.getMapObj(strResult);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Map<String, Object> map2 = JsonUtils.getMapObj(map.get("data").toString());
                    PreferencesUtils.putString(IApplication.applicationContext, "token", map2.get("token").toString());
                    token = map2.get("token").toString();
//                    Log.d("--GetTokenThread", token);
                    isGetToken = true;
                } else {
                    isGetToken = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 获得修改头像七牛上传凭证uploadtoken
	 */
    public static String changeUserImgQiNiuToken() {
        String tokenStr;
        if (PreferencesUtils.getBoolean(IApplication.applicationContext, "isOtherLogin")) {
            tokenStr = PreferencesUtils.getString(IApplication.applicationContext, "otherToken");
        } else {
            tokenStr = PreferencesUtils.getString(IApplication.applicationContext, "token");
        }
        String str = HttpUtils.submitGet(Constants.qiNiuTokenUri + tokenStr);
        Map<String, Object> map = null;
        String token = null;
        try {
            if (!str.equals("-1") && !(str.length() == 0)) {
                map = JsonUtils.getMapObj(str);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Map<String, Object> map2 = JsonUtils.getMapObj(map.get("data").toString());
                    token = map2.get("qiniu_token").toString();
                    return token;
                } else if (TextUtils.equals(map.get("status").toString(), "false")) {
                    String string = HttpUtils.submitPost(Constants.qiNiuTokenUri + TokenUtlils.getToken());
                    map = JsonUtils.getMapObj(str);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        Map<String, Object> map2 = JsonUtils.getMapObj(map.get("data").toString());
                        token = map2.get("qiniu_token").toString();
                        return token;
                    } else {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 获得晒图七牛上传凭证uploadtoken
	 */
    public static String shareImgQiNiuToken() {
        String loginToken;
        if (PreferencesUtils.getBoolean(IApplication.applicationContext, "isOtherLogin")) {
            loginToken = PreferencesUtils.getString(IApplication.applicationContext, "otherToken");
        } else {
            loginToken = PreferencesUtils.getString(IApplication.applicationContext, "token");
        }
        String str = HttpUtils.submitGet(Constants.shareImgQiNiuTokenUri + loginToken);
        Map<String, Object> map = null;
        String token = null;
        try {
            map = JsonUtils.getMapObj(str);
            if (TextUtils.equals(map.get("status").toString(), "true")) {
                Map<String, Object> map2 = JsonUtils.getMapObj(map.get("data").toString());
                token = map2.get("qiniu_token").toString();
//                Log.d("----qiniu_token", map2.get("qiniu_token").toString());
                return token;
            } else if (TextUtils.equals(map.get("status").toString(), "false")) {
                String string = HttpUtils.submitGet(Constants.shareImgQiNiuTokenUri + TokenUtlils.getToken());
                map = JsonUtils.getMapObj(string);
                if (TextUtils.equals(map.get("status").toString(), "true")) {
                    Map<String, Object> map2 = JsonUtils.getMapObj(map.get("data").toString());
                    token = map2.get("qiniu_token").toString();
//                    Log.d("----qiniu_token", map2.get("qiniu_token").toString());
//                    Log.d("--TokenUtlils", token);
                    return token;
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
