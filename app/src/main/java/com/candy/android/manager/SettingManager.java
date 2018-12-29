package com.candy.android.manager;

import android.content.Context;
import android.text.TextUtils;

import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiIncConfigResponse;
import com.candy.android.model.member.MemberInfoChange;
import com.candy.android.utils.RkLogger;
import com.candy.android.utils.RkSharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * @author Favo
 * Created on 18/10/2016.
 */

public class SettingManager {
    public static final boolean ENABLED_OPTION_MENU = false;
    private static SettingManager instance;
    private final RkSharedPreferencesUtils mRspu;

    //add a dummy null member to prevent unexpected crashes
    private static MemberInformation mNullMember;
    private Gson mGson = new GsonBuilder().create();

    private static final String TAG = "IDK-SettingManager";
    private static final String KEY_STORED_WEB_TOKEN = "stored_web_token";
    private static final String KEY_STORED_CONFIG = "stored_config";
    private static final String KEY_STORED_MEMBER = "stored_member";
    private static final String KEY_IS_NAME_SET = "is_mail_set";
    private static final String KEY_FIID_TOKEN = "fire_base_token";
    private static final String KEY_GET_FREE_POINT = "get_free_point";
    private static final String KEY_TUTORIAL = "TUTORIAL";
    public static final String IS_FIRST_START = "is_first_start";

    private SettingManager(Context context) {
        RkSharedPreferencesUtils.initialize(context, Context.MODE_PRIVATE);
        mRspu = RkSharedPreferencesUtils.getInstance();
        mNullMember = new MemberInformation(null, null, null, null);
    }

