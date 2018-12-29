package com.candy.android.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by quannt on 03/11/2016.
 * Des: performer blog property
 */

public class PerformerBlog {

    @SerializedName("status")
    private String status;
    @SerializedName("rows")
    private String row;
    @SerializedName("data")
    private ArrayList<BlogData> data;

    public int getStatus() {
        if (!TextUtils.isEmpty(status) && TextUtils.isDigitsOnly(status)) {
            return Integer.valueOf(status);
        }
        return 0;
    }

    public ArrayList<BlogData> getData() {
        return data;
    }

    public int getRow() {
        if (!TextUtils.isEmpty(row) && TextUtils.isDigitsOnly(row)) {
            return Integer.valueOf(row);
        }
        return 0;
    }
}
