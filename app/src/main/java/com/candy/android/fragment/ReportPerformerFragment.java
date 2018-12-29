package com.candy.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.menu.MainMenuContainer;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.http.response.ApiMemberMissionInfoResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.PropertyChangedEvent;
import com.candy.android.model.eventBus.UpdateBlockPerformerEvent;
import com.candy.android.model.eventBus.UpdateFavoritePerformerEvent;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by NamHV on 10/18/16.
 * Screen: Report/Block Performer
 */
public class ReportPerformerFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "ReportPerformerFragment";
    private static final int MAX_LENGTH_MESSAGE = 100;
    private static final String ARG_PERFORMER_CODE = "performer_code";

    @BindView(R.id.cb_block_user)
    CheckBox mCbBlockUser;
    @BindView(R.id.btn_block_performer)
    View mBtnBlockUser;
    @BindView(R.id.edt_block_message)
    EditText mEdtReportComment;
    @BindView(R.id.report_subject_selection)
    RadioGroup mReportSubjectSelection;

    private String mPerformerCode;
    private DialogBuilder.ConfirmDialog mConfirmDialog;
    private DialogBuilder.NoticeDialog mNoticeDialog;

    /**
     * Create new instance of fragment ReportPerformerFragment.
     */
    public static ReportPerformerFragment newInstance(String performerCode) {
        ReportPerformerFragment fragment = new ReportPerformerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PERFORMER_CODE, performerCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPerformerCode = getArguments().getString(ARG_PERFORMER_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_report_block_performer, container, false);
        ButterKnife.bind(this, rootView);
        mEdtReportComment.clearFocus();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCbBlockUser.setOnCheckedChangeListener(this);

        if(getActivity() instanceof PerformerProfileActivity) {
            initToolbar(view);
            updatePoint();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void updateUI() {
        if(getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setScreenUpActionDisplay(true);
            ((MainActivity) getActivity()).setScreenTitle(getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;

            case R.id.btn_add_point:
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setAction(Define.IntentActions.ACTION_PURCHASE_POINT);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    private void initToolbar(View view) {
        view.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

        TextView mScreenTitle = (TextView) view.findViewById(R.id.screen_title);
        mScreenTitle.setText(getTitle());

        view.findViewById(R.id.performer_age).setVisibility(View.GONE);

        view.findViewById(R.id.btn_back).setOnClickListener(this);
        view.findViewById(R.id.btn_add_point).setOnClickListener(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPropertyChangedEvent(PropertyChangedEvent property) {
        if (property != null && getView() != null) {
            //display unread message
            switch (property.getType()) {
                case PropertyChangedEvent.TYPE_POINT_CHANGED:
                    TextView temp = (TextView) getView().findViewById(R.id.n_pts);
                    if(temp == null) break;
                    if (property.getValue() >= 0) {
                        temp.setText(String.format(Locale.US, getString(R.string.action_bar_n_pts), property.getValue()));
                        temp.setVisibility(View.VISIBLE);
                    } else {
                        temp.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    }

    public void updatePoint() {
        final MemberInformation member = SettingManager.getInstance(getContext()).getMemberInformation();
//        EventBus.getDefault().post(new PropertyChangedEvent(member.getPoint(), PropertyChangedEvent.TYPE_POINT_CHANGED));
        onPropertyChangedEvent(new PropertyChangedEvent(member.getPoint(), PropertyChangedEvent.TYPE_POINT_CHANGED));

        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiMemberMissionInfoResponse> call = apiService.getMemberMissionInfo(member.getId(), member.getPass());
        call.enqueue(new Callback<ApiMemberMissionInfoResponse>() {
            @Override
            public void onResponse(Call<ApiMemberMissionInfoResponse> call, Response<ApiMemberMissionInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int point = response.body().getMember().getPoint();
                    String joinDate = response.body().getMember().getJoinDate();
                    EventBus.getDefault().post(new PropertyChangedEvent(point, PropertyChangedEvent.TYPE_POINT_CHANGED));
                    onPropertyChangedEvent(new PropertyChangedEvent(member.getPoint(), PropertyChangedEvent.TYPE_POINT_CHANGED));
                    // Update point to local
                    MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
                    memberInformation.setPoint(point);
                    memberInformation.setmJoinDate(joinDate);
                    SettingManager.getInstance(getContext()).save(memberInformation);
                }
            }

            @Override
            public void onFailure(Call<ApiMemberMissionInfoResponse> call, Throwable t) {
                RkLogger.e("IDK-MainActivity", "registerPNSetting failed:", t);
            }
        });
    }

    @OnClick(R.id.btn_do_report)
    public void onReportClick(View view) {
        Helpers.showAlertDialog(getContext(),
                getString(R.string.confirm_dialog_message_report),
                null,
                getString(R.string.btn_no_jp),
                getString(R.string.btn_yes_jp),
                true,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = mEdtReportComment.getText().toString();
                        if (TextUtils.isEmpty(message)) {
                            Helpers.showAlertDialog(getContext(),
                                    getString(R.string.comment_not_entered),
                                    null,
                                    null,
                                    getString(R.string.btn_ok),
                                    true,
                                    null,
                                    null,
                                    null
                            );
                        } else {
                            int checkedRadioButtonId = mReportSubjectSelection.getCheckedRadioButtonId();
                            RadioButton checkedButton = (RadioButton) mReportSubjectSelection.findViewById(checkedRadioButtonId);
                            String subject = checkedButton.getText().toString();
                            doReportPerformer(mPerformerCode, message, subject);
                        }
                    }
                },
                null
        );
    }

    @OnClick(R.id.btn_block_performer)
    public void onBlockClick(View view) {
        Helpers.showAlertDialog(getContext(),
                getString(R.string.confirm_dialog_message_block),
                null,
                getString(R.string.btn_no_jp),
                getString(R.string.btn_yes_jp),
                true,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doBlockPerformer(mPerformerCode);
                    }
                },
                null
        );
    }

    private void doReportPerformer(final String performerCode, final String subject, final String message) {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.OnClickListener mOnClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (mMainActivity != null) {
                        mMainActivity.dismissRetryDialog();
                    }
                    doReportPerformer(performerCode, subject, message);
                }

                @Override
                public void onCancelClick() {

                }
            };
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(mOnClickRetry);
                return;
            }
        }
        try {
            if (SettingManager.getInstance(mContext).hasMember()) {
                String id = SettingManager.getInstance(mContext).getID();
                String password = SettingManager.getInstance(mContext).getPassword();

                ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

                //create call object
                Call<ApiFavoriteResponse> call = apiService.reportPerformer(id, password, performerCode, subject, message);

                // send call object
                call.enqueue(new Callback<ApiFavoriteResponse>() {
                    @Override
                    public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                        if (response == null || response.body() == null)
                            return;
                        Log.e(TAG, "onResponse:" + response.message());
                        if (response.body().isSuccess()) {
                            String message = getString(R.string.notice_dialog_message_report_success);
                            showSuccessDialog(message, false);
                        } else {
                            Log.e(TAG, "Delete favorite fail");
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure:" + t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doBlockPerformer(final String performerCode) {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.OnClickListener mOnClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (mMainActivity != null) {
                        mMainActivity.dismissRetryDialog();
                    }
                    doBlockPerformer(performerCode);
                }

                @Override
                public void onCancelClick() {

                }
            };
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(mOnClickRetry);
                return;
            }
        }
        try {
            if (SettingManager.getInstance(mContext).hasMember()) {
                String id = SettingManager.getInstance(mContext).getID();
                String password = SettingManager.getInstance(mContext).getPassword();

                ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

                RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
                //create call object
                Call<ApiFavoriteResponse> call = apiService.blockPerformer(id, password, performerCode);

                // send call object
                call.enqueue(new Callback<ApiFavoriteResponse>() {
                    @Override
                    public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                        if (response == null || response.body() == null)
                            return;
                        Log.e(TAG, "onResponse:" + response.message());
                        if (response.body().isSuccess()) {
                            String message = getString(R.string.notice_dialog_message_block_success);
                            showSuccessDialog(message, true);
                            EventBus.getDefault().post(new UpdateFavoritePerformerEvent(UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE));
                        } else {
                            Log.e(TAG, "Delete favorite fail");
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure:" + t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showWarningDialog() {
        mNoticeDialog = DialogBuilder.buildNoticeDialog("Text is too long", null);
        mNoticeDialog.setCancelable(false);
        mNoticeDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                mNoticeDialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                // Do nothing
            }
        });
        Helpers.showDialogFragment(getChildFragmentManager(), mNoticeDialog);
    }

    public void showSuccessDialog(String message, final boolean isBlock) {
        Helpers.showAlertDialog(getContext(),
                message,
                null,
                null,
                getString(R.string.btn_ok),
                false,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isBlock) {
                            EventBus.getDefault().post(new UpdateBlockPerformerEvent());
                        }

                        if (getParentFragment() != null && getParentFragment() instanceof MainMenuContainer) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Remove current
                                    ((MainMenuContainer) getParentFragment()).removeCurrentFragment();
                                    if (mMainActivity != null) {
                                        mMainActivity.updateBlockPerformerList();
                                    }
                                }
                            }, 200);
                        }

                        else if(getParentFragment() != null && getParentFragment() instanceof BlogContainerFragment) {
                            if(isBlock) {
                                getActivity().onBackPressed();
                                getActivity().onBackPressed();
                            }
                            else getActivity().onBackPressed();
                        }

                        else if(getActivity() instanceof PerformerProfileActivity) {
                            if (isBlock) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setAction(Define.IntentActions.ACTION_OPEN_HOME);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else getActivity().onBackPressed();
                        }
                    }
                },
                null
        );
    }

    @Override
    public String getTitle() {
        return getString(R.string.report_screen_title);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_block_user:
                mBtnBlockUser.setEnabled(b);
                break;
        }
    }
}
