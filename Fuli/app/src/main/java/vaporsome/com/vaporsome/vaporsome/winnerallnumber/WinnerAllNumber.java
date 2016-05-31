package vaporsome.com.vaporsome.vaporsome.winnerallnumber;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class WinnerAllNumber extends BaseActivity {

    @Bind(R.id.winnerAllNumberCancel)
    LinearLayout winnerAllNumberCancel;
    @Bind(R.id.winnerAllNumberTV)
    TextView winnerAllNumberTV;
    @Bind(R.id.winnerAllNumberTimeTV)
    TextView winnerAllNumberTimeTV;
    @Bind(R.id.winnerAllNumberGridView)
    GridView winnerAllNumberGridView;
    private String id;
    private ArrayList<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_all_number);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        id = getIntent().getStringExtra("id");
        for (int i = 0; i < 13; i++) {
            dataList.add("10008" + i);
        }
        if (!dataList.isEmpty())
            winnerAllNumberGridView.setAdapter(new GridAdapter(getBaseContext(), dataList));
        Log.d("--DuoBaoDetail", id);
        downData();
    }

    private void downData() {

    }

    @Override
    public void initListener() {

    }

    @OnClick(R.id.winnerAllNumberCancel)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.winnerAllNumberCancel:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
        }
    }

    class GridAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private ArrayList<String> idList = new ArrayList<String>();

        public GridAdapter(Context context, ArrayList<String> idList) {
            this.idList = idList;
            mInflater = LayoutInflater.from(getBaseContext());
        }

        @Override
        public int getCount() {
            return idList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemViewTag viewTag;
            if (view == null) {
                view = mInflater.inflate(R.layout.activity_duo_bao_detail_gridview_item, null);
                // construct an item tag
                viewTag = new ItemViewTag((TextView) view.findViewById(R.id.duobao_detail_gridView_item_TV));
                view.setTag(viewTag);
            } else {
                viewTag = (ItemViewTag) view.getTag();
            }

            // set name
            viewTag.mName.setText(dataList.get(i));
            return view;
        }
    }

    class ItemViewTag {
        protected TextView mName;

        public ItemViewTag(TextView name) {
            this.mName = name;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
