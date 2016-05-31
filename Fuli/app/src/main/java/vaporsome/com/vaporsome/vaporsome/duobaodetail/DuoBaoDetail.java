package vaporsome.com.vaporsome.vaporsome.duobaodetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class DuoBaoDetail extends BaseActivity {

    @Bind(R.id.duobao_detail_top_guider_RL_Cancel)
    LinearLayout duobaoDetailTopGuiderRLCancel;
    @Bind(R.id.duobao_detail_FL_imgTV)
    TextView duobaoDetailFLImgTV;
    @Bind(R.id.duobao_detail_FL_img)
    ImageView duobaoDetailFLImg;
    @Bind(R.id.duobao_detail_nameTV)
    TextView duobaoDetailNameTV;
    @Bind(R.id.duobao_detail_participate_timesTV)
    TextView duobaoDetailParticipateTimesTV;
    @Bind(R.id.duobao_detail_win_nameTV)
    TextView duobaoDetailWinNameTV;
    @Bind(R.id.duobao_detail_goingTV)
    TextView duobaoDetailGoingTV;
    @Bind(R.id.duobao_detail_announce_timeTV)
    TextView duobaoDetailAnnounceTimeTV;
    @Bind(R.id.duobao_detail_participate_timesLL_participate_timesTV)
    TextView duobaoDetailParticipateTimesLLParticipateTimesTV;
    @Bind(R.id.duobao_detail_bottom_participate_timesTV)
    TextView duobaoDetailBottomParticipateTimesTV;
    @Bind(R.id.duobao_detail_participate_timeTV)
    TextView duobaoDetailParticipateTimeTV;
    @Bind(R.id.duobao_detail_gridView)
    GridView duobaoDetailGridView;
    private String id;
    private ArrayList<String> dataList = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duo_bao_detail);
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
            duobaoDetailGridView.setAdapter(new GridAdapter(getBaseContext(), dataList));
        Log.d("--DuoBaoDetail", id);
        downData();
    }

    private void downData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @OnClick(R.id.duobao_detail_top_guider_RL_Cancel)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.duobao_detail_top_guider_RL_Cancel:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
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
}
