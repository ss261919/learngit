package vaporsome.com.vaporsome.vaporsome.participaterecords;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;

public class ParticipateRecords extends BaseActivity {

    @Bind(R.id.participateRecordsCancel)
    LinearLayout participateRecordsCancel;
    @Bind(R.id.participateRecordsImgLoad)
    ImageView participateRecordsImgLoad;
    @Bind(R.id.participateRecordsRecycleView)
    RecyclerView participateRecordsRecycleView;
    @Bind(R.id.participateRecordsSRL)
    SwipeRefreshLayout participateRecordsSRL;
    private HashMap<String, Object> map;
    private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private Animation anim;
    private ImageLoader imageLoader;
    private FullyLinearLayoutManager linearLayoutManager;
    private ParticipateRecordsAdapter adapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
            }
        }
    };
    private int page = 1;
    private boolean isHaveNext;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participate_records);
        ButterKnife.bind(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        linearLayoutManager = new FullyLinearLayoutManager(getBaseContext());
        participateRecordsRecycleView.setLayoutManager(linearLayoutManager);
        participateRecordsRecycleView.addItemDecoration(new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL_LIST));
        initData();
        initListener();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.load);
        anim.start();
        participateRecordsImgLoad.setAnimation(anim);
        downData();
        setAdapter();
    }

    private void downData() {
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "李四不是王五的张三");
                map.put("participateTimes", "30");
                map.put("time", "2016-05-12 18:12:326");
                dataList.add(map);
            } else if (i % 3 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "李四不是王五的张三");
                map.put("participateTimes", "15");
                map.put("time", "2016-05-09 18:12:326");
                dataList.add(map);
            } else if (i % 4 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "李四不是王五的张三");
                map.put("participateTimes", "25");
                map.put("time", "2016-05-09 18:12:326");
                dataList.add(map);
            } else {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "李四不是王五的张三");
                map.put("participateTimes", "3");
                map.put("time", "2016-05-11 18:12:326");
                dataList.add(map);
            }
        }
    }

    private void setAdapter() {
        if (!(anim == null)) {
            anim.cancel();
            participateRecordsImgLoad.clearAnimation();
            participateRecordsImgLoad.setVisibility(View.GONE);
            participateRecordsRecycleView.setVisibility(View.VISIBLE);
        }
        if (page == 1) {
            if (!(dataList.size() == 0)) {
                adapter = new ParticipateRecordsAdapter(getBaseContext(), dataList, IApplication.options, imageLoader);
                participateRecordsRecycleView.setAdapter(adapter);
            }
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void initListener() {
        participateRecordsSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(ParticipateRecords.this)) {
                    int topRowVerticalPosition = (participateRecordsRecycleView == null || participateRecordsRecycleView.getChildCount() == 0) ? 0 : participateRecordsRecycleView.getChildAt(0).getTop();
                    if (topRowVerticalPosition >= 0) {
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(3, 10);
                        } else {
                            Toast.makeText(getBaseContext(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                            participateRecordsSRLDisable();
                        }
                    } else {
                        participateRecordsSRL.setEnabled(false);
                        participateRecordsSRL.setRefreshing(false);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                    participateRecordsSRLDisable();
                    Toast.makeText(getBaseContext(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        participateRecordsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                if (topRowVerticalPosition >= 0) {
                    participateRecordsSRL.setEnabled(true);
                } else {
                    participateRecordsSRLDisable();
                }
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    boolean isRefreshing = participateRecordsSRL.isRefreshing();
                    if (isRefreshing) {
                        return;
                    }
                    if (isHaveNext) {
                        if (!isLoading) {
                            handler.sendEmptyMessage(4);
                        }
                    }
                }
            }
        });
    }

    private void participateRecordsSRLDisable() {
        participateRecordsSRL.setEnabled(false);
        participateRecordsSRL.setRefreshing(false);
        participateRecordsSRL.post(new Runnable() {
            @Override
            public void run() {
                participateRecordsSRL.setEnabled(true);
            }
        });
    }

    @OnClick(R.id.participateRecordsCancel)
    public void onClick(View view) {
        finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

}
