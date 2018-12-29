package com.candy.android.manager.chat;

import android.content.Context;
import android.text.TextUtils;

import com.candy.android.configs.Define;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiIncConfigResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.eventBus.chat.WebSocketEvent;
import com.candy.android.model.eventBus.chat.WsReadEvent;
import com.candy.android.model.eventBus.chat.WsSendEvent;
import com.candy.android.model.eventBus.chat.WsWriteEvent;
import com.candy.android.utils.RkLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

public class ChatClientManager {
    public static final String TAG = "IDK-ChatClientManager";

    //states
    private static final int STATE_NOT_CONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_FINISHED = 3;

    private static final String WEB_SOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    private static final String NOTIFY = "notify";
    private static final String KEY_ORIGIN = "Origin";
    private static final String ORIGIN_VALUE = "https://candy-app.com";

    // constant for in/out message fields
    private static final String FIELD_CODE = "code";
    private static final String FIELD_ACTION = "action";
    private static final String ACTION_WRITE = "write";
    private static final String ACTION_READ = "read";
    private static final String ACTION_SEND = "send";
    private static final int MAGIC_NUMBER = 2;

    private static ChatClientManager instance;

    private MyWebSocketClient webSocketClient;
    private boolean mAuthenticated;
    private int mRetryCount;

    /**
     * true if this chat manager is initialized
     */
    private int mState;
    private WeakReference<Context> mContext;
    private String mAuthenticatedCode;

    /**
     * also known as member code
     */

    private ChatClientManager(Context context) {
        mContext = new WeakReference<>(context);
        mState = STATE_NOT_CONNECTED;
    }

