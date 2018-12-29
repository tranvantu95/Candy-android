package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * General response which contains success/fail information
 *
 * @author Favo
 * Created on 19/10/2016.
 */
public class ApiGeneralResponse extends BaseApiResponse {

    @SerializedName(Define.Fields.FIELD_SUBMIT)
    private int mSubmit;

    @SerializedName(Define.Fields.FIELD_ERROR_MESSAGE)
    private String mErrorMsg;

    public int getSubmit() {
        return mSubmit;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
