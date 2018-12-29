package jp.fmaru.app.livechatapp;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.fmaru.app.livechatapp.util.JettyUtils;
import jp.fmaru.app.livechatapp.util.RkLog;

/**
 * jettyとのWebSocketClientを生成する
 */
public class JettyWebSocketManager {
    private static final String TAG = JettyWebSocketManager.class.getSimpleName();
    private ChatActivity mActivity;

    /** jetty認証済みかどうか */
    private boolean mIsAuth;

    public JettyWebSocketManager(ChatActivity activity) {
        mActivity = activity;
        //ステータス初期値
        mIsAuth = false;
    }

    /**
     * jettyにWS接続してWebSocketClientを返す
     * 例外が発生した場合nullを返す
     * @param jettyLoginRequest
     * @return WebSocketClient
     */
    public WebSocketClient getWSClient(String jettyLoginRequest) {
        try {
            /** originをセット */
            Map<String, String> headers = new HashMap<>();
            headers.put("origin", JettyUtils.createJettyOrigin(jettyLoginRequest));

            URI uri = new URI(jettyLoginRequest);

            /**
             * jetty WebSocketイベントハンドラ
             * Draft_17:WebSocketのプロトコル、3000(ms):タイムアウト時間
             */
            WebSocketClient mWSClient = new WebSocketClient(uri, new Draft_17(), headers, 3000) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    RkLog.i(TAG, "jettyWS onOpen");
                }

                @Override
                public void onMessage(String s) {
                    RkLog.i(TAG, "jettyWS onMessage:"+s);
                    handleMessage(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    RkLog.i(TAG, "jettyWS onClose code:"+i+" reason:"+s+" boolean:"+b);
                    mActivity.showToast(mActivity.getString(R.string.toast_error_jetty_onclose));
                    if (CloseFrame.NORMAL != i) {
                        mActivity.logout();
                    }
                }

                @Override
                public void onError(Exception e) {
                    RkLog.e(TAG, "jettyWS onError:"+e);
                    mActivity.onSocketError(mActivity.getString(R.string.toast_error_jetty_onerror));
                    mActivity.logout();
                }
            };

            /** 証明書の許可処理 */
            if (uri.getScheme() != null && uri.getScheme().equals("wss")) {
                RkLog.d(TAG, "jetty connect with wss");
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[] {
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[] {};
                            }
                        }
                }, null);
                SSLSocketFactory factory = ctx.getSocketFactory();
                mWSClient.setSocket(factory.createSocket());
            }

            mWSClient.connect();

            return mWSClient;
        } catch (URISyntaxException | NoSuchAlgorithmException | IOException | KeyManagementException e) {
            RkLog.e(TAG, "e:"+e);
            return null;
        }
    }

    /**
     * 受信メッセージのハンドリング
     * @param message
     */
    private void handleMessage(String message) {
        try {
            final JSONObject json = new JSONObject(message);

            RkLog.d(TAG, "handleMessage: " + json.toString());
            /** エラー時表示 */
            if (json.getString("result").equals("NG") && json.has("error")) {
                try {
                    mActivity.onSocketError(json.getString("error"));
                } catch (JSONException e) {
                     RkLog.i(TAG, "JSONException " + e);
                    mActivity.showToast(mActivity.getString(R.string.toast_error_jetty_onerror));
                }
                //認証前のエラー
                if (!mIsAuth) {
                    mActivity.doLogout();
                    return;
                }
            }

            /** ログアウトさせるエラー */
            if (json.has("errorStatus") && json.getInt("errorStatus") == 0) {
                mActivity.logout();
                return;
            }

            /** jsonハンドリング */
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /** 認証前 */
                        if (!mIsAuth) {
                            try {
                                mIsAuth = true;
                                mActivity.connectedJetty(json);
                            }
                            catch (JSONException e) {
                                mIsAuth = false;
                                mActivity.logout();
                            }
                            return;
                        }

                        /** チャットログ */
                        if (json.has("chat")) {
                            mActivity.handleChatLog(json);
                        }

                        if(json.has("status")){
                            mActivity.handleChangeMode();
                        }

                        /** ポイント */
                        if (json.has("point")) {
                            mActivity.changePointText(json);
                        }

                        /** 2shot */
                        if (json.has("2shot")) {
                            //0(現状では1,2以外):チャット
                            //1:2shot申請中
                            //2:2shot成立
                            Log.d(TAG, "2SHOT mode: " + json.getInt("2shot"));
                            mActivity.handle2Shot(json);
                        }
//                        if (json.has("2shotTime")) {
//                            RkLog.i(TAG, "2shotTime");
//                        }
//                        //チップ
//                        if (json.has("chip1")) {
//                            //Log.i(TAG, "chip1");
//                        }
//                        if (json.has("chip2")) {
//                            //Log.i(TAG, "chip2");
//                        }
//                        if (json.has("chip3")) {
//                            //Log.i(TAG, "chip3");
//                        }
//                        if (json.has("status")) {
//                            //Log.i(TAG, "status");
//                        }
//                        if (json.has("chipStatus")) {
//                            //Log.i(TAG, "chipStatus");
//                        }
//                        if (json.has("mainMemberCode")) {
//                            //Log.i(TAG", "mainMemberCode");
//                        }
//                        if (json.has("whisper")) {
//                            //Log.i(TAG, "whisper");
//                        }
//                        if (json.has("wcs")) {
//                            //Log.i(TAG, "wcs");
//                        }
                    } catch(JSONException e)  {
                        RkLog.i(TAG, "JSONException "+e);
                    }
                }
            });
        } catch(JSONException e)  {
            RkLog.i(TAG, "JSONException "+e);
        }
    }
}

