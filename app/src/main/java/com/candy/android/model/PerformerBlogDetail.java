package com.candy.android.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 06/11/2016.
 * Des:
 */

public class PerformerBlogDetail {
    @SerializedName("status")
    private String status;
    @SerializedName("rows")
    private String row;
    @SerializedName("data")
    private BlogData data;

    public int getStatus() {
        if (!TextUtils.isEmpty(status) && TextUtils.isDigitsOnly(status)) {
            return Integer.valueOf(status);
        }
        return 0;
    }

    public int getRow() {
        if (!TextUtils.isEmpty(row) && TextUtils.isDigitsOnly(row)) {
            return Integer.valueOf(row);
        }
        return 0;
    }

    public BlogData getData() {
        return data;
    }
}
