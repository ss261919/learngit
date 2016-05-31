package vaporsome.com.vaporsome.vaporsome.moneydetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;

/**
 * Created by ${Bash} on 2016/4/13.
 */
public class MoneyDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final int TYPE_1 = 0;
    //    final int TYPE_FOOTER = 1;
    private final int width;
    private final int height;
    List<Map<String, Object>> dataList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemViewClickListener onItemViewClickListener;

    public interface OnItemViewClickListener {
        void onItemClick(RelativeLayout linearLayout, int position);
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public MoneyDetailAdapter(Context context, List<Map<String, Object>> dataList, int width, int height) {
        this.dataList = dataList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.width = width;
        this.height = height;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = layoutInflater.inflate(
                    R.layout.money_detail_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            view.setLayoutParams(new LinearLayout.LayoutParams(width, height / 10));
            return itemViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).moneyDetailInfoTV.setText(dataList.get(position).get("description").toString());
            ((ItemViewHolder) holder).moneyDetailInfoTimeTV.setText(dataList.get(position).get("created_at").toString());
            ((ItemViewHolder) holder).moneyDetailInfoMoneyNumber.setText(dataList.get(position).get("value").toString());
            ((ItemViewHolder) holder).moneyDetailInfoStatus.setText(dataList.get(position).get("type").toString().equals("20") ? "+" : "-");
            ((ItemViewHolder) holder).moneyDetailInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClickListener.onItemClick(((ItemViewHolder) holder).moneyDetailInfoLinearLayout, holder.getLayoutPosition());
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

//    class FooterViewHolder extends RecyclerView.ViewHolder {
//        private TextView footer_textview;
//        public FooterViewHolder(View view) {
//            super(view);
//            footer_textview = (TextView) view.findViewById(R.id.red_item_footer);
//        }
//    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout moneyDetailInfoLinearLayout;
        private TextView moneyDetailInfoTV;
        private TextView moneyDetailInfoTimeTV;
        private TextView moneyDetailInfoStatus;
        private TextView moneyDetailInfoMoneyNumber;

        public ItemViewHolder(View view) {
            super(view);
            moneyDetailInfoTV = (TextView) view.findViewById(R.id.moneyDetailInfoTV);
            moneyDetailInfoTimeTV = (TextView) view.findViewById(R.id.moneyDetailInfoTimeTV);
            moneyDetailInfoMoneyNumber = (TextView) view.findViewById(R.id.moneyDetailInfoMoneyNumber);
            moneyDetailInfoStatus = (TextView) view.findViewById(R.id.moneyDetailInfoStatus);
            moneyDetailInfoLinearLayout = (RelativeLayout) view.findViewById(R.id.moneyDetailInfoLinearLayout);
        }
    }
}
