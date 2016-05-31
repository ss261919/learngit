package vaporsome.com.vaporsome.vaporsome.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by ${Bash} on 2016/5/18.
 */
public class DuoBaoFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<android.support.v4.app.Fragment> fragmentList;

    public DuoBaoFragmentAdapter(FragmentManager childFragmentManager) {
        super(childFragmentManager);
    }

    public DuoBaoFragmentAdapter(FragmentManager childFragmentManager, ArrayList<android.support.v4.app.Fragment> mFragmentList) {
        super(childFragmentManager);
        this.fragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
