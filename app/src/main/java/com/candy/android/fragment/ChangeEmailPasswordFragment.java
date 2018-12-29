package com.candy.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.candy.android.R;
import com.candy.android.activity.SplashScreen;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiGeneralResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by NamHV on 10/13/2016.
 * Screen: Change email and Password
 */
public class ChangeEmailPasswordFragment extends BaseFragment {
    private static final String TAG = ChangeEmailPasswordFragment.class.getSimpleName();

    // Views
    @BindView(R.id.edt_new_email)
    EditText mEdtNewEmail;
    @BindView(R.id.edt_new_password)
    EditText mEdtNewPassword;
    @BindView(R.id.edt_new_password_confirm)
    EditText mEdtNewPasswordConfirm;
    @BindView(R.id.btn_confirm_change_email)
    Button mBtnSave;

    private MemberInfoChange mMemberInfoChange;
    private DialogBuilder.NoticeTitleDialog mSuccessDialog;
    private DialogBuilder.NoticeDialog mErrorDialog;

    /**
     * Create new Instance of this class
     *
     * @return A new instance of fragment ChangeEmailPasswordFragment.
     */

    public static ChangeEmailPasswordFragment newInstance(Bundle bundle) {
        ChangeEmailPasswordFragment fragment = new ChangeEmailPasswordFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE) != null) {
            mMemberInfoChange = bundle.getParcelable(Define.ParamKeys.MEMBER_INFO_CHANGE);
        }
        mSuccessDialog = DialogBuilder.NoticeTitleDialog.newInstance(getString(R.string.change_pass_success_title), getString(R.string.change_pass_success_message));
        mSuccessDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                if (mMainActivity != null) {
                    //clear data
                    SettingManager.getInstance(mMainActivity).clearStoredData();
                    //route new intent
                    Intent intent = new Intent(getActivity(), SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mMainActivity.startActivity(intent);
                    mMainActivity.finish();
                }
            }

            @Override
            public void onCancelClick() {

            }
        });
        mSuccessDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_email_password, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.btn_confirm_change_email)
    void onSaveClicked(View view) {
        String newEmail = mEdtNewEmail.getText().toString();
        String newPassword = mEdtNewPassword.getText().toString();
        String newPasswordConfirm = mEdtNewPasswordConfirm.getText().toString();

        // validate input fields
        boolean isEqualPassword = newPassword.equals(newPasswordConfirm);

        String errorMessage = "";

        if (!isEqualPassword) {
            errorMessage = getString(R.string.confirm_pass_not_match);
        }

        if (!TextUtils.isEmpty(errorMessage)) {
            DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(errorMessage, null);
            Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
        } else {
//            Confirm new email and password is ok to change
            confirmCanChangeEmailAndPassword(newEmail, newPassword);
        }
    }

    /**
     * Check current user can change email and password
     */
    private void confirmCanChangeEmailAndPassword(final String newEmail, final String newPass) {

        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (mMainActivity != null) {
                        mMainActivity.dismissRetryDialog();
                    }
                    confirmCanChangeEmailAndPassword(newEmail, newPass);
                }

                @Override
                public void onCancelClick() {

                }
            };

            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(onClickRetry);
            }
            return;
        }

        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        String name = mMemberInfoChange.getMember().getName();
        String id = SettingManager.getInstance(mContext).getID() + System.currentTimeMillis();

        //Birth day is not necessary because we don't confirm it. Only need it is a valid value.
        int year = 1996;
        int month = 1;
        int dayOfMonth = 1;

        //create call object
        Call<ApiGeneralResponse> call = apiService.submit(id, newPass, newEmail, name, year, month, dayOfMonth);

        // Show loading dialog
        Helpers.showCircleProgressDialog(mContext);
        // send call object
        call.enqueue(new Callback<ApiGeneralResponse>() {
            @Override
            public void onResponse(Call<ApiGeneralResponse> call, Response<ApiGeneralResponse> response) {

                Helpers.dismissCircleProgressDialog();
                if (response == null || response.body() == null) {
                    return;
                }

                if (response.body().getSubmit() == Define.SUBMIT_OK) {
                    // Make request change email, password
                    pushMemberInfoChange(newEmail, newPass);
                } else {
                    // Turn of loading dialog
                    Helpers.dismissCircleProgressDialog();

                    String message = response.body().getErrorMsg();
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(message, null);
                    Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                }

            }

            @Override
            public void onFailure(Call<ApiGeneralResponse> call, Throwable t) {
                // Turn of loading dialog
                Helpers.dismissCircleProgressDialog();

                Log.e(TAG, "onFailure:" + t.getMessage());
                DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(t.getMessage(), null);
                Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
            }
        });
    }

    private void pushMemberInfoChange(final String newEmail, final String newPass) {
        if (SettingManager.getInstance(getContext()) != null &&
                mMemberInfoChange != null && mMemberInfoChange.getMember() != null) {
            String id = SettingManager.getInstance(getContext()).getID();
            String password = SettingManager.getInstance(getContext()).getPassword();
            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);
            final MemberInfoChange.Member member = mMemberInfoChange.getMember();
            String birthData = member.getBirth();
            int year = 1996;
            int month = 1;
            int day = 1;
            if (birthData != null) {
                Date birthDate = HimecasUtils.convertStringToDate(Define.DateFormat.YYYYMMDD, birthData);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birthDate);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
//            String loginMailSubmit = "";
//            String mailMagaSubmit = "";
            //create call object
            Call<MemberInfoChange> call = apiService.changeMemberInfo(id,
                    password,
                    newPass,
//                    member.getCode(),
                    member.getName(),
                    member.getBlood(),
                    member.getArea(),
                    newEmail,
                    member.getMessage(),
//                    loginMailSubmit,
//                    mailMagaSubmit,
                    year,
                    month,
                    day);

            // send call object
            call.enqueue(new Callback<MemberInfoChange>() {
                @Override
                public void onResponse(Call<MemberInfoChange> call, Response<MemberInfoChange> response) {
                    // Turn of loading dialog
                    Helpers.dismissCircleProgressDialog();
                    if (response == null || response.body() == null)
                        return;
                    if (response.body().isSuccess()) {
                        String mail = SettingManager.getInstance(mContext).getMemberInformation().getEmail();
//                        if(mail.contains(Define.RAND_EMAIL_POSIX)) {
//                            AdjustUtils.trackEventNewEmailPassword(newEmail, newPass);
//                        }
                        if (mSuccessDialog != null && !mSuccessDialog.isShowing() && isAlive) {
                            mSuccessDialog.show(getChildFragmentManager(), TAG);
                        }
                    } else {
                        // Show error message
                        String message = response.body().getErrorMsg();
                        DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(message, null);
                        Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                    }
                }

                @Override
                public void onFailure(Call<MemberInfoChange> call, Throwable t) {
                    // Turn of loading dialog
                    Helpers.dismissCircleProgressDialog();
                    // Log error here since request failed
                    RkLogger.e(TAG, "MemberInfoChange", t);
                }
            });
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.change_email_screen_title);
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
}
