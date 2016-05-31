package vaporsome.com.vaporsome.vaporsome.adsdetailactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/4/15.
 */
public class AdsDetailActivityAdapter extends RecyclerView.Adapter {

    private final int TYPE_ITEM = 0;
    private final int width;
    private final int height;
    private final List<String> adsImgList;
    private Context context;
    private LayoutInflater inflater;
    private Map<String, Object> dataMap;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private Animation animation;


    public AdsDetailActivityAdapter(Context context, Map<String, Object> dataMap, DisplayImageOptions options, ImageLoader imageLoader, int width, int height) {
        this.context = context;
        this.dataMap = dataMap;
        adsImgList = (List<String>) dataMap.get("adsImgList");
        this.width = width;
        this.height = height;
        this.options = options;
        this.imageLoader = imageLoader;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = inflater.inflate(
                    R.layout.ads_activity_listitem, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final String imgUrl = adsImgList.get(position + 1) + "?imageView2/2/w/" + width + "/";
            ((ItemViewHolder) holder).adsActivityItemImg.setTag(imgUrl);
            ((ItemViewHolder) holder).adsActivityItemImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    ((ItemViewHolder) holder).adsActivityItemAnim.setBackgroundResource(R.drawable.load);
                    animation = AnimationUtils.loadAnimation(IApplication.applicationContext, R.anim.load);
                    animation.start();
                    ((ItemViewHolder) holder).adsActivityItemAnim.startAnimation(animation);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).adsActivityItemImg.setBackgroundResource(R.drawable.imagedownload_fail);
                    ((ItemViewHolder) holder).adsActivityItemAnim.clearAnimation();
                    ((ItemViewHolder) holder).adsActivityItemAnim.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).adsActivityItemImg.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).adsActivityItemImg.setImageBitmap(loadedImage);
                        ((ItemViewHolder) holder).adsActivityItemAnim.setVisibility(View.GONE);
                        ((ItemViewHolder) holder).adsActivityItemAnim.clearAnimation();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
            animation = null;
        }
    }

    @Override
    public int getItemCount() {
        return adsImgList.size() == 0 ? 0 : adsImgList.size() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView adsActivityItemAnim;
        private ImageView adsActivityItemImg;

        public ItemViewHolder(View view) {
            super(view);
            adsActivityItemImg = (ImageView) view.findViewById(R.id.adsActivityItemImg);
            adsActivityItemAnim = (ImageView) view.findViewById(R.id.adsActivityItemAnim);
        }
    }

}