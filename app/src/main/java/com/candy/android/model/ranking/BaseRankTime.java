package com.candy.android.model.ranking;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.candy.android.utils.TextHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 8/30/2017.
 */

public class BaseRankTime {
    @SerializedName("rankingNumber")
    @Expose
    private String rankingNumber;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("orignalName")
    @Expose
    private String orignalName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("profileImageUrl")
    @Expose
    private String profileImageUrl;

    public String getRankingNumber() {
        return rankingNumber;
    }

    public void setRankingNumber(String lastRanking) {
        this.rankingNumber = rankingNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrignalName() {
        if (!TextUtils.isEmpty(orignalName)) {
            try {
                String stringBuilder = URLDecoder.decode(orignalName, "UTF-8");
                return TextHelper.fromHtml(stringBuilder).toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void setOrignalName(String orignalName) {
        this.orignalName = orignalName;
    }

    public String getPrimaryName() {
        return orignalName;
    }

    public void setPrimaryName(String orignalName) {
        this.orignalName = orignalName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArea() {
        if (!TextUtils.isEmpty(area)) {
            try {
                String stringBuilder = URLDecoder.decode(area, "UTF-8");
                return TextHelper.fromHtml(stringBuilder).toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "";

    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
