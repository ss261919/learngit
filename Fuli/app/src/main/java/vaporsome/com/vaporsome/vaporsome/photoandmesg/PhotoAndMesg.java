package vaporsome.com.vaporsome.vaporsome.photoandmesg;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.base.BaseActivity;
import vaporsome.com.vaporsome.vaporsome.base.IApplication;

public class PhotoAndMesg extends BaseActivity {

    @Bind(R.id.photoAndMesgCancel)
    LinearLayout photoAndMesgCancel;
    @Bind(R.id.photoAndMesgRecycleView)
    RecyclerView photoAndMesgRecycleView;
    @Bind(R.id.photoAndMesgSRL)
    SwipeRefreshLayout photoAndMesgSRL;
    @Bind(R.id.photoAndMesgImgLoad)
    ImageView photoAndMesgImgLoad;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    private ArrayList<String> dataList = new ArrayList<>();
    private ImageLoader imageLoader;
    private PhotoAndMesgAdapter adapter;
    private Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_and_mesg);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        photoAndMesgRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initData() {
        anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.load);
        anim.start();
        photoAndMesgImgLoad.setAnimation(anim);
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0)
                dataList.add("http://e.hiphotos.baidu.com/image/h%3D300/sign=cc8b85e036adcbef1e3478069caf2e0e/cdbf6c81800a19d8a017382134fa828ba61e46f9.jpg");
            else if (i % 3 == 0)
                dataList.add("http://a.hiphotos.baidu.com/image/h%3D300/sign=0ef7302baf773912db268361c81b8675/9922720e0cf3d7ca833f49e0f41fbe096a63a95f.jpg");
            else if (i % 7 == 0)
                dataList.add("http://b.hiphotos.baidu.com/image/h%3D200/sign=1dd0f29335fa828bce239ae3cd1d41cd/0e2442a7d933c89570adeeedd71373f083020050.jpg");
        }
        setAdapter();
    }

    private void setAdapter() {
        if (!(anim == null)) {
            anim.cancel();
            photoAndMesgImgLoad.clearAnimation();
            photoAndMesgImgLoad.setVisibility(View.GONE);
            photoAndMesgRecycleView.setVisibility(View.VISIBLE);
        }
        if (!(dataList.size() == 0)) {
            adapter = new PhotoAndMesgAdapter(getBaseContext(), dataList, IApplication.options, imageLoader);
            photoAndMesgRecycleView.setAdapter(adapter);
        }
    }

    @Override
    public void initListener() {

    }

    @OnClick(R.id.photoAndMesgCancel)
    public void onClick() {
        finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_to_right, R.anim.out_to_right);
    }
}
