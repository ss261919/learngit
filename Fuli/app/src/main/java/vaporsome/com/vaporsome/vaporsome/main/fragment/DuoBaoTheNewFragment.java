package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.adapter.DuoBaoTheNewFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.SpacesItemDecoration;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class DuoBaoTheNewFragment extends Fragment implements DuoBaoTheNewFragmentAdapter.onItemViewClickListener {

    @Bind(R.id.fragment_duobao_the_new_loadImg)
    ImageView fragmentDuobaoTheNewLoadImg;
    @Bind(R.id.fragment_duobao_the_new_RecycleView)
    RecyclerView fragmentDuobaoTheNewRecycleView;
    @Bind(R.id.fragment_duobao_the_new_SRL)
    SwipeRefreshLayout fragmentDuobaoTheNewSRL;
    private ArrayList<Map<String, Object>> dataList;
    private HashMap<String, Object> map;
    private DuoBaoTheNewFragmentAdapter adapter;
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
    private boolean isHaveNext = true;
    private GridLayoutManager linearManager;
    private Animation anim;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duobao_the_new, null);
        ButterKnife.bind(this, view);
        linearManager = new GridLayoutManager(getActivity(), 2);
//        linearManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        fragmentDuobaoTheNewRecycleView.setLayoutManager(linearManager);
        fragmentDuobaoTheNewRecycleView.addItemDecoration(new SpacesItemDecoration(2));
//        fragmentDuobaoTheNewRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        initData();
        initListener();
        return view;
    }

    private void initData() {
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.load);
        anim.start();
        fragmentDuobaoTheNewLoadImg.setAnimation(anim);
        dataList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "苹果(Apple)iphone6s");
                map.put("percentage", "30");
                map.put("price", "6088.00");
                dataList.add(map);
            } else if (i % 3 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "苹果(Apple)iphone6s");
                map.put("percentage", "80");
                map.put("price", "6088.00");
                dataList.add(map);
            } else if (i % 4 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "360奇酷");
                map.put("percentage", "90");
                map.put("price", "2088.00");
                dataList.add(map);
            } else {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "360奇酷");
                map.put("percentage", "40");
                map.put("price", "2088.00");
                dataList.add(map);
            }
        }
        setAdapter();
    }

    private void setAdapter() {
        if (!(dataList.size() == 0)) {
            anim.cancel();
            fragmentDuobaoTheNewLoadImg.clearAnimation();
            adapter = new DuoBaoTheNewFragmentAdapter(getActivity(), dataList, IApplication.options, imageLoader);
            adapter.setOnItemViewClickListener(this);
            fragmentDuobaoTheNewRecycleView.setAdapter(adapter);
        } else {

        }
    }

    private void initListener() {
        fragmentDuobaoTheNewSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    int topRowVerticalPosition = (fragmentDuobaoTheNewRecycleView == null || fragmentDuobaoTheNewRecycleView.getChildCount() == 0) ? 0 : fragmentDuobaoTheNewRecycleView.getChildAt(0).getTop();
                    if (topRowVerticalPosition >= 0) {
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(3, 10);
                        } else {
                            Toast.makeText(getActivity(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                            fragmentDuobaoTheNewSRLDisable();
                        }
                    } else {
                        fragmentDuobaoTheNewSRL.setEnabled(false);
                        fragmentDuobaoTheNewSRL.setRefreshing(false);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                    fragmentDuobaoTheNewSRLDisable();
                    Toast.makeText(getActivity(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragmentDuobaoTheNewRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                if (topRowVerticalPosition >= 0) {
                    fragmentDuobaoTheNewSRL.setEnabled(true);
                } else {
                    fragmentDuobaoTheNewSRLDisable();
                }
                int lastVisibleItemPosition = linearManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    boolean isRefreshing = fragmentDuobaoTheNewSRL.isRefreshing();
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

    private void fragmentDuobaoTheNewSRLDisable() {
        fragmentDuobaoTheNewSRL.setEnabled(false);
        fragmentDuobaoTheNewSRL.setRefreshing(false);
        fragmentDuobaoTheNewSRL.post(new Runnable() {
            @Override
            public void run() {
                fragmentDuobaoTheNewSRL.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.fragment_duobao_the_new_item_duobaoBT:
                Toast.makeText(getActivity(), "参加一元夺宝", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_duobao_the_new_item_shopping_cart:
                Toast.makeText(getActivity(), "加入购物车", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
