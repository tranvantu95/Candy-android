package com.candy.android.fragment.mail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.fragment.BaseFragmentContainer;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;

/**
 * @author Favo
 * Created on 27/10/2016.
 */

public class FragmentMailContainer extends BaseFragmentContainer {
    private static final String TAG = "IDK-FragmentMail";

    public FragmentMailContainer() {
        // Required empty public constructor
    }

    /**
     * Create new instance of fragment MainMenuContainer.
     */
    public static FragmentMailContainer newInstance() {
        return new FragmentMailContainer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mail_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // go to start point screen
        gotoFragment(FragmentMailList.newInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        RkLogger.d("Check screen >> ", "onResume");
    }

    /**
     * utility function for replacing fragment
     *
     * @param fragment the fragment to be replaced
     */
    @Override
    public void gotoFragment(BaseFragment fragment) {
        Helpers.replaceFragment(getChildFragmentManager(), R.id.fm_mail_container, fragment);
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

}
