package com.candy.android.fragment.mail.registration;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.custom.gallery.Utils;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiGeneralResponse;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.http.response.ApiUploadAvatarResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.AdjustUtils;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.RkLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Favo
 * Created on 03/11/2016.
 */

public class FragmentProfileRegisterConfirming extends FragmentProfileRegister {

    public FragmentProfileRegisterConfirming() {
        mStep = CONFIRMING_STEP;
    }

    public static FragmentProfileRegisterConfirming newInstance() {
        return new FragmentProfileRegisterConfirming();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_register_confirming, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();

        //set nick name
        TextView itemView = (TextView) view.findViewById(R.id.tv_nick_name);
        itemView.setText(registrationData.getName());

        itemView = (TextView) view.findViewById(R.id.name_name_profile_be_created);
        itemView.setText(getString(R.string.fragment_profile_register_final, registrationData.getName()));

        //set address
        itemView = (TextView) view.findViewById(R.id.tv_you_live);
        itemView.setText(Helpers.getStringAreaFromArray(registrationData.getArea(), Define.MEMBER_AREA));

        //set age
        itemView = (TextView) view.findViewById(R.id.tv_age);
        itemView.setText(registrationData.getAgeStr());

        ImageView imageView = (ImageView) view.findViewById(R.id.img_avatar);
        Uri uriAvatar = registrationData.getUriAvatar();
        if (uriAvatar != null && !TextUtils.isEmpty(uriAvatar.getPath())) {
            Glide.with(this)
                    .load(uriAvatar)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        }

        //register clicks
        view.findViewById(R.id.btn_complete).setOnClickListener(this);
        view.findViewById(R.id.btn_to_correct).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_complete:
                startNewRegistration();
//                saveDataAndProceed();
                break;
            case R.id.btn_to_correct:
                goToStep(SET_NAME_STEP);
                break;
        }
    }

    @Override
    protected void saveDataAndProceed() {
        RkLogger.d(TAG, "gonna request update member's registration");
        requestChangeMemberInfo();
    }

    @Override
    protected void loadRegistrationData() {
        //no-op
    }

    private void requestChangeMemberInfo() {

        MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
        final RegistrationDataV2 registrationData = RegistrationDataV2.getInstance();

        if (memberInformation != null && registrationData != null) {
            String id = memberInformation.getId();
            String password = memberInformation.getPass();
            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d(TAG, "Gonna call api changeMemberInfo, changes=" + registrationData +
                    ", id=" + id + ", " + password);

            //create call object
            Call<MemberInfoChange> call = apiService.changeMemberInfo(id,
                    password,
                    registrationData.getName(),
                    registrationData.getArea(),
                    registrationData.getYear(),
                    registrationData.getMonth() + 1,
                    registrationData.getDay());

            // send call object
            call.enqueue(new Callback<MemberInfoChange>() {
                @Override
                public void onResponse(Call<MemberInfoChange> call, Response<MemberInfoChange> response) {
                    try {
                        Helpers.dismissCircleProgressDialog();
                        if (response == null || response.body() == null)
                            return;
                        RkLogger.d(TAG, "changeMemberInfo response: " + response.body().getResponse());
                        RkLogger.d(TAG, "changeMemberInfo response: " + response.isSuccessful());

                        if (response.body().isSuccess()) {
                            // knv added to fix crash
                            boolean hasImageToUpload = registrationData.getUriAvatar() != null;

                            if (hasImageToUpload) {
                                File fileToUpload = new File(registrationData.getUriAvatar().getPath());
                                uploadAvatar(fileToUpload);
                            } else {
                                onChangeMemberInfoSuccess();
                            }
                        } else {
                            DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(
                                    R.string.fragment_profile_register_change_member_info_failed), null);
                            Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                        }
                    } catch (Exception ex) {
                        RkLogger.w(TAG, "Warning: ", ex);
                    }
                }

                @Override
                public void onFailure(Call<MemberInfoChange> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "Failure:", t);
                    Helpers.dismissCircleProgressDialog();
                    DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(
                            R.string.fragment_profile_register_change_member_info_failed), null);
                    Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                }
            });
        }
    }

    private void uploadAvatar(final File file) {
        final MemberInformation member = SettingManager.getInstance(getActivity()).getMemberInformation();

        // gather your request parameters
        RequestParams params = new RequestParams();
        try {
            params.put(Define.Fields.FIELD_ID, member.getId());
            params.put(Define.Fields.FIELD_PASS, member.getPass());
            if (file != null) {
                params.put(Define.Fields.FIELD_IMAGE, file);
            }
        } catch (FileNotFoundException ex) {
            RkLogger.w(TAG, "doSendMessage exception: ", ex);
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unknown exception: ", ex);
        }
        Helpers.showCircleProgressDialog(getContext());
        // send request
        AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(20 * 1000);
        String baseUrl = SettingManager.getInstance(mContext).getConfig().getWebviewBaseUrl();
        client.post(baseUrl + "member/profile/pictureUpload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                Helpers.dismissCircleProgressDialog();
                try {
                    String response = new String(bytes, Define.CONST_UTF_8);
                    Gson gson = new GsonBuilder().setLenient().create();
                    ApiUploadAvatarResponse apiResponse = gson.fromJson(response, ApiUploadAvatarResponse.class);
                    if (null != apiResponse) {
                        // handle success response
                        if (apiResponse.isSuccess()) {
                            onChangeMemberInfoSuccess();
                        } else {
                            DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(
                                    R.string.fragment_profile_register_change_member_info_failed), null);
                            Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                // handle failure response
                Helpers.dismissCircleProgressDialog();
                Utils.showToast(mContext, R.string.update_profile_unsuccess);
                RkLogger.e(TAG, "Failed: ", throwable);
            }
        });
    }


    private void onChangeMemberInfoSuccess() {
//        DialogBuilder.NoticeDialog noticeDialog = DialogBuilder.buildNoticeDialog(getString(
//                R.string.fragment_profile_register_change_member_info_success), new DialogBuilder.OnClickListener() {
//            @Override
//            public void onOkClick(Object object) {
//                // go back to mail line
//                onProfileRegisterSuccess();
//            }
//
//            @Override
//            public void onCancelClick() {
//
//            }
//        });
//        Helpers.showDialogFragment(getChildFragmentManager(), noticeDialog);
        // old app doesn't have success dialog
        // go back to mail line
        onProfileRegisterSuccess();
    }

    public void startNewRegistration() {
        checkConnectivity();
        try {
            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            final MemberInformation member = MemberInformation.generateNewMemberInfo();

            //create call object
            final Call<ApiGeneralResponse> call = apiService.submit(member.getId(),
                    member.getPass(),
                    member.getEmail(),
                    member.getName(),
                    member.getBirthYear(),
                    member.getBirthMonth(),
                    member.getBirthDay());

            Helpers.showCircleProgressDialog(getActivity());
            // send call object
            call.enqueue(new Callback<ApiGeneralResponse>() {
                @Override
                public void onResponse(Call<ApiGeneralResponse> call, Response<ApiGeneralResponse> response) {
                    if (response == null || response.body() == null) {
                        Helpers.dismissCircleProgressDialog();
                        return;
                    }
                    handleMemberInfoConfirmResponse(response.body(), member);
                }

                @Override
                public void onFailure(Call<ApiGeneralResponse> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "memberInfoConfirm failed", t);
                    Helpers.dismissCircleProgressDialog();

                    if (isAlive) {
                        checkConnectivity();
                    }
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unexpected exception:", ex);
            Helpers.dismissCircleProgressDialog();
            checkConnectivity();
        }
    }

    /**
     * Handle the success of API which allow to register
     *
     * @param result Whether it's OK t register the member
     * @param member The member to be registered
     */
    private void handleMemberInfoConfirmResponse(final ApiGeneralResponse result, final MemberInformation member) {
        RkLogger.d(TAG, "submit MemberInfoConfirmApi return: " + result);
        if (result.getSubmit() == Define.REQUEST_OK) {
            //response says ok, register this member
            doRegister(member);

            // Send adjust log event
            AdjustUtils.trackEventConfirmIDPassword(member.getId(), member.getPass());
        } else {
            Helpers.dismissCircleProgressDialog();
            Toast.makeText(getActivity(), getString(R.string.register_failed), Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Call to member register API
     *
     * @param member the object member to be registered
     */
    private void doRegister(final MemberInformation member) {
        try {
            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d(TAG, "member to be registered: " + member);

            //create api object
            Call<ApiMemberResponse> call = apiService.register(member.getId(),
                    member.getPass(),
                    member.getEmail(),
                    member.getName(),
                    member.getBirthYear(),
                    member.getBirthMonth(),
                    member.getBirthDay(),
                    member.getBannerCode(),
                    member.getOption15(),
                    member.getOption28());

            //send api object
            call.enqueue(new Callback<ApiMemberResponse>() {

                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                    if (response == null || response.body() == null)
                        return;
                    handleMemberRegisterResponse(response.body(), member);
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                    Helpers.dismissCircleProgressDialog();
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "submit MemberInfoConfirmApi:", ex);
            Helpers.dismissCircleProgressDialog();
        }
    }

    /**
     * handle the success of member register API
     *
     * @param result the response of the API
     * @param member the input object which has been registered
     */
    private void handleMemberRegisterResponse(final ApiMemberResponse result, final MemberInformation member) {
        RkLogger.d(TAG, "call MemberRegistApi return: " + result);
        if (result.getStatus() == Define.REQUEST_OK &&
                result.getMember() != null) {
            //response says ok, save this member
            // the result doesn't give id and/or pwd, so we pass the input member object to store with
            // the member in the result, too
            doSaveMemberInfo(result.getMember(), member.getId(), member.getPass(), false, result.getMember().ismIs3Day());
            saveDataAndProceed();
//            gotoProfileRegistrationActivity();
        } else {
//            mStartBtn.setEnabled(true);
            Toast.makeText(getActivity(), "Warning: register failed, please try again later", Toast.LENGTH_LONG)
                    .show();
        }
    }

    protected void doSaveMemberInfo(MemberInformation member, String id, String password, boolean isNameSet, boolean is3Day) {
        member.setId(id);
        member.setPass(password);
        member.setmIs3Day(true);
        SettingManager.getInstance(getActivity()).save(member);
        SettingManager.getInstance(getActivity()).setIsNameSet(isNameSet);
    }
}
