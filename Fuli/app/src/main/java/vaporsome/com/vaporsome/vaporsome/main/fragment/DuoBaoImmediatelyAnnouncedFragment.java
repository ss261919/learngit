package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.GetTypeByHead;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.commoditydetail.CommodityDetail;
import vaporsome.com.vaporsome.vaporsome.main.adapter.DuoBaoImmediatelyAnnouncedFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.adapter.DuoBaoTheNewFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class DuoBaoImmediatelyAnnouncedFragment extends Fragment implements DuoBaoImmediatelyAnnouncedFragmentAdapter.onItemViewClickListener {

    @Bind(R.id.fragment_duobao_immediately_announced_RecycleView)
    RecyclerView fragmentDuobaoImmediatelyAnnouncedRecycleView;
    @Bind(R.id.fragment_duobao_immediately_announced_SRL)
    SwipeRefreshLayout fragmentDuobaoImmediatelyAnnouncedSRL;
    @Bind(R.id.fragment_duobao_immediately_announcedLoadImg)
    ImageView fragment_duobao_immediately_announcedLoadImg;
    private Animation anim;
    private ArrayList<Map<String, Object>> dataList;
    private Map<String, Object> map;
    private DuoBaoImmediatelyAnnouncedFragmentAdapter adapter;
    private FullyLinearLayoutManager linearManager;
    private ImageLoader imageLoader;
    private boolean isLoading;

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
    private boolean isHaveNext;
    private Intent intent;
    private ArrayList<Boolean> flagList;
    private HashMap<String, Object> dataMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duobao_immediately_announced, null);
        ButterKnife.bind(this, view);
        linearManager = new FullyLinearLayoutManager(getActivity());
        fragmentDuobaoImmediatelyAnnouncedRecycleView.setLayoutManager(linearManager);
        fragmentDuobaoImmediatelyAnnouncedRecycleView.setHasFixedSize(true);
        fragmentDuobaoImmediatelyAnnouncedRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        fragmentDuobaoImmediatelyAnnouncedSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    int topRowVerticalPosition = (fragmentDuobaoImmediatelyAnnouncedRecycleView == null || fragmentDuobaoImmediatelyAnnouncedRecycleView.getChildCount() == 0) ? 0 : fragmentDuobaoImmediatelyAnnouncedRecycleView.getChildAt(0).getTop();
                    if (topRowVerticalPosition >= 0) {
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(3, 10);
                        } else {
                            Toast.makeText(getActivity(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                            fragmentDuobaoImmediatelyAnnouncedSRLDisable();
                        }
                    } else {
                        fragmentDuobaoImmediatelyAnnouncedSRL.setEnabled(false);
                        fragmentDuobaoImmediatelyAnnouncedSRL.setRefreshing(false);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                    fragmentDuobaoImmediatelyAnnouncedSRLDisable();
                    Toast.makeText(getActivity(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragmentDuobaoImmediatelyAnnouncedRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case 1:
                        //正在滑动
                        imageLoader.pause();
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //滑动停止
                        imageLoader.resume();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                if (topRowVerticalPosition >= 0) {
                    fragmentDuobaoImmediatelyAnnouncedSRL.setEnabled(true);
                } else {
                    fragmentDuobaoImmediatelyAnnouncedSRLDisable();
                }
                int lastVisibleItemPosition = linearManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    boolean isRefreshing = fragmentDuobaoImmediatelyAnnouncedSRL.isRefreshing();
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

    private void fragmentDuobaoImmediatelyAnnouncedSRLDisable() {
        fragmentDuobaoImmediatelyAnnouncedSRL.setEnabled(false);
        fragmentDuobaoImmediatelyAnnouncedSRL.setRefreshing(false);
        fragmentDuobaoImmediatelyAnnouncedSRL.post(new Runnable() {
            @Override
            public void run() {
                fragmentDuobaoImmediatelyAnnouncedSRL.setEnabled(true);
            }
        });
    }

    private void initData() {
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.load);
        anim.start();
        fragment_duobao_immediately_announcedLoadImg.setAnimation(anim);
        dataList = new ArrayList<>();
        downData();
    }

    private void downData() {
        flagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "苹果(Apple)iphone6s 16G版 4G手机");
                map.put("price", "6088.00");
                map.put("minute", "123456");
                map.put("second", "46");
                map.put("hair", "78");
                map.put("id", i);
                map.put("winnerName", "李四不是王二麻子");
                map.put("participateNumber", "李四不是王二麻子");
                map.put("announceTime", "2016-05-27 17:39:16:127");
                dataList.add(map);
                flagList.add(true);
            } else if (i % 3 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "360奇酷青春版 16G版 4G手机");
                map.put("price", "6088.00");
                map.put("minute", "456789");
                map.put("second", "56");
                map.put("hair", "34");
                map.put("id", i);
                dataList.add(map);
                flagList.add(false);
            } else if (i % 4 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "360奇酷青春版 16G版 4G手机");
                map.put("price", "2088.00");
                map.put("minute", "456789");
                map.put("second", "48");
                map.put("hair", "13");
                map.put("id", i);
                map.put("winnerName", "李四不是王二麻子");
                map.put("participateNumber", "李四不是王二麻子");
                map.put("announceTime", "2016-05-27 17:39:16:127");
                dataList.add(map);
                flagList.add(true);
            } else {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "360奇酷青春版 16G版 4G手机");
                map.put("price", "2088.00");
                map.put("minute", "56789");
                map.put("second", "45");
                map.put("hair", "56");
                map.put("id", i);
                dataList.add(map);
                flagList.add(false);
            }
        }
        dataMap.put("dataList", dataList);
        dataMap.put("flagList", flagList);
        setAdapter();
    }

    private void setAdapter() {
        if (!(dataList.size() == 0)) {
            anim.cancel();
            fragment_duobao_immediately_announcedLoadImg.clearAnimation();
            adapter = new DuoBaoImmediatelyAnnouncedFragmentAdapter(getActivity(), getActivity(), dataMap, IApplication.options, imageLoader);
            adapter.setOnItemViewClickListener(this);
            fragmentDuobaoImmediatelyAnnouncedRecycleView.setAdapter(adapter);
        } else {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.fragment_duobao_immediately_announced_itemRL:
                intent = new Intent(getActivity(), CommodityDetail.class);
                intent.putExtra("id", String.valueOf(dataList.get(position).get("id")));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
        }
    }
}
