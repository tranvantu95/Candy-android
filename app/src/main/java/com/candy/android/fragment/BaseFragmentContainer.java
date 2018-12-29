package com.candy.android.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * @author Favo
 * Created on 18/11/2016.
 * This base treats as a container,
 */

public class BaseFragmentContainer extends BaseFragment implements FragmentManager.OnBackStackChangedListener {

    protected FragmentManager mChildFragmentMgr;

    @Override
    public String getTitle() {
        return null;
    }

    /**
     * This method is called to update UI of the last fragment in this container, accordingly, as it's visible to user
     */
    @Override
    public void updateUI() {
        // this is a container, don't call super
        Fragment lastFragment = findLastFragment();

        if (null != lastFragment && lastFragment instanceof BaseFragment) {
            ((BaseFragment) lastFragment).updateUI();
        }
    }

    /**
     * Use this method to re-validate child fragment inside this container
     */
    public void invalidateUI() {
        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChildFragmentMgr = getChildFragmentManager();
        // register callback
        mChildFragmentMgr.addOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {
        // call delegate an update ui event
        if (mMainActivity != null && mMainActivity.isHasChangedTab()) {
            updateUI();
        }
    }

    /**
     * @return the last fragment, as it's visible to user
     */
    public Fragment findLastFragment() {
        if (mChildFragmentMgr == null)
            return null;
        Fragment fragment = null;

        int index = mChildFragmentMgr.getBackStackEntryCount() - 1;

        if (index >= 0) {
            FragmentManager.BackStackEntry backEntry = mChildFragmentMgr.getBackStackEntryAt(index);
            String tag = backEntry.getName();
            fragment = mChildFragmentMgr.findFragmentByTag(tag);
        }

        return fragment;
    }

    public void gotoFragment(BaseFragment fragment) {
        //no-op
    }
}
