package com.candy.android.model.member;

import com.candy.android.configs.Define;
import com.candy.android.model.PerformerFavorite;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Favo
 * Created on 18/10/2016.
 */

public class MemberRegistAPIMemberField {
    @SerializedName(Define.Fields.FIELD_CODE)
    private int mCode;

    @SerializedName(Define.Fields.FIELD_NAME)
    private String mName;

    @SerializedName(Define.Fields.FIELD_EMAIL)
    private String mEmail;

    @SerializedName(Define.Fields.FIELD_POINT)
    private int mPoint;

    @SerializedName(Define.Fields.FIELD_NOT_OPEN_COUNT)
    private int mNotOpenCount;

    @SerializedName(Define.Fields.FIELD_JOIN_DATE)
    private String mJoinDate;

    @SerializedName(Define.Fields.FIELD_BIRTH_DATE)
    private String mBirthdate;

    @SerializedName(Define.Fields.FIELD_FAVORITE)
    private List<PerformerFavorite> mFavoriteList;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public List<PerformerFavorite> getFavoriteList() {
        return mFavoriteList;
    }
}
