package com.candy.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.LoginActivity;
import com.candy.android.configs.Define;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiGeneralResponse;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Favo
 * Created on 25/10/2016.
 */

public class DialogReIssuePassword extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "IDK-DialogReIssuePassword";
    private TextView emailField;
    private Button reissuePwdBtn;

    public static final int PASSWORD_SENT = 0;
    public static final int PASSWORD_NOT_SENT = 1;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View inflated = View.inflate(getContext(), R.layout.dialog_re_issue_password, null);
        builder.setView(inflated).setCancelable(true);
        initWidgets(inflated);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        setFullWidth(dialog);
        return dialog;
    }

    private void setFullWidth(AlertDialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window.getAttributes() != null) {
            lp.copyFrom(window.getAttributes());
            // This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setAttributes(lp);
        }
    }

    private void initWidgets(View view) {
        emailField = (TextView) view.findViewById(R.id.edt_email);
        reissuePwdBtn = (Button) view.findViewById(R.id.btn_action_reissue_password);

        reissuePwdBtn.setOnClickListener(this);
        view.findViewById(R.id.tv_dialog_close).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dialog_close:
                dismiss();
                break;
            case R.id.btn_action_reissue_password:
                startReissuePassword();
                break;
        }
    }

    private void startReissuePassword() {
        if (!NetworkUtils.hasConnection(mContext)) {
            showRetryReissuePassDialog();
            return;
        }
        try {
            String email = emailField.getText().toString();
            if (TextUtils.isEmpty(email)) {
                if (mContext instanceof LoginActivity) {
                    ((LoginActivity) mContext).showErrorDialog(getString(R.string.email_address_not_input));
                }
                return;
            }
            if (!HimecasUtils.isValidEmail(email)) {
                if (mContext instanceof LoginActivity) {
                    ((LoginActivity) mContext).showErrorDialog(getString(R.string.input_mail_invalid));
                }
                return;
            }
            Helpers.showCircleProgressDialog(getActivity());

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            Call<ApiGeneralResponse> call = apiService.reIssuePassword(email);

            // send call object
            call.enqueue(new Callback<ApiGeneralResponse>() {
                @Override
                public void onResponse(Call<ApiGeneralResponse> call, Response<ApiGeneralResponse> response) {
                    Helpers.dismissCircleProgressDialog();
                    if (response == null || response.body() == null)
                        return;
                    handleMemberSendPasswordSuccess(response.body());
                }

                @Override
                public void onFailure(Call<ApiGeneralResponse> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "MemberSendPasswordApi failed", t);
                    Helpers.dismissCircleProgressDialog();
//                    gotoStartPageScreen();
                    showRetryReissuePassDialog();
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "MemberSendPasswordApi failed", ex);
            Helpers.dismissCircleProgressDialog();
            showRetryReissuePassDialog();
        }
    }

    private void handleMemberSendPasswordSuccess(final ApiGeneralResponse response) {
        try {
            RkLogger.d(TAG, "MemberSendPasswordApi return " + response);
            onReIssuePasswordResult(response.getSubmit() == Define.REQUEST_OK ?
                    PASSWORD_SENT : PASSWORD_NOT_SENT, response.getErrorMsg());
        } catch (Exception ex) {
            RkLogger.w(TAG, "unexpected error", ex);
        }
    }

    public void onReIssuePasswordResult(int result, String message) {
        String notice;
        if (result == DialogReIssuePassword.PASSWORD_SENT) {
            notice = getString(R.string.str_send_pwd_to_email);
        } else {
            notice = message;
        }
        DialogBuilder.NoticeDialog sendPwdResultDialog = DialogBuilder.buildNoticeDialog(notice, null);
        Helpers.showDialogFragment(getChildFragmentManager(), sendPwdResultDialog);
    }

    public void showRetryReissuePassDialog() {
        final DialogBuilder.NoticeDialog retryDialog = DialogBuilder.NoticeDialog.newInstance(getString(R.string.can_not_connect_to_server), getString(R.string.retry));
        retryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                retryDialog.dismiss();
                startReissuePassword();
            }

            @Override
            public void onCancelClick() {

            }
        });
        retryDialog.show(getChildFragmentManager(), TAG);

    }
}
