package com.candy.android.http.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hungnq on 1/3/18.
 */

public class ApiPointAdditionResponse extends BaseApiResponse {
    @SerializedName("isSuccess")
    @Expose
    private boolean isSuccess;
    @SerializedName("addPoint")
    @Expose
    private AddPointLimited addPoint;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public AddPointLimited getAddPoint() {
        return addPoint;
    }
}
