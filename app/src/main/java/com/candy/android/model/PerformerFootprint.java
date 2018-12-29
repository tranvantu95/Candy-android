package com.candy.android.model;

import android.text.TextUtils;

import com.candy.android.configs.Define;
import com.candy.android.utils.HimecasUtils;
import com.candy.android.utils.RkLogger;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Hungnq on 12/27/2017.
 */

public class PerformerFootprint {
    @SerializedName("code")
    private String mCode;
    @SerializedName("name")
    private String mName;
    @SerializedName("age")
    private String mAge;
    @SerializedName("isPublicAge")
    private boolean isPublicAge;
    @SerializedName("areaCode")
    private String areaCode;
    @SerializedName("areaString")
    private String areaString;
    @SerializedName("statusCode")
    private String statusCode;
    @SerializedName("statusString")
    private String statusString;
    @SerializedName("profileImageUrl")
    private String mProfileImageUrl;
    @SerializedName("isFavorite")
    private boolean isFavorite;
    @SerializedName("footprintsTime")
    private String footprintsTime;

    public String getFootprintsTime() {
        return HimecasUtils.formatSentEmailDateAndTime(footprintsTime);
    }

    public boolean getPublicAge() {
        return isPublicAge;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public PerformerFootprint() {
        //Empty constructor
    }

    public PerformerFootprint(PerformerOnline performerOnline) {
        mCode = String.valueOf(performerOnline.getCode());
        mProfileImageUrl = performerOnline.getProfileImageUrl();
        statusCode = String.valueOf(performerOnline.getStatus());
        statusString = performerOnline.getStatusString();
        mAge = String.valueOf(performerOnline.getAge());
        areaCode = performerOnline.getArea();
        mName = performerOnline.getName();
        isPublicAge = performerOnline.isPublicAge();
    }

    public int getCode() {
        if (!TextUtils.isEmpty(mCode) && TextUtils.isDigitsOnly(mCode)) {
            return Integer.valueOf(mCode);
        }
        return 0;
    }

    public int getStatus() {
        return Integer.valueOf(statusCode);
    }

    public String getStatusString() {
        try {
            String str = URLDecoder.decode(statusString, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return statusString;
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

    public String getArea() {
        try {
            String str = URLDecoder.decode(areaCode, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return areaCode;
    }

    public String getAreaString() {
        try {
            String str = URLDecoder.decode(areaString, "UTF-8");
            if (str != null) {
                return str.replace(Define.ERROR_FORMAT_STRING, Define.REPLACE_STRING);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return areaString;
    }

    public void setAreaString(String areaString) {
        this.areaString = areaString;
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

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }
}
