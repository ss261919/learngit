package vaporsome.com.vaporsome.vaporsome.redresultactivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.constants.Constants;
import vaporsome.com.vaporsome.common.parser.JsonUtils;
import vaporsome.com.vaporsome.common.utils.BitMapUtils;
import vaporsome.com.vaporsome.common.utils.HttpUtils;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.common.utils.PreferencesUtils;
import vaporsome.com.vaporsome.common.utils.TokenUtlils;
import vaporsome.com.vaporsome.common.utils.WXUtils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.login.LoginActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;
import vaporsome.com.vaporsome.vaporsome.main.recycleviewdivider.DividerItemDecoration;
import vaporsome.com.vaporsome.vaporsome.outofmoney.SmallChangeActivity;
import vaporsome.com.vaporsome.vaporsome.shareimg.ShareImgActivity;

public class RedResultActivity extends BaseActivity implements View.OnClickListener {

    List<Map<String, Object>> dataList = new ArrayList<>();

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private RequestQueue queue;
    private RedRecorderAdapter redRecorderAdapter;

    private String flag;
    private String companyLogo;
    private String companyName;
    private String companyComment;
    private String bag_id;
    private Map<String, String> params = new HashMap<>();
    private Message message;
    private int page = 1;
    private String money;
    private Bundle bundle;
    private boolean isShowBottomLL = false;

