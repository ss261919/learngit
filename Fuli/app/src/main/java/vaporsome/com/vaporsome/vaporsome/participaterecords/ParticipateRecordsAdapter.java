package vaporsome.com.vaporsome.vaporsome.participaterecords;

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
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

/**
 * Created by ${Bash} on 2016/5/23.
 */
public class ParticipateRecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ITEM = 0;
    private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;

    public ParticipateRecordsAdapter(Context context, ArrayList<Map<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
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
                    R.layout.activity_participate_records_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Integer.parseInt(PreferencesUtils.getString(IApplication.applicationContext, "screenHeight")) * 0.1)));
            return itemViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            String imgUrl = String.valueOf(dataList.get(position).get("imgUrl"));

            ((ItemViewHolder) holder).participateRecordsItemImg.setTag(imgUrl);
            ((ItemViewHolder) holder).participateRecordsItemNameTV.setText(String.valueOf(dataList.get(position).get("name")));
            ((ItemViewHolder) holder).participateRecordsItemParticipateTV.setText(String.valueOf(dataList.get(position).get("participateTimes")));
            ((ItemViewHolder) holder).participateRecordsItemParticipateTimeTV.setText(String.valueOf(dataList.get(position).get("time")));
            imageLoader.loadImage(imgUrl, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((ItemViewHolder) holder).participateRecordsItemImg.setImageResource(R.mipmap.red_head_icon);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((ItemViewHolder) holder).participateRecordsItemImg.getTag().equals(imageUri)) {
                        ((ItemViewHolder) holder).participateRecordsItemImg.setImageBitmap(loadedImage);
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
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView participateRecordsItemImg;
        private final TextView participateRecordsItemNameTV;
        private final TextView participateRecordsItemParticipateTV;
        private final TextView participateRecordsItemParticipateTimeTV;

        public ItemViewHolder(View itemView) {
            super(itemView);
            participateRecordsItemImg = (ImageView) itemView.findViewById(R.id.participateRecordsItemImg);
            participateRecordsItemNameTV = (TextView) itemView.findViewById(R.id.participateRecordsItemNameTV);
            participateRecordsItemParticipateTV = (TextView) itemView.findViewById(R.id.participateRecordsItemParticipateTV);
            participateRecordsItemParticipateTimeTV = (TextView) itemView.findViewById(R.id.participateRecordsItemParticipateTimeTV);
        }
    }

}
