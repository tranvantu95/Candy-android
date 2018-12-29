package com.candy.android.fragment.ranking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.utils.RkLogger;
import com.candy.android.R;
import com.candy.android.utils.Helpers;

/**
 * Created by namhv on 31/08/2017.
 * Des: Ranking girl screen
 */

public class RankingContainerFragment extends BaseFragmentContainer {

    private BaseFragment currentFragment;

    public static RankingContainerFragment newInstance() {

        Bundle args = new Bundle();

        RankingContainerFragment fragment = new RankingContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranking_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        RkLogger.d("Check screen >> ", "onResume");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RkLogger.d("Check onSaveInstance >> ", "onSaveInstance");
    }

    @Override
    public String getTitle() {
        try {
            currentFragment.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void replaceFragment(BaseFragment fragment) {
        if (null != fragment) {
            currentFragment = fragment;
            Helpers.replaceFragment(getChildFragmentManager(), R.id.ranking_container, fragment);
        }
    }

    public boolean doSelfBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStack();
            return true;
        } else {
            return false;
        }
    }

    public void init() {
        RankingFragment rankingFragment = RankingFragment.newInstance();
        currentFragment = rankingFragment;
        Helpers.replaceFragment(getChildFragmentManager(), R.id.ranking_container, rankingFragment);
    }
}
