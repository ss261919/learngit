package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.main.adapter.ShareFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.dialog.ShareImageDialogActivity;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;

/**
 * Created by Administrator on 2016/3/21.
 */
public class ShareFragment extends Fragment implements ShareFragmentAdapter.onItemViewClickListener {
    RecyclerView shareFragmentRecycleView;
    List<Map<String, Object>> dataList = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    RequestQueue queue;
    private ShareFragmentAdapter shareFragmentAdapter;
    private SwipeRefreshLayout share_fragment_SRW;
    private boolean isShow = true;
    private Toolbar shareFragmentToolbar;
    private int shareFragmentToolbarHeight;
    private int height;
    private int width;
    private Map<String, String> params = new HashMap<>();
    private int page = 1;

    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setAdapter();
                    share_fragment_SRWDisable();
                    shareFragmentLoadLL.setVisibility(View.GONE);
                    shareFragmentLoadImg.clearAnimation();
                    shareFragmentLoadImg.setVisibility(View.GONE);
                    isLoading = false;
                    break;
                case 1:
                    if (page == 1) {
                        shareFragmentLoadImg.clearAnimation();
                        shareFragmentLoadImg.setVisibility(View.GONE);
                        shareFragmentLoadTV.setText("没有数据！");
                        isLoading = false;
                        isHaveNext = false;
                        share_fragment_SRWDisable();
                    } else {
                        isHaveNext = false;
                        isLoading = false;
                        share_fragment_SRW.setEnabled(false);
                    }
                    break;
                case 3:
                    if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                        page = 1;
                        downData();
                        isLoading = true;
                    } else {
                        share_fragment_SRWDisable();
                        isLoading = false;
                        Toast.makeText(getActivity(), "网络有问题，确认无误后重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    page++;
                    downData();
                    isLoading = true;
                case 2:
                    share_fragment_SRWDisable();
                    isLoading = false;
                    shareFragmentLoadImg.clearAnimation();
                    shareFragmentLoadImg.setVisibility(View.GONE);
                    shareFragmentLoadTV.setText("没有数据！");
                    break;
                case 4:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        PreferencesUtils.putBoolean(getActivity(), "isFirstLogin", true);
                        isFirstGoToLogin = false;
                    }
                    break;
            }
        }
    };
    private FrameLayout shareFragmentFF;

    private void share_fragment_SRWDisable() {
        share_fragment_SRW.setRefreshing(false);
        share_fragment_SRW.setEnabled(false);
        share_fragment_SRW.post(new Runnable() {
            @Override
            public void run() {
                share_fragment_SRW.setEnabled(true);
            }
        });
    }

    private LinearLayout shareFragmentLoadLL;
    private ImageView shareFragmentLoadImg;
    private TextView shareFragmentLoadTV;
    private boolean isLoading = false;
    private LinearLayoutManager linearLayoutManager;
    private Message message;
    private boolean firstToast = true;
    private Animation anim;

    private void setAdapter() {
        if (!(dataList.size() == 0) && page == 1) {
            shareFragmentAdapter = new ShareFragmentAdapter(getActivity(), dataList, options, imageLoader, width, height);
            shareFragmentRecycleView.setAdapter(shareFragmentAdapter);
            shareFragmentRecycleView.setVisibility(View.VISIBLE);
            shareFragmentAdapter.setOnItemViewClickListener(this);
        } else if (!(dataList.size() == 0)) {
            shareFragmentAdapter.notifyDataSetChanged();
        }
        if ((dataList.size() == 0)) {
            isHaveNext = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, null);
        shareFragmentRecycleView = (RecyclerView) view.findViewById(R.id.shareFragmentRecycleView);
        share_fragment_SRW = (SwipeRefreshLayout) view.findViewById(R.id.share_fragment_SRW);
        shareFragmentToolbar = (Toolbar) view.findViewById(R.id.shareFragmentToolbar);
        shareFragmentLoadLL = (LinearLayout) view.findViewById(R.id.shareFragmentLoadLL);
        shareFragmentFF = (FrameLayout) view.findViewById(R.id.shareFragmentFF);
        shareFragmentLoadImg = (ImageView) view.findViewById(R.id.shareFragmentLoadImg);
        shareFragmentLoadTV = (TextView) view.findViewById(R.id.shareFragmentLoadTV);
        linearLayoutManager = new FullyLinearLayoutManager(getActivity());
        shareFragmentRecycleView.setLayoutManager(linearLayoutManager);
        shareFragmentRecycleView.setHasFixedSize(true);
        shareFragmentRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        options = IApplication.options;
        queue = Volley.newRequestQueue(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();
    }

    private int disy = 0;

    private void initListener() {
        shareFragmentRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
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
                    share_fragment_SRW.setEnabled(true);
                } else {
                    share_fragment_SRW.setEnabled(false);
                    share_fragment_SRW.setRefreshing(false);
                }
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == shareFragmentAdapter.getItemCount()) {
                    boolean isRefreshing = share_fragment_SRW.isRefreshing();
                    if (isRefreshing) {
                        return;
                    }
                    if (isHaveNext) {
                        if (!isLoading) {
                            message = new Message();
                            message.what = 5;
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        });

        share_fragment_SRW.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                                                            int topRowVerticalPosition = (shareFragmentRecycleView == null || shareFragmentRecycleView.getChildCount() == 0) ? 0 : shareFragmentRecycleView.getChildAt(0).getTop();
                                                            if (topRowVerticalPosition >= 0) {
                                                                if (!isLoading) {
                                                                    handler.sendEmptyMessageAtTime(3, 10);
                                                                } else {
                                                                    Toast.makeText(getActivity(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                                                                    share_fragment_SRWDisable();
                                                                }
                                                            } else {
                                                                share_fragment_SRW.setEnabled(false);
                                                                share_fragment_SRW.setRefreshing(false);
                                                            }
                                                        } else {
                                                            handler.sendEmptyMessage(1);
                                                            share_fragment_SRWDisable();
                                                            Toast.makeText(getActivity(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

        );
    }

    private void showToolbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(shareFragmentToolbar, "translationY", -shareFragmentToolbarHeight, 0);
        animator.setDuration(300).start();
    }

    private void hideToolbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(shareFragmentToolbar, "translationY", 0, -shareFragmentToolbarHeight);
        animator.setDuration(300).start();
    }

    private void initView() {

    }

    private void initData() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        shareFragmentToolbarHeight = shareFragmentToolbar.getLayoutParams().height;
        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.load);
            anim.start();
            shareFragmentLoadImg.setAnimation(anim);
            downData();
            isLoading = true;
        } else {
            shareFragmentLoadImg.setVisibility(View.GONE);
            shareFragmentLoadTV.setText("网络有问题，确认无误后重试！");
            Toast.makeText(getActivity(), "很遗憾，网络没连接，请检查网络设置", Toast.LENGTH_SHORT).show();
        }
    }

    private void downData() {
        if (NetWorkInfo.isNetworkAvailable(getActivity())) {
            GetShareImgData thread = new GetShareImgData();
            thread.start();
        } else Toast.makeText(getActivity(), "网络还没准备好,请检查网络连接！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent;
        if (position <= dataList.size()) {
            intent = new Intent(getActivity(), ShareImageDialogActivity.class);
            intent.putExtra("file_url_small", dataList.get(position - 1).get("file_url").toString() + "?imageView2/2/w/" + (int) (width * 0.28) + "/h/" + (int) (height * 0.28));
            intent.putExtra("file_url_large", dataList.get(position - 1).get("file_url").toString() + "?imageView2/2/w/" + width + "/");
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
        }
    }

    boolean isHaveNext = true;

    @Override
    public void onItemfooterClick(TextView view, int position) {
    }

    class GetShareImgData extends Thread {
        @Override
        public void run() {
            String strUrlPath;
            if (PreferencesUtils.getBoolean(getActivity(), "isOtherLogin")) {
                strUrlPath = Constants.shareImgFragmentUri + PreferencesUtils.getString(getActivity(), "otherToken");
            } else {
                strUrlPath = Constants.shareImgFragmentUri + PreferencesUtils.getString(getActivity(), "token");
            }
            params.put("page", String.valueOf(page));
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            if (TextUtils.equals(String.valueOf(page), "1")) {
                                dataList.clear();
                                dataList = JsonUtils.getListMap(map.get("data").toString());
                            } else
                                dataList.addAll(JsonUtils.getListMap(map.get("data").toString()));
                            message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessageAtTime(1, 10);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false")
                            && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.shareImgFragmentUri + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            if (!map.get("data").toString().equals("[]")) {
                                if (TextUtils.equals(String.valueOf(page), "1")) {
                                    dataList = JsonUtils.getListMap(map.get("data").toString());
                                } else
                                    dataList.addAll(JsonUtils.getListMap(map.get("data").toString()));
                                message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            } else if (map.get("data").toString().equals("[]")) {
                                handler.sendEmptyMessage(1);
                            }
                        } else {
                            handler.sendEmptyMessage(4);
                            isFirstGoToLogin = true;
                        }
                    } else {
                        handler.sendEmptyMessageAtTime(1, 10);
                    }
                } else if (strResult.equals("-1")) {
                    message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(shareFragmentRecycleView == null))
            shareFragmentRecycleView.requestFocus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
// TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {// 不在最前端界面显示
        } else {// 重新显示到最前端中
            if (!(shareFragmentRecycleView == null))
                shareFragmentRecycleView.requestFocus();
        }
    }
}