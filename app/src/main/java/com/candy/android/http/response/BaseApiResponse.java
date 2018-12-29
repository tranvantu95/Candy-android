package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

/**
 * Created by namhv on 10/19/16.
 * BaseApiResponse
 */

public class BaseApiResponse {
    @SerializedName(Define.Fields.FIELD_STATUS)
    protected int mStatus;


    @SerializedName(Define.Fields.FIELD_RESPONSE)
    protected String mResponse;


    public int getStatus() {
        return mStatus;
    }

    public String getResponse() {
        return mResponse;
    }

    public boolean isSuccess() {
        return Define.REQUEST_OK == mStatus;
    }
}
