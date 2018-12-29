package jp.fmaru.app.livechatapp.util;

import android.net.Uri;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.fmaru.app.livechatapp.ChatActivity;
import jp.fmaru.app.livechatapp.model.ChatMessage;

/**
 * jettyとのやりとりで必要な処理のユーティリティクラス
 */
public class JettyUtils {
    private static final String TAG = JettyUtils.class.getSimpleName();

    public static final String DEFAULT_RECEIVE_TEXT_COLOR = "#00FFFFFF";
    public static final String DEFAULT_SEND_TEXT_COLOR = "#00000000";

    /**
     * jettyログイン用のjsonの文字列を返す
     * @param performerCode
     * @param id
     * @param pass
     * @param ownerName
     * @return
     */
    public static String createLoginJsonString(String performerCode, String id, int mode, String pass, String ownerName, String mediaServerUrl, String appCode) {
        JSONObject json = new JSONObject();
        try {
            Uri uri = Uri.parse(mediaServerUrl);

            json.put(Constants.RequestParams.PARAM_PERFORMER_CODE, performerCode);
            json.put(Constants.RequestParams.PARAM_PERFORMER_ID, id);
            json.put(Constants.RequestParams.PARAM_PERFORMER_PASS, pass);
            json.put(Constants.RequestParams.PARAM_PERFORMER_NAME, ownerName);
            json.put(Constants.RequestParams.PARAM_STATUS, mode);
            json.put(Constants.RequestParams.PARAM_STATUS, appCode);
            json.put("wcsip", uri.getAuthority());

            return json.toString();
        }
        catch (JSONException e) {
            RkLog.i(TAG, "createLoginJsonString:"+e);
            return null;
        }
    }

    /**
     * jettyログイン用のリクエストURLを返す
     * @param jettyUrl
     * @param loginJsonString
     * @return
     */
    public static String createJettyLoginRequest(String jettyUrl, String loginJsonString) {
        Uri uri = Uri.parse(jettyUrl);
        Uri.Builder builder=new Uri.Builder();
        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.path(uri.getEncodedPath());
        builder.appendQueryParameter("data", loginJsonString);

        return builder.build().toString();
    }

    /**
     * リクエストURLからOriginヘッダー用の文字列を返す
     */
    public static String createJettyOrigin(String request) {
        Uri uri = Uri.parse(request);
        return "http://"+uri.getAuthority();
    }

    /**
     * jsonから受信メッセージの形成
     * テキストカラーがnullまたは黒の場合はDEFAUTLT_RECEIVE_TEXT_COLORに
     * @param json
     * @return
     */
    @Nullable
    public static List<ChatMessage> handleChatArray(JSONObject json, int mode) {
        try {
            String name = mode == ChatActivity.NORMAL_MODE ? "chat" : "whisper";
            if(!json.has(name)) return null;

            JSONArray jsonArray = json.getJSONArray(name);
            RkLog.i(TAG, "Server Message Size: "+ jsonArray.length());
//            if (lastMessageCount == jsonArray.length() && lastMessageCount < 15) {
//                return null;
//            } else {
                List<ChatMessage> result = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++) {

                    String arrays[] = jsonArray.getString(i).split("\\t");
                    RkLog.d(TAG, arrays[0]+"/"+arrays[1]+"/"+arrays[2]);
                    boolean isMyMessage = arrays[1] == null || arrays[1].equals("null") || arrays[1].equals("#000000") || arrays[1].equals("#00000000");
                    result.add(new ChatMessage(isMyMessage, arrays[0], arrays[2]));
                }
                return result;
//                return result.subList(0, result.size() - lastMessageCount);
//            }
        } catch(JSONException e) {
            RkLog.i(TAG, "handleChatArray:"+e);
            return null;
        }
    }

    /**
     * 送信メッセージ形成(2shot申請などのアクション用)
     * @param action
     * @return
     */
    public static String createActionJsonString(String action) {
        try {
            JSONObject json = new JSONObject();
            json.put("action", action);

            return json.toString();
        }
        catch(JSONException e) {
            RkLog.i(TAG, "createActionJsonString:"+e);
            return null;
        }
    }

    /**
     * 送信メッセージ形成(テキストチャット用)
     * @param message
     * @return
     */
    public static String createChatJsonString(String message, int mode) {
        try {
            JSONObject json = new JSONObject();
            json.put("action", "message");
            json.put("msg", message);
            json.put("color", DEFAULT_SEND_TEXT_COLOR);
            json.put("messageType", mode == ChatActivity.NORMAL_MODE ? "Write" : "Whisper");

            return json.toString();
        }
        catch(JSONException e) {
            RkLog.i(TAG, "createChatJsonString:"+e);
            return null;
        }
    }
}
