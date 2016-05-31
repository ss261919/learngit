package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;

/**
 * Created by Administrator on 2016/3/22.
 */
public class ShareFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    private final int TYPE_HEADER = 2;
    private final int width;
    private final int height;
    List<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private ShareFragmentAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);

        void onItemfooterClick(TextView view, int position);
    }

    public void setOnItemViewClickListener(ShareFragmentAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public ShareFragmentAdapter(Context context, List<Map<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader, int width, int height) {
        this.dataList = dataList;
        this.options = options;
        this.imageLoader = imageLoader;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.width = width;
        this.height = height;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = layoutInflater.inflate(
                    R.layout.share_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            return itemViewHolder;
        } else if (viewType == TYPE_HEADER) {
            View view = layoutInflater.inflate(
                    R.layout.share_header, null);
            return new HeaderViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String avatar = (String) dataList.get(position - 1).get("avatar");
            String file_url = (String) dataList.get(position - 1).get("file_url");
            ((ItemViewHolder) holder).shareItemTv.setText(String.valueOf(dataList.get(position - 1).get("comment")));
            ((ItemViewHolder) holder).shareItemUserId.setText(String.valueOf(dataList.get(position - 1).get("nickname")));
            ((ItemViewHolder) holder).shareItemCreatedTime.setText(String.valueOf(dataList.get(position - 1).get("created_at")));
            ((ItemViewHolder) holder).shareItemUserImg.setTag(avatar + "?imageView2/1/w/100");
            ((ItemViewHolder) holder).shareItemUserShareImg.setTag(file_url + "?imageView2/2/w/" + (int) (width * 0.28) + "/h/" + (int) (height * 0.28));
            imageLoader.loadImage(avatar + "?imageView2/1/w/100", options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).shareItemUserImg.setImageResource(R.mipmap.red_head_icon);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).shareItemUserImg.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).shareItemUserImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            imageLoader.loadImage(file_url + "?imageView2/2/w/" + (int) (width * 0.28) + "/h/" + (int) (height * 0.28), options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).shareItemUserShareImg.setImageResource(R.drawable.imagedownload_fail);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).shareItemUserShareImg.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).shareItemUserShareImg.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            if (onItemViewClickListener != null) {
                ((ItemViewHolder) holder).shareItemUserShareImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemViewClickListener.onItemClick(v, ((ItemViewHolder) holder).getLayoutPosition());
                    }
                });
            }
        } else {
            ((HeaderViewHolder) holder).shareFragmentHeaderLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.075)));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size() + 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout shareFragmentHeaderLinearLayout;

        public HeaderViewHolder(View view) {
            super(view);
            shareFragmentHeaderLinearLayout = (LinearLayout) view.findViewById(R.id.shareFragmentHeaderLinearLayout);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView shareItemUserId;
        private TextView shareItemTv;
        private TextView shareItemCreatedTime;
        private ImageView shareItemUserImg;
        private ImageView shareItemUserShareImg;

        public ItemViewHolder(View view) {
            super(view);
            shareItemTv = (TextView) view.findViewById(R.id.shareItemTv);
            shareItemCreatedTime = (TextView) view.findViewById(R.id.shareItemCreatedTime);
            shareItemUserId = (TextView) view.findViewById(R.id.shareItemUserId);
            shareItemUserImg = (ImageView) view.findViewById(R.id.shareItemUserImg);
            shareItemUserShareImg = (ImageView) view.findViewById(R.id.shareItemUserShareImg);
        }
    }
}
