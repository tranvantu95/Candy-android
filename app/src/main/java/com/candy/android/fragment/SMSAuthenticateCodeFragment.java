package com.candy.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.menu.MainMenuContainer;
import com.candy.android.fragment.menu.MenuFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiSmsPostResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SMSAuthenticateCodeFragment extends BaseFragment {
    private static final String TAG = "SMS認証";
    private static final String TELNO = "telno";

    private EditText mEdtCodeInput;
    private Button mBtnAuthen;
    private Button mBtnClearCode;
    private String mTelno;
    private int mCode;
    private boolean isAuthenticating;

    public SMSAuthenticateCodeFragment() {
        // Required empty public constructor
    }

    public static SMSAuthenticateCodeFragment newInstance(String telno) {

        Bundle args = new Bundle();
        args.putString(TELNO, telno);
        SMSAuthenticateCodeFragment fragment = new SMSAuthenticateCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTelno = args.getString(TELNO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smsauthenticate_code, container, false);
        initView(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initView(View view) {
        mEdtCodeInput = (EditText) view.findViewById(R.id.edt_code_input);
        mBtnAuthen = (Button) view.findViewById(R.id.authenticate);
        mBtnAuthen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get code from edt
                String code = mEdtCodeInput.getText().toString();
                if (!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code) && !isAuthenticating) {
                    mCode = Integer.valueOf(code);
                    postSMSCode(mTelno, mCode);
                } else {
                    DialogBuilder.buildNoticeDialog(Define.SMS_TOKEN_ERROR[2], null).show(getChildFragmentManager(), TAG);
                }
            }
        });
        mBtnClearCode = (Button) view.findViewById(R.id.btn_clear_code);
        mBtnClearCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEdtCodeInput.getText().clear();
            }
        });
    }

    @Override
    public String getTitle() {
        return TAG;
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(true);
            // set keyboard mode
            mMainActivity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    private void postSMSCode(final String telno, final int code) {
        final DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                if (mMainActivity != null) {
                    mMainActivity.dismissRetryDialog();
                }
                postSMSCode(telno, code);
            }

            @Override
            public void onCancelClick() {

            }
        };
        if (!NetworkUtils.hasConnection(mContext)) {
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(onClickRetry);
            }
        }
        showCircleDialog();
        isAuthenticating = true;
        String id = SettingManager.getInstance(mContext).getID();
        String password = SettingManager.getInstance(mContext).getPassword();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiSmsPostResponse> call = apiService.postTelnoSms(id, password, telno, code);
        call.enqueue(new Callback<ApiSmsPostResponse>() {
            @Override
            public void onResponse(Call<ApiSmsPostResponse> call, Response<ApiSmsPostResponse> response) {
                hideCircleDialog();
                isAuthenticating = false;

                if (response == null || response.body() == null)
                    return;

                if (response.body().isSuccess()) {
                    ApiSmsPostResponse body = response.body();
                    if (body != null) {
                        int submit = body.getSms().getSubmit();
                        if (submit == Define.SMS_SUBMIT_SUCCESS) {
                            HimecasUtils.postEventBus(mContext, response.body().getMember());
                            SettingManager.getInstance(mContext).setKeyGetFreePoint(true);
                            DialogBuilder.buildNoticeDialog2(getString(R.string.get_free_point_success), new DialogBuilder.OnClickListener() {
                                @Override
                                public void onOkClick(Object object) {
                                    Fragment parentFragment = getParentFragment();
                                    if (parentFragment != null && parentFragment instanceof MainMenuContainer) {
                                        ((MainMenuContainer) parentFragment).replaceFragment(MenuFragment.newInstance());

                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.setAction(Define.IntentActions.ACTION_OPEN_HOME);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelClick() {

                                }
                            }).show(getChildFragmentManager(), TAG);
                        } else {
                            DialogBuilder.buildNoticeDialog(Define.SMS_TOKEN_ERROR[submit - 1], null).show(getChildFragmentManager(), TAG);
                        }
                    }
                } else {
                    DialogBuilder.buildNoticeDialog("Authenticate error!", null).show(getChildFragmentManager(), TAG);
                }
            }

            @Override
            public void onFailure(Call<ApiSmsPostResponse> call, Throwable t) {
                hideCircleDialog();
                if (mMainActivity != null) {
                    mMainActivity.showRetryDialog(onClickRetry);
                }
                isAuthenticating = false;
            }
        });
    }
}
