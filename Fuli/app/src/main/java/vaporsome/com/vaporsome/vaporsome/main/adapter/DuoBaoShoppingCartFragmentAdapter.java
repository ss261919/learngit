package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class DuoBaoShoppingCartFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private DuoBaoShoppingCartFragmentAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(DuoBaoShoppingCartFragmentAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public DuoBaoShoppingCartFragmentAdapter(Context context, ArrayList<Map<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
        this.dataList = dataList;
        this.options = options;
        this.imageLoader = imageLoader;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder itemViewHolder;
        if (viewType == TYPE_ITEM) {
            View view = layoutInflater.inflate(
                    R.layout.fragment_duobao_shopping_cart_item, null);
            itemViewHolder = new ItemViewHolder(view);
            //(int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.15))
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.15)));
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
            Log.d("--DuoBao", "position:" + position);
            String imgUrl = (String) dataList.get(position).get("imgUrl");
            ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_name.setText(String.valueOf(dataList.get(position).get("name")));
            ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_surplus_number.setText(String.valueOf("剩余" + dataList.get(position).get("surplusNumber")) + "人次");
            ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_countTV.setText(String.valueOf(dataList.get(position).get("participateNumber")));
            ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_money.setText(String.valueOf(Float.parseFloat(String.valueOf(dataList.get(position).get("participateNumber"))) * Float.parseFloat(String.valueOf(dataList.get(position).get("price")))));
            ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_img.setTag(imgUrl);
            imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_img.setImageResource(R.mipmap.red_head_icon);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_img.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_img.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            if (onItemViewClickListener != null) {
                ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_decreaseCountBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                    }
                });
                ((ItemViewHolder) holder).fragment_duobao_shopping_cart_item_increaseCountBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                    }
                });
                ((ItemViewHolder) holder).fragment_duobao_records_item_delRL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fragment_duobao_shopping_cart_item_img;
        private final TextView fragment_duobao_shopping_cart_item_name;
        private final Button fragment_duobao_shopping_cart_item_decreaseCountBT;
        private final TextView fragment_duobao_shopping_cart_item_surplus_number;
        private final TextView fragment_duobao_shopping_cart_item_countTV;
        private final Button fragment_duobao_shopping_cart_item_increaseCountBT;
        private final TextView fragment_duobao_shopping_cart_item_money;
        private final RelativeLayout fragment_duobao_records_item_delRL;

        public ItemViewHolder(View view) {
            super(view);
            fragment_duobao_shopping_cart_item_img = (ImageView) view.findViewById(R.id.fragment_duobao_shopping_cart_item_img);
            fragment_duobao_shopping_cart_item_name = (TextView) view.findViewById(R.id.fragment_duobao_shopping_cart_item_name);
            fragment_duobao_shopping_cart_item_surplus_number = (TextView) view.findViewById(R.id.fragment_duobao_shopping_cart_item_surplus_number);
            fragment_duobao_shopping_cart_item_countTV = (TextView) view.findViewById(R.id.fragment_duobao_shopping_cart_item_countTV);
            fragment_duobao_shopping_cart_item_decreaseCountBT = (Button) view.findViewById(R.id.fragment_duobao_shopping_cart_item_decreaseCountBT);
            fragment_duobao_shopping_cart_item_increaseCountBT = (Button) view.findViewById(R.id.fragment_duobao_shopping_cart_item_increaseCountBT);
            fragment_duobao_shopping_cart_item_money = (TextView) view.findViewById(R.id.fragment_duobao_shopping_cart_item_money);
            fragment_duobao_records_item_delRL = (RelativeLayout) view.findViewById(R.id.fragment_duobao_records_item_delRL);
        }
    }
}
