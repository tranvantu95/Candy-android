package jp.fmaru.app.livechatapp;

import android.view.View;

import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.bean.Data;
import com.flashphoner.fpwcsapi.bean.StreamStatus;
import com.flashphoner.fpwcsapi.session.Session;
import com.flashphoner.fpwcsapi.session.SessionEvent;
import com.flashphoner.fpwcsapi.session.SessionOptions;
import com.flashphoner.fpwcsapi.session.Stream;
import com.flashphoner.fpwcsapi.session.StreamOptions;
import com.flashphoner.fpwcsapi.session.StreamStatusEvent;

import org.webrtc.SurfaceViewRenderer;

import jp.fmaru.app.livechatapp.util.RkLog;

/**
 *MediaServerとのやりとりを管理
 */
public class MediaServerManager {
    private ChatActivity mActivity;
    /** for MediaServer */
    private SessionOptions mSessionOptions;
    private Session mSession;
    private Connection mConnection;

    private String mPerformerCode;
    private String mMemberCode;
    private String mSessionCode;

    private static final String TAG = MediaServerManager.class.getSimpleName();

    /**
     * コンストラクタ
     * MediaServer接続に必要なものをフィールドで保持
     * @param activity
     * @param mediaServerUrl
     * @param appKey
     * @param remoteRender
     * @param localRender
     * @param memberCode
     * @param performerCode
     * @param sessionCode
     */
    public MediaServerManager(ChatActivity activity, String mediaServerUrl, String appKey,
                             SurfaceViewRenderer remoteRender, SurfaceViewRenderer localRender,
                             String memberCode, String performerCode, String sessionCode) {

        mActivity = activity;
        mPerformerCode = performerCode;
        mMemberCode = memberCode;
        mSessionCode = sessionCode;

        mSessionOptions = new SessionOptions(mediaServerUrl);
        //SurfaceViewRenderをSessionOptionsにセット
        mSessionOptions.setRemoteRenderer(remoteRender);
        mSessionOptions.setLocalRenderer(localRender);

        //Connection生成、カスタムオブジェクトで値追加
        mConnection = new Connection();
        mConnection.setAppKey(appKey);
        mConnection.setCustom("performerCode", performerCode);
        mConnection.setCustom("memberCode", memberCode);
        mConnection.setCustom("sessionCode", sessionCode);
    }

    /**
     * MediaServerに接続
     */
    public void connect() {
        //MediaServerへの接続セッションを生成
        mSession = Flashphoner.createSession(mSessionOptions);

        /**
         * MediaServerコネクションイベントハンドラ
         */
        mSession.on(new SessionEvent() {
            @Override
            public void onAppData(Data data) {
                RkLog.i(TAG, "MediaServer onAppData");
            }

            @Override
            public void onConnected(Connection connection) {
                RkLog.i(TAG, "MediaServer onConnected");
                mActivity.connectedMediaServer();
            }

            @Override
            public void onRegistered(Connection connection) {
                RkLog.i(TAG, "MediaServer onRegistered");
            }

            @Override
            public void onDisconnection(Connection connection) {
                RkLog.i(TAG, "MediaServer onDisconnection");
//                mActivity.showToast(mActivity.getString(R.string.toast_error_media_disconnect));
                if(!mActivity.isLogout()) {
                    mActivity.logout();
                }
            }
        });

        //MediaServerへ接続
        mSession.connect(mConnection);
    }

    /**
     * MediaServerと切断
     */
    public void disconnect() {
        if (mSession != null) {
            mSession.disconnect();
            mSession = null;
        }
    }

    /**
     * PlayStream生成してplayして返す
     */
    public Stream getPlayStream() {
        Stream playStream = mSession.createStream(createCustomStreamOptions("stream-"+mPerformerCode));

        /**
         * StreamStatus変更コールバックによる処理
         */
        playStream.on(new StreamStatusEvent() {
            @Override
            public void onStreamStatus(final Stream stream, final StreamStatus streamStatus) {
                RkLog.i(TAG, "Play StreamStatus:"+streamStatus.toString());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (streamStatus) {
                            case PLAYING:
                                ChatActivity.mSpinnerLayout.setVisibility(View.GONE);
                                break;
                            case STOPPED:
                                break;
                            case FAILED:
                                mActivity.retryPlayStream();
                                break;
                            case PLAYBACK_PROBLEM:
                                break;
                            default:
                                RkLog.i(TAG, "Play StreamStatus Unknown streamStatus:"+streamStatus);
                        }
                    }
                });
            }
        });

        playStream.play();

        return playStream;
    }

    /**
     * PublishStream生成してpublishして返す
     */
    public Stream getPublishStream() {
        Stream publishStream = mSession.createStream(createCustomStreamOptions("stream-member-"+mMemberCode));

        /**
         * StreamStatus変更コールバックによる処理
         */
        publishStream.on(new StreamStatusEvent() {
            @Override
            public void onStreamStatus(final Stream stream, final StreamStatus streamStatus) {
                RkLog.i(TAG, "Publish StreamStatus:"+streamStatus.toString());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (streamStatus) {
                            case PUBLISHING:
                                RkLog.i(TAG, "Publish StreamStatus PUBLISHING");
                                //カメラ切替ボタン表示
                                mActivity.showLocalRender();
                                break;
                            case STOPPED:
                                RkLog.i(TAG, "Publish StreamStatus STOPPED");
                                break;
                            case FAILED:
                                RkLog.i(TAG, "Publish StreamStatus FAILED");
                                //publishStream();
                                break;
                            case UNPUBLISHED:
                                RkLog.i(TAG, "Publish StreamStatus UNPUBLISHED");
                                break;
                            case PLAYBACK_PROBLEM:
                                RkLog.i(TAG, "Publish StreamStatus PLAYBACK_PROBLEM");
                                break;
                            default:
                                RkLog.i(TAG, "Publish StreamStatus Unknown streamStatus:"+streamStatus);
                        }
                    }
                });
            }
        });

        publishStream.publish();

        return publishStream;
    }

    /**
     * カスタムオブジェクトをセットしたStreamOptionsを返す
     * @param streamName
     * @return streamOptions
     */
    private StreamOptions createCustomStreamOptions(String streamName) {
        //StreamOptions生成、カスタムオブジェクトで値追加
        StreamOptions streamOptions = new StreamOptions(streamName);
        streamOptions.setCustom("performerCode", mPerformerCode);
        streamOptions.setCustom("memberCode" ,mMemberCode);
        streamOptions.setCustom("sessionCode", mSessionCode);

        return streamOptions;
    }
}
