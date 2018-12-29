package com.candy.android.model.member;

import com.candy.android.configs.Define;
import com.candy.android.model.PerformerFavorite;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Favo
 * Created on 18/10/2016.
 */

public class MemberInfoAPIMemberField extends MemberRegistAPIMemberField {
    @SerializedName(Define.Fields.FIELD_BUY_TIMES)
    private int mBuyTime;

    @SerializedName(Define.Fields.FIELD_FREE_MAIL)
    private String mFreeMail;

    @SerializedName(Define.Fields.FIELD_NOT_OPEN_SUPPORT_COUNT)
    private int mNotOpenSupportCount;

    @SerializedName(Define.Fields.FIELD_BIRTH_AGE)
    private int mBirthAge;

    @SerializedName(Define.Fields.FIELD_AGE)
    private int mAge;

    @SerializedName(Define.Fields.FIELD_BLOOD)
    private String mBlood;

    @SerializedName(Define.Fields.FIELD_AREA)
    private int mArea;

    @SerializedName(Define.Fields.FIELD_TELEAUTH)
    private int mTeleauth;

    @SerializedName(Define.Fields.FIELD_TELEAUTH_OK)
    private int mTeleauthOk;

    @SerializedName(Define.Fields.FIELD_MESSAGE)
    private String mMessage;

    @SerializedName(Define.Fields.FIELD_UN_FAVORITE_CODES)
    private List<String> mUnfavoriteCodes;

    @SerializedName(Define.Fields.FIELD_UN_FAVORITE_LIST)
    private List<PerformerFavorite> mUnfavoriteList;

}