    public static ChatClientManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatClientManager(context);
        }

        return instance;
    }

    private boolean isConnected() {
        return mState == STATE_CONNECTED;
    }

    public void setState(int state) {
        mState = state;
    }

    void sendMessage(String message) {
        if (webSocketClient != null) {
            webSocketClient.send(message);
        }
    }

    private void sendAuthenticationMessage() {
        if (mState == STATE_CONNECTED && webSocketClient != null) {
            RkLogger.d(TAG, "try sendAuthenticationMessage...");
            String authenMsg = buildAuthenticationMessage();

            if (!TextUtils.isEmpty(authenMsg)) {
                sendMessage(authenMsg);
            }
        }
    }

    private String buildAuthenticationMessage() {
//        {"ownerName":"reas55","user":"member","id":"IZR1D1wART1OVpY1","pass":"l3c7QFSn"}
        SettingManager manager = SettingManager.getInstance(mContext.get());
        ApiIncConfigResponse config = SettingManager.getInstance(mContext.get()).getConfig();

        //incConfig is called from Splash screen, we should have a non-null config object here
        //double check
        if (config == null) {
            RkLogger.w(TAG, "Warning, Config object is null, there is no web socket url, no connection");
            return null;
        }

        if (!manager.hasMember()) {
            RkLogger.w(TAG, "Warning, there is no member (how can you get here?), no connection");
            return null;
        }

        final MemberInformation member = manager.getMemberInformation();
        String authenMsg = String.format("{\"ownerName\":\"%s\",\"user\":\"member\",\"id\":\"%s\",\"pass\":\"%s\"}",
                config.getOwnerName(), member.getId(), member.getPass());

        return authenMsg;
    }

    private void connect() {
        if (mState == STATE_CONNECTING) {
            // already has a connecting session, return
            return;
        }
        try {
            if (mRetryCount < MAGIC_NUMBER) {
                RkLogger.d(TAG, "connecting...");
            }
            SettingManager manager = SettingManager.getInstance(mContext.get());
            ApiIncConfigResponse config = SettingManager.getInstance(mContext.get()).getConfig();

            //incConfig is called from Splash screen, we should have a non-null config object here
            //double check
            if (config == null) {
                RkLogger.w(TAG, "Warning, Config object is null, there is no web socket url, no connection");
                return;
            }

            Map<String, String> params = new HashMap<>();
            params.put(KEY_ORIGIN, ORIGIN_VALUE);   // add origin for CORS passthrough
            params.put(WEB_SOCKET_PROTOCOL, NOTIFY);

            // webSocketClient = new ChatClientManager(new URI(uriField.getText()), area, ( Draft ) draft.getSelectedItem() );
            webSocketClient = new MyWebSocketClient(URI.create(config.getWebSocketUrl()), params);
            SSLSocketFactory factory = TrustAllSSLContext.getSocketFactory();
            if (factory != null) {
                webSocketClient.setSocket(factory.createSocket());
                webSocketClient.connect();
            }

        } catch (Exception ex) {
            RkLogger.e(TAG, "connection exception: ", ex);
        }
    }

    private void tryReconnect() {
        if (mRetryCount < MAGIC_NUMBER) {
            RkLogger.d(TAG, "try reconnecting...");
        }
        if (!isConnected()) {
            connect();
        }
    }

    public void close() {
        RkLogger.d(TAG, "close connection...");
        if (webSocketClient != null) {
            webSocketClient.close();
            mState = STATE_FINISHED;
        }
    }

    public void initialize() {
        if (!isConnected()) {
            connect();
        }
    }

    public void onResume() {
        if (!isConnected()) {
            connect();
        }
    }

    /**
     * post an event to web socket to notify performer that, member is typing
     */
    public void sendTypingMessage() {
        if (mState == STATE_CONNECTED && mAuthenticated) {
            String typingEvent = String.format("{\"action\":\"write\",\"partnerCode\":\"%s\"}", mAuthenticatedCode);
            sendMessage(typingEvent);
        }
    }

    private class MyWebSocketClient extends WebSocketClient {

        private static final int CONNECTION_TIMEOUT = 30000;    // 30s

        MyWebSocketClient(URI serverURI, Map<String, String> params) {
            super(serverURI, new Draft_17(), params, CONNECTION_TIMEOUT);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            RkLogger.d(TAG, "connection opened");
            mState = STATE_CONNECTED;

            //send authenticated message
            sendAuthenticationMessage();
        }

        @Override
        public void onMessage(String message) {
            if (Define.CONST_OPEN.equals(message)) {
                RkLogger.d(TAG, "onMessage: " + message);
            } else {
                parseMessage(message);
            }
        }

        /**
         * Parse incoming message from socket
         */
        private void parseMessage(String message) {
            try {
                RkLogger.d(TAG, "parsing message=" + message);
                JSONObject object = new JSONObject(message);
                if (object.has(FIELD_CODE)) {
                    mAuthenticatedCode = object.getString(FIELD_CODE);
                    mAuthenticated = true;
                } else if (object.has(FIELD_ACTION)) {
                    handleActionMessage(object.getString(FIELD_ACTION), message);
                }
            } catch (JSONException | JsonSyntaxException e) {
                RkLogger.w(TAG, "Warning parsing message \"" + message + "\" from web socket: ", e);
            }
        }

        /**
         * Handle incoming action message from web socket and in the end, post an eventBus accordingly
         */
        private void handleActionMessage(String action, String message) throws JSONException, JsonSyntaxException {
            Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            WebSocketEvent socketEvent = null;

            switch (action) {
                case ACTION_WRITE:
                    socketEvent = gson.fromJson(message, WsWriteEvent.class);
                    break;
                case ACTION_READ:
                    socketEvent = gson.fromJson(message, WsReadEvent.class);
                    break;
                case ACTION_SEND:
                    socketEvent = gson.fromJson(message, WsSendEvent.class);
                    break;
                default:
                    break;
            }

            RkLogger.d(TAG, "receive action message: " + socketEvent);
            // post event
            EventBus.getDefault().post(socketEvent);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if (mRetryCount < MAGIC_NUMBER) {
                RkLogger.d(TAG, "You are disconnected from Chat server" + getURI());
            }
            // if connection closed by accidental network, re-try connect
            if (STATE_FINISHED != mState) {
                mState = STATE_NOT_CONNECTED;
                mAuthenticated = false;
                mAuthenticatedCode = null;

                tryReconnect();
                mRetryCount++;
            }
        }

        @Override
        public void onError(Exception ex) {
            if (mRetryCount < MAGIC_NUMBER) {
                RkLogger.e(TAG, "Exception in Chat server " + getURI(), ex);
            }
            mAuthenticated = false;
            mAuthenticatedCode = null;
            mState = STATE_NOT_CONNECTED;
        }

        @Override
        public void connect() {
            if (mRetryCount < MAGIC_NUMBER) {
                RkLogger.d(TAG, "connecting...");
            }
            mState = STATE_CONNECTING;
            super.connect();
        }
    }
}