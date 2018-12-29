package jp.fmaru.app.livechatapp;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.handler.CameraSwitchHandler;
import com.flashphoner.fpwcsapi.session.Stream;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.fmaru.app.livechatapp.adapter.ChatAdapter;
import jp.fmaru.app.livechatapp.adapter.ChatPagerAdapter;
import jp.fmaru.app.livechatapp.custom.RoundCornerImageView;
import jp.fmaru.app.livechatapp.fragment.AlertDialogFragment;
import jp.fmaru.app.livechatapp.fragment.BottomBarFragment;
import jp.fmaru.app.livechatapp.fragment.SettingDialogFragment;
import jp.fmaru.app.livechatapp.model.ChatMessage;
import jp.fmaru.app.livechatapp.util.Constants;
import jp.fmaru.app.livechatapp.util.JettyUtils;
import jp.fmaru.app.livechatapp.util.RkLog;
import jp.fmaru.app.livechatapp.util.RkSharedPreferencesUtils;

/**
 * チャットactivity
 */
public class ChatActivity extends AppCompatActivity
        implements AlertDialogFragment.DialogInterface, BottomBarFragment.BottomBarInterface {

    /**
     * チャット状態
     */
    public static final int CHAT_STATUS_LOGOUT = -1;
    public static final int CHAT_STATUS_CHAT = 0;
    public static final int CHAT_STATUS_TWO_SHOT_REQUEST = 1;
    public static final int CHAT_STATUS_TWO_SHOT = 2;

    public static final String EXTRA_PERFORMER_CODE = "performer_code";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_PASS = "pass";
    public static final String EXTRA_PERFORMER_NAME = "performer_name";
    public static final String EXTRA_PERFORMER_IMAGE = "performer_image";
    public static final String EXTRA_APP_CODE = "appCode";
    public static final String EXTRA_MODE = "mode";

    public static final int MIN_POINT_TO_SHOW_MESSAGE = 600;
    public static final int MAX_POINT_TO_SHOW_MESSAGE = 1000;

    /**
     * PublishStreamの状態
     */
    public static final int UNPUBLISHED = 0;
    public static final int PUBLISHED = 1;

    /**
     * PlayStreamをリトライする回数
     */
    public static final int PLAY_RETRY_TIMES = 3;

    private static final String TAG = ChatActivity.class.getSimpleName();

    /**
     * 共有変数オブジェクト
     */
    private Globals globals;

    /*
    * Mode
    * */
    public static final int NORMAL_MODE = 0;
    public static final int PEEP_MODE = 1;

    /**
     * for user
     */
    protected String mMemberId;
    protected String mMemberPass;
    protected String mMemberCode;
    protected String mPerformerCode;
    protected String mSessionCode;
    protected String mAppCode;
    protected String mainMemberCode;

    /**
     * 状態
     */
    private int mChatStatus;
    private int mPublishStatus;

    /**
     * PlayStreamリトライ回数
     */
    private int mPlayRetryCount;

    /**
     * for view
     */
    private TextView mPointText;
    private ImageView mStatusImage;
    private ImageView mSwitchCameraImage;
    private ImageView mSwitchCameraBigImage;
    private LinearLayout mHeaderLayout;
    protected LinearLayout mBottomBarLayout;
    public static LinearLayout mSpinnerLayout;

    protected ImageView mPlusImage;
    protected LinearLayout mAddPointLayout;

    /**
     * for dialog
     */
    private DialogFragment mDialogFragment;

    /**
     * for jetty
     */
    private WebSocketClient mWSClient;

    /**
     * for MediaServer,video
     */
    private MediaServerManager mMediaServerClient;
    private SurfaceViewRenderer mLocalRender;
    private SurfaceViewRenderer mRemoteRender;
    private Stream mPlayStream;
    private Stream mPublishStream;

    private boolean isHideHeader;
    private boolean isLogout;

    // Local video view
    private boolean isShowBigCamera = false;
    private ViewGroup mLocalCameraLayout;
    private ViewGroup mLocalCameraContainer;
    private ViewGroup mLocalCameraBigLayout;
    private ViewGroup mLocalCameraBigContainer;
    private boolean isShowCamera = false, isOnAudio = false;
    private boolean firstOnCamera =  true;

    // Chat view
    private RecyclerView chatList, chatPeepList;
    private ChatAdapter chatAdapter, chatPeepAdapter;
    private ViewPager chatViewPager;
    private ChatPagerAdapter chatPagerAdapter;

    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 画面スリープさせない */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /* タイトルバー非表示 */

        setContentView(R.layout.activity_chat);
        mSpinnerLayout = (LinearLayout) findViewById(R.id.layoutProgressBar);
        mSpinnerLayout.setVisibility(View.VISIBLE);

        /* 各ステータス初期状態 */
        mChatStatus = CHAT_STATUS_LOGOUT;
        mPublishStatus = UNPUBLISHED;
        mPlayRetryCount = 0;

        /* フォーム値取得 */
        Intent intent = getIntent();
        mPerformerCode = intent.getStringExtra(EXTRA_PERFORMER_CODE);
        mMemberId = intent.getStringExtra(EXTRA_ID);
        mMemberPass = intent.getStringExtra(EXTRA_PASS);
        String performerName = intent.getStringExtra(EXTRA_PERFORMER_NAME);
        String performerImage = intent.getStringExtra(EXTRA_PERFORMER_IMAGE);
        mAppCode = intent.getStringExtra(EXTRA_APP_CODE);
        mode = intent.getIntExtra(EXTRA_MODE, NORMAL_MODE);



        /* BottomBar作成 */
        if(mode == NORMAL_MODE) createBottomBarFragment(BottomBarFragment.BOTTOM_LAYOUT_CHAT);
        else createBottomBarFragment(BottomBarFragment.BOTTOM_LAYOUT_PEEP);

        /* 共有変数オブジェクト */
        globals = (Globals) getApplication();

        /* 外部APIで設定値取得(mediaServerUrl,jettyUrl,appKey,ownerName) */
        //成功後にconnectJetty
        JsonLoaderManager mJm = new JsonLoaderManager(this);
        mJm.runDataLoader(getString(R.string.api_inc_chat_config_url));

        /* MediaServer API init */
        Flashphoner.init(this);

        /* for view */
        mPointText = (TextView) findViewById(R.id.point);
        ImageView logoutImage = (ImageView) findViewById(R.id.logoutImage);
        logoutImage.setOnClickListener(mViewClickListener);
        ImageView settingImage = (ImageView) findViewById(R.id.settingImage);
        settingImage.setOnClickListener(mViewClickListener);
        mSwitchCameraImage = (ImageView) findViewById(R.id.switchCameraImage);
        mSwitchCameraImage.setOnClickListener(mViewClickListener);
        mAddPointLayout = (LinearLayout) findViewById(R.id.add_point_layout);

        //カメラ切替は初期状態は非表示
        mSwitchCameraImage.setVisibility(View.INVISIBLE);
        //未対応
        RoundCornerImageView ivPerformerImage = (RoundCornerImageView) findViewById(R.id.performerImage);
        if (!TextUtils.isEmpty(performerImage)) {
            Glide.with(this).load(performerImage).into(ivPerformerImage);
        }
        TextView tvPerformerName = (TextView) findViewById(R.id.performerName);
        if (!TextUtils.isEmpty(performerName)) {
            tvPerformerName.setText(Html.fromHtml(performerName));
        }
        mPlusImage = (ImageView) findViewById(R.id.plusImage);
        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);

        //for remote video
        mRemoteRender = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);
        mRemoteRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mRemoteRender.setMirror(false);
        mRemoteRender.requestLayout();
        mRemoteRender.setOnClickListener(mViewClickListener);

        //for local video
        mLocalRender = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        mLocalRender.setOnClickListener(mViewClickListener);
        mLocalRender.setZOrderMediaOverlay(true);
        mLocalRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        mLocalRender.setMirror(true);
        mLocalRender.requestLayout();

        // Local video views
        mLocalCameraLayout = (ViewGroup) findViewById(R.id.localVideoLayout);
        mLocalCameraContainer = (ViewGroup) findViewById(R.id.localCameraContainer);
        mLocalCameraBigLayout = (ViewGroup) findViewById(R.id.localVideoBigLayout);
        mLocalCameraBigContainer = (ViewGroup) findViewById(R.id.localCameraBigContainer);
        mSwitchCameraBigImage = (ImageView) findViewById(R.id.switchCameraBigImage);
        mSwitchCameraBigImage.setOnClickListener(mViewClickListener);

        mBottomBarLayout = (LinearLayout) findViewById(R.id.bottomBarLayout);

        // Chat view
        chatAdapter = new ChatAdapter();
        chatList = (RecyclerView) findViewById(R.id.chatLogRC);
        chatList.setAdapter(chatAdapter);

        setupMode(mode);

