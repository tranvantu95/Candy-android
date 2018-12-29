package jp.fmaru.app.livechatapp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import jp.fmaru.app.livechatapp.R;


/**
 * BottomBarFragment
 */
public class BottomBarFragment extends Fragment {
    public interface BottomBarInterface {
        //ボタン押された時のリスナー
        void onBottomBarClick(int buttonPattern);
    }

    private static final String ARG_PATTERN = "pattern";
    private static final String ARG_ON_CAMERA = "is_on_camera";
    private static final String ARG_ON_MICRO = "is_on_micro";

    /**
     * bottomBarレイアウトパターン
     * チャット送信レイアウトは各ボタンの表示／非表示で対応
     * enableChatSendButton / disableChatSendButton
     * BottomBarFragment生成時にもdisableChatSendButton必要
     */
    public static final int BOTTOM_LAYOUT_CHAT = 0;
    public static final int BOTTOM_LAYOUT_TWO_SHOT = 1;
    public static final int BOTTOM_LAYOUT_PEEP = 2;

    /**
     * buttonPattern
     * ChatActivityのリスナーにボタン種別を渡すために使用
     */
    public static final int BUTTON_TWO_SHOT_REQUEST = 1;
    public static final int BUTTON_VIDEO_CALL_REQUEST = 8;
    public static final int BUTTON_CHAT_SEND = 2;
    public static final int BUTTON_TWO_SHOT_CANCEL = 3;
    public static final int BUTTON_MIC_OFF = 4;
    public static final int BUTTON_MIC_ON = 5;
    public static final int BUTTON_VIDEO_OFF = 6;
    public static final int BUTTON_VIDEO_ON = 7;

    private static final String TAG = BottomBarFragment.class.getSimpleName();

    /** リスナーインターフェース */
    private BottomBarInterface mBottomBarListener;

    private Context mContext;

    private int mLayoutPattern;
    //標準アプリでは2shot開始時にカメラ／マイクはONのみ
    private boolean mIsMutedVideo = false;
    private boolean mIsMutedMic = false;

    private EditText mEditChatText;
    private View btnSendMessage;
    private View mTwoShotRequestImage;
    private View twoShotControlLayout;
    private View callRequestImage;
    private ImageView mMicImage;
    private ImageView mVideoImage;
    private ImageView mTwoShotCancelImage;

    public BottomBarFragment() {
        // Required empty public constructor
    }

