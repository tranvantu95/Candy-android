package com.candy.android.model;

import com.candy.android.configs.Define;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;
import com.google.gson.annotations.SerializedName;

/**
 * @author Favo
 * Created on 17/10/2016.
 */

public class MessageDetail extends IMessage {
    private static final String TAG = "IDK-Message";
    @SerializedName(Define.Fields.FIELD_MAIL_CODE)
    private String mMailCode;

    /**
     * Urlencode
     */
    @SerializedName(Define.Fields.PERFORMER_NAME)
    private String mPerformerName;

    /**
     * urlencode
     */
    @SerializedName(Define.Fields.FIELD_PERFORMER_ORIGINAL_NAME)
    private String mPerformerOrignalName;

    @SerializedName(Define.Fields.FIELD_PERFORMER_IMAGE)
    private String mPerformerImage;

    @SerializedName(Define.Fields.FIELD_PERFORMER_AGE)
    private String mPerformerAge;

    /**
     * integer
     */
    @SerializedName(Define.Fields.FIELD_PERFORMER_STATUS)
    private String mPerformerStatus;

    @SerializedName(Define.Fields.FIELD_PERFORMER_STATUS_STRING)
    private String mPerformerStatusString;

    @SerializedName(Define.Fields.FIELD_PERFORMER_SMARTPHONE)
    private String mPerformerSmartPhone;

    /**
     * integer
     */
    @SerializedName(Define.Fields.FIELD_PERFORMER_POS)
    private String mPerformerPos;

    /**
     * integer
     */
    @SerializedName(Define.Fields.FIELD_MEMBER_CODE)
    private String mMemberCode;

    /**
     * urlencode, 0 = Normal 1 = rookie
     */
    @SerializedName(Define.Fields.FIELD_MEMBER_NAME)
    private String mMemberName;

    /**
     * urlencode
     */
    @SerializedName(Define.Fields.FIELD_SUBJECT)
    private String mSubject;

    /**
     * integer, 0 = normal, 1 = replied
     */
    @SerializedName(Define.Fields.FIELD_RETURN)
    private String mReturn;

    /**
     * integer
     */
    @SerializedName(Define.Fields.FIELD_PRESENT_POINT)
    private String mPresentPoint;

//    @SerializedName(Define.Fields.FIELD_ATTACH_IMAGE)
//    private AttachImage mAttachImage;

    @SerializedName(Define.Fields.FIELD_ATTACH_IMAGE_URL)
    private AttachImage mAttachImageUrl;

    @SerializedName(Define.Fields.FIELD_PERFORMER_PUBLIC)
    private String mPerformerPublic;

    private boolean sentError;

    public int getMailCode() {
        try {
            return Integer.parseInt(mMailCode);
        } catch (NumberFormatException nfe) {
            RkLogger.w(TAG, "Warning parsing: ", nfe);
        }

        return Define.REQUEST_FAILED;
    }

    public AttachImage getAttachImageUrl() {
        return mAttachImageUrl;
    }

    public String getPerformerName() {
        return HimecasUtils.decodeUrlEncodedString(mPerformerName, false);
    }

    public String getPerformerOrignalName() {
        return HimecasUtils.decodeUrlEncodedString(mPerformerOrignalName, false);
    }

    public String getPerformerImage() {
        return mPerformerImage;
    }

    public int getPerformerStatus() {
        try {
            if (getmPerformerSmartPhone() == Define.SMARTPHONE_ON) {
                return 5;
            }
            return Integer.valueOf(mPerformerStatus);
        } catch (Exception ex) {
            return -1;
        }
    }

    public int getPerformerAge() {
        try {
            return Integer.parseInt(mPerformerAge);
        } catch (NumberFormatException nfe) {
            RkLogger.w(TAG, "Exception: ", nfe);
        }

        return Define.REQUEST_FAILED;
    }

    public String getPerformerStatusString() {
        if (getmPerformerSmartPhone() == Define.SMARTPHONE_ON) {
            return Define.STATUS_STRING_OFFLINE;
        }
        return mPerformerStatusString;
    }

    public String getPerformerPos() {
        return mPerformerPos;
    }

    public String getMemberCode() {
        return mMemberCode;
    }

    public String getMemberName() {
        return HimecasUtils.decodeUrlEncodedString(mMemberName, false);
    }

    public String getSubject() {
        return HimecasUtils.decodeUrlEncodedString(mSubject, false);
    }

    public String getReturn() {
        return mReturn;
    }

    public String getPresentPoint() {
        return mPresentPoint;
    }

    /**
     * @return 0=private 1=public
     */
    public boolean isPerformerPublic() {
        return "1".contentEquals(mPerformerPublic);
    }

    public boolean isSentError() {
        return sentError;
    }

    public void setAttachImageUrl(AttachImage attachImageUrl) {
        mAttachImageUrl = attachImageUrl;
    }

    public void setSentError(boolean sentError) {
        this.sentError = sentError;
    }

    public int getmPerformerSmartPhone() {
        try {
            return Integer.parseInt(mPerformerSmartPhone);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isLive() {
        if(mPerformerStatus == null || mPerformerStatus.isEmpty()){
            return false;
        }
        Define.OnlineStatus onlineStatus = Define.OnlineStatus.getStatusFromCode(getPerformerStatus());
        return onlineStatus == Define.OnlineStatus.CALL_WAITING || onlineStatus == Define.OnlineStatus.CALLING;
    }
}
