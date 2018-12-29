package com.candy.android.http.firebase;

import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiMemberWithNoticeResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.utils.RkLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Favo
 * Created on 10/11/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "IDK-FirebaseIID";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        RkLogger.d(TAG, "::onTokenRefresh token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        RkLogger.d(TAG, "::sendRegistrationToServer save to prefs: " + token);
        SettingManager.getInstance(this).saveFireBaseIIDToken(token);

        // send token to our app server, when user is already exists
        if (SettingManager.getInstance(this).hasMember()) {
            doRegisterPNSetting();
        }
    }

    /**
     * register PN setting with new token, for example
     */
    private void doRegisterPNSetting() {

        if (SettingManager.getInstance(this).hasMember()) {
            final MemberInformation member = SettingManager.getInstance(this).getMemberInformation();

            ApiInterface apiService =
                    ApiClientManager.getApiClientManager().create(ApiInterface.class);

            RkLogger.d(TAG, "registering PN setting");
            //create api object
            Call<ApiMemberWithNoticeResponse> call = apiService.registerPNSetting(member.getId(),
                    member.getPass(),
                    SettingManager.getInstance(this).getFireBaseIIDToken(),
                    0);

            RkLogger.d(TAG, "call object: " + call.request().body());

            //send api object
            call.enqueue(new Callback<ApiMemberWithNoticeResponse>() {

                @Override
                public void onResponse(Call<ApiMemberWithNoticeResponse> call, Response<ApiMemberWithNoticeResponse> response) {
                    handleRegisterPNSettingSuccess(response);
                }

                @Override
                public void onFailure(Call<ApiMemberWithNoticeResponse> call, Throwable t) {
                    RkLogger.e(TAG, "registerPNSetting failed:", t);
                }
            });
        }
    }

    /**
     * This method is called when registering PN token finished (no matter it's success or not)
     * We confirm that this method is called only when user has correct id and password, and
     * should register new token for PN setting
     */
    private void handleRegisterPNSettingSuccess(Response<ApiMemberWithNoticeResponse> response) {
        if (response != null) {
            RkLogger.d(TAG, "registerPNSetting response=" + response.body());
        }
    }

}