    public static SettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingManager(context);
        }

        return instance;
    }

    public static SettingManager getInstance() {
        if (instance != null) {
            return instance;
        }

        return null;
    }

    public void save(final MemberInformation member) {
        RkLogger.e("ImageUrl: " + member.getProfileImageUrl());
        MemberInformation currentMemberInfo = getMemberInformation();
        if (TextUtils.isEmpty(member.getProfileImageUrl()) && !TextUtils.isEmpty(currentMemberInfo.getProfileImageUrl())) {
            member.setProfileImageUrl(currentMemberInfo.getProfileImageUrl());
        }
        if (null != currentMemberInfo && currentMemberInfo.getCode() > 0) {
            member.setCode(currentMemberInfo.getCode());
        }
        if(null != currentMemberInfo && currentMemberInfo.getAge() > 0) {
            member.setAge(currentMemberInfo.getAge());
        }
        if(null != currentMemberInfo && currentMemberInfo.getmBlood() != null) {
            member.setBlood(currentMemberInfo.getmBlood());
        }
        if(null != currentMemberInfo && currentMemberInfo.getMessage() != null) {
            member.setMessage(currentMemberInfo.getMessage());
        }
        RkLogger.e("Code1: " + member.getCode());
        // reload id and pass
        persistsMemberInfos(member);

        String jsonMember = mGson.toJson(member);
        mRspu.saveString(KEY_STORED_MEMBER, jsonMember);
    }

    /**
     * Usage: save user name, age, blood type and message when change
     */
    public void saveUserInfo(MemberInfoChange.Member member) {
        MemberInformation currentMemberInfo = getMemberInformation();
        currentMemberInfo.setName(member.getName());
        currentMemberInfo.setAge(Integer.valueOf(member.getAge()));
        currentMemberInfo.setBlood(member.getBlood());
        currentMemberInfo.setMessage(member.getMessage());
        String jsonMember = mGson.toJson(currentMemberInfo);
        mRspu.saveString(KEY_STORED_MEMBER, jsonMember);
    }

    /**
     * There are some information we should persist our self, since api is not persistent
     */
    private void persistsMemberInfos(final MemberInformation member) {
        if (hasMember()) {
            member.setId(getID());
            member.setPass(getPassword());
            if (TextUtils.isEmpty(member.getName())) {
                member.setName(getName());
            }
        }
    }

    public boolean hasMember() {
        try {
            String jsonMember = mRspu.getString(KEY_STORED_MEMBER, null);
            final MemberInformation memberInformation = mGson.fromJson(jsonMember, MemberInformation.class);
            if (!TextUtils.isEmpty(memberInformation.getId()) &&
                    !TextUtils.isEmpty(memberInformation.getPass())) {
                return true;
            }
        } catch (Exception ex) {
            RkLogger.w(TAG, "hasMember exception:" + ex);
        }

        return false;
    }

    /**
     * @return stored member's ID
     */
    public String getID() {
        try {
            final MemberInformation member = getMemberInformation();
            return member.getId();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * @return stored member's password
     */
    public String getPassword() {
        try {
            final MemberInformation member = getMemberInformation();
            return member.getPass();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * @return stored member's passwod
     */
    public String getName() {
        try {
            final MemberInformation member = getMemberInformation();
            return member.getName();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * @return the stored member's email
     */
    public String getEmail() {
        final MemberInformation member = getMemberInformation();
        if (null != member) {
            String mailAddress = member.getEmail();
            String emailAddress = member.getMailAddress();
            return !TextUtils.isEmpty(mailAddress) ? mailAddress : !TextUtils.isEmpty(emailAddress) ? emailAddress : "";
        } else {
            return "";
        }
    }

    /**
     * @return the stored member in preferences
     */
    public MemberInformation getMemberInformation() {
        try {
            String jsonMember = mRspu.getString(KEY_STORED_MEMBER, null);
            if (!TextUtils.isEmpty(jsonMember)) {
                return mGson.fromJson(jsonMember, MemberInformation.class);
            }
        } catch (JsonSyntaxException jse) {
            RkLogger.w(TAG, "getMemberInformation exception:" + jse);
        }

        return mNullMember;
    }
    public void setIsFirstStart(boolean isFirstStart) {
        mRspu.saveBoolean(IS_FIRST_START, isFirstStart);
    }

    public boolean getIsFirstStart() {
        return mRspu.getBoolean(IS_FIRST_START, true);
    }


    public void setIsFirstPerformerStart(boolean isFirstStart) {
        mRspu.saveBoolean(KEY_TUTORIAL, isFirstStart);
    }

    public boolean getIsFirstPerformerStart() {
        return mRspu.getBoolean(KEY_TUTORIAL, true);
    }
    /**
     * @param isSet indicates that the stored member's name is set
     */
    public void setIsNameSet(boolean isSet) {
        mRspu.saveBoolean(KEY_IS_NAME_SET, isSet);
    }

    /**
     * @return true if the stored member's name     is set
     */
    public boolean isNameSet() {
        return mRspu.getBoolean(KEY_IS_NAME_SET, false);
    }

    public boolean getKeyGetFreePoint() {
        return mRspu.getBoolean(KEY_GET_FREE_POINT, false);
    }

    public void setKeyGetFreePoint(boolean getFreePoint) {
        mRspu.saveBoolean(KEY_GET_FREE_POINT, getFreePoint);
    }

    /**
     * Clear all data in app sharedPreferences
     */
    public void clearStoredData() {
        mRspu.remove(KEY_GET_FREE_POINT);
        mRspu.remove(KEY_STORED_WEB_TOKEN);
        mRspu.remove(KEY_STORED_CONFIG);
        mRspu.remove(KEY_IS_NAME_SET);
        mRspu.remove(KEY_STORED_MEMBER);
    }

    public void saveFireBaseIIDToken(final String token) {
        mRspu.saveString(KEY_FIID_TOKEN, token);
    }

    public String getFireBaseIIDToken() {
        return mRspu.getString(KEY_FIID_TOKEN, null);
    }

    /**
     * Save setting from incConfig api response
     *
     * @param configResponse response from incConfig api
     */
    public void saveConfig(ApiIncConfigResponse configResponse) {
        String jsonConfig = mGson.toJson(configResponse);
        mRspu.saveString(KEY_STORED_CONFIG, jsonConfig);
    }

    public ApiIncConfigResponse getConfig() {
        try {
            String jsonConfig = mRspu.getString(KEY_STORED_CONFIG, null);
            if (!TextUtils.isEmpty(jsonConfig)) {
                return mGson.fromJson(jsonConfig, ApiIncConfigResponse.class);
            }
        } catch (JsonSyntaxException jse) {
            RkLogger.w(TAG, "getMemberInformation exception:" + jse);
        }

        return null;
    }

//    @Deprecated
//    public void saveWebToken(ApiWebTokenResponse tokenResponse) {
//        String jsonConfig = mGson.toJson(tokenResponse);
//        mRspu.saveString(KEY_STORED_WEB_TOKEN, jsonConfig);
//    }

//    @Deprecated
//    public ApiWebTokenResponse getWebToken() {
//        try {
//            String jsonWebToken = mRspu.getString(KEY_STORED_WEB_TOKEN, null);
//            if (!TextUtils.isEmpty(jsonWebToken)) {
//                return mGson.fromJson(jsonWebToken, ApiWebTokenResponse.class);
//            }
//        } catch (JsonSyntaxException jse) {
//            RkLog.w(TAG, "getMemberInformation exception:" + jse);
//        }
//
//        return null;
//    }

    public void removeWebToken() {
        mRspu.remove(KEY_STORED_WEB_TOKEN);
    }

}
