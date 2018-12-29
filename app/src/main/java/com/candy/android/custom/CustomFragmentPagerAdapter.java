package com.candy.android.custom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by quannt on 10/13/2016.
 * Des:
 */

public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mListFragment;
    private List<String> mListTitleFragment;

    public CustomFragmentPagerAdapter(List<Fragment> listFragment, List<String> listTitleFragment, FragmentManager fm) {
        super(fm);
        mListFragment = listFragment;
        mListTitleFragment = listTitleFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if (mListFragment != null && position < mListFragment.size()) {
            return mListFragment.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mListFragment != null) {
            return mListFragment.size();
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mListTitleFragment != null && position < mListTitleFragment.size()) {
            return mListTitleFragment.get(position);
        }
        return null;
    }
}
