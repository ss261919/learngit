package vaporsome.com.vaporsome.vaporsome.commoditydetail;

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

import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;

import static android.support.v7.widget.RecyclerView.ViewHolder;


/**
 * Created by Administrator on 2016/3/22.
 */
public class CommodityDetailtPartivcipateNumberNotEnoughRecycleViewAdapter extends RecyclerView.Adapter<ViewHolder> {


    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private final boolean[] booleanArray;

    public CommodityDetailtPartivcipateNumberNotEnoughRecycleViewAdapter(Context context, boolean[] booleanArray) {
        mContext = context;
        this.booleanArray = booleanArray;
        mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.commodity_recycleview_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            if (booleanArray[position])
                ((ItemViewHolder) holder).commodityDetailRecycleViewItemImg.setBackgroundResource(R.mipmap.zhifu_pressed);
            else
                ((ItemViewHolder) holder).commodityDetailRecycleViewItemImg.setBackgroundResource(R.mipmap.zhifu_normal);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return booleanArray.length == 0 ? 0 : booleanArray.length;
    }

    private class ItemViewHolder extends ViewHolder {
        ImageView commodityDetailRecycleViewItemImg;

        public ItemViewHolder(View itemView) {
            super(itemView);
            commodityDetailRecycleViewItemImg = (ImageView) itemView.findViewById(R.id.commodityDetailRecycleViewItemImg);
        }
    }
}
