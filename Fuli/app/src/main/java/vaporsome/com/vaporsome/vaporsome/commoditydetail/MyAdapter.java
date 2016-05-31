package vaporsome.com.vaporsome.vaporsome.commoditydetail;

/**
 * Created by ${Bash} on 2016/5/25.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import vaporsome.com.vaporsome.R;

/**
 * 适配器，负责装配 、销毁  数据  和  组件 。
 */
public class MyAdapter extends PagerAdapter {

    private ArrayList<View> dataList;
    private Context context;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String[] urls;
    private ImageView image;
    private View view;


    public MyAdapter(Context context, ArrayList<View> dataList, String[] urls, DisplayImageOptions options, ImageLoader imageLoader) {
        this.context = context;
        this.dataList = dataList;
        this.options = options;
        this.imageLoader = imageLoader;
        this.urls = urls;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }

    /**
     * Remove a page for the given position.
     * 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
     * instantiateItem(View container, int position)
     * This method was deprecated in API level . Use instantiateItem(ViewGroup, int)
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        container.removeView(dataList.get(position));
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    /**
     * Create the page for the given position.
     */
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        view = dataList.get(position);
        image = ((ImageView) view.findViewById(R.id.commodityDetailViewPagerItemImg));
        image.setTag(urls[position]);
        imageLoader.loadImage(urls[position], options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                image.setBackgroundResource(R.drawable.imagedownload_fail);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (!(image.getTag() == null) && image.getTag().equals(urls[position])) {
                    image.setImageBitmap(loadedImage);
//                    container.removeView(dataList.get(position));
                    container.addView(dataList.get(position));
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return dataList.get(position);
    }
}
