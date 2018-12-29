package com.candy.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.DialogReIssuePassword;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Favo
 * Created on 13/10/2016.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "IDK-Login";
    private Toolbar mToolbar;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mReIssuePwd;
    private Button mLoginBtn;
    private Button mBackToRegisterBtn;
    private ImageView mClearEmail;
    private ImageView mClearPass;
    private DialogBuilder.NoticeDialog2 mErrorDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        initToolbar();
        initView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartPageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(false);
            ab.setCustomView(R.layout.login_toolbar);
            ab.setDisplayShowCustomEnabled(true);
            mToolbar.findViewById(R.id.ic_action_up).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
//                    Intent intent = NavUtils.getParentActivityIntent(LoginActivity.this);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    NavUtils.navigateUpTo(LoginActivity.this, intent);
                }
            });
        }
    }

    private void initView() {
        mEmailField = (EditText) findViewById(R.id.edt_email);
        mPasswordField = (EditText) findViewById(R.id.edt_password);
        mReIssuePwd = (TextView) findViewById(R.id.tv_re_issue_password);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mBackToRegisterBtn = (Button) findViewById(R.id.btn_back_to_register);
        mClearEmail = (ImageView) findViewById(R.id.btn_clear_email);
        mClearPass = (ImageView) findViewById(R.id.btn_clear_password);

        mBackToRegisterBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        mReIssuePwd.setOnClickListener(this);
        mClearEmail.setOnClickListener(this);
        mClearPass.setOnClickListener(this);
    }

    private void onReIssuePassword() {
//        startActivityForResult(new Intent(this, DialogActivityReIssuePassword.class), Define.REQUEST_CODE_SEND_PASSWORD);
        DialogFragment df = new DialogReIssuePassword();
        df.show(getSupportFragmentManager(), DialogReIssuePassword.TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_re_issue_password:
                onReIssuePassword();
                break;
            case R.id.btn_login:
                tryLogin();
                break;
            case R.id.btn_back_to_register:
                onBackPressed();
                break;
            case R.id.btn_clear_email:
                mEmailField.getText().clear();
                break;
            case R.id.btn_clear_password:
                mPasswordField.getText().clear();
                break;
        }
    }

    private void tryLogin() {
        if (!NetworkUtils.hasConnection(this)) {
            showRetryLoginDialog();
            return;
        }
        try {
            String inputEmail = mEmailField.getText().toString();
            String inputPassword = mPasswordField.getText().toString();

            // here, the input email acts as the member's ID
            final MemberInformation member = new MemberInformation(inputEmail, inputPassword, inputEmail, null);

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            final Call<ApiMemberResponse> call = apiService.getMemberInfo(inputEmail, inputPassword);

            Helpers.showCircleProgressDialog(this);

            // send call object
            call.enqueue(new Callback<ApiMemberResponse>() {
                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                    Helpers.dismissCircleProgressDialog();
                    handleGetMemberInfoSuccess(response.body(), member);
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "memberInfoConfirm failed", t);
                    Helpers.dismissCircleProgressDialog();
                    showRetryLoginDialog();
                }
            });

        } catch (Exception ex) {
            RkLogger.e(TAG, "Try logging in", ex);
            Helpers.dismissCircleProgressDialog();
            showRetryLoginDialog();
        }
    }

    public void showErrorDialog(String message) {
        boolean dialogShown = mErrorDialog != null && mErrorDialog.getDialog() != null &&
                mErrorDialog.getDialog().isShowing();

        if (!dialogShown) {
            mErrorDialog = DialogBuilder.buildNoticeDialog2(message, null);
            Helpers.showDialogFragment(getSupportFragmentManager(), mErrorDialog);
        }
    }

    /**
     * Handle the success of get-member-info API
     *
     * @param response the result of the API
     * @param member   the member object which holds information passed to the API
     */
    private void handleGetMemberInfoSuccess(final ApiMemberResponse response, final MemberInformation member) {
        try {
            RkLogger.d(TAG, "MemberInfoApi return " + response);
            if (response.isSuccess() &&
                    response.getMember() != null) {
                // the result doesn't give id and/or pwd, so we pass the input member object to store with
                // the member in the result, too
                doSaveMemberInfo(response.getMember(), member.getId(), member.getPass(), true, response.getMember().ismIs3Day());
                if (member.getmTeleauthOk() == Define.TEL_AUTHEN_OK) {
                    SettingManager.getInstance(this).setKeyGetFreePoint(true);
                }
                gotoMainScreen();
            } else {
                showErrorDialog(getString(R.string.error_incorrect_email_or_password));
            }
        } catch (Exception ex) {
            RkLogger.e(TAG, "Try logging in", ex);
        }
    }

    public void showRetryLoginDialog() {
        final DialogBuilder.NoticeDialog retryDialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server), getString(R.string.retry));
        retryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                retryDialog.dismiss();
                tryLogin();
            }

            @Override
            public void onCancelClick() {

            }
        });
        retryDialog.show(getSupportFragmentManager(), TAG);
    }
}
