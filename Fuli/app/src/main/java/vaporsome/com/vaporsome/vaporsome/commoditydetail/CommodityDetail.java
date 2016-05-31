package vaporsome.com.vaporsome.vaporsome.commoditydetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ConcurrentModificationException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.common.utils.SetSystemColumnColorUtils;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.commoditydetail.CommodityDetailWaitForAnnounceFragment.UpdateFragmentListener;

public class CommodityDetail extends FragmentActivity implements UpdateFragmentListener {

    @Bind(R.id.commodityDetailCancelLL)
    LinearLayout commodityDetailCancelLL;
    @Bind(R.id.commodityDetailNumber1TV)
    TextView commodityDetailNumber1TV;
    @Bind(R.id.commodityDetailNumber2TV)
    TextView commodityDetailNumber2TV;
    @Bind(R.id.commodityDetailNumber3TV)
    TextView commodityDetailNumber3TV;
    @Bind(R.id.commodityDetailFrameLayout)
    FrameLayout commodityDetailFrameLayout;
    private CommodityDetailWaitForAnnounceFragment commodityDetailWaitForAnnounceFragment;
    private CommodityDetailParticipateNumberNotEnoughFragment commodityDetailParticipateNumberNotEnoughFragment;
    private CommodityDetailEndedFragment commodityDetailEndedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetSystemColumnColorUtils.setTranslucentStatus(this);
        setContentView(R.layout.activity_commodity_detail);
        ButterKnife.bind(this);
        BaseActivity.activityList.add(CommodityDetail.this);
        commodityDetailParticipateNumberNotEnoughFragment = new CommodityDetailParticipateNumberNotEnoughFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.commodityDetailFrameLayout, commodityDetailParticipateNumberNotEnoughFragment, "commodityDetailParticipateNumberNotEnoughFragment")
                .show(commodityDetailParticipateNumberNotEnoughFragment).commit();
    }


    @OnClick({R.id.commodityDetailCancelLL, R.id.commodityDetailNumber1TV, R.id.commodityDetailNumber2TV, R.id.commodityDetailNumber3TV})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commodityDetailCancelLL:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.commodityDetailNumber1TV:
                commodityDetailParticipateNumberNotEnoughFragment = new CommodityDetailParticipateNumberNotEnoughFragment();
                if (!(commodityDetailWaitForAnnounceFragment == null)) {
                    getSupportFragmentManager().beginTransaction().hide(commodityDetailWaitForAnnounceFragment).commit();
                }
                if (!(commodityDetailEndedFragment == null)) {
                    getSupportFragmentManager().beginTransaction().hide(commodityDetailEndedFragment).commit();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.commodityDetailFrameLayout, commodityDetailParticipateNumberNotEnoughFragment,
                        "commodityDetailParticipateNumberNotEnoughFragment")
                        .commit();
                getSupportFragmentManager().executePendingTransactions();
//                if (!(commodityDetailWaitForAnnounceFragment == null)) {
//                    getSupportFragmentManager().beginTransaction().remove(commodityDetailWaitForAnnounceFragment).commit();
//                }
//                if (!(commodityDetailEndedFragment == null)) {
//                    getSupportFragmentManager().beginTransaction().remove(commodityDetailEndedFragment).commit();
//                }
                commodityDetailWaitForAnnounceFragment = null;
                commodityDetailEndedFragment = null;
                break;
            case R.id.commodityDetailNumber2TV:
                commodityDetailWaitForAnnounceFragment = new CommodityDetailWaitForAnnounceFragment();
                if (!(commodityDetailParticipateNumberNotEnoughFragment == null)) {
                    getSupportFragmentManager().beginTransaction().hide(commodityDetailParticipateNumberNotEnoughFragment).commit();
                }
                if (!(commodityDetailEndedFragment == null)) {
                    getSupportFragmentManager().beginTransaction().hide(commodityDetailEndedFragment).commit();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.commodityDetailFrameLayout, commodityDetailWaitForAnnounceFragment,
                        "commodityDetailWaitForAnnounceFragment")
                        .commit();
                getSupportFragmentManager().executePendingTransactions();
//                if (!(commodityDetailParticipateNumberNotEnoughFragment == null)) {
//                    getSupportFragmentManager().beginTransaction().remove(commodityDetailParticipateNumberNotEnoughFragment).commit();
//                }
//                if (!(commodityDetailEndedFragment == null)) {
//                    getSupportFragmentManager().beginTransaction().remove(commodityDetailEndedFragment).commit();
//                }
                commodityDetailParticipateNumberNotEnoughFragment = null;
                commodityDetailEndedFragment = null;
                break;
            case R.id.commodityDetailNumber3TV:
                commodityDetailEndedFragment = new CommodityDetailEndedFragment();
                if (!(commodityDetailParticipateNumberNotEnoughFragment == null)) {
                    getSupportFragmentManager().beginTransaction().hide(commodityDetailParticipateNumberNotEnoughFragment).commit();
                }
                if (!(commodityDetailWaitForAnnounceFragment == null)) {
                    getSupportFragmentManager().beginTransaction().hide(commodityDetailWaitForAnnounceFragment).commit();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.commodityDetailFrameLayout, commodityDetailEndedFragment,
                        "commodityDetailEndedFragment")
                        .commit();
                getSupportFragmentManager().executePendingTransactions();
//                if (!(commodityDetailParticipateNumberNotEnoughFragment == null)) {
//                    getSupportFragmentManager().beginTransaction().remove(commodityDetailParticipateNumberNotEnoughFragment).commit();
//                }
//                if (!(commodityDetailWaitForAnnounceFragment == null)) {
//                    getSupportFragmentManager().beginTransaction().remove(commodityDetailWaitForAnnounceFragment).commit();
//                }
                commodityDetailParticipateNumberNotEnoughFragment = null;
                commodityDetailWaitForAnnounceFragment = null;
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    public void updateFragment() {
        commodityDetailEndedFragment = new CommodityDetailEndedFragment();
        if (!(commodityDetailParticipateNumberNotEnoughFragment == null)) {
            getSupportFragmentManager().beginTransaction().hide(commodityDetailParticipateNumberNotEnoughFragment).commit();
        }
        if (!(commodityDetailWaitForAnnounceFragment == null)) {
            getSupportFragmentManager().beginTransaction().hide(commodityDetailWaitForAnnounceFragment).commit();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.commodityDetailFrameLayout, commodityDetailEndedFragment,
                "commodityDetailEndedFragment")
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        commodityDetailParticipateNumberNotEnoughFragment = null;
        commodityDetailWaitForAnnounceFragment = null;
    }
}
