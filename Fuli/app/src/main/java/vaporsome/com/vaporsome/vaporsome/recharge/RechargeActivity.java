package vaporsome.com.vaporsome.vaporsome.recharge;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class RechargeActivity extends BaseActivity {

    @Bind(R.id.rechargeBackLL)
    LinearLayout rechargeBackLL;
    @Bind(R.id.rechargeWXChooseImg)
    ImageView rechargeWXChooseImg;
    @Bind(R.id.rechargeWXLL)
    RelativeLayout rechargeWXLL;
    @Bind(R.id.rechargeZFBChooseImg)
    ImageView rechargeZFBChooseImg;
    @Bind(R.id.rechargeZFBLL)
    RelativeLayout rechargeZFBLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.rechargeBackLL, R.id.rechargeWXChooseImg, R.id.rechargeWXLL, R.id.rechargeZFBChooseImg, R.id.rechargeZFBLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rechargeBackLL:
                finish();
                overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
                break;
            case R.id.rechargeWXChooseImg:
                wXRecharge();
                rechargeWXChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                rechargeZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
            case R.id.rechargeWXLL:
                rechargeWXChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                rechargeZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                wXRecharge();
                break;
            case R.id.rechargeZFBChooseImg:
                zFBCharge();
                rechargeZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                rechargeWXChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
            case R.id.rechargeZFBLL:
                zFBCharge();
                rechargeZFBChooseImg.setBackgroundResource(R.mipmap.zhifu_pressed);
                rechargeWXChooseImg.setBackgroundResource(R.mipmap.zhifu_normal);
                break;
        }
    }

    private void zFBCharge() {
        Toast.makeText(this, "微信支付", Toast.LENGTH_SHORT).show();
    }

    private void wXRecharge() {
        Toast.makeText(this, "支付宝支付", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
