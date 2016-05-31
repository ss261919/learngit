package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autonavi.aps.amapapi.model.AmapLoc;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.MinuteSecondTimeCountUtil;
import vaporsome.com.vaporsome.common.utils.TimeCountUtil;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by Administrator on 2016/3/22.
 */
public class DuoBaoImmediatelyAnnouncedFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    private final Activity activity;
    ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private ArrayList<Boolean> flagList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private DuoBaoImmediatelyAnnouncedFragmentAdapter.onItemViewClickListener onItemViewClickListener;
    private Animation anim;
    private MinuteSecondTimeCountUtil timeCountUtil;


    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(DuoBaoImmediatelyAnnouncedFragmentAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public DuoBaoImmediatelyAnnouncedFragmentAdapter(Activity activity, Context context, HashMap<String, Object> map, DisplayImageOptions options, ImageLoader imageLoader) {
        this.dataList = (ArrayList<Map<String, Object>>) map.get("dataList");
        this.flagList = (ArrayList<Boolean>) map.get("flagList");
        this.activity = activity;
        this.options = options;
        this.imageLoader = imageLoader;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = layoutInflater.inflate(
                    R.layout.fragment_duobao_immediately_announced_item, null);
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
            if (flagList.get(position)) {
                String imgUrl = (String) dataList.get(position).get("imgUrl");
                if (String.valueOf(dataList.get(position).get("name")).length() != 0)
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_name.setText(String.valueOf(dataList.get(position).get("name")));
                if (String.valueOf(dataList.get(position).get("price")).length() != 0)
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_money.setText(String.valueOf(dataList.get(position).get("price")));
                ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_time_decreaseMinuteTV.setTag(position);
                if (((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_time_decreaseMinuteTV.getTag().equals(position)) {
                    timeCountUtil = new MinuteSecondTimeCountUtil(activity, Long.parseLong(String.valueOf(dataList.get(position).get("minute"))), 500, ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_time_decreaseMinuteTV);
                    timeCountUtil.start();
                }
                anim = AnimationUtils.loadAnimation(IApplication.applicationContext, R.anim.clock_load);
                anim.start();
                ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_clock.setAnimation(anim);
                ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_img.setTag(imgUrl);
                imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_img.setImageResource(R.mipmap.tu);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_img.getTag().equals(imageUri)) {
                            ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_img.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
                if (onItemViewClickListener != null) {
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_itemRL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemViewClickListener.onItemClick(v, holder.getLayoutPosition());
                        }
                    });
                }
            } else {
                ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_RL.setVisibility(View.GONE);
                ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_Ll2.setVisibility(View.VISIBLE);
                if (!(dataList.get(position).get("winnerName") == null))
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_WinnerName.setText(String.valueOf(dataList.get(position).get("winnerName")));
                if (!(dataList.get(position).get("price") == null))
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_Value.setText(String.valueOf(dataList.get(position).get("price")));
                if (!(dataList.get(position).get("participateNumber") == null))
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_ParticipateNumber.setText(String.valueOf(dataList.get(position).get("participateNumber")));
                if (!(dataList.get(position).get("announceTime") == null))
                    ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_AnnounceTime.setText(String.valueOf(dataList.get(position).get("announceTime")));
            }

        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView fragment_duobao_immediately_announced_item_name;
        private final TextView fragment_duobao_immediately_announced_item_money;
        private final TextView fragment_duobao_immediately_announced_item_time_decreaseMinuteTV;
        private final ImageView fragment_duobao_immediately_announced_item_clock;
        private final ImageView fragment_duobao_immediately_announced_item_img;
        private final RelativeLayout fragment_duobao_immediately_announced_itemRL;
        private final RelativeLayout fragment_duobao_immediately_announced_item_RL;
        private final LinearLayout fragment_duobao_immediately_announced_item_Ll2;
        private final TextView fragment_duobao_immediately_announced_item_WinnerName;
        private final TextView fragment_duobao_immediately_announced_item_Value;
        private final TextView fragment_duobao_immediately_announced_item_ParticipateNumber;
        private final TextView fragment_duobao_immediately_announced_item_AnnounceTime;

        public ItemViewHolder(View view) {
            super(view);
            fragment_duobao_immediately_announced_item_img = (ImageView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_img);
            fragment_duobao_immediately_announced_item_name = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_name);
            fragment_duobao_immediately_announced_item_money = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_money);
            fragment_duobao_immediately_announced_item_clock = (ImageView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_clock);
            fragment_duobao_immediately_announced_item_time_decreaseMinuteTV = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_time_decreaseMinuteTV);
            fragment_duobao_immediately_announced_itemRL = (RelativeLayout) view.findViewById(R.id.fragment_duobao_immediately_announced_itemRL);
            fragment_duobao_immediately_announced_item_RL = (RelativeLayout) view.findViewById(R.id.fragment_duobao_immediately_announced_item_RL);
            fragment_duobao_immediately_announced_item_Ll2 = (LinearLayout) view.findViewById(R.id.fragment_duobao_immediately_announced_item_Ll2);

            fragment_duobao_immediately_announced_item_WinnerName = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_WinnerName);
            fragment_duobao_immediately_announced_item_Value = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_Value);
            fragment_duobao_immediately_announced_item_ParticipateNumber = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_ParticipateNumber);
            fragment_duobao_immediately_announced_item_AnnounceTime = (TextView) view.findViewById(R.id.fragment_duobao_immediately_announced_item_AnnounceTime);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((ItemViewHolder) holder).fragment_duobao_immediately_announced_item_clock.clearAnimation();
    }
}
