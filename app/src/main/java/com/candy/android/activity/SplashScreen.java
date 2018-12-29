package com.candy.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.candy.android.BuildConfig;
import com.candy.android.R;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiIncConfigResponse;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.Emojione;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends BaseActivity {

    private static final String TAG = "IDK-SplashScreen";
    private static final String CONST_MARKET_URL1 = "market://details?id=";
    private static final String CONST_MARKET_URL2 = "http://play.google.com/store/apps/details?id=";
    private DialogBuilder.NoticeDialog mUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        //knv added
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.hasConnection(SplashScreen.this)) {
                    doRequestConfiguration();
                } else {
                    showRetryConnectionDialog();
                }
            }
        }, 1500);

//        Emojione.checkEmoji(this);
    }

    @Override
    protected void onRetryConnectionOk() {
        if (NetworkUtils.hasConnection(SplashScreen.this)) {
            doRequestConfiguration();
        } else {
            showRetryConnectionDialog();
        }
    }

    /**
     * CALL API INCCONFIG TO GET VERSION, WEB URLS AND OWNER NAME
     */
    private void doRequestConfiguration() {
        ApiInterface apiService =
                ApiClientManager.getApiClientManager().create(ApiInterface.class);

        //create call object
        Call<ApiIncConfigResponse> call = apiService.getIncConfig();

        Helpers.showCircleProgressDialog(this);
        // send call object
        call.enqueue(new Callback<ApiIncConfigResponse>() {
            @Override
            public void onResponse(Call<ApiIncConfigResponse> call, Response<ApiIncConfigResponse> response) {
                handleGetIncConfigResponse(response, null);
            }

            @Override
            public void onFailure(Call<ApiIncConfigResponse> call, Throwable t) {
                handleGetIncConfigResponse(null, t);
            }
        });
    }

    private void handleGetIncConfigResponse(Response<ApiIncConfigResponse> response, Throwable t) {
        Helpers.dismissCircleProgressDialog();

        if (response != null && response.body() != null) {
            RkLogger.d(TAG, "getIncConfig response=" + response.body());

            SettingManager.getInstance(this).saveConfig(response.body());
            if (appHasNewVersion(response.body())) {
                showUpdateAppDialog();
            } else {
                //execute normal flow
                checkIdAndPasswordExist();
            }

        } else {
            RkLogger.w(TAG, "getIncConfig failed: ", t);
            //execute normal flow
            checkIdAndPasswordExist();
        }
    }

    private void showUpdateAppDialog() {
        boolean dialogShown = mUpdateDialog != null && mUpdateDialog.getDialog() != null &&
                mUpdateDialog.getDialog().isShowing();

        if (!dialogShown) {
            mUpdateDialog = DialogBuilder.buildNoticeDialog(
                    getString(R.string.str_app_has_newer_version), getString(R.string.notice_dialog_default_ok_jp), new DialogBuilder.OnClickListener() {
                        @Override
                        public void onOkClick(Object object) {
                            mUpdateDialog.dismiss();
                            final String appPackageName = BuildConfig.APPLICATION_ID;
                            RkLogger.d(TAG, "alert update: " + appPackageName);
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CONST_MARKET_URL1 + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                RkLogger.w(TAG, "Warning: ", anfe);
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CONST_MARKET_URL2 + appPackageName)));
                            }
                            // finish this app
                            SplashScreen.this.finish();
                        }

                        @Override
                        public void onCancelClick() {
                            RkLogger.d(TAG, "user cancels update");

                            mUpdateDialog.dismiss();
                            //execute normal flow
                            checkIdAndPasswordExist();
                        }
                    });

            Helpers.showDialogFragment(getSupportFragmentManager(), mUpdateDialog);
        }

    }

    private boolean appHasNewVersion(ApiIncConfigResponse response) {

        try {
            String responseVersion = response.getVersion();

            String appVersion = BuildConfig.VERSION_NAME;

            if (HimecasUtils.compareVersionNames(appVersion, responseVersion) < 0) {
                RkLogger.d(TAG, "app has newer version");
                return true;
            }
        } catch (Exception ex) {
            RkLogger.w(TAG, "Warning: ", ex);
        }

        RkLogger.d(TAG, "app doesn't have newer version");
        return false;
    }

    private void gotoStartPageScreen() {
        Intent intent = new Intent(SplashScreen.this, StartPageActivity.class);
        startActivity(intent);
        //Remove activity
        finish();
    }

    private void checkIdAndPasswordExist() {
        if (SettingManager.getInstance(this).hasMember()) {
            final MemberInformation member = SettingManager.getInstance(this).getMemberInformation();
            String id = member.getId();
            String password = member.getPass();

            RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
            postMemberInfoConfirmApi(id, password);
        } else {
            RkLogger.w(TAG, "don't have stored user");
            gotoStartPageScreen();
        }
    }

    private void postMemberInfoConfirmApi(final String id, final String password) {
        try {
            Helpers.showCircleProgressDialog(this);

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            //create call object
            Call<ApiMemberResponse> call = apiService.getMemberInfo(id, password);

            // send call object
            call.enqueue(new Callback<ApiMemberResponse>() {
                @Override
                public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                    if (response == null || response.body() == null)
                        return;
                    handleGetMemberInfoSuccess(response.body(), id, password);
                }

                @Override
                public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                    // Log error here since request failed
                    RkLogger.e(TAG, "getMemberInfo failed: ", t);
                    if (isAlive) {
                        showRetryConnectionDialog();
                    }
                }
            });
        } catch (Exception ex) {
            RkLogger.e(TAG, "Unhandled Exception:", ex);
            showRetryConnectionDialog();
        }
    }

    private void handleGetMemberInfoSuccess(final ApiMemberResponse result, String id, String password) {
        RkLogger.d(TAG, "call MemberInfoApi return: " + (result != null ? result.isSuccess() : null));
        Helpers.dismissCircleProgressDialog();
        if (result.isSuccess() &&
                result.getMember() != null) {
            //save this member
            doSaveMemberInfo(result.getMember(), id, password, result.getMember().ismIs3Day());
            gotoMainScreen();
        } else {
            gotoStartPageScreen();
        }
    }

}
