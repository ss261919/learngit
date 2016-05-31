package vaporsome.com.vaporsome.vaporsome.commoditydetail;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.security.Key;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.custom.MyScrollView;
import vaporsome.com.vaporsome.common.utils.NetWorkInfo;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;
import vaporsome.com.vaporsome.vaporsome.computedetail.ComputeDetail;
import vaporsome.com.vaporsome.vaporsome.participaterecords.ParticipateRecords;
import vaporsome.com.vaporsome.vaporsome.photoandmesg.PhotoAndMesg;
import vaporsome.com.vaporsome.vaporsome.winnerallnumber.WinnerAllNumber;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class CommodityDetailEndedFragment extends Fragment {

    @Bind(R.id.commodityDetailEndedUserHeadIcon)
    ImageView commodityDetailEndedUserHeadIcon;
    @Bind(R.id.commodityDetailEndedUserHeadIconRL)
    RelativeLayout commodityDetailEndedUserHeadIconRL;
    @Bind(R.id.commodityDetailEndedUserName)
    TextView commodityDetailEndedUserName;
    @Bind(R.id.commodityDetailEndedUserLocation)
    TextView commodityDetailEndedUserLocation;
    @Bind(R.id.commodityDetailEndedUserParticipateTimesNumberTV)
    TextView commodityDetailEndedUserParticipateTimesNumberTV;
    @Bind(R.id.commodityDetailEndedLuckyNumberTV)
    TextView commodityDetailEndedLuckyNumberTV;
    @Bind(R.id.commodityDetailEndedAnnounceTimeTV)
    TextView commodityDetailEndedAnnounceTimeTV;
    @Bind(R.id.commodityDetailEndedWinnerAllNumber)
    TextView commodityDetailEndedWinnerAllNumber;
    @Bind(R.id.commodityDetailEndedCommodityImg)
    ImageView commodityDetailEndedCommodityImg;
    @Bind(R.id.commodityDetailEndedCommodityName)
    TextView commodityDetailEndedCommodityName;
    @Bind(R.id.commodityDetailEndedCommodityMoney)
    TextView commodityDetailEndedCommodityMoney;
    @Bind(R.id.commodityDetailEndedPhotoAndMesgDetailTV)
    TextView commodityDetailEndedPhotoAndMesgDetailTV;
    @Bind(R.id.commodityDetailEndedPhotoAndMesgDetailImgInside)
    ImageView commodityDetailEndedPhotoAndMesgDetailImgInside;
    @Bind(R.id.commodityDetailEndedPhotoAndMesgDetail)
    RelativeLayout commodityDetailEndedPhotoAndMesgDetail;
    @Bind(R.id.commodityDetailEndedAComputeDetailInsideImg)
    ImageView commodityDetailEndedAComputeDetailInsideImg;
    @Bind(R.id.commodityDetailEndedAComputeDetailRL)
    RelativeLayout commodityDetailEndedAComputeDetailRL;
    @Bind(R.id.commodityDetailEndedParticipateRecordsInsideImg)
    ImageView commodityDetailEndedParticipateRecordsInsideImg;
    @Bind(R.id.commodityDetailEndedParticipateRecords)
    RelativeLayout commodityDetailEndedParticipateRecords;
    @Bind(R.id.computeDetailEndedBottomRL)
    TextView computeDetailEndedBottomRL;
    @Bind(R.id.computeDetailEndedImgGoing)
    ImageView computeDetailEndedImgGoing;
    @Bind(R.id.commodityDetailEndedSRL)
    SwipeRefreshLayout commodityDetailEndedSRL;
    @Bind(R.id.commodityDetailEndedLL)
    LinearLayout commodityDetailEndedLL;
    private Intent intent;
    private String id = "0";
    private AnimationDrawable animationDrawable;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setData();
                    break;
            }
        }
    };
    private ImageLoader imageLoader;
    private int startY;
    private int startX;
    private boolean isShow;

    private void setData() {
        imageLoader.displayImage("http://h.hiphotos.baidu.com/image/pic/item/11385343fbf2b2117c2dc3c3c88065380cd78e38.jpg", commodityDetailEndedUserHeadIcon, IApplication.options);
        imageLoader.displayImage("http://a.hiphotos.baidu.com/image/pic/item/3bf33a87e950352ad6465dad5143fbf2b2118b6b.jpg", commodityDetailEndedCommodityImg, IApplication.options);
        commodityDetailEndedUserName.setText("王五的张三不是李四");
        commodityDetailEndedUserLocation.setText("来自:福建省厦门市");
        commodityDetailEndedUserParticipateTimesNumberTV.setText("3");
        commodityDetailEndedLuckyNumberTV.setText("1000008");
        commodityDetailEndedAnnounceTimeTV.setText("揭晓时间: " + "2016-05-25 18:20:16:526");
        commodityDetailEndedCommodityName.setText("(第90749宝)苹果（Apple）iPhone6s 16G版 4G手机");
        commodityDetailEndedCommodityMoney.setText("价值:￥" + "5088.00");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commodity_detail_ended_fragment, null);
        ButterKnife.bind(this, view);
        initData();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        return view;
    }

    private void initData() {
    }

    private void downData() {
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
    }

    private void initListener() {
        commodityDetailEndedSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkInfo.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "toast", Toast.LENGTH_SHORT).show();
                } else {
                    commodityDetailEndedSRLDisable();
                }
            }
        });
        commodityDetailEndedLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指按下时
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指移动时

                        break;
                    case MotionEvent.ACTION_UP:
                        int currentX = (int) motionEvent.getRawX();// 获取当前x坐标
                        int currentY = (int) motionEvent.getRawY();// 获取当前y坐
                        if ((currentY - startY) > 20)
                            commodityDetailEndedSRL.setEnabled(true);
                        else
                            commodityDetailEndedSRL.setEnabled(false);
                        break;
                }
                return false;
            }
        });
    }

    private void commodityDetailEndedSRLDisable() {
        commodityDetailEndedSRL.setEnabled(false);
        commodityDetailEndedSRL.setRefreshing(false);
        commodityDetailEndedSRL.post(new Runnable() {
            @Override
            public void run() {
                commodityDetailEndedSRL.setEnabled(true);
            }
        });
    }

    private void initView() {
        animationDrawable = (AnimationDrawable) computeDetailEndedImgGoing.getDrawable();
        animationDrawable.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.commodityDetailEndedWinnerAllNumber, R.id.commodityDetailEndedPhotoAndMesgDetailImgInside, R.id.commodityDetailEndedPhotoAndMesgDetail,
            R.id.commodityDetailEndedAComputeDetailInsideImg, R.id.commodityDetailEndedAComputeDetailRL, R.id.commodityDetailEndedParticipateRecordsInsideImg, R.id.commodityDetailEndedParticipateRecords})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commodityDetailEndedWinnerAllNumber:
                intent = new Intent(getActivity(), WinnerAllNumber.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailEndedPhotoAndMesgDetailImgInside:
                intent = new Intent(getActivity(), PhotoAndMesg.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailEndedPhotoAndMesgDetail:
                intent = new Intent(getActivity(), PhotoAndMesg.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailEndedAComputeDetailInsideImg:
                intent = new Intent(getActivity(), ComputeDetail.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailEndedAComputeDetailRL:
                intent = new Intent(getActivity(), ComputeDetail.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailEndedParticipateRecordsInsideImg:
                intent = new Intent(getActivity(), ParticipateRecords.class);
                startActivity(intent, id);
                break;
            case R.id.commodityDetailEndedParticipateRecords:
                intent = new Intent(getActivity(), ParticipateRecords.class);
                startActivity(intent, id);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            downData();
            Log.d("--Commodity", "isVisibleToUser" + isVisibleToUser);
            isShow = true;
        } else {
            //相当于Fragment的onPause
            isShow = false;
        }
    }

    public void startActivity(Intent intent, String id) {
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("--Commodity", "onResume" + "ended");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("--Commodity", "ended");
    }

}
