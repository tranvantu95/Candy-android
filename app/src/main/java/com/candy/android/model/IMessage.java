package com.candy.android.model;

import com.candy.android.configs.Define;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;
import com.google.gson.annotations.SerializedName;

/**
 * @author Favo
 * Created on 09/11/2016.
 */
public abstract class IMessage {
    private static final String TAG = "IDK-IMessage";

    /**
     * integer
     */
    @SerializedName(Define.Fields.FIELD_PERFORMER_CODE)
    private String mPerformerCode;

    @SerializedName(Define.Fields.FIELD_PERFORMER_IMAGE_URL)
    private String mPerformerImageUrl;

    /**
     * urlencode
     */
    @SerializedName(Define.Fields.FIELD_BODY)
    private String mBody;

    /**
     * integer, 0 = unread, 1 = read
     */
    @SerializedName(Define.Fields.FIELD_OPEN)
    private String mOpen;

    @SerializedName(Define.Fields.FIELD_SEND_DATE)
    private String mSendDate;

    /**
     * 0 = got mail 1 = e-mail that I sent
     */
    @SerializedName(Define.Fields.FIELD_SEND_MAIL)
    private String mSendMail;

    public int getPerformerCode() {
        try {
            return Integer.parseInt(mPerformerCode);
        } catch (NumberFormatException nfe) {
            RkLogger.e(TAG, "Warning: ", nfe);
        }

        return Define.REQUEST_FAILED;
    }

    public String getPerformerImageUrl() {
        return mPerformerImageUrl;
    }

    public String getBody(boolean format) {
        return HimecasUtils.decodeUrlEncodedString(mBody, format);
    }

    public String getOpen() {
        return mOpen;
    }

    public String getSendDate() {
        return mSendDate;
    }

    public String getSendMail() {
        return mSendMail;
    }

    /**
     * 0 = unread, 1 = read
     *
     * @return true if this object is read
     */
    public boolean isRead() {
        return "1".equalsIgnoreCase(mOpen);
    }

    /**
     * 0 = got mail 1 = e-mail that I sent
     */
    public boolean isMemberMail() {
        return "1".equalsIgnoreCase(mSendMail);
    }

    public void setPerformerImageUrl(String performerImageUrl) {
        mPerformerImageUrl = performerImageUrl;
    }

    public void setBody(String body) {
        mBody = HimecasUtils.encodeUrlString(body);
    }

    /**
     * 0 = unread, 1 = read
     */
    public void setRead(boolean isRead) {
        mOpen = isRead ? "1" : "0";
    }

    public void setSendDate(String sendDate) {
        mSendDate = sendDate;
    }

    public void setPerformerCode(int performerCode) {
        mPerformerCode = String.valueOf(performerCode);
    }

    /**
     * 0 = got mail 1 = e-mail that I sent
     */
    public void setMemberMail(boolean isMember) {
        mSendMail = (isMember ? "1" : "0");
    }
}