    /**
     * パターンを引数としてインスタンス生成
     *
     * @param pattern
     * @return fragment
     */
    public static BottomBarFragment newInstance(int pattern, boolean isMutedVideo, boolean isMutedMic) {
        //Log.d(TAG, " BottomBarFragment newInstance:"+pattern);
        BottomBarFragment fragment = new BottomBarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATTERN, pattern);
        args.putBoolean(ARG_ON_CAMERA, isMutedVideo);
        args.putBoolean(ARG_ON_MICRO, isMutedMic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, " BottomBarFragment onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            //ChatActivity起動時
            mLayoutPattern = BOTTOM_LAYOUT_CHAT;
        } else {
            Bundle bundle = getArguments();
            mLayoutPattern = bundle.getInt(ARG_PATTERN);
            mIsMutedVideo = bundle.getBoolean(ARG_ON_CAMERA);
            mIsMutedMic = bundle.getBoolean(ARG_ON_MICRO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Log.d(TAG, "mLayoutPattern:" + mLayoutPattern + " / mIsMutedVideo:" + mIsMutedVideo + " / mIsMutedMic:" + mIsMutedMic);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return inflater.inflate(R.layout.bottom_bar_chat_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d(TAG, "onViewCreated");
        /** ボタンリスナー設定 */
        mEditChatText = (EditText) view.findViewById(R.id.editChatText);
        btnSendMessage = view.findViewById(R.id.chatSendImage);
        btnSendMessage.setOnClickListener(new ButtonListener());

        mEditChatText.post(new Runnable() { // #fix bug not show keyboard in nokia N5
            @Override
            public void run() {
                mEditChatText.clearFocus();
            }
        });

        switch (mLayoutPattern) {
            /** チャット */
            case BOTTOM_LAYOUT_CHAT:
                mTwoShotRequestImage = view.findViewById(R.id.twoShotRequestImage);
                mTwoShotRequestImage.setOnClickListener(new ButtonListener());
                disableChatSendButton();
                break;

            /** 2shot中 */
            case BOTTOM_LAYOUT_TWO_SHOT:
                btnSendMessage.setBackgroundResource(R.drawable.bg_btn_2shot_indigo);
                twoShotControlLayout = view.findViewById(R.id.two_shot_control_layout);
                mTwoShotCancelImage = (ImageView) view.findViewById(R.id.twoShoCancelImage);
                mTwoShotCancelImage.setOnClickListener(new ButtonListener());
                mMicImage = (ImageView) view.findViewById(R.id.micImage);
                mMicImage.setOnClickListener(new ButtonListener());
                mVideoImage = (ImageView) view.findViewById(R.id.videoImage);
                mVideoImage.setOnClickListener(new ButtonListener());
                disableChatSendButton();

                // Initialize micro and camera status
//                Log.d("testSetting3", "" + (!mIsMutedMic) + "  " + (!mIsMutedVideo));
                changeMicroImage(!mIsMutedMic);
                changeVideoImage(!mIsMutedVideo);

                break;

            case BOTTOM_LAYOUT_PEEP:
                mEditChatText.setHint(R.string.edit_chat_text_hint_peep);
                callRequestImage = view.findViewById(R.id.callRequestImage);
                callRequestImage.setOnClickListener(new ButtonListener());
                disableChatSendButton();
                break;

            default:
                Log.i(TAG, "onViewCreated Warning pattern not known");
        }

        /** チャット文字入力リスナー設定 */
        mEditChatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Log.d(TAG, String.format("onTextChanged s = %s start = %s before = %s count = %s", charSequence, start, before, count));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                //Log.d(TAG, String.format("onTextChanged s = %s start = %s count = %s after = %s", charSequence, start, count, after));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d(TAG, String.format("afterTextChanged s = %s", editable));
                //チャット送信ボタン非表示、かつ入力フォームが空でない時にチャット送信ボタンを表示する
                if (btnSendMessage.getVisibility() != View.VISIBLE && !editable.toString().equals("")) {
                    enableChatSendButton();
                    return;
                }
                //チャット送信ボタン表示、かつ入力フォームが空の時にチャット送信ボタンを非表示にする
                if (btnSendMessage.getVisibility() == View.VISIBLE && editable.toString().equals("")) {
                    //Log.d(TAG, "afterTextChanged s = null");
                    disableChatSendButton();
                }
            }
        });
    }

    public void changeMicroImage(boolean isOn) {
        if (null == mMicImage) {
            return;
        }
        if (isOn) {
            mMicImage.setImageResource(R.drawable.ico_mic_on);
        } else {
            mMicImage.setImageResource(R.drawable.ico_mic_off_black);
        }
    }

    public void changeVideoImage(boolean isOn) {
        mIsMutedVideo = !isOn;
        if (null == mVideoImage) {
            return;
        }
        if (isOn) {
            mVideoImage.setImageResource(R.drawable.ico_video_on);
        } else {
            mVideoImage.setImageResource(R.drawable.ico_video_off_black);
        }
    }

    /**
     * android6以上しか呼ばれない
     * @param context
    @Override
    */
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach context");
        super.onAttach(context);
        onAttachContext(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach activity");
        //android6以上はonAttach(Activity activity)で実行
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return;
        }
        onAttachContext(activity);
    }

    private void onAttachContext(Context context) {
        mContext = context;
        if (context instanceof BottomBarInterface) {
            Log.d(TAG, "onAttachContext context");
            mBottomBarListener = (BottomBarInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BottomBarInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.d(TAG, "onDetach");
        mBottomBarListener = null;
    }

    /**
     * チャット送信ボタン表示、その他のボタン非表示
     */
    public void enableChatSendButton() {
        //Log.d(TAG, "BottomBarFragment changeBottomBarButton enableChatSendButton");
        /** チャット送信ボタン非表示→表示 */
        btnSendMessage.setVisibility(View.VISIBLE);
        switch (mLayoutPattern) {
            /** チャット */
            case BOTTOM_LAYOUT_CHAT:
                mTwoShotRequestImage.setVisibility(View.GONE);
                break;
            /** 2shot中 */
            case BOTTOM_LAYOUT_TWO_SHOT:
                twoShotControlLayout.setVisibility(View.GONE);
                break;
            case BOTTOM_LAYOUT_PEEP:
                callRequestImage.setVisibility(View.GONE);
                break;
        }
        mEditChatText.requestFocus();
    }


    /**
     * チャット送信ボタン非表示、その他のボタン表示
     */
    public void disableChatSendButton() {
        //Log.d(TAG, "BottomBarFragment changeBottomBarButton disableChatSendButton");
        /** チャット送信ボタン表示→非表示 */
        btnSendMessage.setVisibility(View.GONE);
        switch (mLayoutPattern) {
            /** チャット */
            case BOTTOM_LAYOUT_CHAT:
                mTwoShotRequestImage.setVisibility(View.VISIBLE);
                break;
            /** 2shot中 */
            case BOTTOM_LAYOUT_TWO_SHOT:
                twoShotControlLayout.setVisibility(View.VISIBLE);
                break;
            case BOTTOM_LAYOUT_PEEP:
                callRequestImage.setVisibility(View.VISIBLE);
                break;
        }
        mEditChatText.requestFocus();
    }

    /**
     * ボタンリスナー設定クラス
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.chatSendImage) {//Log.d(TAG, "BottomBarFragment chatSendImage click");
                if (mBottomBarListener != null) {
                    mBottomBarListener.onBottomBarClick(BUTTON_CHAT_SEND);
                    //入力文字クリア
                    mEditChatText.getEditableText().clear();
                    InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                /** 2shot申請 */
            } else if (i == R.id.twoShotRequestImage) {//Log.d(TAG, "BottomBarFragment twoShotRequestImage click");
                if (mBottomBarListener != null) {
                    mBottomBarListener.onBottomBarClick(BUTTON_TWO_SHOT_REQUEST);
                }

                /** マイクON/OFF */
            } else if (i == R.id.callRequestImage) {
                if (mBottomBarListener != null) {
                    mBottomBarListener.onBottomBarClick(BUTTON_VIDEO_CALL_REQUEST);
                }
            } else if (i == R.id.micImage) {//Log.d(TAG, "micImage clicked");
                if (mBottomBarListener != null) {
                    if (mIsMutedMic) {
                        //Log.d(TAG, "mic off -> on");
                        mBottomBarListener.onBottomBarClick(BUTTON_MIC_ON);
                        mIsMutedMic = false;
                        mMicImage.setImageResource(R.drawable.ico_mic_on);
                    } else {
                        //Log.d(TAG, "mic on -> off");
                        mBottomBarListener.onBottomBarClick(BUTTON_MIC_OFF);
                        mIsMutedMic = true;
                        mMicImage.setImageResource(R.drawable.ico_mic_off_black);
                    }
                }

                /** カメラON/OFF */
            } else if (i == R.id.videoImage) {//Log.d(TAG, "videoImage clicked ");
                if (mBottomBarListener != null) {
                    if (mIsMutedVideo) {
                        //Log.d(TAG, "video off -> on");
                        mBottomBarListener.onBottomBarClick(BUTTON_VIDEO_ON);
//                        mIsMutedVideo = false;
//                        mVideoImage.setImageResource(R.drawable.ico_video_on);
                    } else {
                        //Log.d(TAG, "video on -> off");
                        mBottomBarListener.onBottomBarClick(BUTTON_VIDEO_OFF);
                        mIsMutedVideo = true;
                        mVideoImage.setImageResource(R.drawable.ico_video_off_black);
                    }
                }

                /** 2shot解除 */
            } else if (i == R.id.twoShoCancelImage) {//Log.d(TAG,"twoShoCancelImage clicked");
                if (mBottomBarListener != null) {
                    mBottomBarListener.onBottomBarClick(BUTTON_TWO_SHOT_CANCEL);
                }

            } else {
                Log.i(TAG, "ButtonListener Warning button not known");
            }
        }
    }
}

