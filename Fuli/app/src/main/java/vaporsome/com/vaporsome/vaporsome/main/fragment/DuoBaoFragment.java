package vaporsome.com.vaporsome.vaporsome.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import vaporsome.com.vaporsome.R;
import vaporsome.com.vaporsome.vaporsome.main.adapter.DuoBaoFragmentAdapter;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class DuoBaoFragment extends Fragment implements View.OnClickListener {
    private ViewPager duoBaoFragmentViewPager;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private DuoBaoFragmentAdapter mFragmentAdapter;
    private LinearLayout duobao_ll_the_new_ll;
    private LinearLayout duobao_ll_immediately_announced_ll;
    private LinearLayout duobao_ll_shopping_cart_ll;
    private LinearLayout duobao_ll_records_ll;
    private ImageView duobao_ll_the_new_img;
    private ImageView duobao_ll_immediately_announced_img;
    private ImageView duobao_ll_shopping_cart_img;
    private ImageView duobao_ll_records_img;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duobao, null);
        duoBaoFragmentViewPager = (ViewPager) view.findViewById(R.id.duoBaoFragmentViewPager);

        duobao_ll_the_new_ll = (LinearLayout) view.findViewById(R.id.duobao_ll_the_new_ll);
        duobao_ll_immediately_announced_ll = (LinearLayout) view.findViewById(R.id.duobao_ll_immediately_announced_ll);
        duobao_ll_shopping_cart_ll = (LinearLayout) view.findViewById(R.id.duobao_ll_shopping_cart_ll);
        duobao_ll_records_ll = (LinearLayout) view.findViewById(R.id.duobao_ll_records_ll);

        duobao_ll_the_new_img = (ImageView) view.findViewById(R.id.duobao_ll_the_new_img);
        duobao_ll_immediately_announced_img = (ImageView) view.findViewById(R.id.duobao_ll_immediately_announced_img);
        duobao_ll_shopping_cart_img = (ImageView) view.findViewById(R.id.duobao_ll_shopping_cart_img);
        duobao_ll_records_img = (ImageView) view.findViewById(R.id.duobao_ll_records_img);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
    }

    private void initListener() {
        duobao_ll_the_new_ll.setOnClickListener(this);
        duobao_ll_immediately_announced_ll.setOnClickListener(this);
        duobao_ll_shopping_cart_ll.setOnClickListener(this);
        duobao_ll_records_ll.setOnClickListener(this);
    }

    private void initView() {
        mFragmentList.add(new DuoBaoTheNewFragment());
        mFragmentList.add(new DuoBaoImmediatelyAnnouncedFragment());
        mFragmentList.add(new DuoBaoShoppingCartFragment());
        mFragmentList.add(new DuoBaoRecordsFragment());
        mFragmentAdapter = new DuoBaoFragmentAdapter(getChildFragmentManager(), mFragmentList);
        duoBaoFragmentViewPager.setAdapter(mFragmentAdapter);
        duoBaoFragmentViewPager.setCurrentItem(0);
        duoBaoFragmentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_pressed);
                        duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_normal);
                        duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_normal);
                        duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_normal);
                        break;
                    case 1:
                        duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_normal);
                        duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_pressed);
                        duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_normal);
                        duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_normal);
                        break;
                    case 2:
                        duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_normal);
                        duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_normal);
                        duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_pressed);
                        duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_normal);
                        break;
                    case 3:
                        duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_normal);
                        duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_normal);
                        duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_normal);
                        duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_pressed);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.duobao_ll_the_new_ll:
                duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_pressed);
                duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_normal);
                duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_normal);
                duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_normal);
                duoBaoFragmentViewPager.setCurrentItem(0, true);
                break;
            case R.id.duobao_ll_immediately_announced_ll:
                duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_normal);
                duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_pressed);
                duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_normal);
                duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_normal);
                duoBaoFragmentViewPager.setCurrentItem(1, true);
                break;
            case R.id.duobao_ll_shopping_cart_ll:
                duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_normal);
                duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_normal);
                duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_pressed);
                duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_normal);
                duoBaoFragmentViewPager.setCurrentItem(2, true);
                break;
            case R.id.duobao_ll_records_ll:
                duobao_ll_the_new_img.setBackgroundResource(R.mipmap.zuixin_normal);
                duobao_ll_immediately_announced_img.setBackgroundResource(R.mipmap.jijiangjiexiao_normal);
                duobao_ll_shopping_cart_img.setBackgroundResource(R.mipmap.gouwuche_normal);
                duobao_ll_records_img.setBackgroundResource(R.mipmap.goumajilu_pressed);
                duoBaoFragmentViewPager.setCurrentItem(3, true);
                break;
        }
    }
}
