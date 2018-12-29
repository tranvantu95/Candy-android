package com.candy.android.fragment.menu;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.MainViewPager;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.model.eventBus.UpdateMissionInfoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Mission screen
 */
public class MissionFragmentContainer extends BaseFragment {

    private MissionPagerAdapter mMissionPageAdapter;

    public MissionFragmentContainer() {
        // Required empty public constructor
    }

    public static MissionFragmentContainer newInstance() {
        return new MissionFragmentContainer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mission, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.mission_tabs);

        ViewPager mMissionPager = (ViewPager) view.findViewById(R.id.mission_pager);

        mMissionPageAdapter = new MissionPagerAdapter(getFragmentManager());
        mMissionPager.setAdapter(mMissionPageAdapter);

        mTabLayout.setupWithViewPager(mMissionPager);

        // Fire event
        EventBus.getDefault().post(new UpdateMissionInfoEvent());

        //
        FragmentManager fragmentManager = getFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fm_menu_container);
        if (null != f)
        {
            if (f instanceof MissionFragmentContainer) {
                MainViewPager.setTouch(false);
                Define.webFragment = (MissionFragmentContainer) f;
            } else {
                MainViewPager.setTouch(true);
                Define.webFragment = null;
            }
        }

        //
        MainActivity activity = (MainActivity) getActivity();
        activity.disableSelectMenu();
    }

    /**
     * Listent mission change
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(UpdateMissionInfoEvent event) {
        forceTabsReloadData();
    }

    public void forceTabsReloadData() {
        if (null != mMissionPageAdapter) {
            List<ListMissionFragment> fragments = mMissionPageAdapter.getMissionFragment();
            for (ListMissionFragment fragment : fragments) {
                fragment.loadFirstData();
            }
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.mission_screen_title);
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(true);
        }
    }

    private class MissionPagerAdapter extends FragmentStatePagerAdapter {
        List<ListMissionFragment> mMissionFragment;

        MissionPagerAdapter(FragmentManager fm) {
            super(fm);
            mMissionFragment = new ArrayList<>();
            mMissionFragment.add(new ListUnCompletedMission());
            mMissionFragment.add(new ListCompletedMission());
        }

        @Override
        public Fragment getItem(int position) {
            return mMissionFragment.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return 0 == position ? getString(R.string.mission_uncompleted_title) : getString(R.string.mission_completed_title);
        }

        List<ListMissionFragment> getMissionFragment() {
            return mMissionFragment;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
