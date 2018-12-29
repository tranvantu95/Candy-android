package jp.fmaru.app.livechatapp.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 指定されたURLにリクエストし、レスポンスのjsonを返す
 * 例外が発生した場合nullを返す
 */
public class JsonLoader extends AsyncTaskLoader<JSONObject> {
    private static final String TAG = JsonLoader.class.getSimpleName();
    private static final int TIME_OUT_MILLISECOND = 5000;
    private String mUrl;

    public JsonLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public JSONObject loadInBackground() {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIME_OUT_MILLISECOND);
            connection.setReadTimeout(TIME_OUT_MILLISECOND);
            connection.connect();

            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                if (length > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return new JSONObject(new String(outputStream.toByteArray()));
        } catch (IOException | JSONException e) {
            Log.e(TAG, "loadInBackground:"+e);
        }
        return null;
    }
}
