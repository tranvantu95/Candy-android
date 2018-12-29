package jp.fmaru.app.livechatapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import org.json.JSONObject;

import jp.fmaru.app.livechatapp.util.JsonLoader;

/**
 * JsonLoaderを処理するクラス
 * jetty接続を行う
 */
public class JsonLoaderManager implements LoaderManager.LoaderCallbacks<JSONObject> {
    private ChatActivity mActivity;
    private String mUrl;

    public JsonLoaderManager(ChatActivity activity) {
        mActivity = activity;
    }

    public void runDataLoader(String url) {
        mUrl = url;
        mActivity.getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        JsonLoader jsonLoader = new JsonLoader(mActivity.getApplicationContext(), mUrl);
        jsonLoader.forceLoad();
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject json) {
        if (json == null) {
            mActivity.showToast(mActivity.getString(R.string.toast_error_chat_config));
            mActivity.logout();
            return;
        }

        /** jetty接続 */
        mActivity.connectJetty(json);
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {

    }
}
