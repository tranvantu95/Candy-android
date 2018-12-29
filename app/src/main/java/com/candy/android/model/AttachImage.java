package com.candy.android.model;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

/**
 * @author Favo
 * Created on 14/11/2016.
 */

public class AttachImage {

    @SerializedName(Define.Fields.FIELD_BIG)
    private String mBig;

    @SerializedName(Define.Fields.FIELD_SMALL)
    private String mSmall;

    private boolean fromCached;

    public String getBig() {
        return mBig;
    }

    public String getSmall() {
        return mSmall;
    }

    public void setBig(String big) {
        mBig = big;
    }

    public void setSmall(String small) {
        mSmall = small;
    }

    public boolean isFromCached() {
        return fromCached;
    }

    public void setFromCached() {
        this.fromCached = true;
    }
}
