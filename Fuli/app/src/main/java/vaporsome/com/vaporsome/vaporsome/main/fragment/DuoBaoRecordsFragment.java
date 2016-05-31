package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.duobaodetail.DuoBaoDetail;
import vaporsome.com.vaporsome.vaporsome.main.adapter.DuoBaoRecordsFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.adapter.ShareFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.dialog.ShareImageDialogActivity;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class DuoBaoRecordsFragment extends Fragment implements DuoBaoRecordsFragmentAdapter.onItemViewClickListener {

    List<HashMap<String, Object>> dataList = new ArrayList<>();
    private HashMap<String, Object> map;
    private ImageLoader imageLoader;
    private int page = 1;
    private DuoBaoRecordsFragmentAdapter duoBaoRecordsFragmentAdapter;
    private ImageView fragment_duobao_records_loadImg;
    private SwipeRefreshLayout fragment_duobao_records_SRL;
    private RecyclerView fragment_duobao_records_RecycleView;
    private LinearLayoutManager linearLayoutManager;
    private boolean isHaveNext = true;
    private Animation anim;
    private boolean isLoading = false;
    private Message message;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duobao_records, null);
        fragment_duobao_records_loadImg = (ImageView) view.findViewById(R.id.fragment_duobao_records_loadImg);
        fragment_duobao_records_SRL = (SwipeRefreshLayout) view.findViewById(R.id.fragment_duobao_records_SRL);
        linearLayoutManager = new FullyLinearLayoutManager(getActivity());
        fragment_duobao_records_RecycleView = (RecyclerView) view.findViewById(R.id.fragment_duobao_records_RecycleView);
        fragment_duobao_records_RecycleView.setLayoutManager(linearLayoutManager);
        fragment_duobao_records_RecycleView.setHasFixedSize(true);
        fragment_duobao_records_RecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        fragment_duobao_records_SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    int topRowVerticalPosition = (fragment_duobao_records_RecycleView == null || fragment_duobao_records_RecycleView.getChildCount() == 0) ? 0 : fragment_duobao_records_RecycleView.getChildAt(0).getTop();
                    if (topRowVerticalPosition >= 0) {
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(3, 10);
                        } else {
                            Toast.makeText(getActivity(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                            fragment_duobao_records_SRLDisable();
                        }
                    } else {
                        fragment_duobao_records_SRL.setEnabled(false);
                        fragment_duobao_records_SRL.setRefreshing(false);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                    fragment_duobao_records_SRLDisable();
                    Toast.makeText(getActivity(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragment_duobao_records_RecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                if (topRowVerticalPosition >= 0) {
                    fragment_duobao_records_SRL.setEnabled(true);
                } else {
                    fragment_duobao_records_SRLDisable();
                }
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == duoBaoRecordsFragmentAdapter.getItemCount()) {
                    boolean isRefreshing = fragment_duobao_records_SRL.isRefreshing();
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

    private void fragment_duobao_records_SRLDisable() {
        fragment_duobao_records_SRL.setEnabled(false);
        fragment_duobao_records_SRL.setRefreshing(false);
        fragment_duobao_records_SRL.post(new Runnable() {
            @Override
            public void run() {
                fragment_duobao_records_SRL.setEnabled(true);
            }
        });
    }

    private void initData() {
        dataList.clear();
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.load);
        anim.start();
        fragment_duobao_records_loadImg.setAnimation(anim);
        map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.clear();
            map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
            map.put("name", "360奇酷手机");
            map.put("winName", "张昇");
            map.put("announceTime", "20分钟前");
            map.put("time", "28441");
            map.put("isAnnounce", 0);
            map.put("id", i);
            dataList.add(map);
        }
        setAdapter();
    }

    public void setAdapter() {
        fragment_duobao_records_loadImg.setVisibility(View.GONE);
        anim.cancel();
        fragment_duobao_records_loadImg.clearAnimation();
        fragment_duobao_records_RecycleView.setVisibility(View.VISIBLE);
        if (!(dataList.size() == 0) && page == 1) {
            duoBaoRecordsFragmentAdapter = new DuoBaoRecordsFragmentAdapter(getActivity(), dataList, IApplication.options, imageLoader);
            fragment_duobao_records_RecycleView.setAdapter(duoBaoRecordsFragmentAdapter);
            fragment_duobao_records_RecycleView.setVisibility(View.VISIBLE);
            duoBaoRecordsFragmentAdapter.setOnItemViewClickListener(this);
        } else if (!(dataList.size() == 0)) {
            duoBaoRecordsFragmentAdapter.notifyDataSetChanged();
        }
        if ((dataList.size() == 0)) {
            isHaveNext = false;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent;
        if (position <= dataList.size()) {
            intent = new Intent(getActivity(), DuoBaoDetail.class);
            intent.putExtra("id", String.valueOf(dataList.get(position).get("id")));
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
            Log.d("--DuoBaoRecordsFragment", "--");
        }
    }
}
