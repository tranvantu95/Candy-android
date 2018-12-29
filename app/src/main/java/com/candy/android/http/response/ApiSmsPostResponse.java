package com.candy.android.http.response;

import android.text.TextUtils;

import com.candy.android.model.BaseMember;
import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 10/11/2016.
 * Des: sms response
 */

public class ApiSmsPostResponse extends BaseApiResponse {

    @SerializedName("member")
    private BaseMember member;
    @SerializedName("sms")
    private Sms sms;

    public class Sms {
        @SerializedName("pointup")
        private String pointup;
        @SerializedName("errorMessage")
        private String errorMessage;
        @SerializedName("submit")
        private String submit;

        public int getPointup() {
            if (!TextUtils.isEmpty(pointup) && TextUtils.isDigitsOnly(pointup)) {
                return Integer.valueOf(pointup);
            }
            return -1;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public int getSubmit() {
            if (!TextUtils.isEmpty(submit) && TextUtils.isDigitsOnly(submit)) {
                return Integer.valueOf(submit);
            }
            return -1;
        }
    }

    public BaseMember getMember() {
        return member;
    }

    public Sms getSms() {
        return sms;
    }
}
