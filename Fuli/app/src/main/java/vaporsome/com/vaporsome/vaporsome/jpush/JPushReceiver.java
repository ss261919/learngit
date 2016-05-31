package vaporsome.com.vaporsome.vaporsome.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.init.SplashActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;

/**
 * Created by ${Bash} on 2016/5/3.
 */
public class JPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String jPushRegisterId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            if (!jPushRegisterId.isEmpty()) {
                PreferencesUtils.putString(IApplication.applicationContext, "jPushRegisterId", jPushRegisterId);
            }
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            NotificationManager notificationManager = (NotificationManager) IApplication.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();
            notification.sound = Uri.parse("android.resource://" + IApplication.applicationContext.getPackageName() + "/" + R.raw.classic);
            notificationManager.notify(0, notification);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // 在这里可以自己写代码去定义用户点击后的行为
            if (PreferencesUtils.getBoolean(IApplication.applicationContext, "isLogin", false)) {
                Intent i = new Intent(context, MainActivity.class);  //自定义打开的界面
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                Intent i = new Intent(context, SplashActivity.class);  //自定义打开的界面
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } else {
        }
    }
}
