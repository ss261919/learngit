package vaporsome.com.vaporsome.vaporsome.about;

import android.os.Bundle;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class About extends BaseActivity {

    @Bind(R.id.aboutImgBack)
    LinearLayout aboutImgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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

    @OnClick(R.id.aboutImgBack)
    public void onClick() {
        finish();
        overridePendingTransition(R.anim.in_to_right,R.anim.out_to_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
