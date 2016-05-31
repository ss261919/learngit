package vaporsome.com.vaporsome.vaporsome.init;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;

public class GuideActivity extends BaseActivity implements OnPageChangeListener {
    List datas = new ArrayList();
    private Bitmap imgTemp;  //临时标记图
    private int screenWidth;
    private int screenHeigh;
    private TextView guideTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initData();
        initView();
    }

    public void initView() {
        ViewPager vp = (ViewPager) findViewById(R.id.guideVP);
        vp.setAdapter(new ViewPagerAdapter());

    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View) datas.get(position));
            return (View) datas.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) datas.get(position));
        }

    }

    public void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
        ImageView a = new ImageView(this);
        a.setImageResource(R.mipmap.guid_1);
        a.setScaleType(ImageView.ScaleType.FIT_XY);
        a.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenHeigh));
        ImageView b = new ImageView(this);
        b.setImageResource(R.mipmap.guid_2);
        b.setScaleType(ImageView.ScaleType.FIT_XY);
        b.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenHeigh));
        FrameLayout d = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.guide_last, null);
        d.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenHeigh));

        guideTv = (TextView) d.findViewById(R.id.guide_Tv);
        guideTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });

        datas.add(a);
        datas.add(b);
        datas.add(d);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int position, float arg1, int arg2) {
    }

    private void jump() {
        Intent it;
        PreferencesUtils.putBoolean(getBaseContext(), "isFirst", false);
        if (PreferencesUtils.getBoolean(getBaseContext(), "isLogin", false)) {
            it = new Intent(this, MainActivity.class);
        } else {
            it = new Intent(this, LoginActivity.class);
        }
        startActivity(it);
        finish();

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
    }
}