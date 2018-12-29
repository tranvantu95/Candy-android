package com.candy.android.http.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hungnq on 1/3/18.
 */

public class AddPointLimited {

    @SerializedName("isSuccess")
    @Expose
    private boolean isSuccess;

    public boolean getIsSuccess() {
        return isSuccess;
    }

}