    private boolean isFirstGoToLogin = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (page == 1) {
                        redRecorderAdapter = new RedRecorderAdapter(getBaseContext(), dataList, options, imageLoader);
                        if (flag.equals("activity_red_result")) {
                            popWindowRecycleViewSRLDisable();
                            popWindowRecycleView.setAdapter(redRecorderAdapter);
                        } else if (flag.equals("activity_red_result_complete")) {
                            redResultCompleteRecycleView.setAdapter(redRecorderAdapter);
                            redResultCompleteSRLDisable();
                            redResultCompleteTV.setVisibility(View.GONE);
                            redResultCompleteTV.setVisibility(View.GONE);
                        }
                    } else {
                        redRecorderAdapter.notifyDataSetChanged();
                        if (flag.equals("activity_red_result")) {
                            popWindowRecycleViewSRLDisable();
                        } else if (flag.equals("activity_red_result_complete")) {
                            redResultCompleteSRLDisable();
                        }
                    }
                    if (dataList.size() == 0) {
                    }
                    isLoading = false;
                    break;
                case 1:
                    isLoading = false;
                    if (page == 1) {
                        if (flag.equals("activity_red_result")) {
                            popWindowRecycleViewSRLDisable();
                        } else if (flag.equals("activity_red_result_complete")) {
                            redResultCompleteSRLDisable();
                            redResultCompleteTV.setText("没有数据！");
                            redResultCompleteTV.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isHaveNext = false;
                    }
                    break;
                case 2:
                    popWindowCompanyRedMoneyNumber.setText(money);
                    popWindowMoneyTV.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    bundle = msg.getData();
                    Toast.makeText(RedResultActivity.this, bundle.get("data").toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
                        page = 1;
                        dataList.clear();
                        loadData();
                        redResultCompleteSRL.setRefreshing(true);
                        isLoading = true;
                        isHaveNext = true;
                        firstToast = true;
                    } else {
                        Toast.makeText(RedResultActivity.this, "网络有问题，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 5:
                    page++;
                    loadData();
                    isLoading = true;
                    break;
                case 6:
//                    if (popWindowRedSRL.isRefreshing()) {
//                        popWindowRedSRL.setEnabled(false);
//                        popWindowRedSRL.setRefreshing(false);
//                        popWindowRedSRL.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                popWindowRedSRL.setEnabled(true);
//                            }
//                        });
//                        Toast.makeText(RedResultActivity.this, "网络有问题，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(RedResultActivity.this, "网络有问题，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
                case 7:
                    if (popWindowRecycleViewSRL.isRefreshing()) {
                        popWindowRecycleViewSRL.setEnabled(false);
                        popWindowRecycleViewSRL.setRefreshing(false);
                        popWindowRecycleViewSRL.post(new Runnable() {
                            @Override
                            public void run() {
                                popWindowRecycleViewSRL.setEnabled(true);
                            }
                        });
                        Toast.makeText(RedResultActivity.this, "获取红包记录信息失败，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(RedResultActivity.this, "网络有问题，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
                        page = 1;
                        GetGrabedRedMoneyThread thread = new GetGrabedRedMoneyThread();
                        thread.start();
                        dataList.clear();
                        loadData();
                        popWindowRecycleViewSRL.setRefreshing(true);
                        isLoading = true;
                        isHaveNext = true;
                        firstToast = true;
                    } else
                        Toast.makeText(RedResultActivity.this, "网络有问题，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
                        loadData();
                    } else {
                        Toast.makeText(RedResultActivity.this, "网络有问题，确认网络无误后重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 10:
                    if (isFirstGoToLogin) {
                        startActivity(new Intent(RedResultActivity.this, LoginActivity.class));
                        PreferencesUtils.putBoolean(RedResultActivity.this, "isFirstLogin", true);
                        isFirstGoToLogin = false;
                        finish();
                    }
                    break;

                case 11:
                    String webPageUrl = Constants.wxShare + "bag-id=" + bag_id + "&company-name=" +
                            companyName + "&company-logo=" + avatar + "&comment=" + companyComment + "&total-fee=" + money;
                    if (isFriendsOrFriendsCircle.equals("friendsCircle")) {
                        WXUtils.shareWXWebPage(SendMessageToWX.Req.WXSceneTimeline, webPageUrl, "看广告～抢免费红包，尽在疯狂红包APP", "快快下载疯狂红包APP吧，每天不定时的免费红包发放，更多惊喜等着你哦...");
                    } else {
                        WXUtils.shareWXWebPage(SendMessageToWX.Req.WXSceneSession, webPageUrl, "看广告～抢免费红包，尽在疯狂红包APP", "快快下载疯狂红包APP吧，每天不定时的免费红包发放，更多惊喜等着你哦...");
                    }
                    break;
                case 12:

                    break;

            }
        }
    };
    private TextView redResultCompleteTV;
    private String avatar;
    private String webPageUrl;
    private String isFriendsOrFriendsCircle;

    private void redResultCompleteSRLDisable() {
        redResultCompleteSRL.setEnabled(false);
        redResultCompleteSRL.setRefreshing(false);
        redResultCompleteSRL.post(new Runnable() {
            @Override
            public void run() {
                redResultCompleteSRL.setEnabled(true);
            }
        });
    }

    private LinearLayout redResultMoveLL;
    private boolean isMoveUp = false;
    private int height;
    private float redResultMoveLLLocation;
    private float popWindowRecycleViewSRLLocation;
    private LinearLayout redResultMoveLL2;
    private float redResultMoveLL2Location;
    private TextView popWindowMoneyTV;
    private IWXAPI api;

    private void popWindowRecycleViewSRLDisable() {
        popWindowRecycleViewSRL.setEnabled(false);
        popWindowRecycleViewSRL.setRefreshing(false);
        popWindowRecycleViewSRL.post(new Runnable() {
            @Override
            public void run() {
                popWindowRecycleViewSRL.setEnabled(true);
            }
        });
    }

    private LinearLayout popWindowCancleLL;
    private LinearLayout popWindowShareLL;
    private TextView popWindowCompanyName;
    private ImageView popWindowCompanyIcon;
    private TextView popWindowCompanyWishText;
    private TextView popWindowCompanyRedMoneyNumber;
    private TextView popWindowHaveSaved;
    private SwipeRefreshLayout popWindowRecycleViewSRL;
    private LinearLayout popWindowRedSRL;
    private RecyclerView popWindowRecycleView;
    private LinearLayout redResultCompleteCancle;
    private RecyclerView redResultCompleteRecycleView;
    private SwipeRefreshLayout redResultCompleteRecycleViewSRL;
    private LinearLayout redResultBottomLayout;
    private LinearLayout redResultBottomLayoutShare;
    private LinearLayout redResultBottomLayoutShareWXFriends;
    private LinearLayout redResultBottomLayoutShareWXFriendsCircle;
    private Button redResultBottomLayoutCancel;
    private LinearLayout redRecoderBGLL;
    private boolean isLoading = false;
    private boolean firstToast = true;
    private boolean isHaveNext = true;
    private SwipeRefreshLayout redResultCompleteSRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if (TextUtils.equals(bundle.getString("code"), "0") | TextUtils.equals(bundle.getString("code"), "01")) {
            setContentView(R.layout.activity_red_result);
            flag = "activity_red_result";
        } else if (TextUtils.equals(bundle.getString("code"), "1")) {
            setContentView(R.layout.activity_red_result_complete);
            flag = "activity_red_result_complete";
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        options = IApplication.options;
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        options = IApplication.options;
        queue = Volley.newRequestQueue(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        bag_id = bundle.getString("bag-id");
        if (flag.equals("activity_red_result")) {
            popWindowCancleLL = (LinearLayout) findViewById(R.id.popWindowCancleLL);
            redRecoderBGLL = (LinearLayout) findViewById(R.id.redRecorderBGLL);
            popWindowShareLL = (LinearLayout) findViewById(R.id.popWindowShareLL);
            redResultBottomLayout = (LinearLayout) findViewById(R.id.redResultBottomLayout);
            redResultBottomLayoutShare = (LinearLayout) findViewById(R.id.redResultBottomLayoutShare);
            redResultBottomLayoutShareWXFriends = (LinearLayout) findViewById(R.id.redResultBottomLayoutShareWXFriends);
            redResultBottomLayoutShareWXFriendsCircle = (LinearLayout) findViewById(R.id.redResultBottomLayoutShareWXFriendsCircle);
            redResultBottomLayoutCancel = (Button) findViewById(R.id.redResultBottomLayoutCancel);

            popWindowCompanyName = (TextView) findViewById(R.id.popWindowCompanyName);
            popWindowCompanyIcon = (ImageView) findViewById(R.id.popWindowCompanyIcon);
            popWindowCompanyWishText = (TextView) findViewById(R.id.popWindowCompanyWishText);
            popWindowCompanyRedMoneyNumber = (TextView) findViewById(R.id.popWindowCompanyRedMoneyNumber);
            popWindowHaveSaved = (TextView) findViewById(R.id.popWindowHaveSaved);
            popWindowMoneyTV = (TextView) findViewById(R.id.popWindowMoneyTV);
            popWindowHaveSaved.setOnClickListener(this);
            popWindowRecycleViewSRL = (SwipeRefreshLayout) findViewById(R.id.popWindowRecycleViewSRL);
            popWindowRedSRL = (LinearLayout) findViewById(R.id.popWindowRedSRL);
            redResultMoveLL = (LinearLayout) findViewById(R.id.redResultMoveLL);
            redResultMoveLL2 = (LinearLayout) findViewById(R.id.redResultMoveLL2);
            popWindowRecycleView = (RecyclerView) findViewById(R.id.popWindowRecycleView);
            popWindowRecycleView.setLayoutManager(new LinearLayoutManager(this));
            popWindowRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            if (bundle.getString("code").equals("0")) {
                popWindowCompanyRedMoneyNumber.setText(bundle.getString("money"));
                popWindowMoneyTV.setVisibility(View.VISIBLE);
            } else {
                GetGrabedRedMoneyThread thread = new GetGrabedRedMoneyThread();
                thread.start();
            }
        } else if (flag.equals("activity_red_result_complete")) {
            redResultCompleteCancle = (LinearLayout) findViewById(R.id.redResultCompleteCancle);
            redResultCompleteTV = (TextView) findViewById(R.id.redResultCompleteTV);
            redResultCompleteRecycleView = (RecyclerView) findViewById(R.id.redResultCompleteRecycleView);
            redResultCompleteSRL = (SwipeRefreshLayout) findViewById(R.id.redResultCompleteSRL);
            redResultCompleteRecycleView.setLayoutManager(new LinearLayoutManager(this));
            redResultCompleteRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        }

    }

    @Override
    public void initData() {
        if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
            if (flag.equals("activity_red_result")) {
                companyLogo = PreferencesUtils.getString(RedResultActivity.this, "userHeadIcon", "");
                companyName = bundle.getString("companyName");
                popWindowCompanyName.setText(companyName);
                companyComment = bundle.getString("companyComment");
                popWindowCompanyWishText.setText(companyComment);
                redResultMoveLLLocation = redResultMoveLL.getPivotY();
                redResultMoveLL2Location = redResultMoveLL2.getPivotY();
                if (!companyLogo.equals("")) {
                    popWindowCompanyIcon.setTag(companyLogo);
                    imageLoader.loadImage(companyLogo, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            popWindowCompanyIcon.setBackgroundResource(R.mipmap.red_head_icon);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            popWindowCompanyIcon.setImageBitmap(BitMapUtils.getRoundedCornerBitmap(loadedImage, 15, 15));
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                }
                loadData();
            } else if (flag.equals("activity_red_result_complete")) {
                loadData();
            }
        } else Toast.makeText(this, "网络还没准备好，请检查网络连接！", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
            GetRedRankingThread thread = new GetRedRankingThread();
            thread.start();
        } else {
            Toast.makeText(this, "网络还没准备好！", Toast.LENGTH_SHORT).show();
        }
    }

    private int disy = 0;

    @Override
    public void initListener() {
        if (flag.equals("activity_red_result")) {
            popWindowCancleLL.setOnClickListener(this);
            popWindowShareLL.setOnClickListener(this);
            redResultBottomLayoutShareWXFriendsCircle.setOnClickListener(this);
            redResultBottomLayoutShareWXFriends.setOnClickListener(this);
            redResultBottomLayoutShare.setOnClickListener(this);
            redResultBottomLayoutCancel.setOnClickListener(this);
            popWindowRecycleViewSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    int topRowVerticalPosition = (popWindowRecycleView == null || popWindowRecycleView.getChildCount() == 0) ? 0 : popWindowRecycleView.getChildAt(0).getTop();
                    if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
                        if (topRowVerticalPosition >= 0) {
                            popWindowRecycleViewSRL.setEnabled(true);
                            if (!isLoading) {
                                handler.sendEmptyMessageAtTime(8, 10);
                            } else
                                Toast.makeText(RedResultActivity.this, "正在刷新", Toast.LENGTH_SHORT).show();
                        } else popWindowRecycleViewSRL.setEnabled(false);
                    } else {
                        popWindowRecycleViewSRL.setEnabled(false);
                        popWindowRecycleViewSRL.post(new Runnable() {
                            @Override
                            public void run() {
                                popWindowRecycleViewSRL.setEnabled(true);
                            }
                        });
                        Toast.makeText(RedResultActivity.this, "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            popWindowRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    popWindowRecycleViewSRL.setEnabled(topRowVerticalPosition >= 0);
                    int lastVisibleItemPosition = ((LinearLayoutManager) popWindowRecycleView.getLayoutManager()).findLastVisibleItemPosition();
                    int firstVisibleItem = ((LinearLayoutManager) popWindowRecycleView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (lastVisibleItemPosition + 1 == redRecorderAdapter.getItemCount()) {
                        boolean isRefreshing = popWindowRecycleViewSRL.isRefreshing();
                        if (isRefreshing) {
                            return;
                        }
                        if (!isLoading) {
                            if (isHaveNext) {
                                message = new Message();
                                message.what = 5;
                                handler.sendMessage(message);
                            }
                        }
                    }
//                    if (firstVisibleItem == 0) {
//                        if (isMoveUp) {
//                            isMoveUp = false;
//                            moveDown();
//                        }
//                    } else {
//                        if (disy > 25 && !isMoveUp) {
//                            isMoveUp = true;
//                            moveUp();
//                            disy = 0;
//                        }
//                        if (disy < -25 && isMoveUp) {
//                            isMoveUp = false;
//                            moveDown();
//                            disy = 0;
//                        }
//                    }
//                    if ((!isMoveUp && dy > 0) || (isMoveUp && dy < 0)) {
//                        disy += dy;
//                    }
                }
            });
//            popWindowRedSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
//                        popWindowRedSRL.setEnabled(true);
//                        if (!isLoading) {
//                            handler.sendEmptyMessageAtTime(4, 10);
//                        } else
//                            Toast.makeText(RedResultActivity.this, "正在刷新", Toast.LENGTH_SHORT).show();
//                    } else {
//                        popWindowRedSRL.setEnabled(false);
//                        popWindowRedSRL.post(new Runnable() {
//                                                 @Override
//                                                 public void run() {
//                                                     popWindowRecycleViewSRL.setEnabled(true);
//                                                 }
//                                             }
//                        );
//                        Toast.makeText(RedResultActivity.this, "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        } else if (flag.equals("activity_red_result_complete")) {
            redResultCompleteCancle.setOnClickListener(this);
            redResultCompleteSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    int topRowVerticalPosition = (redResultCompleteRecycleView == null || redResultCompleteRecycleView.getChildCount() == 0) ? 0 : redResultCompleteRecycleView.getChildAt(0).getTop();
                    if (NetWorkInfo.isNetworkAvailable(RedResultActivity.this)) {
                        if (topRowVerticalPosition >= 0) {
                            redResultCompleteSRL.setEnabled(true);
                            if (!isLoading) {
                                handler.sendEmptyMessageAtTime(4, 10);
                            } else
                                Toast.makeText(RedResultActivity.this, "正在刷新", Toast.LENGTH_SHORT).show();
                        } else redResultCompleteSRLDisable();
                    } else {
                        redResultCompleteSRLDisable();
                        Toast.makeText(RedResultActivity.this, "网络还没准备好，请检查后重试！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            redResultCompleteRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    redResultCompleteSRL.setEnabled(topRowVerticalPosition >= 0);
                    int lastVisibleItemPosition = ((LinearLayoutManager) redResultCompleteRecycleView.getLayoutManager()).findLastVisibleItemPosition();
                    if (lastVisibleItemPosition + 1 == redRecorderAdapter.getItemCount()) {
                        boolean isRefreshing = redResultCompleteSRL.isRefreshing();
                        if (isRefreshing) {
                            return;
                        }
                        if (!isLoading) {
                            if (isHaveNext) {
                                message = new Message();
                                message.what = 5;
                                handler.sendMessage(message);
                            } else {
                                if (firstToast) {
                                    firstToast = false;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void moveDown() {
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0, redResultMoveLLLocation);
        ObjectAnimator.ofPropertyValuesHolder(redResultMoveLL, pvhY).setDuration(150).start();
        isMoveUp = false;
    }

    private void moveUp() {
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", redResultMoveLLLocation, -redResultMoveLL2.getHeight());
        ObjectAnimator.ofPropertyValuesHolder(redResultMoveLL, pvhY).setDuration(150).start();
        isMoveUp = true;
    }

    private void show() {
        redRecoderBGLL.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(redResultBottomLayout, "translationY", redResultBottomLayout.getPivotY(), -redResultBottomLayout.getHeight());
        animator.setDuration(500);
        animator.start();
        isShowBottomLL = true;
        redResultBottomLayout.clearAnimation();
    }

    private void disAppear() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(redResultBottomLayout, "translationY", height - redResultBottomLayout.getHeight(), redResultBottomLayout.getHeight());
        animator.setDuration(500);
        animator.start();
        redRecoderBGLL.setVisibility(View.GONE);
        isShowBottomLL = false;
    }


    private void startActivity() {
        startActivity(new Intent(this, ShareImgActivity.class));
        overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
    }

    private void loadMore() {
    }

    @Override
    public void finish() {
        if (isShowBottomLL == false) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
        } else disAppear();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popWindowCancleLL:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                setResult(2);
                break;
            case R.id.popWindowShareLL:
                show();
                break;
            case R.id.redResultCompleteCancle:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in_appear, R.anim.fade_out);
                setResult(2);
                break;
            case R.id.popWindowHaveSaved:
                startActivity(new Intent(this, SmallChangeActivity.class));
                overridePendingTransition(R.anim.in_to_left, R.anim.out_to_left);
                break;
            case R.id.redResultBottomLayoutShare:
                startActivity();
                disAppear();
                break;
            case R.id.redResultBottomLayoutCancel:
                disAppear();
                break;
            case R.id.redResultBottomLayoutShareWXFriends:
                shareWXFriends();
                disAppear();
                break;
            case R.id.redResultBottomLayoutShareWXFriendsCircle:
                shareWXFriendsCircle();
                disAppear();
                break;

        }
    }

    private void shareWXFriends() {
        isFriendsOrFriendsCircle = "friends";
        if (!PreferencesUtils.getString(RedResultActivity.this, "avatar", "").equals("")) {
            avatar = PreferencesUtils.getString(RedResultActivity.this, "avatar", "");
            webPageUrl = Constants.wxShare + "bag-id=" + bag_id + "&company-name=" +
                    companyName + "&company-logo=" + avatar + "&comment=" + companyComment + "&total-fee=" + money;
            WXUtils.shareWXWebPage(SendMessageToWX.Req.WXSceneSession, webPageUrl, "看广告～抢免费红包，尽在疯狂红包APP", "快快下载疯狂红包APP吧，每天不定时的免费红包发放，更多惊喜等着你哦...");
        } else {
            downData();
        }
    }

    private void shareWXFriendsCircle() {
        isFriendsOrFriendsCircle = "friendsCircle";
        if (!PreferencesUtils.getString(RedResultActivity.this, "avatar", "").equals("")) {
            avatar = PreferencesUtils.getString(RedResultActivity.this, "avatar", "");
            webPageUrl = Constants.wxShare + "bag-id=" + bag_id + "&company-name=" +
                    companyName + "&company-logo=" + avatar + "&comment=" + companyComment + "&total-fee=" + money;
            WXUtils.shareWXWebPage(SendMessageToWX.Req.WXSceneTimeline, webPageUrl, "看广告～抢免费红包，尽在疯狂红包APP", "快快下载疯狂红包APP吧，每天不定时的免费红包发放，更多惊喜等着你哦...");
        } else {
            downData();
        }
    }

    private void downData() {
        String strUrlPath;
        if (PreferencesUtils.getBoolean(RedResultActivity.this, "isOtherLogin")) {
            strUrlPath = Constants.personCenterUri + PreferencesUtils.getString(RedResultActivity.this, "otherToken");
        } else {
            strUrlPath = Constants.personCenterUri + PreferencesUtils.getString(RedResultActivity.this, "token");
        }
        StringRequest request = new StringRequest(Request.Method.POST, strUrlPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Map<String, Object> map = JsonUtils.getMapObj(response);
                            if (TextUtils.equals(map.get("status").toString(), "true")) {
                                avatar = JsonUtils.getMapStr(map.get("info").toString()).get("avatar");
                                handler.sendEmptyMessage(11);
                            } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                                StringRequest request2 = new StringRequest(Request.Method.POST, Constants.personCenterUri + TokenUtlils.getToken(),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Map<String, Object> map2 = null;
                                                try {
                                                    map2 = JsonUtils.getMapObj(response);
                                                    if (TextUtils.equals(map2.get("status").toString(), "true")) {
                                                        avatar = JsonUtils.getMapStr(map2.get("info").toString()).get("avatar");
                                                        handler.sendEmptyMessage(11);
                                                    } else {
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                                queue.add(request2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }

    class GetRedRankingThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(RedResultActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.redRankingUrl + PreferencesUtils.getString(RedResultActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.redRankingUrl + PreferencesUtils.getString(RedResultActivity.this, "token");
            }
            params.clear();
            params.put("page", String.valueOf(page));
            params.put("bag-id", bag_id);
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
                            message = new Message();
                            message.what = 1;
                            bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.redRankingUrl + TokenUtlils.getToken();
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
                            } else {
                                message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        } else {
                            handler.sendEmptyMessage(10);
                        }
                    } else {
                        message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } else if (strResult.equals("-1")) {
                    message = new Message();
                    message.what = 7;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                message = new Message();
                message.what = 7;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }

    class GetGrabedRedMoneyThread extends Thread {
        @Override
        public void run() {
            super.run();
            String strUrlPath;
            if (PreferencesUtils.getBoolean(RedResultActivity.this, "isOtherLogin")) {
                strUrlPath = Constants.getGrabedRedMoney + PreferencesUtils.getString(RedResultActivity.this, "otherToken");
            } else {
                strUrlPath = Constants.getGrabedRedMoney + PreferencesUtils.getString(RedResultActivity.this, "token");
            }
            params.clear();
            params.put("bag-id", bag_id);
            String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
            try {
                if (!strResult.isEmpty() && !strResult.equals("-1")) {
                    Map<String, Object> map = JsonUtils.getMapObj(strResult);
                    if (TextUtils.equals(map.get("status").toString(), "true")) {
                        if (!map.get("data").toString().equals("[]")) {
                            message = new Message();
                            message.what = 2;
                            money = map.get("data").toString();
                            handler.sendMessage(message);
                        } else {
                            message = new Message();
                            message.what = 3;
                            Bundle bundle = new Bundle();
                            bundle.putString("data", map.get("data").toString());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    } else if (TextUtils.equals(map.get("status").toString(), "false") && TextUtils.equals(map.get("data").toString(), "invalid token")) {
                        strUrlPath = Constants.getGrabedRedMoney + TokenUtlils.getToken();
                        strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
                        map = JsonUtils.getMapObj(strResult);
                        if (TextUtils.equals(map.get("status").toString(), "true")) {
                            if (!map.get("data").toString().equals("[]")) {
                                message = new Message();
                                message.what = 2;
                                money = map.get("data").toString();
                                handler.sendMessage(message);
                            } else {
                                message = new Message();
                                message.what = 3;
                                Bundle bundle = new Bundle();
                                bundle.putString("data", map.get("data").toString());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        } else {
                            handler.sendEmptyMessage(10);
                            isFirstGoToLogin = true;
                        }
                    } else {
                        message = new Message();
                        message.what = 3;
                        Bundle bundle = new Bundle();
                        bundle.putString("data", map.get("data").toString());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                } else if (strResult.equals("-1")) {
                    message = new Message();
                    message.what = 6;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = new Message();
                message.what = 6;
                handler.sendMessage(message);
            }
        }
    }
}
