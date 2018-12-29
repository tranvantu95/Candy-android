package com.candy.android.model;

import android.text.TextUtils;

import com.candy.android.configs.Define;
import com.candy.android.utils.RkLogger;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by quannt on 10/19/2016.
 * Description: property of performer which user liked
 */

public class PerformerFavorite {
    @SerializedName("code")
    private String mCode;
    @SerializedName("orignalName")
    private String mOrignalName;
    @SerializedName("image")
    private String mImage;
    @SerializedName("profileImageUrl")
    private String mProfileImageUrl;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("statusString")
    private String mStatusString;
    @SerializedName("age")
    private String mAge;
    @SerializedName("pos")
    private String mPos;
    @SerializedName("area")
    private String mArea;
    @SerializedName("smartPhone")
    private String mSmartPhone;
    @SerializedName("name")
    private String mName;

    public PerformerFavorite() {
        //Empty constructor
    }

    public PerformerFavorite(PerformerOnline performerOnline) {
        mCode = String.valueOf(performerOnline.getCode());
        mOrignalName = performerOnline.getOrignalName();
        mImage = performerOnline.getImage();
        mProfileImageUrl = performerOnline.getProfileImageUrl();
        mStatus = String.valueOf(performerOnline.getStatus());
        mStatusString = performerOnline.getStatusString();
        mAge = String.valueOf(performerOnline.getAge());
        mPos = String.valueOf(performerOnline.getPos());
        mArea = performerOnline.getArea();
        mSmartPhone = String.valueOf(performerOnline.getSmartPhone());
        mName = performerOnline.getName();
    }

    public int getCode() {
        if (!TextUtils.isEmpty(mCode) && TextUtils.isDigitsOnly(mCode)) {
            return Integer.valueOf(mCode);
        }
        return 0;
    }

    public String getOrignalName() {
        // modified by knv
        try {
            return URLDecoder.decode(mOrignalName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mOrignalName;
    }

    public String getImage() {
        return mImage;
    }

    public int getStatus() {
        try {
            if (getSmartPhone() == Define.SMARTPHONE_ON) {
                return 5;
            } else {
                return Integer.valueOf(mStatus);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getStatusString() {
        if (getSmartPhone() == Define.SMARTPHONE_ON) {
            return Define.STATUS_STRING_OFFLINE;
        }
        return mStatusString;
    }

    /**
     * knv modified this
     */
    public int getAge() {
        try {
            return Integer.parseInt(mAge);
        } catch (Exception ex) {
            RkLogger.e("IDK", "Exception: ", ex);
        }

        return Define.REQUEST_FAILED;
    }

    public String getPos() {
        return mPos;
    }

    public String getArea() {
        return mArea;
    }

    public int getSmartPhone() {
        try {
            return Integer.valueOf(mSmartPhone);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getName() {
        try {
            String str = URLDecoder.decode(mName, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mName;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public String getmProfileImageUrl() {
        return mProfileImageUrl;
    }

    public boolean isLive() {
        if(mStatus == null || mStatus.isEmpty()){
            return false;
        }
        Define.OnlineStatus onlineStatus = Define.OnlineStatus.getStatusFromCode(getStatus());
        return onlineStatus == Define.OnlineStatus.CALL_WAITING || onlineStatus == Define.OnlineStatus.CALLING;
    }

}
