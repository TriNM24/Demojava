package fpt.isc.nshreport.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import fpt.isc.nshreport.activities.Fragment.BaseFragment;

/**
 * Created by Chick on 8/11/2017.
 */

public class CustomViewPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mListFragments;
    private List<String> menu;
    private List<String> month;

    public CustomViewPagerAdapter(FragmentManager fm,
                                  List<BaseFragment> fragments,List<String> menu,List<String> month_) {
        super(fm);
        mListFragments = fragments;
        this.menu = menu;
        this.month = month_;
    }
    public String getMonth(int position)
    {
        return  month.get(position);
    }
    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);
    }
    @Override
    public int getCount() {
        return mListFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return menu.get(position);
    }
}
