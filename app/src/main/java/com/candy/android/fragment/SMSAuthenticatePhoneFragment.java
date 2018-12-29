package com.candy.android.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.candy.android.model.eventBus.ReceiveSmsEvent;
import com.candy.android.smshandler.IncomingSmsReceiver;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SMSAuthenticatePhoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SMSAuthenticatePhoneFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "SMS認証";
    private static final int TIME_TO_CHECK_TOKEN = 5000;

    private EditText mEdtPhoneInput;
    private String mTelno;
    private boolean isAuthenticating;
    private boolean isHandInputCode;
    private boolean isReceiverRegistered;
    private IncomingSmsReceiver mSmsReceiver;
    DialogBuilder.SMSTitleDialog mSMSCheckCodeDialog;

    private boolean isReceivedToken;
    private Handler mHandler;

    public SMSAuthenticatePhoneFragment() {
        // Required empty public constructor
    }

    public static SMSAuthenticatePhoneFragment newInstance() {
        SMSAuthenticatePhoneFragment fragment = new SMSAuthenticatePhoneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEvent(ReceiveSmsEvent event) {
        if (mSmsReceiver != null && isReceiverRegistered) {
            mContext.unregisterReceiver(mSmsReceiver);
            isReceiverRegistered = false;
        }
        String code = event.getMessage();
        if (!TextUtils.isEmpty(code) && TextUtils.isDigitsOnly(code) && !isHandInputCode) {
            isReceivedToken = true;
            hideCircleDialog();
            postSMSCode(mTelno, Integer.valueOf(code));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mSmsReceiver = new IncomingSmsReceiver();
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_smsauthenticate_phone, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (isReceiverRegistered) {
            mContext.unregisterReceiver(mSmsReceiver);
            isReceiverRegistered = false;
        }
    }

    private void initView(View rootView) {
        mEdtPhoneInput = rootView.findViewById(R.id.edt_phone_input);
        TextView tvAuthenIfNotPerform = rootView.findViewById(R.id.tv_authen_if_not_perform);
        tvAuthenIfNotPerform.setOnClickListener(this);
        Button register = rootView.findViewById(R.id.register);
        register.setOnClickListener(this);
        Button clearText = rootView.findViewById(R.id.btn_clear_txt);
        clearText.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_authen_if_not_perform:
                // Move to purchase point screen
                HimecasUtils.routeToPurchasePointScreen(getActivity());
                break;
            case R.id.register:
                mTelno = mEdtPhoneInput.getText().toString();
                if (TextUtils.isEmpty(mTelno)) {
                    DialogBuilder.buildNoticeDialog2(Define.SMS_TRANSMISSION_ERROR[3], null).show(getChildFragmentManager(), TAG);
                    return;
                }
                if (!isAuthenticating) {
                    isHandInputCode = false;
                    postSMS(mTelno);
                }
                break;
            case R.id.btn_clear_txt:
                mEdtPhoneInput.getText().clear();
                break;
        }
    }

    private void postSMS(final String telno) {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (mMainActivity != null) {
                        mMainActivity.dismissRetryDialog();
                    }
                    postSMS(telno);
                }

                @Override
                public void onCancelClick() {

                }
            };

            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(onClickRetry);
            }
        }
        isAuthenticating = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        intentFilter.setPriority(Integer.MAX_VALUE);
        if (!isReceiverRegistered) {
            isReceiverRegistered = true;
            mContext.registerReceiver(mSmsReceiver, intentFilter);
        }
        showCircleDialog();
        String id = SettingManager.getInstance(mContext).getID();
        String password = SettingManager.getInstance(mContext).getPassword();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiSmsPostResponse> call = apiService.postTelnoSms(id, password, telno);
        call.enqueue(new Callback<ApiSmsPostResponse>() {
            @Override
            public void onResponse(Call<ApiSmsPostResponse> call, Response<ApiSmsPostResponse> response) {
                isAuthenticating = false;

                if (response == null || response.body() == null) {
                    hideCircleDialog();
                    return;
                }

                if (response.body().isSuccess()) {
                    int submit = response.body().getSms().getSubmit();
                    switch (submit) {
                        case 0:
                            //success
                            if (isHandInputCode) {
                                hideCircleDialog();
                                showNoticeCheckCode(telno);
                            } else {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //After 5s, if can not receiver token, show notice dialog
                                        if (!isReceivedToken) {
                                            hideCircleDialog();
                                            showNoticeCheckCode(telno);
                                            //unregister receiver
                                            if (isReceiverRegistered) {
                                                mContext.unregisterReceiver(mSmsReceiver);
                                                isReceiverRegistered = false;
                                            }
                                        }
                                    }
                                }, TIME_TO_CHECK_TOKEN);
                            }
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            hideCircleDialog();
                            DialogBuilder.buildNoticeDialog2(Define.SMS_TRANSMISSION_ERROR[submit - 1], null).show(getChildFragmentManager(), TAG);
                            break;
                        default:
                            hideCircleDialog();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiSmsPostResponse> call, Throwable t) {
                isAuthenticating = false;
                hideCircleDialog();
            }
        });
    }

    private void showNoticeCheckCode(final String telno) {
        mSMSCheckCodeDialog = DialogBuilder.SMSTitleDialog.newInstance(getString(R.string.sms_dialog_title), getString(R.string.sms_dialog_message));
        mSMSCheckCodeDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null && parentFragment instanceof MainMenuContainer) {
                    ((MainMenuContainer) parentFragment).replaceFragment(SMSAuthenticateCodeFragment.newInstance(telno));
                }
            }

            @Override
            public void onCancelClick() {

            }
        });
        mSMSCheckCodeDialog.show(getChildFragmentManager(), TAG);
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
        String id = SettingManager.getInstance(mContext).getID();
        String password = SettingManager.getInstance(mContext).getPassword();
        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiSmsPostResponse> call = apiService.postTelnoSms(id, password, telno, code);
        call.enqueue(new Callback<ApiSmsPostResponse>() {
            @Override
            public void onResponse(Call<ApiSmsPostResponse> call, Response<ApiSmsPostResponse> response) {
                hideCircleDialog();

                if (response == null || response.body() == null)
                    return;

                if (response.body().isSuccess()) {
                    int submit = response.body().getSms().getSubmit();
                    if (submit == Define.SMS_SUBMIT_SUCCESS) {
                        //11/12/2016   int pointUp = response.body().getSms().getPointup();
                        HimecasUtils.postEventBus(mContext, response.body().getMember());
                        SettingManager.getInstance(mContext).setKeyGetFreePoint(true);
                        DialogBuilder.buildNoticeDialog(getString(R.string.get_free_point_success), new DialogBuilder.OnClickListener() {
                            @Override
                            public void onOkClick(Object object) {
                                Fragment parentFragment = getParentFragment();
                                if (parentFragment != null && parentFragment instanceof MainMenuContainer) {
                                    ((MainMenuContainer) parentFragment).replaceFragment(MenuFragment.newInstance());
                                }
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setAction(Define.IntentActions.ACTION_OPEN_HOME);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        }).show(getChildFragmentManager(), TAG);
                    } else {
                        RkLogger.d("Check sms >>", "show error postSMScode");
                        DialogBuilder.buildNoticeDialog(Define.SMS_TOKEN_ERROR[submit - 1], null).show(getChildFragmentManager(), TAG);
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
            }
        });
    }
}
