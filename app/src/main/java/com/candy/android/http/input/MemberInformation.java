package com.candy.android.http.input;

import com.candy.android.configs.Define;
import com.candy.android.model.MemberNoticeSetting;
import com.candy.android.model.PerformerFavorite;
import com.candy.android.utils.RandomStringUtils;
import com.candy.android.utils.RkLogger;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * An instance of this class acts as input object for an api
 *
 * @author Favo
 * Created on 19/10/2016.
 */
public class MemberInformation {
    private static final String TAG = "IDK-MemberInfoConfirm";
    //input
    @SerializedName(Define.Fields.FIELD_EMAIL)
    private String mEmail;

    @SerializedName(Define.Fields.FIELD_PASS)
    private String mPass;

    @SerializedName(Define.Fields.FIELD_NAME)
    private String mName;

    @SerializedName(Define.Fields.FIELD_ID)
    private String mId;

    @SerializedName(Define.Fields.FIELD_BIRTH_YEAR)
    private int mBirthYear;

    @SerializedName(Define.Fields.FIELD_BIRTH_MONTH)
    private int mBirthMonth;

    @SerializedName(Define.Fields.FIELD_BIRTH_DAY)
    private int mBirthDay;

    @SerializedName(Define.Fields.FIELD_BANNER_CODE)
    private String mBannerCode;

    @SerializedName(Define.Fields.FIELD_OPTION15)
    private String mOption15;

    @SerializedName(Define.Fields.FIELD_OPTION28)
    private String mOption28;

    @SerializedName(Define.Fields.FIELD_CODE)
    private int mCode;

    @SerializedName(Define.Fields.FIELD_POINT)
    private String mPoint;

    @SerializedName(Define.Fields.FIELD_NOT_OPEN_COUNT)
    private String mNotOpenCount;

    @SerializedName(Define.Fields.FIELD_JOIN_DATE)
    private String mJoinDate;

    @SerializedName(Define.Fields.FIELD_BIRTH_DATE)
    private String mBirthdate;

    @SerializedName(Define.Fields.FIELD_FAVORITE)
    private List<PerformerFavorite> mFavoriteList;

    @SerializedName(Define.Fields.FIELD_BUY_TIMES)
    private int mBuyTime;

    @SerializedName(Define.Fields.FIELD_IS_3DAY)
    private boolean mIs3Day;

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
    private boolean mTeleauth;

    @SerializedName(Define.Fields.FIELD_TELEAUTH_OK)
    private int mTeleauthOk;

    @SerializedName(Define.Fields.FIELD_MESSAGE)
    private String mMessage;

    @SerializedName(Define.Fields.FIELD_UN_FAVORITE_CODES)
    private List<String> mUnfavoriteCodes;

    @SerializedName(Define.Fields.FIELD_UN_FAVORITE_LIST)
    private List<PerformerFavorite> mUnfavoriteList;

    @SerializedName(Define.Fields.FIELD_NOTIFICATION)
    private MemberNoticeSetting mMemberNoticeSetting;
    @SerializedName("profileImageUrl")
    private String profileImageUrl;

    @SerializedName("mailAddress")
    @Expose
    private String mailAddress;

    public MemberInformation(String id, String pass, String email, String name) {
        this.mId = id;
        this.mEmail = email;
        this.mName = name;
        this.mPass = pass;

        this.mBirthDay = Define.FIX_BIRTHDAY;
        this.mBirthMonth = Define.FIX_BIRTH_MONTH;
        this.mBirthYear = Define.FIX_BIRTH_YEAR;

        this.mOption15 = Define.CONST_SP;
        this.mOption28 = Define.CONST_CANDY_ANDROID;
    }

    public void setPass(String pass) {
        mPass = pass;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public void setPoint(int point) {
        mPoint = String.valueOf(point);
    }

    public void setNotOpenCount(int notOpenCount) {
        mNotOpenCount = String.valueOf(notOpenCount);
    }

    public void setAge(int age) {
        mAge = age;
    }

    public void setBlood(String mBlood) {
        this.mBlood = mBlood;
    }

    public void setmBirthAge(int mBirthAge) {
        this.mBirthAge = mBirthAge;
    }

    public void setArea(int area) {
        mArea = area;
    }

    public void setMessage(String message) {
        if (message.equals("　")) {
            mMessage = "";
        } else {
            mMessage = message;
        }
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmBlood() {
        return mBlood;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getEmail() {
        return mEmail;
    }

    public int getPoint() {
        try {
            return Integer.parseInt(mPoint);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public int getNotOpenCount() {
        try {
            return Integer.parseInt(mNotOpenCount);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public int getAge() {
        return mAge;
    }

    public int getArea() {
        return mArea;
    }

    public String getMessage() {
        return mMessage;
    }

    public List<PerformerFavorite> getFavoriteList() {
        return mFavoriteList;
    }

    public List<PerformerFavorite> getUnfavoriteList() {
        return mUnfavoriteList;
    }

    public String getPass() {
        return mPass;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public int getBirthYear() {
        return mBirthYear;
    }

    public int getBirthMonth() {
        return mBirthMonth;
    }

    public int getBirthDay() {
        return mBirthDay;
    }

    public String getBannerCode() {
        return mBannerCode;
    }

    public String getOption15() {
        return mOption15;
    }

    public String getOption28() {
        return mOption28;
    }

    public int getCode() {
        return mCode;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean getmTeleauth() {
        return mTeleauth;
    }

    public int getmTeleauthOk() {
        return mTeleauthOk;
    }

    public MemberNoticeSetting getMemberNoticeSetting() {
        return mMemberNoticeSetting;
    }

    public int getBuyTimes() {
        return mBuyTime;
    }

    public static MemberInformation generateNewMemberInfo() {
        String id = RandomStringUtils.randomAlphanumeric(Define.RAND_ID_COUNT);
        String pass = RandomStringUtils.randomAlphanumeric(Define.RAND_PASS_COUNT);
        String email = RandomStringUtils.randomAlphanumeric(Define.RAND_EMAIL_COUNT) + Define.RAND_EMAIL_POSIX;
        String name = Define.RAND_NAME_PREFIX + RandomStringUtils.randomAlphanumeric(Define.RAND_NAME_COUNT);
        MemberInformation mic = new MemberInformation(id, pass, email, name);
        RkLogger.d(TAG, "generate MemberInfoConfirm: " + mic);

        return mic;
    }

    public String getmJoinDate() {
        return mJoinDate;
    }

    public void setmJoinDate(String mJoinDate) {
        this.mJoinDate = mJoinDate;
    }

    public int getmBuyTime() {
        return mBuyTime;
    }

    public void setmBuyTime(int mBuyTime) {
        this.mBuyTime = mBuyTime;
    }

    public boolean ismIs3Day() {
        return mIs3Day;
    }

    public void setmIs3Day(boolean mIs3Day) {
        this.mIs3Day = mIs3Day;
    }
}
