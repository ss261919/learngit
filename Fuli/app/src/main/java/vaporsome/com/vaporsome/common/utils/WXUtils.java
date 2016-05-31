package vaporsome.com.vaporsome.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/5/12.
 */
public class WXUtils {
    private static IWXAPI api;

    //微信分享网页
    public static void shareWXWebPage(int wxSceneSession, String webPageUrl, String title,String description) {
//        Log.d("--RedResultActivity", webPageUrl);
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = webPageUrl;
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = description;
        msg.thumbData = BitMapUtils.Bitmap2Bytes(BitMapUtils.ReadBitmapById(IApplication.applicationContext, R.mipmap.ic_launcher));
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webapge");//transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = wxSceneSession;
        if (!IApplication.api.isWXAppInstalled()) {
            Toast.makeText(IApplication.applicationContext, "还没有安装微信", Toast.LENGTH_SHORT).show();
        } else {
            if (!(IApplication.api == null)) {
                IApplication.api.sendReq(req);
            } else {
                api = WXAPIFactory.createWXAPI(IApplication.applicationContext, Constants.APP_ID, false);
                api.registerApp(Constants.APP_ID);
                api.sendReq(req);
            }
        }
    }

    //微信分享图片，imgId 资源文件id
    public static void shareWXImg(int wxSceneSession, int imgId) {
        Bitmap bmp = BitmapFactory.decodeResource(IApplication.applicationContext.getResources(), imgId);

        //初始化WXImageObject和WXMediaMessage对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        bmp.recycle();
        msg.thumbData = BitMapUtils.Bitmap2Bytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");//transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = wxSceneSession;
        if (!IApplication.api.isWXAppInstalled()) {
            Toast.makeText(IApplication.applicationContext, "还没有安装微信", Toast.LENGTH_SHORT).show();
        } else {
            if (!(IApplication.api == null)) {
                IApplication.api.sendReq(req);
            } else {
                api = WXAPIFactory.createWXAPI(IApplication.applicationContext, Constants.APP_ID, false);
                api.registerApp(Constants.APP_ID);
                api.sendReq(req);
            }
        }
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
