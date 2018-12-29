package com.candy.android.model;

import com.candy.android.configs.Define;
import com.candy.android.utils.RkLogger;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Favo
 * Created on 21/10/2016.
 */

public class MailList {

    @SerializedName(Define.Fields.FIELD_LIST_EMAIL)
    private List<MessageDetail> mMailDetailList;

    @SerializedName(Define.Fields.FIELD_ROW_COUNT)
    private String mRowCount;

    /**
     * Contents consumed at the time of transmission
     */
    @SerializedName(Define.Fields.FIELD_PAYMENT)
    private String mPayment;

    @SerializedName(Define.Fields.FIELD_ERROR_MESSAGE)
    private String mErrorMessage;

    public List<MessageDetail> getMailDetailList() {
        return mMailDetailList;
    }

    public int getRowCount() {
        try {
            return Integer.parseInt(mRowCount);
        } catch (NumberFormatException nfe) {
            RkLogger.e(Define.TAG_EXCEPTION, "Unable to parse to integer, mRowCount=" + mRowCount);
        }

        return 0;
    }

    public String getPayment() {
        return mPayment;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
