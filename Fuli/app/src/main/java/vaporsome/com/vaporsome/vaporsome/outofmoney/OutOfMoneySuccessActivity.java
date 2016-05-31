package vaporsome.com.vaporsome.vaporsome.outofmoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.main.activity.MainActivity;

public class OutOfMoneySuccessActivity extends BaseActivity {

    @Bind(R.id.outOfMoneySuccessMesgTV)
    TextView outOfMoneySuccessMesgTV;
    @Bind(R.id.outOfMoneySuccessBankId)
    TextView outOfMoneySuccessBankId;
    @Bind(R.id.outOfMoneySuccessBankName)
    TextView outOfMoneySuccessBankName;
    @Bind(R.id.outOfMoneySuccessBankIdLast)
    TextView outOfMoneySuccessBankIdLast;
    @Bind(R.id.outOfMoneySuccessMoneyNumber)
    TextView outOfMoneySuccessMoneyNumber;
    @Bind(R.id.outOfMoneySuccessCompleteBT)
    Button outOfMoneySuccessCompleteBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_of_money_success);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        outOfMoneySuccessMesgTV.setText(bundle.getString("meshStr"));
        outOfMoneySuccessBankName.setText(bundle.getString("bankName"));
        outOfMoneySuccessBankId.setText(bundle.getString("bankIdLast"));
        outOfMoneySuccessMoneyNumber.setText(bundle.getString("moneyNumber").toString() + ".00");
    }

    @Override
    public void initListener() {

    }

    @OnClick(R.id.outOfMoneySuccessCompleteBT)
    public void onClick() {
        startActivity(new Intent(OutOfMoneySuccessActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
