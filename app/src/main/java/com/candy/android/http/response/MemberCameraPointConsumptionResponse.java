package com.candy.android.http.response;

import com.google.gson.annotations.SerializedName;

public class MemberCameraPointConsumptionResponse {

    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("errorMessages")
    private String[] errorMessages;


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String[] getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String[] errorMessages) {
        this.errorMessages = errorMessages;
    }

}
