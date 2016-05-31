package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.adsdetailactivity.FullyLinearLayoutManager;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.main.adapter.DuoBaoShoppingCartFragmentAdapter;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;
import vaporsome.com.vaporsome.vaporsome.payment.PaymentActivity;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class DuoBaoShoppingCartFragment extends Fragment implements DuoBaoShoppingCartFragmentAdapter.onItemViewClickListener {


    @Bind(R.id.fragmentDuoBaoSCImg)
    ImageView fragmentDuoBaoSCImg;
    @Bind(R.id.fragmentDuoBaoSCLoadImg)
    ImageView fragmentDuoBaoSCLoadImg;
    @Bind(R.id.fragmentDuoBaoSCBottomRL)
    RelativeLayout fragmentDuoBaoSCBottomRL;
    @Bind(R.id.fragmentDuoBaoSCBottomCommodityTV)
    TextView fragmentDuoBaoSCBottomCommodityTV;
    @Bind(R.id.fragmentDuoBaoSCBottomTV3)
    TextView fragmentDuoBaoSCBottomTV3;
    @Bind(R.id.fragmentDuoBaoSCBottomPostBT)
    Button fragmentDuoBaoSCBottomPostBT;
    @Bind(R.id.fragmentDuoBaoSCRecycleView)
    RecyclerView fragmentDuoBaoSCRecycleView;
    @Bind(R.id.fragmentDuoBaoSCSRL)
    SwipeRefreshLayout fragmentDuoBaoSCSRL;

    protected int number = 0;
    protected double moneyNumber = 0;
    private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
    private Animation anim;

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
    private HashMap<String, String> params;
    private DuoBaoShoppingCartFragmentAdapter duoBaoShoppingCartFragmentAdapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private FullyLinearLayoutManager linearLayoutManager;
    private boolean isHaveNext = true;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duobao_shopping_cart, null);
        ButterKnife.bind(this, view);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        linearLayoutManager = new FullyLinearLayoutManager(getActivity());
        options = IApplication.options;
        fragmentDuoBaoSCRecycleView.setLayoutManager(linearLayoutManager);
        fragmentDuoBaoSCRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        return view;
    }

    private void initData() {
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.load);
        anim.start();
        fragmentDuoBaoSCLoadImg.setAnimation(anim);
        downData();
        setAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
    }

    private void initListener() {
        fragmentDuoBaoSCSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    int topRowVerticalPosition = (fragmentDuoBaoSCRecycleView == null || fragmentDuoBaoSCRecycleView.getChildCount() == 0) ? 0 : fragmentDuoBaoSCRecycleView.getChildAt(0).getTop();
                    if (topRowVerticalPosition >= 0) {
                        if (!isLoading) {
                            handler.sendEmptyMessageAtTime(3, 10);
                        } else {
                            Toast.makeText(getActivity(), "正在加载数据！", Toast.LENGTH_SHORT).show();
                            fragmentDuoBaoSCSRLDisable();
                        }
                    } else {
                        fragmentDuoBaoSCSRL.setEnabled(false);
                        fragmentDuoBaoSCSRL.setRefreshing(false);
                    }
                } else {
                    handler.sendEmptyMessage(1);
                    fragmentDuoBaoSCSRLDisable();
                    Toast.makeText(getActivity(), "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragmentDuoBaoSCRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                if (topRowVerticalPosition >= 0) {
                    fragmentDuoBaoSCSRL.setEnabled(true);
                } else {
                    fragmentDuoBaoSCSRLDisable();
                }
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == duoBaoShoppingCartFragmentAdapter.getItemCount()) {
                    boolean isRefreshing = fragmentDuoBaoSCSRL.isRefreshing();
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

    private void fragmentDuoBaoSCSRLDisable() {
        fragmentDuoBaoSCSRL.setEnabled(false);
        fragmentDuoBaoSCSRL.setRefreshing(false);
        fragmentDuoBaoSCSRL.post(new Runnable() {
            @Override
            public void run() {
                fragmentDuoBaoSCSRL.setEnabled(true);
            }
        });
    }

    private void setAdapter() {
        if (!(dataList.size() == 0)) {
            anim.cancel();
            fragmentDuoBaoSCLoadImg.clearAnimation();
            fragmentDuoBaoSCLoadImg.setVisibility(View.GONE);
            fragmentDuoBaoSCBottomRL.setVisibility(View.VISIBLE);
            duoBaoShoppingCartFragmentAdapter = new DuoBaoShoppingCartFragmentAdapter(getActivity(), dataList, options, imageLoader);
            fragmentDuoBaoSCRecycleView.setAdapter(duoBaoShoppingCartFragmentAdapter);
            duoBaoShoppingCartFragmentAdapter.setOnItemViewClickListener(this);
            if (fragmentDuoBaoSCBottomRL.getVisibility() == View.VISIBLE) {
                number = 0;
                moneyNumber = 0;
                for (Map<String, Object> map :
                        dataList) {
                    number += Integer.parseInt(String.valueOf(map.get("participateNumber")));
                    moneyNumber += Double.parseDouble(String.valueOf(map.get("price"))) * Double.parseDouble(String.valueOf(map.get("participateNumber")));
                }
            }
            //商品件数
            fragmentDuoBaoSCBottomCommodityTV.setText(String.valueOf(number));
            //商品总金额
            fragmentDuoBaoSCBottomTV3.setText(String.valueOf(moneyNumber));
        } else {
            fragmentDuoBaoSCImg.setVisibility(View.VISIBLE);
            fragmentDuoBaoSCBottomRL.setVisibility(View.GONE);
        }
    }

    private void downData() {
        HashMap<String, Object> map;
        dataList.clear();
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "(第28439宝)360奇酷 不一样的安全");
                map.put("surplusNumber", "128");
                map.put("participateNumber", "5");
                map.put("price", "1.00");
                dataList.add(map);
            } else {
                map = new HashMap<>();
                map.put("imgUrl", "http://img5.imgtn.bdimg.com/it/u=691632226,3965189645&fm=21&gp=0.jpg");
                map.put("name", "(第28439宝)苹果(Apple)iphone6s");
                map.put("surplusNumber", "100");
                map.put("participateNumber", "3");
                map.put("price", "3.00");
                dataList.add(map);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.fragmentDuoBaoSCBottomPostBT)
    public void onClick() {
        params = new HashMap<>();
        startActivity(new Intent(getActivity(), PaymentActivity.class));
        getActivity().overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
    }

    @Override
    public void onItemClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.fragment_duobao_shopping_cart_item_decreaseCountBT:
                if (Integer.parseInt(dataList.get(position).get("participateNumber").toString()) == 1) {
                } else {
                    if (dataList.get(position).containsKey("participateNumber")) {
                        dataList.get(position).put("participateNumber", String.valueOf(Integer.parseInt(String.valueOf(dataList.get(position).get("participateNumber"))) - 1));
                        dataList.get(position).put("surplusNumber", String.valueOf(Integer.parseInt(String.valueOf(dataList.get(position).get("surplusNumber"))) + 1));
                        duoBaoShoppingCartFragmentAdapter.notifyItemChanged(position);
                        if (fragmentDuoBaoSCBottomRL.getVisibility() == View.VISIBLE) {
                            number = 0;
                            moneyNumber = 0;
                            for (Map<String, Object> map :
                                    dataList) {
                                number += Integer.parseInt(String.valueOf(map.get("participateNumber")));
                                moneyNumber += Double.parseDouble(String.valueOf(map.get("price"))) * Double.parseDouble(String.valueOf(map.get("participateNumber")));
                            }
                            fragmentDuoBaoSCBottomCommodityTV.setText(String.valueOf(number));
                            fragmentDuoBaoSCBottomTV3.setText(String.valueOf(moneyNumber));
                        }
                    }
                }
                break;
            case R.id.fragment_duobao_shopping_cart_item_increaseCountBT:
                if (dataList.get(position).containsKey("participateNumber")) {
                    dataList.get(position).put("participateNumber", String.valueOf(Integer.parseInt(String.valueOf(dataList.get(position).get("participateNumber"))) + 1));
                    dataList.get(position).put("surplusNumber", String.valueOf(Integer.parseInt(String.valueOf(dataList.get(position).get("surplusNumber"))) - 1));
                    duoBaoShoppingCartFragmentAdapter.notifyItemChanged(position);
                    if (fragmentDuoBaoSCBottomRL.getVisibility() == View.VISIBLE) {
                        number = 0;
                        moneyNumber = 0;
                        for (Map<String, Object> map :
                                dataList) {
                            number += Integer.parseInt(String.valueOf(map.get("participateNumber")));
                            moneyNumber += Double.parseDouble(String.valueOf(map.get("price"))) * Double.parseDouble(String.valueOf(map.get("participateNumber")));
                        }
                        fragmentDuoBaoSCBottomCommodityTV.setText(String.valueOf(number));
                        fragmentDuoBaoSCBottomTV3.setText(String.valueOf(moneyNumber));
                    }
                }
                break;
            case R.id.fragment_duobao_records_item_delRL:
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.duobao_shopping_cart_del_dialog);
                TextView okTV = (TextView) window.findViewById(R.id.fragment_duobao_records_item_delOk);
                TextView cancelTV = (TextView) window.findViewById(R.id.fragment_duobao_records_item_delCancel);
                okTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delMethod(position);
                        alertDialog.cancel();
                    }
                });
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
                break;
        }
    }

    private void delMethod(int position) {
        dataList.remove(position);
        duoBaoShoppingCartFragmentAdapter.notifyDataSetChanged();
        if (fragmentDuoBaoSCBottomRL.getVisibility() == View.VISIBLE) {
            number = 0;
            moneyNumber = 0;
            for (Map<String, Object> map :
                    dataList) {
                number += Integer.parseInt(String.valueOf(map.get("participateNumber")));
                moneyNumber += Double.parseDouble(String.valueOf(map.get("price"))) * Double.parseDouble(String.valueOf(map.get("participateNumber")));
            }
            fragmentDuoBaoSCBottomCommodityTV.setText(String.valueOf(number));
            fragmentDuoBaoSCBottomTV3.setText(String.valueOf(moneyNumber));
        }
    }
}