//        mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_WAITING_CONNECTED);
//        mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_WAITING_CONNECTED_NAME);

    }

    private void setupMode(int mode) {
        this.mode = mode;

        mStatusImage = (ImageView) findViewById(R.id.statusImage);
        mStatusImage.setImageResource(mode == NORMAL_MODE ? R.drawable.status_video : R.drawable.status_peep);

        if(mode == NORMAL_MODE) {
            TabLayout tabLayout = (TabLayout) findViewById(R.id.chat_tab_layout);
            tabLayout.setSelectedTabIndicatorHeight(0);

            //revert
//            if(tabLayout.getChildCount() > 1) tabLayout.getChildAt(1).setVisibility(View.GONE);
            if(chatPagerAdapter != null && chatPagerAdapter.getCount() > 1) {
                chatPagerAdapter.getViews().remove(1);
//                chatPagerAdapter.notifyDataSetChanged();
//                chatViewPager.setCurrentItem(0);
                chatViewPager.setAdapter(chatPagerAdapter);
            }

            return;
        }

        if(chatPeepAdapter == null) chatPeepAdapter = new ChatAdapter();
        if(chatPeepList == null) chatPeepList = (RecyclerView) getLayoutInflater().inflate(R.layout.chat_list, null);
        chatPeepList.setAdapter(chatPeepAdapter);

        if(chatList.getParent() != null)
            ((ViewGroup) chatList.getParent()).removeView(chatList);

        if(chatPeepList.getParent() != null)
            ((ViewGroup) chatPeepList.getParent()).removeView(chatPeepList);

        //
        chatPagerAdapter = new ChatPagerAdapter(this);
        chatPagerAdapter.getViews().add(chatList);
        chatPagerAdapter.getViews().add(chatPeepList);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.chat_tab_layout);
        chatViewPager = (ViewPager) findViewById(R.id.chat_view_pager);
        chatViewPager.setAdapter(chatPagerAdapter);
        tabLayout.setupWithViewPager(chatViewPager);
    }

    public void handleChangeMode() {
        setupMode(NORMAL_MODE);
        createBottomBarFragment(BottomBarFragment.BOTTOM_LAYOUT_CHAT);

    }

    @Override
    protected void onResume() {
        super.onResume();
        RkLog.d(TAG, "onResume");
        try {
            mRemoteRender.init(Flashphoner.context, null);
        } catch (IllegalStateException e) {
            RkLog.d(TAG, "onResume " + e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doLogout();
    }

    /**
     * 外部APIからチャット情報jsonを取得して実行される
     * jettyに接続してWebSocketClientを生成、取得
     *
     * @param json Jetty json message
     */
    public void connectJetty(JSONObject json) {
        if (json == null) {
            showToast(this.getString(R.string.toast_error_chat_config));
            logout();
            return;
        }
        /* 共有するデータの保持 */
        String loginJson;

        /* 共有するデータの保持 */
        try {
            if ( json.isNull("jettyUrl") || json.isNull("appKey") || json.isNull("ownerName")) {
                showToast(this.getString(R.string.toast_error_chat_config));
                doLogout();
                return;
            }
            if(json.has("mediaServerUrl")) {
                globals.setMediaServerUrl(json.getString("mediaServerUrl"));
            }
            globals.setJettyUrl(json.getString("jettyUrl"));
            globals.setAppKey(json.getString("appKey"));
            globals.setOwnerName(json.getString("ownerName"));

            /** パラメータで渡すjson生成 */
            loginJson = createLoginJettyString();
        } catch (JSONException e) {
            showToast(this.getString(R.string.toast_error_chat_config));
            logout();
            return;
        }
        /* パラメータを含むURL生成 */
        String jettyLoginRequest = JettyUtils.createJettyLoginRequest(globals.getJettyUrl(), loginJson);

        JettyWebSocketManager jettyWebSocketManager = new JettyWebSocketManager(this);
        mWSClient = jettyWebSocketManager.getWSClient(jettyLoginRequest);
        RkLog.i(TAG, "connectJetty: mWSClient = " + mWSClient);
        if (mWSClient == null) {
            showToast(this.getString(R.string.toast_error_jetty_connect));
            logout();
        }
    }

    /**
     * jettyログイン用のjsonの文字列を返す
     * @return String
     * @throws JSONException
     */
    private String createLoginJettyString() throws JSONException {
        JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.RequestParams.PARAM_PERFORMER_CODE, mPerformerCode);
            jsonObject.put(Constants.RequestParams.PARAM_PERFORMER_ID, mMemberId);
            jsonObject.put(Constants.RequestParams.PARAM_PERFORMER_PASS, mMemberPass);
            jsonObject.put(Constants.RequestParams.PARAM_PERFORMER_NAME, globals.getOwnerName());
            jsonObject.put(Constants.RequestParams.PARAM_STATUS, mode);
            jsonObject.put(Constants.RequestParams.PARAM_APP_CODE, mAppCode);

            String mediaServerUrl = globals.getMediaServerUrl();
            if(mediaServerUrl != null ) {
                Uri uri = Uri.parse(mediaServerUrl);
                jsonObject.put("wcsip", uri.getAuthority());
            }
            return jsonObject.toString();
    }

    /**
     * jettyのWebSocket接続成功時に呼ばれ、MediaServerに接続する
     *
     * @param json Jetty json message
     */
    public void connectedJetty(JSONObject json) throws JSONException {
        // Close loading dialog
        {
            AlertDialogFragment dialogFragment = (AlertDialogFragment) getFragmentManager().findFragmentByTag(AlertDialogFragment.DIALOG_WAITING_CONNECTED_NAME);
            if (dialogFragment != null && mDialogFragment.isAdded()) {
                getFragmentManager().beginTransaction().remove(dialogFragment).commitAllowingStateLoss();
            }
        }

        RkLog.i(TAG, "connectedJetty");
        if (json.has("memberCode")) {
            mMemberCode = json.getString("memberCode");
        }
        if(json.has("mainMemberCode")) {
            mainMemberCode = json.getString("mainMemberCode");
        }

        if (json.has("sessionCode")) {
            mSessionCode = json.getString("sessionCode");
        }

        mChatStatus = CHAT_STATUS_CHAT;

        String serverMeiaServerUrl = globals.getMediaServerUrl();
        if(json.has("mediaServerUrl")){
            serverMeiaServerUrl = "wss://" + json.getString("mediaServerUrl");
        }
        mMediaServerClient = new MediaServerManager(this,
                serverMeiaServerUrl, globals.getAppKey(),
                mRemoteRender, mLocalRender,
                mMemberCode, mPerformerCode, mSessionCode);

        mMediaServerClient.connect();
    }

    /**
     * チャット状態を返す
     *
     * @return mChatStatus
     */
    public int getChatStatus() {
        return mChatStatus;
    }

    /**
     * ポイント数の表示を更新
     *
     * @param json Message json
     */
    public void changePointText(JSONObject json) {
        try {
            String point = json.getString("point");
            mPointText.setText(String.format(Locale.US, getString(R.string.action_bar_n_pts), Integer.valueOf(point)));
        } catch (JSONException e) {
            RkLog.i(TAG, "JSONException " + e);
        }
    }

    public void changePointText(int point) {
        mPointText.setText(String.format(Locale.US, getString(R.string.action_bar_n_pts), point));
    }

    /**
     * 2shot時のview表示
     * To be called after media server published stream
     */
    public void showLocalRender() {
        if (isShowCamera) {
            showLocalCameraView();
        } else {
            muteCamera();
        }

        if(!isOnAudio) muteMic();

        createBottomBarFragment(BottomBarFragment.BOTTOM_LAYOUT_TWO_SHOT);
    }

    /**
     * チャットログ形成
     *
     * @param json Message json
     */
    public void handleChatLog(JSONObject json) {
        RkLog.i(TAG, "handleChatLog: " + json);
        if(mode == NORMAL_MODE) {
            updateChatList(chatAdapter, JettyUtils.handleChatArray(json, NORMAL_MODE));
        }
        else {
            updateChatList(chatAdapter, JettyUtils.handleChatArray(json, NORMAL_MODE));
            updateChatList(chatPeepAdapter, JettyUtils.handleChatArray(json, PEEP_MODE));
        }
    }

    private void updateChatList(ChatAdapter chatAdapter, List<ChatMessage> newMessage) {
        if (null != newMessage && !newMessage.isEmpty()) {
            chatAdapter.getItems().clear();
            chatAdapter.getItems().addAll(newMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * MediaServerに接続してPlayStream取得
     */
    public void connectedMediaServer() {
        RkLog.i(TAG + "", "Play stream retry");
        if (mMediaServerClient != null) {
            mPlayStream = mMediaServerClient.getPlayStream();

            // Show dialog suggest 2SHOT
//            boolean isNeedShowFirst = RkSharedPreferencesUtils.getInstance().isNeedShowFirstTimeDialog();
//            if (isNeedShowFirst) {
//                // Block media connected multi time
//                DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(AlertDialogFragment.DIALOG_TWO_SHOT_FIRST_TIME_NAME);
//                if (null == dialog) {
//                    mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_TWO_SHOT_FIRST_TIME);
//                    mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_TWO_SHOT_FIRST_TIME_NAME);
//                }
//            }
        }
    }

    /**
     * playStreamのリトライ
     * リトライ回数：PLAY_RETRY_TIMES、それ以上はログアウト
     */
    public void retryPlayStream() {
        RkLog.i(TAG + "", "Play stream retry");
        if (mPlayRetryCount <= PLAY_RETRY_TIMES) {
            mPlayRetryCount++;
            mPlayStream.stop();
            mPlayStream = null;
            connectedMediaServer();
        } else {
            RkLog.i(TAG + "", "Play stream was retried 3 times");
            Toast.makeText(this, getString(R.string.toast_error_media_play_failed), Toast.LENGTH_LONG).show();
            logout();
        }
    }

    public void handle2Shot(JSONObject json) {
        int _2shot = -1;

        try {
            _2shot = json.getInt("2shot");
            if(json.has("mainMemberCode")) {
                mainMemberCode = json.getString("mainMemberCode");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        switch (mChatStatus) {
            case CHAT_STATUS_CHAT:
                if (_2shot == CHAT_STATUS_TWO_SHOT_REQUEST) {
                    /** チャット→2shot申請 */
                    RkLog.i(TAG, "2shot request");
                    if(TextUtils.equals(mainMemberCode, mMemberCode)) twoShotRequestSuccess();
                    else noGoto2Shot();
                }
                /** チャット→2shot成立 */
                else if (_2shot == CHAT_STATUS_TWO_SHOT) {
                    //このパターンはないはず
                    RkLog.i(TAG, "2shot unusual success");
                    //mActivity.twoShotSuccess();
                }
                break;

            case CHAT_STATUS_TWO_SHOT_REQUEST:
                if (_2shot == CHAT_STATUS_TWO_SHOT) {
                    /** 2shot申請中→2shot成立 */
                    RkLog.i(TAG, "2shot success");
                    twoShotSuccess();
                }
                else if (_2shot == CHAT_STATUS_CHAT) {
                    /** 2shot申請中→拒否 */
                    RkLog.i(TAG, "2shot reject");
                    twoShotRejected();
                }
                break;

            case CHAT_STATUS_TWO_SHOT:
                if (_2shot == CHAT_STATUS_CHAT) {
                    /** 2shot→2shot解除(パフォーマからの解除含む) */
                    twoShotDestroyed();
                }
                break;
        }

        mChatStatus = _2shot;

    }

    /**
     * 2shot申請成功
     * チャットステータスを申請中に
     * 返事待ちダイアログ表示
     */
    public void twoShotRequestSuccess() {
        RkLog.i(TAG, "twoShotRequestSuccess");
        // Change chat status
        mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_TWO_SHOT_WAIT);
        mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_TWO_SHOT_WAIT_NAME);
    }

    /**
     * 2shot成功
     * チャットステータス／画像を2shotに
     * 返事待ちダイアログ非表示
     * BottomBarを2shot用に
     * ローカル映像パブリッシュ
     */
    public void twoShotSuccess() {
        // Show toast notice 2 shot success
        showToast(getString(R.string.toast_two_shot_success));

        AlertDialogFragment alertDialog = (AlertDialogFragment) getFragmentManager().findFragmentByTag(AlertDialogFragment.DIALOG_TWO_SHOT_WAIT_NAME);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        mStatusImage = (ImageView) findViewById(R.id.statusImage);
        mStatusImage.setImageResource(R.drawable.status_2shot);

        // Show dialog setting media
        SettingDialogFragment settingDialogFragment = SettingDialogFragment.new2SHOTFirstTimeInstance();
        settingDialogFragment.show(getSupportFragmentManager(), SettingDialogFragment.TAG);
    }

    private void noGoto2Shot() {
        mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_NO_GOTO_2SHOT);
        mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_NO_GOTO_2SHOT_NAME);
    }

    /**
     * 2shot拒否
     * チャットステータスをチャットに
     * 返事待ちダイアログ非表示
     */
    public void twoShotRejected() {
        AlertDialogFragment alertDialog = (AlertDialogFragment) getFragmentManager().findFragmentByTag(AlertDialogFragment.DIALOG_TWO_SHOT_WAIT_NAME);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_TWO_SHOT_RE_REQUEST);
        mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_TWO_SHOT_RE_REQUEST_NAME);
    }


    /**
     * 2shot解除成功
     * チャットステータス／画像をチャットに
     * 2shotキャンセルダイアログが表示されている場合非表示
     * bottomBarをチャット用に
     * ローカル映像アンパブリッシュ
     */
    public void twoShotDestroyed() {
        AlertDialogFragment alertDialog = (AlertDialogFragment) getFragmentManager().findFragmentByTag(AlertDialogFragment.DIALOG_TWO_SHOT_CANCEL_NAME);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        mStatusImage = (ImageView) findViewById(R.id.statusImage);
        mStatusImage.setImageResource(R.drawable.status_video);

        createBottomBarFragment(BottomBarFragment.BOTTOM_LAYOUT_CHAT);

        //カメラ切替ボタン非表示
//        mSwitchCameraImage.setVisibility(View.INVISIBLE);
        hideLocalCameraView();

        if (mPublishStream != null) {
            mPublishStream.stop();
            mPublishStream = null;
        }

        mLocalRender.release();
        mLocalRender.surfaceDestroyed(mLocalRender.getHolder());
        //非表示
//        mLocalRender.setVisibility(View.INVISIBLE);
    }

    /**
     * ログアウト
     */
    public void logout() {
        if (isDestroyed()) {
            return;
        }
        mDialogFragment = (DialogFragment) getFragmentManager().findFragmentByTag(AlertDialogFragment.DIALOG_SOCKET_ERROR_NAME);
        if (mDialogFragment == null) {
            onNetworkClose();
        }
    }

    public void doLogout() {
        setLogout(true);
        RkLog.i(TAG, "Do logout");
        /* jettyWS切断 */

        RkLog.i(TAG, "Do logout: mWSClient = " + mWSClient);
        if (mWSClient != null && mWSClient.getReadyState() != WebSocket.READYSTATE.CLOSED) {
            RkLog.i(TAG, "Do logout: mWSClient status = " + mWSClient.getReadyState());
            mWSClient.close();
            RkLog.i(TAG, "Do logout: Do close");
        }
        mWSClient = null;

        /* stream停止 */
        if (mPlayStream != null) {
            mPlayStream.stop();
            mPlayStream = null;
        }
        if (mPublishStream != null) {
            mPublishStream.stop();
            mPublishStream = null;
        }
        /* MediaServer切断 */
        if (mMediaServerClient != null) {
            mMediaServerClient.disconnect(); //playStream停止、unPublishはdisconnect内で呼んでる
            mMediaServerClient = null;
        }

        /* surfaceView release */
        if (mRemoteRender != null) {
            mRemoteRender.release();
        }
        if (mLocalRender != null) {
            mLocalRender.release();
        }

        /* 各ステータス変更 */
        mChatStatus = CHAT_STATUS_LOGOUT;
        mPublishStatus = UNPUBLISHED;

        /* 共有変数クリア */
        globals.clearAll();

        // Finish this screen
        ChatActivity.this.finish();
    }

    /**
     * チャットメッセージ送信send
     */
    private void send() {
        String sendMessage = ((EditText) findViewById(R.id.editChatText)).getText().toString();
        if (!TextUtils.isEmpty(sendMessage)) {
            //Log.d(TAG, JettyUtils.createChatJsonString(sendMessage));
            if (mWSClient != null) {
                mWSClient.send(JettyUtils.createChatJsonString(sendMessage, mode));
                if(chatViewPager != null) chatViewPager.setCurrentItem(mode, true);
            }
        }
    }

    /**
     * 2shot申請send
     */
    private void requestTwoShot() {
        /* android 6.0以降のパーミッション対応 */
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            //カメラ、マイク共に許可されている場合
            RkLog.d(TAG, "requestTwoShot granted permission");
            makeRequest2SHOT();
        } else {
            //権限取得
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: { //requestPermissions()の第2引数で指定した値
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //カメラ、マイク共に許可された場合
                    RkLog.d(TAG, "onRequestPermissionsResult granted permission");
                    Toast.makeText(this, getString(R.string.toast_permission_granted), Toast.LENGTH_LONG).show();
                    makeRequest2SHOT();
                } else {
                    //許可しないとした場合
                    RkLog.d(TAG, "onRequestPermissionsResult don't grant permission");
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
                        Toast.makeText(this, getString(R.string.toast_permission_not_granted_forever), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, getString(R.string.toast_permission_not_granted), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }

    /**
     * Send 2SHOT request to Performer
     */
    private void makeRequest2SHOT() {
        if (mWSClient != null) {
//            isAccept2SHOT = true;
            if(!TextUtils.isEmpty(mainMemberCode)&&TextUtils.equals(mainMemberCode, mMemberCode)) mWSClient.send(JettyUtils.createActionJsonString(Constants.SocketActions.ACTION_2SHOT_REQUEST));
            else noGoto2Shot();
        }
    }

    /**
     * 2shot解除send
     */
    private void cancelTwoShot() {
        if (mWSClient != null) {
            mWSClient.send(JettyUtils.createActionJsonString(Constants.SocketActions.ACTION_2SHOT_DESTROY));
        }
    }

    /**
     * bottomBarの生成
     *
     * @param pattern pattern
     */
    private void createBottomBarFragment(int pattern) {
        Log.d("testSetting2", "" + (!isMicroMute()) + "  " + (!isVideoMute()));
        BottomBarFragment fragment = BottomBarFragment.newInstance(pattern, isVideoMute(), isMicroMute());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.bottomBarLayout, fragment);
        transaction.commit();
    }

    /**
     * ボタンリスナー設定クラス
     * ダイアログ表示の場合DialogFragmentを設定して表示
     */
    private View.OnClickListener mViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.logoutImage) {
                mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_LOGOUT);
                mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_LOGOUT_NAME);

                /* 設定 */
            } else if (i == R.id.settingImage) {
                SettingDialogFragment settingDialogFragment;
                if (mChatStatus == CHAT_STATUS_TWO_SHOT) {
                    settingDialogFragment = SettingDialogFragment.newInstance(true);
                } else {
                    settingDialogFragment = SettingDialogFragment.newInstance(false);
                }
                settingDialogFragment.show(getSupportFragmentManager(), SettingDialogFragment.TAG);
            } else if (i == R.id.switchCameraImage || i == R.id.switchCameraBigImage) {
                RkLog.e(TAG, "ButtonListener switchCameraImage");
                if (mPublishStream != null) {
                    mPublishStream.switchCamera(new CameraSwitchHandler() {
                        @Override
                        public void onCameraSwitchDone(boolean b) {
                            RkLog.d(TAG, "onCameraSwitchDone");
                        }

                        @Override
                        public void onCameraSwitchError(String s) {
                            RkLog.d(TAG, "onCameraSwitchError");
                        }
                    });
                }
            } else if (i == R.id.remote_video_view) {
                if (isHideHeader) {
                    mHeaderLayout.setVisibility(View.VISIBLE);
                } else {
                    mHeaderLayout.setVisibility(View.INVISIBLE);
                }
                isHideHeader = !isHideHeader;
            } else if (i == R.id.local_video_view) {
                changeLocalCameraSize();
            } else {
                RkLog.i(TAG, "ButtonListener Warning button not known");
            }
        }
    };

    /**
     * Change local camera view size
     */
    private void changeLocalCameraSize() {
        isShowBigCamera = !isShowBigCamera;
        showLocalCameraView();
        if (!isShowBigCamera) {
            mLocalCameraBigContainer.removeView(mLocalRender);
            mLocalCameraContainer.addView(mLocalRender);
        } else {
            mLocalCameraContainer.removeView(mLocalRender);
            mLocalCameraBigContainer.addView(mLocalRender);
        }
    }

    /**
     * Hide all local cameras view
     */
    protected void hideLocalCameraView() {
        mLocalRender.setVisibility(View.INVISIBLE);
        mSwitchCameraImage.setVisibility(View.INVISIBLE);
        mSwitchCameraBigImage.setVisibility(View.INVISIBLE);
        mLocalCameraLayout.setVisibility(View.INVISIBLE);
        mLocalCameraBigLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Re-show local camera
     */
    private void showLocalCameraView() {
        mLocalRender.setVisibility(View.VISIBLE);
        if (isShowBigCamera) {
            mSwitchCameraImage.setVisibility(View.INVISIBLE);
            mLocalCameraLayout.setVisibility(View.INVISIBLE);
            mSwitchCameraBigImage.setVisibility(View.VISIBLE);
            mLocalCameraBigLayout.setVisibility(View.VISIBLE);
        } else {
            mSwitchCameraImage.setVisibility(View.VISIBLE);
            mLocalCameraLayout.setVisibility(View.VISIBLE);
            mSwitchCameraBigImage.setVisibility(View.INVISIBLE);
            mLocalCameraBigLayout.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * ダイアログボタンリスナー
     *
     * @param pattern     pattern
     * @param clickAccept is accept dialog content
     */
    @Override
    public void onDialogClick(int pattern, boolean clickAccept) {
        switch (pattern) {
            /* ログアウト */
            case AlertDialogFragment.DIALOG_LOGOUT:
                if (clickAccept) {
                    doLogout();
                    RkLog.e(TAG, "Dialog click logout");
                }
                break;
            // FIXME
            case AlertDialogFragment.DIALOG_VIDEO_CALL_REQUEST:
                if (clickAccept) {
//                    doLogout1();
                    mWSClient.send(JettyUtils.createActionJsonString("changeStatus"));
                }
                break;
            /* 2shot申請 */
            case AlertDialogFragment.DIALOG_TWO_SHOT_REQUEST:
                if (clickAccept) {
                    requestTwoShot();
                }
                break;
            /* 2shotキャンセル */
            case AlertDialogFragment.DIALOG_TWO_SHOT_CANCEL:
                if (clickAccept) {
                    cancelTwoShot();
                }
                break;
            /* 2shot再申請 */
            case AlertDialogFragment.DIALOG_TWO_SHOT_RE_REQUEST:
                if (clickAccept) {
                    if (mChatStatus == CHAT_STATUS_TWO_SHOT_REQUEST) {
                        showToast(getString(R.string.toast_error_two_shot_request));
                    } else {
                        RkLog.d(TAG, "onDialogClick DIALOG_TWO_SHOT_RE_REQUEST no");
                        requestTwoShot();
                    }
                }
                break;
            case AlertDialogFragment.DIALOG_SOCKET_ERROR:
                if (clickAccept) {
                    doLogout();
                }
                break;
            case AlertDialogFragment.DIALOG_CAM_EATHOTHER:
                if (clickAccept) {
                    firstOnCamera = false;
                    unMuteCamera();
                    sendMemberCameraPointConsumption();
                }
                else {
                    SettingDialogFragment fragment = (SettingDialogFragment) getSupportFragmentManager().findFragmentByTag(SettingDialogFragment.TAG);
                    if(fragment != null && fragment.getSwVideo() != null) {
                        fragment.getSwVideo().setChecked(false);
                    }
                }
                break;
            default:
                RkLog.i(TAG, "onDialogClick Warning button not known");
        }
    }


    @Override
    public void onDialogFirstTimeClick(boolean clickAccept, boolean isDoNotShowAgain) {
        if (clickAccept) {
            if (mChatStatus == CHAT_STATUS_TWO_SHOT_REQUEST) {
                showToast(getString(R.string.toast_error_two_shot_request));
            } else {
                RkLog.d(TAG, "onDialogClick DIALOG_TWO_SHOT_RE_REQUEST no");
                requestTwoShot();
            }
        }

        RkSharedPreferencesUtils.getInstance().setNotShowDialogFirstTimeAgain(isDoNotShowAgain);
    }

    /**
     * BottomBarボタンリスナー
     *
     * @param buttonPattern button Pattern
     */
    @Override
    public void onBottomBarClick(int buttonPattern) {
        switch (buttonPattern) {
            /* チャット送信 */
            case BottomBarFragment.BUTTON_CHAT_SEND:
                send();
                break;
            /* 2shot申請 */
            case BottomBarFragment.BUTTON_TWO_SHOT_REQUEST:
                RkLog.d(TAG, "onBottomBarClick BUTTON_TWO_SHOT_REQUEST");
                if (mChatStatus == CHAT_STATUS_TWO_SHOT_REQUEST) {
                    showToast(getString(R.string.toast_error_two_shot_request));
                } else {
                    if(TextUtils.equals(mainMemberCode, mMemberCode)){
                        mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_TWO_SHOT_REQUEST);
                        mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_TWO_SHOT_REQUEST_NAME);
                    }
                    else{
                        noGoto2Shot();
                    }
                }
                break;
            /* マイクOFF */
            case BottomBarFragment.BUTTON_MIC_OFF:
                RkLog.d(TAG, "onBottomBarClick BUTTON_MIC_OFF");
                mPublishStream.muteAudio();
                break;
            /* マイクON */
            case BottomBarFragment.BUTTON_MIC_ON:
                RkLog.d(TAG, "onBottomBarClick BUTTON_MIC_ON");
                mPublishStream.unmuteAudio();
                break;
            /* カメラOFF */
            case BottomBarFragment.BUTTON_VIDEO_OFF:
                RkLog.d(TAG, "onBottomBarClick BUTTON_CAMERA_OFF");
                muteCamera();
                break;
            /* カメラON */
            case BottomBarFragment.BUTTON_VIDEO_ON:
                RkLog.d(TAG, "onBottomBarClick BUTTON_CAMERA_ON");
                if(!firstOnCamera) {
                    unMuteCamera();
                    break;
                }
                mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_CAM_EATHOTHER);
                mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_CAM_EATHOTHER_NAME);
                break;
            /* 2shot解除 */
            case BottomBarFragment.BUTTON_TWO_SHOT_CANCEL:
                mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_TWO_SHOT_CANCEL);
                mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_TWO_SHOT_CANCEL_NAME);
                break;
            case BottomBarFragment.BUTTON_VIDEO_CALL_REQUEST:
                mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_VIDEO_CALL_REQUEST);
                mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_VIDEO_CALL_REQUEST_NAME);
                break;
            default:
                RkLog.i(TAG, "onBottomBarClick Warning button not known");
        }
    }

    public void onSocketError(String errorMessage) {
        showToast(errorMessage);
    }

    public void onNetworkClose() {
        mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_SOCKET_ERROR);
        mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_SOCKET_ERROR_NAME);
    }

    /**
     * メッセージをToastで表示
     *
     * @param message Message to be showed on Toast
     */
    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (null != mDialogFragment && mDialogFragment.isAdded()) {
            mDialogFragment.dismiss();
        } else {
            mDialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.DIALOG_LOGOUT);
            mDialogFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_LOGOUT_NAME);
        }
    }

    public void muteMic() {
        if (mPublishStream != null && !mPublishStream.isAudioMuted()) {
            mPublishStream.muteAudio();
            isOnAudio = false;

            // Change bottom bar
            BottomBarFragment fragment = getBottomBarFragment();
            if (null != fragment) {
                fragment.changeMicroImage(false);
            }
        }
    }

    public void unMuteMic() {
        if (mPublishStream != null && mPublishStream.isAudioMuted()) {
            mPublishStream.unmuteAudio();
            isOnAudio = true;

            BottomBarFragment fragment = getBottomBarFragment();
            if (null != fragment) {
                fragment.changeMicroImage(true);

            }
        }
    }

    public void muteCamera() {
        if (mPublishStream != null && !mPublishStream.isVideoMuted()) {
            isShowCamera = false;
            mLocalRender.release();
            mPublishStream.muteVideo();
            hideLocalCameraView();
            BottomBarFragment fragment = getBottomBarFragment();
            if (null != fragment) {
                fragment.changeVideoImage(false);

            }
        }
    }

    public void unMuteCamera() {
        if (mPublishStream != null && mPublishStream.isVideoMuted()) {
            mPublishStream.unmuteVideo();
            isShowCamera = true;
            mLocalRender.init(Flashphoner.context, null);
            showLocalCameraView();
            BottomBarFragment fragment = getBottomBarFragment();
            if (null != fragment) {
                fragment.changeVideoImage(true);

            }
        }
    }

    protected void sendMemberCameraPointConsumption() {}

    private BottomBarFragment getBottomBarFragment() {
        return (BottomBarFragment) getFragmentManager().findFragmentById(R.id.bottomBarLayout);
    }

    /**
     * To be called when user click done in 2Shot setting
     *
     * @param isVideoOn Is Setting enable video
     */
    public void doChangeVideoSetting(final boolean isAudioOn, final boolean isVideoOn) {
        Log.d("testSetting1", "" + isAudioOn + "  " + isVideoOn);
        if(mMediaServerClient == null) return;
        isShowCamera = isVideoOn;
        isOnAudio = isAudioOn;
        if (mPublishStatus != PUBLISHED) {
            mPublishStream = mMediaServerClient.getPublishStream();
            createBottomBarFragment(BottomBarFragment.BOTTOM_LAYOUT_TWO_SHOT);
        }
        if (!isAudioOn) {
            muteMic();
        } else {
            unMuteMic();
        }
        if (!isVideoOn) {
            muteCamera();
        } else {
            unMuteCamera();
        }
    }

    public boolean isVideoMute() {
        return !isShowCamera;
//        return mPublishStream == null || mPublishStream.isVideoMuted();
    }

    public boolean isMicroMute() {
        return !isOnAudio;
//        return mPublishStream == null || mPublishStream.isAudioMuted();
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public boolean isLogout() {
        return isLogout;
    }

    public void setLogout(boolean logout) {
        isLogout = logout;
    }
}
