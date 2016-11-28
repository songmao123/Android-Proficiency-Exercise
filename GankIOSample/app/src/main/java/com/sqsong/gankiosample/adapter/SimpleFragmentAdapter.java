package com.sqsong.gankiosample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 青松 on 2016/11/24.
 */

public class SimpleFragmentAdapter extends FragmentPagerAdapter {

    private List<String> mTabTitles;
    private List<Fragment> mFragments;

    public SimpleFragmentAdapter(FragmentManager fm, List<String> tabTitles, List<Fragment> fragments) {
        super(fm);
        this.mTabTitles = tabTitles;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }
}
