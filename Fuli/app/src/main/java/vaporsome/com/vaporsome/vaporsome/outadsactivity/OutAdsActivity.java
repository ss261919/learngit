package vaporsome.com.vaporsome.vaporsome.outadsactivity;

import android.os.Bundle;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;

public class OutAdsActivity extends BaseActivity {

    @Bind(R.id.outAdsWebView)
    WebView outAdsWebView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_ads);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        url = getIntent().getStringExtra("link_url");
        outAdsWebView.loadUrl(url);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }
}
