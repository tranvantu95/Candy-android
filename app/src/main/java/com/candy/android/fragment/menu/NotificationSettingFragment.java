package com.candy.android.fragment.menu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiGeneralResponse;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.MemberNoticeSetting;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Setting Notification
 */
public class NotificationSettingFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "IDK-NotificationSetting";

    @BindView(R.id.sw_enable_push_login)
    Switch mSwEnablePushLogin;

    @BindView(R.id.sw_enable_push_message_from_woman)
    Switch mSwEnablePushMessageFromWoman;

    @BindView(R.id.sw_enable_notify_blog_post)
    Switch mSwEnablePushBlogPost;

    @BindView(R.id.sw_enable_push_message_new_from_program)
    Switch mSwEnablePushMessageNewFromProgram;

    @BindView(R.id.edt_reject_push_time_start)
    TextView mEdtRejectPushTimeStart;

    @BindView(R.id.edt_reject_push_time_end)
    TextView mEdtRejectPushTimeEnd;

    DialogBuilder.NumberPickerDialog mDialogRejectTimeStart;
    DialogBuilder.NumberPickerDialog mDialogRejectTimeEnd;

    private int mSelectedTimeStartPosition;
    private int mSelectedTimeEndPosition;

    private boolean isDialogShowing = false;
    AlertDialog mAlertDialog;

    /**
     * Create new instance of fragment NotificationSettingFragment.
     */
    public static NotificationSettingFragment newInstance() {
        return new NotificationSettingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (NotificationManagerCompat.from(mContext).areNotificationsEnabled()) {
            loadConfig();
        } else {
            showSettingDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAlertDialog == null && !NotificationManagerCompat.from(mContext).areNotificationsEnabled()) {
            showSettingDialog();
        }else {
            if(mAlertDialog != null && NotificationManagerCompat.from(mContext).areNotificationsEnabled()){
                mAlertDialog.dismiss();
                mAlertDialog = null;
                loadConfig();
            }
        }
    }

    private void showSettingDialog() {
        mAlertDialog = Helpers.showAlertDialog(getContext(), getString(R.string.notification_setting_push_dialog_change_setting), null, null, "OK", false, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogShowing = false;
                MainMenuContainer mainMenuContainer = (MainMenuContainer) getParentFragment();
                if (mainMenuContainer != null) {
                    mainMenuContainer.doSelfBackPressed();
                }
            }
        }, null);
    }

    private void registerOnClickListener(CompoundButton.OnCheckedChangeListener listener) {
        mSwEnablePushLogin.setOnCheckedChangeListener(listener);
        mSwEnablePushBlogPost.setOnCheckedChangeListener(listener);
        mSwEnablePushMessageFromWoman.setOnCheckedChangeListener(listener);
        mSwEnablePushMessageNewFromProgram.setOnCheckedChangeListener(listener);
    }

    @Override
    public String getTitle() {
        return getString(R.string.notification_setting_title);
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

    @Override
    protected void onRetryConnectionOk() {
        if (!NetworkUtils.hasConnection(getActivity())) {
            showRetryConnectionDialog();
        }
    }

    @OnClick(R.id.edt_reject_push_time_start)
    public void onStartTimeClick(final View view) {
        mDialogRejectTimeStart = DialogBuilder.buildNumberPickerDialog(mSelectedTimeStartPosition, 0, 23, Define.NOTIFICATION_TIME, new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mDialogRejectTimeStart.dismiss();
                mSelectedTimeStartPosition = (Integer) object;
                mEdtRejectPushTimeStart.setText(Define.NOTIFICATION_TIME[mSelectedTimeStartPosition]);

                doChangePNSettingForPush();
            }

            @Override
            public void onCancelClick() {
                mDialogRejectTimeStart.dismiss();
            }
        });

        Helpers.showDialogFragment(getChildFragmentManager(), mDialogRejectTimeStart);
    }

    @OnClick(R.id.edt_reject_push_time_end)
    public void onEndTimeClick(final View view) {
        mDialogRejectTimeEnd = DialogBuilder.buildNumberPickerDialog(mSelectedTimeEndPosition, 0, 23, Define.NOTIFICATION_TIME, new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mDialogRejectTimeEnd.dismiss();
                mSelectedTimeEndPosition = (Integer) object;
                mEdtRejectPushTimeEnd.setText(Define.NOTIFICATION_TIME[mSelectedTimeEndPosition]);

                doChangePNSettingForPush();
            }

            @Override
            public void onCancelClick() {
                mDialogRejectTimeEnd.dismiss();
            }
        });

        Helpers.showDialogFragment(getChildFragmentManager(), mDialogRejectTimeEnd);
    }

    private void loadConfig() {
        if (checkConnectivity()) {
            return;
        }

        if (SettingManager.getInstance(getContext()) != null && SettingManager.getInstance(getContext()).getMemberInformation() != null) {
            String id = SettingManager.getInstance(getContext()).getMemberInformation().getId();
            String password = SettingManager.getInstance(getContext()).getMemberInformation().getPass();
            ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            Call<ApiMemberResponse> call = apiService.getPNSetting(id, password);

            // send call object
            Helpers.showCircleProgressDialog(getActivity());
            call.enqueue(new Callback<ApiMemberResponse>() {
                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                    Helpers.dismissCircleProgressDialog();
                    if (response == null || response.body() == null)
                        return;
                    RkLogger.d(TAG, "getPNSetting's response=" + response.body());
                    onGetPNSettingSuccess(response.body());
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                    Helpers.dismissCircleProgressDialog();
                    // Log error here since request failed
                    RkLogger.e(TAG, "getPNSetting failed:", t);
                }
            });
        }
    }

    private void onGetPNSettingSuccess(ApiMemberResponse response) {
        if (response != null &&
                response.getMember() != null &&
                response.getMember().getMemberNoticeSetting() != null) {
            // logically, in this procedure, we should not notice any changes,
            // to make those buttons's listeners not being invoked
            registerOnClickListener(null);

            final MemberNoticeSetting noticeSetting = response.getMember().getMemberNoticeSetting();
            mSwEnablePushLogin.setChecked(noticeSetting.getPushLogin() == Define.SETTINGS.PN_SETTING_ON);
            mSwEnablePushBlogPost.setChecked(noticeSetting.getPushBlog() == Define.SETTINGS.PN_SETTING_ON);
            mSwEnablePushMessageFromWoman.setChecked(noticeSetting.getPushMail() == Define.SETTINGS.PN_SETTING_ON);
            mSwEnablePushMessageNewFromProgram.setChecked(noticeSetting.getPushMaga() == Define.SETTINGS.PN_SETTING_ON);

            try {
                mSelectedTimeStartPosition = noticeSetting.getRejectFr();
                mEdtRejectPushTimeStart.setText(Define.NOTIFICATION_TIME[mSelectedTimeStartPosition]);
            } catch (IndexOutOfBoundsException ioobe) {
                mEdtRejectPushTimeStart.setText(Define.NOTIFICATION_TIME[0]);
            }

            try {
                mSelectedTimeEndPosition = noticeSetting.getRejectTo();
                mEdtRejectPushTimeEnd.setText(Define.NOTIFICATION_TIME[mSelectedTimeEndPosition]);
            } catch (IndexOutOfBoundsException ioobe) {
                mEdtRejectPushTimeEnd.setText(Define.NOTIFICATION_TIME[0]);
            }

            // after done applying changes, re-register those events
            registerOnClickListener(this);
        }
    }

    private void doChangePNSettingForPush() {
        if (checkConnectivity()) {
            return;
        }

        if (SettingManager.getInstance(getActivity()).hasMember()) {
            final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();
            ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            Call<ApiMemberResponse> call = apiService.changePNSettingForPush(
                    member.getId(),
                    member.getPass(),
                    mSwEnablePushMessageFromWoman.isChecked() ? 1 : 0,
                    mSwEnablePushLogin.isChecked() ? 1 : 0,
                    mSwEnablePushBlogPost.isChecked() ? 1 : 0,
                    mSwEnablePushMessageNewFromProgram.isChecked() ? 1 : 0,
                    mSelectedTimeStartPosition,
                    mSelectedTimeEndPosition);

            RkLogger.d(TAG, "enqueue call changePNSettingForPush");
            //send object
            Helpers.showCircleProgressDialog(getActivity());
            call.enqueue(new Callback<ApiMemberResponse>() {
                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                    Helpers.dismissCircleProgressDialog();
                    onChangePNSettingSuccess(response.body());
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                    Helpers.dismissCircleProgressDialog();
                    // Log error here since request failed
                    RkLogger.e(TAG, "changePNSettingForPush failed:", t);
                }
            });
        }
    }

    private void onChangePNSettingSuccess(ApiGeneralResponse response) {
        if (response != null) {
            RkLogger.d(TAG, "changePNSettingForPush response=" + response);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            default:
                RkLogger.d("notification setting", "isChecked=" + isChecked);
                doChangePNSettingForPush();
                break;
        }
    }
}
