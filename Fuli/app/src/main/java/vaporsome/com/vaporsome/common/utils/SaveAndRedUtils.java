package vaporsome.com.vaporsome.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/5/5.
 */
public class SaveAndRedUtils {
    /**
     * 保存方法
     */
    public static boolean saveImg(Bitmap bitmap, String imgUrl) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean res = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);//将图像读取到logoStream中
//        Log.d("---res:", "res:" + res);
        byte[] logoBuf = byteArrayOutputStream.toByteArray();//将图像保存到byte[]中
//        Log.d("--logoBuf.length:", "logoBuf.length:" + logoBuf.length);
        FileOutputStream phone_outStream = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.qingcheng.crzay/");
            if (!file.exists()) {
                file.mkdirs();
            }
            phone_outStream = IApplication.applicationContext.openFileOutput(imgUrl, Context.MODE_PRIVATE);
            phone_outStream.write(logoBuf);
            phone_outStream.flush();
//            Log.d("----logoBuf.length", "logoBuf.length:" + logoBuf);
//            Log.d("--SplashActivity", "已经保存");
            PreferencesUtils.putString(IApplication.applicationContext, imgUrl, imgUrl);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            if (phone_outStream != null) {
                try {
                    phone_outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap readSplashImg(String imgUrl) {
        FileInputStream phone_inputStream = null;
        try {
            byte[] bytes = new byte[1024 * 1024 * 8];
            phone_inputStream = IApplication.applicationContext.openFileInput(imgUrl);
            phone_inputStream.read(bytes);
            if (!(bytes.length == 0)) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
