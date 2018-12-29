package com.candy.android.fragment.performer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.fragment.PerformersListFragment;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

/**
 * @author Favo
 * Created on 12/12/2016.
 */

public class FragmentPerformerContainer extends BaseFragmentContainer {

    private static final String TAG = "IDK-FragmentPerformerContainer";

    private BaseFragment currentFragment;

    public FragmentPerformerContainer() {
        //no-op
    }

    public static FragmentPerformerContainer newInstance() {
        return new FragmentPerformerContainer();
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_performer_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // go to start point screen
        gotoFragment(PerformersListFragment.newInstance());
    }

    /**
     * utility function for replacing fragment
     *
     * @param fragment the fragment to be replaced
     */
    @Override
    public void gotoFragment(BaseFragment fragment) {
        currentFragment = fragment;
        Helpers.replaceFragment(getChildFragmentManager(), R.id.fm_performer_container, fragment);
    }

    public boolean doSelfBackPressed() {
        if (mChildFragmentMgr == null) {
            mChildFragmentMgr = getChildFragmentManager();
            // register callback
            getChildFragmentManager().addOnBackStackChangedListener(this);
        }
        if (null != mChildFragmentMgr && mChildFragmentMgr.getBackStackEntryCount() > 1) {
            RkLogger.d(TAG, "doSelfBackPressed");
            mChildFragmentMgr.popBackStack();
            return true;
        } else {
            return false;
        }
    }

    public void removePerformer(String performerCode) {
        if (TextUtils.isEmpty(performerCode) || !TextUtils.isDigitsOnly(performerCode))
            return;
        Fragment currentFragment = mChildFragmentMgr.findFragmentById(R.id.fm_performer_container);
        if (null != currentFragment && currentFragment instanceof PerformersListFragment) {
            ((PerformersListFragment) currentFragment).removePerformer(performerCode);
        }
    }

}
