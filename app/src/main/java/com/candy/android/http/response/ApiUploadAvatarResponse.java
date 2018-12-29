package com.candy.android.http.response;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Response object when upload avatar
 * Created by namhv on 10/12/2016.
 */
public class ApiUploadAvatarResponse {
    @SerializedName(Define.Fields.FIELD_SUBMIT)
    protected int mSubmit;


    @SerializedName(Define.Fields.FIELD_ERROR_MESSEGE)
    protected String mErrorMessage;

    public boolean isSuccess() {
        return Define.REQUEST_OK == mSubmit;
    }

    public String getErrorMessage() {
        try {
            return URLDecoder.decode(mErrorMessage, Define.CONST_UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
