package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class DuoBaoRecordsFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    List<HashMap<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private DuoBaoRecordsFragmentAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(DuoBaoRecordsFragmentAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public DuoBaoRecordsFragmentAdapter(Context context, List<HashMap<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
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
                    R.layout.fragment_duobao_records_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.15)));
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
            ((ItemViewHolder) holder).fragment_duobao_records_item_ImgTV.setText(String.valueOf(dataList.get(position).get("isAnnounce")).equals("0") ? "已揭晓" : "未揭晓");
            ((ItemViewHolder) holder).fragment_duobao_records_item_nameTV.setText(String.valueOf(dataList.get(position).get("name")));
            ((ItemViewHolder) holder).fragment_duobao_records_item_win_nameTV.setText(String.valueOf(dataList.get(position).get("winName")));
            ((ItemViewHolder) holder).fragment_duobao_records_item_announce_timeTV.setText(String.valueOf(dataList.get(position).get("announceTime")));
            ((ItemViewHolder) holder).fragment_duobao_records_item_goingTV.setText("第" + String.valueOf(dataList.get(position).get("time")) + "云进行中...");
            ((ItemViewHolder) holder).fragment_duobao_records_item_Img.setTag(imgUrl);
            imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).fragment_duobao_records_item_Img.setImageResource(R.mipmap.red_head_icon);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).fragment_duobao_records_item_Img.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).fragment_duobao_records_item_Img.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            if (onItemViewClickListener != null) {
                ((ItemViewHolder) holder).fragment_duobao_records_item.setOnClickListener(new View.OnClickListener() {
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
        private final TextView fragment_duobao_records_item_ImgTV;
        private final TextView fragment_duobao_records_item_nameTV;
        private final TextView fragment_duobao_records_item_win_nameTV;
        private final TextView fragment_duobao_records_item_announce_timeTV;
        private final TextView fragment_duobao_records_item_goingTV;
        private final RelativeLayout fragment_duobao_records_item;
        private ImageView fragment_duobao_records_item_Img;

        public ItemViewHolder(View view) {
            super(view);
            fragment_duobao_records_item_Img = (ImageView) view.findViewById(R.id.fragment_duobao_records_item_Img);
            fragment_duobao_records_item_ImgTV = (TextView) view.findViewById(R.id.fragment_duobao_records_item_ImgTV);
            fragment_duobao_records_item_nameTV = (TextView) view.findViewById(R.id.fragment_duobao_records_item_nameTV);
            fragment_duobao_records_item_win_nameTV = (TextView) view.findViewById(R.id.fragment_duobao_records_item_win_nameTV);
            fragment_duobao_records_item_announce_timeTV = (TextView) view.findViewById(R.id.fragment_duobao_records_item_announce_timeTV);
            fragment_duobao_records_item_goingTV = (TextView) view.findViewById(R.id.fragment_duobao_records_item_goingTV);
            fragment_duobao_records_item = (RelativeLayout) view.findViewById(R.id.fragment_duobao_records_item);
        }
    }
}
