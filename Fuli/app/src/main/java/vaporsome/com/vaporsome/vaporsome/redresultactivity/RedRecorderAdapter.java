package vaporsome.com.vaporsome.vaporsome.redresultactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by ${Bash} on 2016/3/29.
 */
public class RedRecorderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int TYPE_1 = 0;
    private final int width;
    private final int height;
    List<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemViewClickLisenter onItemViewClickListener;

    public interface OnItemViewClickLisenter {
        void onItemfooterClick(TextView view, int position);
    }

    public void setOnItemViewClickListener(OnItemViewClickLisenter onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public RedRecorderAdapter(Context context, List<Map<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
        this.dataList = dataList;
        this.options = options;
        this.imageLoader = imageLoader;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_1) {
            View view = layoutInflater.inflate(
                    R.layout.red_recoder_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new LinearLayout.LayoutParams(width, height / 10));
            return itemViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String avatar = dataList.get(position).get("avatar").toString() + "?imageView2/2/w/100/h/100";
            if (dataList.get(position).get("nickname").toString().length() > 10) {
                ((ItemViewHolder) holder).redRecoderItemUserId.setText(dataList.get(position).get("nickname").toString().substring(0, 10) + "...");
            } else {
                ((ItemViewHolder) holder).redRecoderItemUserId.setText(dataList.get(position).get("nickname").toString());
            }
            ((ItemViewHolder) holder).redRecoderItemMoney.setText(dataList.get(position).get("total_fee").toString() + "元");
            ((ItemViewHolder) holder).redRecoderItemTime.setText(dataList.get(position).get("created_at").toString());
            ((ItemViewHolder) holder).redRecoderItemUserIcon.setTag(avatar);
            imageLoader.loadImage(avatar, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).redRecoderItemUserIcon.setImageResource(R.drawable.imagedownload_fail);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).redRecoderItemUserIcon.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).redRecoderItemUserIcon.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 10, 10));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_1;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView redRecoderItemUserId;
        private TextView redRecoderItemMoney;
        private TextView redRecoderItemTime;
        private ImageView redRecoderItemUserIcon;

        public ItemViewHolder(View view) {
            super(view);
            redRecoderItemUserId = (TextView) view.findViewById(R.id.redRecoderItemUserId);
            redRecoderItemMoney = (TextView) view.findViewById(R.id.redRecoderItemMoney);
            redRecoderItemTime = (TextView) view.findViewById(R.id.redRecoderItemTime);
            redRecoderItemUserIcon = (ImageView) view.findViewById(R.id.redRecoderItemUserIcon);
        }
    }
}
