package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;

/**
 * Created by Administrator on 2016/3/22.
 */
public class DuoBaoTheNewFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private DuoBaoTheNewFragmentAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(DuoBaoTheNewFragmentAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public DuoBaoTheNewFragmentAdapter(Context context, ArrayList<Map<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
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
                    R.layout.fragment_duobao_the_new_item, null);
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
            String imgUrl = (String) dataList.get(position).get("imgUrl");
            ((ItemViewHolder) holder).fragment_duobao_the_new_item_percentage.setHorizontalScrollBarEnabled(true);
            ((ItemViewHolder) holder).fragment_duobao_the_new_item_percentage.setProgress(Integer.parseInt(String.valueOf(dataList.get(position).get("percentage"))));
            ((ItemViewHolder) holder).fragment_duobao_the_new_item_name.setText(String.valueOf(dataList.get(position).get("name")));
            ((ItemViewHolder) holder).fragment_duobao_the_new_item_money.setText(String.valueOf(dataList.get(position).get("price")));
            ((ItemViewHolder) holder).fragment_duobao_the_new_item_img.setTag(imgUrl);
            imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).fragment_duobao_the_new_item_img.setImageResource(R.mipmap.red_head_icon);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).fragment_duobao_the_new_item_img.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).fragment_duobao_the_new_item_img.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            if (onItemViewClickListener != null) {
                ((ItemViewHolder) holder).fragment_duobao_the_new_item_shopping_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                    }
                });
                ((ItemViewHolder) holder).fragment_duobao_the_new_item_duobaoBT.setOnClickListener(new View.OnClickListener() {
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
        private final TextView fragment_duobao_the_new_item_name;
        private final TextView fragment_duobao_the_new_item_money;
        private final ProgressBar fragment_duobao_the_new_item_percentage;
        private final Button fragment_duobao_the_new_item_duobaoBT;
        private final ImageView fragment_duobao_the_new_item_shopping_cart;
        private final ImageView fragment_duobao_the_new_item_xiangou;
        private ImageView fragment_duobao_the_new_item_img;

        public ItemViewHolder(View view) {
            super(view);
            fragment_duobao_the_new_item_img = (ImageView) view.findViewById(R.id.fragment_duobao_the_new_item_img);
            fragment_duobao_the_new_item_name = (TextView) view.findViewById(R.id.fragment_duobao_the_new_item_name);
            fragment_duobao_the_new_item_money = (TextView) view.findViewById(R.id.fragment_duobao_the_new_item_money);
            fragment_duobao_the_new_item_percentage = (ProgressBar) view.findViewById(R.id.fragment_duobao_the_new_item_percentage);
            fragment_duobao_the_new_item_shopping_cart = (ImageView) view.findViewById(R.id.fragment_duobao_the_new_item_shopping_cart);
            fragment_duobao_the_new_item_xiangou = (ImageView) view.findViewById(R.id.fragment_duobao_the_new_item_xiangou);
            fragment_duobao_the_new_item_duobaoBT = (Button) view.findViewById(R.id.fragment_duobao_the_new_item_duobaoBT);
        }
    }
}
