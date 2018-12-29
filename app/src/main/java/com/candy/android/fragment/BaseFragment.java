package com.candy.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

/**
 * Created by quannt on 10/12/2016.
 * Des: Base property for fragment
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected boolean isAlive;
    protected MainActivity mMainActivity;
    private DialogBuilder.NoticeDialog mRetryDialog;
    protected ProgressDialog mProgressDialog;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mContext instanceof MainActivity) {
            mMainActivity = (MainActivity) mContext;
        }
        isAlive = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(mContext, R.style.ProgressTheme);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isAlive = false;
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        isAlive = false;
//    }

    /**
     * Return screen title
     */
    public abstract String getTitle();

    /**
     * Update this fragment's UI (title, keyboard) accordingly, when it becomes visible
     */
    public void updateUI() {

        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(isUpActionDisplayVisible());
            // set keyboard mode
            mMainActivity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /**
     * knv added
     * update ui with only age
     */
    public void updateUI(int age) {

        if (mMainActivity != null) {
            if (age != 0)
                mMainActivity.setAgeViewValue(age);
        }
    }

    /**
     * @return true, if this fragment has UP action
     */
    protected boolean isUpActionDisplayVisible() {
        return false;
    }

    /**
     * Check if current activity has connection
     *
     * @return true, if retry dialog is shown (mean that current activity doesn't have connection
     */
    protected boolean checkConnectivity() {
        if (!NetworkUtils.hasConnection(getActivity())) {
            RkLogger.d("IDK", "Don't have connection");
            showRetryConnectionDialog();
            return true;
        }

        return false;
    }

    /**
     * knv added
     */
    protected void showRetryConnectionDialog() {
        boolean dialogShown = mRetryDialog != null && mRetryDialog.getDialog() != null
                && mRetryDialog.getDialog().isShowing();

        if (!dialogShown) {
            RkLogger.d("IDK", "dialog not shown, showing retry dialog");
            mRetryDialog = DialogBuilder.NoticeDialog.newInstance(
                    getString(R.string.can_not_connect_to_server), getString(R.string.retry), false);
            mRetryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    mRetryDialog.dismiss();
                    onRetryConnectionOk();
                }

                @Override
                public void onCancelClick() {

                }
            });
            mRetryDialog.setOnDialogBackPress(new DialogBuilder.OnDialogBackPress() {
                @Override
                public void onDialogBackPress() {
                    onRetryConnectionOk();
                }
            });

            Helpers.showDialogFragment(getChildFragmentManager(), mRetryDialog);
        }
    }

    protected void onRetryConnectionOk() {
        //no-op
    }

    /**
     * end knv
     */

    protected void showCircleDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void hideCircleDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
