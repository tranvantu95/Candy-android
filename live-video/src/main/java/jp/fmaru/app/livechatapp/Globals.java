package jp.fmaru.app.livechatapp;

import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import jp.fmaru.app.livechatapp.util.RkSharedPreferencesUtils;

/**
 * 共有変数オブジェクト
 */
public class Globals extends Application {
    private static final String TAG = Globals.class.getSimpleName();

    private String mMediaServerUrl;
    private String mJettyUrl;
    private String mAppKey;
    private String mOwnerName;

    @Override
    public void onCreate() {
        super.onCreate();
        RkSharedPreferencesUtils.initialize(this, MODE_PRIVATE);
    }

    public String getMediaServerUrl() {
        return mMediaServerUrl;
    }

    public String getJettyUrl() {
        return mJettyUrl;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public String getOwnerName() {
        return mOwnerName;
    }

    public void setMediaServerUrl(String mediaServerUrl) {
        mMediaServerUrl = mediaServerUrl;
    }

    public void setJettyUrl(String jettyUrl) {
        mJettyUrl = jettyUrl;
    }

    public void setAppKey(String appKey) {
        mAppKey = appKey;
    }

    public void setOwnerName(String ownerName) {
        mOwnerName = ownerName;
    }

    public void clearAll() {
        mMediaServerUrl = null;
        mJettyUrl = null;
        mAppKey = null;
        mOwnerName = null;
    }
}
