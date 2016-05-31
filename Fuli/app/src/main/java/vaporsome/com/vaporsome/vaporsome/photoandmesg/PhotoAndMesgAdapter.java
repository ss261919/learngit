package vaporsome.com.vaporsome.vaporsome.photoandmesg;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by Administrator on 2016/3/22.
 */
public class PhotoAndMesgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    private ArrayList<String> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private PhotoAndMesgAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(PhotoAndMesgAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public PhotoAndMesgAdapter(Context context, ArrayList<String> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
        this.dataList = dataList;
        this.options = options;
        this.imageLoader = imageLoader;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = layoutInflater.inflate(
                    R.layout.activity_photo_and_mesg_recycleview_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return itemViewHolder;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String imgUrl = dataList.get(position);
            ((ItemViewHolder) holder).paymentItemImg.setTag(imgUrl);
            imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).paymentItemImg.setImageResource(R.mipmap.red_head_icon);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).paymentItemImg.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).paymentItemImg.setImageBitmap(loadedImage);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView paymentItemImg;

        public ItemViewHolder(View view) {
            super(view);
            paymentItemImg = (ImageView) view.findViewById(R.id.photoAndMesgRecylceViewItemImg);
        }
    }
}
