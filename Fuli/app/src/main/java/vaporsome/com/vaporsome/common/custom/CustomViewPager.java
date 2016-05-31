package vaporsome.com.vaporsome.common.custom;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ${Bash} on 2016/5/18.
 */

/**
 * 自定义滑动翻页可控，可通过设置isCanScroll参数来控制是否允许滑动切换页面
 */
public class CustomViewPager extends ViewPager {
    /**
     * 是否允许滑动翻页 ,默认可滑动
     */
    private boolean isCanScroll = true;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    public boolean isCanScroll() {
        return isCanScroll;
    }

    /**
     * 设置是否可以滑动翻页
     */
    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    // 设置禁止滑动的关键
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!isCanScroll)
            return true;
        return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {

        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}
