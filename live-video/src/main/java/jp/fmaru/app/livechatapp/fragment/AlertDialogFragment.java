package jp.fmaru.app.livechatapp.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import jp.fmaru.app.livechatapp.R;

/**
 * ダイアログFragment
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnKeyListener {
    private static final String ARG_PATTERN = "dialog_pattern";

    @Override
    public boolean onKey(android.content.DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (mDialogPattern != DIALOG_TWO_SHOT_WAIT
                && mDialogPattern != DIALOG_SOCKET_ERROR
                && mDialogPattern != DIALOG_WAITING_CONNECTED )) {
            if (getActivity() != null && !getActivity().isDestroyed()) {
                getDialog().dismiss(); //Dialogを消す
            }
            return false;
        }
        return true;
    }

    public interface DialogInterface {
        void onDialogClick(int dialogPattern, boolean clickAccept);

        void onDialogFirstTimeClick(boolean clickAccept, boolean isDoNotShowAgain);
    }

    /**
     * ダイアログパターン
     */
    public static final int DIALOG_LOGOUT = 1;
    public static final int DIALOG_TWO_SHOT_REQUEST = 2;
    public static final int DIALOG_TWO_SHOT_WAIT = 3;
    public static final int DIALOG_TWO_SHOT_RE_REQUEST = 4;
    public static final int DIALOG_TWO_SHOT_CANCEL = 5;
    public static final int DIALOG_TWO_SHOT_FIRST_TIME = 6;
    public static final int DIALOG_SOCKET_ERROR = 7;
    public static final int DIALOG_WAITING_CONNECTED = 8;
    public static final int DIALOG_VIDEO_CALL_REQUEST = 9;
    public static final int DIALOG_CAM_EATHOTHER = 10;
    public static final int DIALOG_TWO_SHOT_NOT_CONNECTED = 11;
    public static final int DIALOG_NO_GOTO_2SHOT = 12;

    // Dialog name
    public static final String DIALOG_LOGOUT_NAME = "DIALOG_LOGOUT";
    public static final String DIALOG_TWO_SHOT_REQUEST_NAME = "DIALOG_TWO_SHOT_REQUEST";
    public static final String DIALOG_TWO_SHOT_FIRST_TIME_NAME = "DIALOG_TWO_SHOT_FIRST_TIME";
    public static final String DIALOG_TWO_SHOT_WAIT_NAME = "DIALOG_TWO_SHOT_WAIT";
    public static final String DIALOG_TWO_SHOT_RE_REQUEST_NAME = "DIALOG_TWO_SHOT_RE_REQUEST";
    public static final String DIALOG_TWO_SHOT_CANCEL_NAME = "DIALOG_TWO_SHOT_CANCEL";
    public static final String DIALOG_SETTING_FIRST_TIME_NAME = "DIALOG_SETTING_FIRST_TIME";
    public static final String DIALOG_SOCKET_ERROR_NAME = "DIALOG_SOCKET_ERROR";
    public static final String DIALOG_WAITING_CONNECTED_NAME = "DIALOG_WAITING_CONNECTED";
    public static final String DIALOG_VIDEO_CALL_REQUEST_NAME = "DIALOG_VIDEO_CALL_REQUEST";
    public static final String DIALOG_CAM_EATHOTHER_NAME = "DIALOG_CAM_EATHOTHER";
    public static final String DIALOG_TWO_SHOT_NOT_CONNECTED_NAME = "DIALOG_TWO_SHOT_NOT_CONNECTED";
    public static final String DIALOG_NO_GOTO_2SHOT_NAME = "DIALOG_NO_GOTO_2SHOT";

    private static final String TAG = AlertDialogFragment.class.getSimpleName();

    /**
     * ダイアログリスナーインターフェース
     */
    private DialogInterface mOnClickListener;

    private int mDialogPattern;
    private boolean isDoNotShowAgain = false;

    /**
     * パターンを引数としてインスタンス生成
     *
     * @param pattern Dialog pattern
     * @return fragment
     */
    public static AlertDialogFragment newInstance(int pattern) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATTERN, pattern);
        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateDialog");
        mOnClickListener = (DialogInterface) getActivity();

        Bundle args = getArguments();
        if (args.containsKey(ARG_PATTERN)) {
            mDialogPattern = args.getInt(ARG_PATTERN);
        }
        int layoutID;
        switch (mDialogPattern) {
             /* 2shot再申請 */
            case DIALOG_TWO_SHOT_RE_REQUEST:
            case DIALOG_TWO_SHOT_NOT_CONNECTED:
            case DIALOG_NO_GOTO_2SHOT:
                layoutID = R.layout.confirm_two_shot_re_request;
                break;
            case DIALOG_VIDEO_CALL_REQUEST:
            case DIALOG_LOGOUT:
                layoutID = R.layout.alert_confirm_layout;
                break;
            case DIALOG_SOCKET_ERROR:
                layoutID = R.layout.alert_notice_layout;
                break;
            /* 2shot申請 */
            case DIALOG_TWO_SHOT_REQUEST:
                layoutID = R.layout.alert_layout;
                break;
            /* 2shot解除 */
            case DIALOG_TWO_SHOT_CANCEL:
                layoutID = R.layout.confirm_exit_two_shot;
                break;
            /* 2shot返事待ち */
            case DIALOG_TWO_SHOT_WAIT:
                layoutID = R.layout.alert_loading_layout;
                break;
            case DIALOG_TWO_SHOT_FIRST_TIME:
                layoutID = R.layout.alert_first_time_layout;
                break;
            case DIALOG_WAITING_CONNECTED:
                layoutID = R.layout.dialog_loading_layout;
                break;
            case DIALOG_CAM_EATHOTHER:
                layoutID = R.layout.dialog_cam_eathother;
                break;
            default:
                Log.i(TAG, "onCreateDialog Dialog pattern not exist.");
                return null;
        }

        /* レイアウト,view設定 */
        View alertView = getActivity().getLayoutInflater().inflate(layoutID, null);
        TextView dialogText = (TextView) alertView.findViewById(R.id.dialogText);
        TextView dialogTexSub = (TextView) alertView.findViewById(R.id.dialogTextSub);
        //yes or no button
        View yesImage = alertView.findViewById(R.id.yesImage);
        View noImage = alertView.findViewById(R.id.noImage);
        if (null != yesImage) {
            yesImage.setOnClickListener(btnClick);
        }

        if (null != noImage) {
            noImage.setOnClickListener(btnClick);
        }

        CheckBox cbDoNotShowAgain = (CheckBox) alertView.findViewById(R.id.cb_remember);
        if (cbDoNotShowAgain != null) {
            cbDoNotShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isDoNotShowAgain = isChecked;
                }
            });
        }

        //プログレスバー

        //必要なもの追加でなく不要なものを削除
        switch (mDialogPattern) {
            /* ログアウト */
            case DIALOG_LOGOUT:
                dialogText.setText(getResources().getString(R.string.dialog_logout_text));
                break;
            /* 2shot申請 */
            case DIALOG_TWO_SHOT_REQUEST:
                dialogText.setText(getResources().getString(R.string.dialog_2shot_request_text));
                dialogTexSub.setText(getResources().getString(R.string.dialog_2shot_request_text_sub));
                break;
            /* 2shot返事待ち */
            case DIALOG_TWO_SHOT_WAIT:
                dialogText.setText(getResources().getString(R.string.dialog_2shot_wait_text));
                break;
            /* 2shot再申請 */
            case DIALOG_TWO_SHOT_RE_REQUEST:
                dialogText.setText(getResources().getString(R.string.dialog_2shot_re_request_text));
                break;
            case DIALOG_NO_GOTO_2SHOT:
                dialogText.setText(getResources().getString(R.string.dialog_no_goto_2shot_text));
                if(yesImage != null) ((TextView) yesImage).setText(getString(R.string.btn_ok));
                if(noImage != null) noImage.setVisibility(View.GONE);
                break;
            /* 2shot解除 */
            case DIALOG_TWO_SHOT_CANCEL:
                dialogText.setText(getResources().getString(R.string.dialog_2shot_cancel_text));
                dialogTexSub.setText(getResources().getString(R.string.dialog_2shot_cancel_text_sub));
                break;
            case DIALOG_SOCKET_ERROR:
                dialogText.setText(getResources().getString(R.string.toast_error_jetty_onerror));
                break;
            case DIALOG_TWO_SHOT_NOT_CONNECTED:
                dialogTexSub.setVisibility(View.VISIBLE);
                dialogText.setText(getResources().getString(R.string.dialog_2shot_not_connected_text));
                dialogTexSub.setText(getResources().getString(R.string.dialog_2shot_not_connected_text_sub));
                break;
            case DIALOG_CAM_EATHOTHER:
                dialogText.setText(getResources().getString(R.string.dialog_cam_eathother_text));
                dialogTexSub.setText(getResources().getString(R.string.dialog_cam_eathother_text_sub));
                break;
            case DIALOG_VIDEO_CALL_REQUEST:
                dialogText.setText(getResources().getString(R.string.dialog_call_video_text));
                break;
            default:
                Log.i(TAG, "onCreateDialog Dialog pattern not exist.");
        }

        /* ダイアログ組み立て,表示 */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Custom AlertDialog");
        builder.setView(alertView);
        //戻るボタン無効
        setCancelable(false);
        AlertDialog dialog = builder.create();
        //ダイアログタイトル非表示
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //ダイアログ外タップで消えないように設定
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
//        if (mDialogPattern == DIALOG_WAITING_CONNECTED) {
//            if (dialog.getWindow() != null) {
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            }
//        }
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        }

        dialog.setOnKeyListener(this);
        return dialog;
    }

    /**
     * ボタンリスナー設定クラス
     */
    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            // Dialog first time
            if (mDialogPattern == DIALOG_TWO_SHOT_FIRST_TIME) {
                if (i == R.id.yesImage) {
                    Log.i(TAG, "yesImage clicked");
                    mOnClickListener.onDialogFirstTimeClick(true, isDoNotShowAgain);
                    if (getActivity() != null && !getActivity().isDestroyed()) {
                        getDialog().dismiss(); //Dialogを消す
                    }

                /* no button */
                } else if (i == R.id.noImage) {
                    Log.i(TAG, "noImage clicked");
                    mOnClickListener.onDialogFirstTimeClick(false, isDoNotShowAgain);
                    if (getActivity() != null && !getActivity().isDestroyed()) {
                        getDialog().dismiss(); //Dialogを消す
                    }

                } else {
                    Log.i(TAG, "ButtonListener Warning button not known");
                }
            } else {

                if (i == R.id.yesImage) {
                    Log.i(TAG, "yesImage clicked");
                    mOnClickListener.onDialogClick(mDialogPattern, true);
                    if (getActivity() != null && !getActivity().isDestroyed()) {
                        getDialog().dismiss(); //Dialogを消す
                    }

                /* no button */
                } else if (i == R.id.noImage) {
                    Log.i(TAG, "noImage clicked");
                    mOnClickListener.onDialogClick(mDialogPattern, false);
                    if (getActivity() != null && !getActivity().isDestroyed()) {
                        getDialog().dismiss(); //Dialogを消す
                    }

                } else {
                    Log.i(TAG, "ButtonListener Warning button not known");
                }
            }
        }
    };
}