package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author Favo
 * Created on 15/11/2016.
 */
public class ApiIncConfigResponse {

    @SerializedName(Define.Fields.FIELD_VERSION)
    private String mVersion;

    @SerializedName(Define.Fields.FIELD_WEB_VIEW_BASE_URL)
    private String mWebviewBaseUrl;

    @SerializedName(Define.Fields.FIELD_WEB_SOCKET_URL)
    private String mWebSocketUrl;

    @SerializedName(Define.Fields.FIELD_OWNER_NAME)
    private String mOwnerName;

    @SerializedName(Define.Fields.FIELD_IMAGE_URL_DOMAIN)
    private String profileImageUrlDomain;

    @SerializedName(Define.Fields.FIELD_APP_CODE)
    private String appCode;

    public String getVersion() {
        return mVersion;
    }

    public String getWebviewBaseUrl() {
        return mWebviewBaseUrl;
    }

    public String getWebSocketUrl() {
        return mWebSocketUrl;
    }

    public String getOwnerName() {
        return mOwnerName;
    }

    public String getProfileImageUrlDomain() {
        return profileImageUrlDomain;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
