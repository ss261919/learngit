package vaporsome.com.vaporsome.common.utils;

import android.app.Activity;
import android.content.Intent;

import vaporsome.com.vaporsome.R;


/**
 * Created by Administrator on 2016/5/8.
 */
public class SetThemes {
    private static int sTheme;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_WHITE = 1;
    public final static int THEME_BLUE = 2;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity
     * of the same type.
     */
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            default:
            case 0:
                activity.setTheme(R.style.Transparent);
                break;
        }
    }
}

