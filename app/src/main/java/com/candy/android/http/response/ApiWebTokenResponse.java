package com.candy.android.http.response;

import com.google.gson.annotations.SerializedName;

/**
 * As response of get web token
 * Created by namhv on 03/12/2016.
 */
public class ApiWebTokenResponse {
    @SerializedName("token")
    private String mToken;
    @SerializedName("secret")
    private String mSecretKey;
    @SerializedName("errorMessage")
    private String mErrorMessage;

    public String getToken() {
        return mToken;
    }

    public String getSecretKey() {
        return mSecretKey;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
