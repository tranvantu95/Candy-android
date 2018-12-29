package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoalt on 8/29/18.
 */

public class ApiSendMailPictureSubPointResponse {
    @SerializedName(Define.Fields.FIELD_ISSUCCES)
    private boolean isSuccess = true;

    @SerializedName(Define.Fields.FIELD_ERROR_MESSAGE)
    private String mErrorMessage;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }
}
