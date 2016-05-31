package vaporsome.com.vaporsome.vaporsome.payment;

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
import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by Administrator on 2016/3/22.
 */
public class PaymentRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    private final int TYPE_MORE = 1;
    private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private PaymentRecycleViewAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(PaymentRecycleViewAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public PaymentRecycleViewAdapter(Context context, ArrayList<Map<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
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
                    R.layout.payment_activity_recycleview_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.1)));
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
            String imgUrl = (String) dataList.get(position).get("imgUrl");
            ((ItemViewHolder) holder).paymentItemName.setText(String.valueOf(dataList.get(position).get("name")));
            ((ItemViewHolder) holder).paymentParticipateNumberTv.setText(String.valueOf(dataList.get(position).get("participateNumber")) + "人次/");
            ((ItemViewHolder) holder).paymentMoneyTv.setText("￥" + String.valueOf(dataList.get(position).get("money")));
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
                        ((ItemViewHolder) holder).paymentItemImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
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
        private final TextView paymentItemName;
        private final TextView paymentParticipateNumberTv;
        private final TextView paymentMoneyTv;
        private ImageView paymentItemImg;

        public ItemViewHolder(View view) {
            super(view);
            paymentItemImg = (ImageView) view.findViewById(R.id.paymentItemImg);
            paymentItemName = (TextView) view.findViewById(R.id.paymentItemName);
            paymentParticipateNumberTv = (TextView) view.findViewById(R.id.paymentParticipateNumberTv);
            paymentMoneyTv = (TextView) view.findViewById(R.id.paymentMoneyTv);
        }
    }
}
