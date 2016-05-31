package vaporsome.com.vaporsome.vaporsome.computedetail;

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

public class ComputeDetailRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_ITEM = 0;
    private ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private Context context;
    private ComputeDetailRecycleViewAdapter.onItemViewClickListener onItemViewClickListener;

    public interface onItemViewClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemViewClickListener(ComputeDetailRecycleViewAdapter.onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public ComputeDetailRecycleViewAdapter(Context context, ArrayList<HashMap<String, Object>> dataList, DisplayImageOptions options, ImageLoader imageLoader) {
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
                    R.layout.activity_compute_detail_item, null);
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
            ((ItemViewHolder) holder).computeDetailItemTimeDay.setText(String.valueOf(dataList.get(position).get("dayTime")));
            ((ItemViewHolder) holder).computeDetailItemTimeSecond.setText(String.valueOf(dataList.get(position).get("secondTime")));
            ((ItemViewHolder) holder).computeDetailItemNumber.setText(String.valueOf(dataList.get(position).get("number")));
            ((ItemViewHolder) holder).computeDetailItemName.setText(String.valueOf(dataList.get(position).get("name")));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView computeDetailItemTimeSecond;
        private final TextView computeDetailItemNumber;
        private final TextView computeDetailItemName;
        private TextView computeDetailItemTimeDay;

        public ItemViewHolder(View view) {
            super(view);
            computeDetailItemTimeDay = (TextView) view.findViewById(R.id.computeDetailItemTimeDay);
            computeDetailItemTimeSecond = (TextView) view.findViewById(R.id.computeDetailItemTimeSecond);
            computeDetailItemNumber = (TextView) view.findViewById(R.id.computeDetailItemNumber);
            computeDetailItemName = (TextView) view.findViewById(R.id.computeDetailItemName);
        }
    }
}
