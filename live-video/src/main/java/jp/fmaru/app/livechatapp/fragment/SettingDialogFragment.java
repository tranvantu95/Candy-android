package jp.fmaru.app.livechatapp.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import jp.fmaru.app.livechatapp.ChatActivity;
import jp.fmaru.app.livechatapp.R;

/**
 * Created by quannt on 21/03/2017.
 * Des: Setting dialog
 */

public class SettingDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = SettingDialogFragment.class.getSimpleName();
    private static final String EXTRAS_MODE = "mode";
    private static final String EXTRAS_FIRST_TIME = "first_time";
    private static final int MIN_VOICE_VOLUME = 1;

    private Context mContext;
    private ChatActivity mChatActivity;

    private ImageView mIvSound;
    private SeekBar mSbSound;
    private TextView mTvCompleteSetting;
    private CheckBox mSwVideo, mSwMicro;
    private AudioManager mAudioManager;

    private SettingsContentObserver mSettingsContentObserver;
    private VoiceIntentReceiver mVoiceIntentReceiver;

    private int mSoundMax;
    private boolean isOffSound, isTwoShotMode, isTwoShotFirstTime;

    public static SettingDialogFragment newInstance(boolean isTwoShotMode) {

        Bundle args = new Bundle();
        args.putBoolean(EXTRAS_MODE, isTwoShotMode);
        SettingDialogFragment fragment = new SettingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SettingDialogFragment new2SHOTFirstTimeInstance() {
        Bundle args = new Bundle();
        args.putBoolean(EXTRAS_FIRST_TIME, true);
        SettingDialogFragment fragment = new SettingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (mContext instanceof ChatActivity) {
            mChatActivity = (ChatActivity) mContext;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            isTwoShotFirstTime = args.getBoolean(EXTRAS_FIRST_TIME, false);
            isTwoShotMode = isTwoShotFirstTime || args.getBoolean(EXTRAS_MODE, false);
        }
        mSettingsContentObserver = new SettingsContentObserver( new Handler() );
        mContext.getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver );

        mVoiceIntentReceiver = new VoiceIntentReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mContext.registerReceiver(mVoiceIntentReceiver, filter);

        mChatActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        try {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_first_setting_dialog, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rooView) {
        mIvSound = (ImageView) rooView.findViewById(R.id.ic_sound);
        mSbSound = (SeekBar) rooView.findViewById(R.id.sb_sound_volume);
        mSbSound.getThumb().setColorFilter(getResources().getColor(R.color.seekbar_tint_color), PorterDuff.Mode.SRC_IN);
        mAudioManager = (AudioManager) mChatActivity.getSystemService(Context.AUDIO_SERVICE);
        mSoundMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        mSbSound.setMax(mSoundMax);

        int soundVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        Log.d("Check sound volume >> ", "soundVolume:" + soundVolume);
        mSbSound.setProgress(soundVolume);
        if(soundVolume == 0) {
            isOffSound = true;
            mIvSound.setImageResource(R.drawable.ic_volume_off);
        } else {
            isOffSound = false;
            mIvSound.setImageResource(R.drawable.ic_sound_volume);
        }

        mSbSound.setOnSeekBarChangeListener(mOnSoundChangeListener);

        mSwVideo = (CheckBox) rooView.findViewById(R.id.sw_video);
        mSwVideo.setOnClickListener(mOnCameraCheckChangeListener);
        if (null != mSwVideo && mChatActivity != null) {
            mSwVideo.setChecked(!mChatActivity.isVideoMute());
        }

        mSwMicro = (CheckBox) rooView.findViewById(R.id.sw_microphone);
        mSwMicro.setOnClickListener(mOnMicroCheckChangeListener);
        if (null != mSwMicro && mChatActivity != null) {
            mSwMicro.setChecked(!mChatActivity.isMicroMute());
        }

        mTvCompleteSetting = (TextView) rooView.findViewById(R.id.tv_setting_complete);
        if (null != mTvCompleteSetting) {
            mTvCompleteSetting.setOnClickListener(this);
        }

        View tvMicro2Shot = rooView.findViewById(R.id.tv_micro_two_shot_only);
        View tvCamera2Shot = rooView.findViewById(R.id.tv_camera_two_shot_only);
        if (isTwoShotMode) {
            tvMicro2Shot.setVisibility(View.GONE);
            tvCamera2Shot.setVisibility(View.GONE);
            mSwMicro.setEnabled(true);
            mSwVideo.setEnabled(true);
        } else {
            tvMicro2Shot.setVisibility(View.VISIBLE);
            tvCamera2Shot.setVisibility(View.VISIBLE);
            mSwMicro.setEnabled(false);
            mSwVideo.setEnabled(false);
        }

        // Initialize for first time setting dialog
        if (isTwoShotFirstTime) {
            mTvCompleteSetting.setVisibility(View.VISIBLE);
            mSwMicro.setChecked(true);
            mSwVideo.setChecked(false);
        } else {
            mTvCompleteSetting.setVisibility(View.GONE);
            mSwVideo.setChecked(!mChatActivity.isVideoMute());
            mSwMicro.setChecked(!mChatActivity.isMicroMute());
        }
    }

    View.OnClickListener mOnMicroCheckChangeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != mChatActivity) {
                if (mSwMicro.isChecked()) {
                    mChatActivity.unMuteMic();
                } else {
                    mChatActivity.muteMic();
                }
            }
        }
    };

    View.OnClickListener mOnCameraCheckChangeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (null != mChatActivity) {
                if (mSwVideo.isChecked()) {
//                    mChatActivity.unMuteCamera();
                    mChatActivity.onBottomBarClick(BottomBarFragment.BUTTON_VIDEO_ON);
                } else {
                    mChatActivity.muteCamera();
                }
            }
        }
    };

    SeekBar.OnSeekBarChangeListener mOnSoundChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            if (progress < MIN_VOICE_VOLUME) {
                seekBar.setProgress(MIN_VOICE_VOLUME);
                return;
            }
//            mSbSound.getProgressDrawable().setColorFilter(Color.parseColor(getColorSeekbar(progress, mSoundMax)), PorterDuff.Mode.MULTIPLY);
            if(progress == 0) {
                if(!isOffSound) {
                    mIvSound.setImageResource(R.drawable.ic_volume_off);
                }
                isOffSound = true;
            } else {
                if (isOffSound) {
                    mIvSound.setImageResource(R.drawable.ic_sound_volume);
                }
                isOffSound = false;
            }
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public CheckBox getSwVideo() {
        return mSwVideo;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isTwoShotFirstTime) {
            mChatActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChatActivity.doChangeVideoSetting(mSwMicro.isChecked(), mSwVideo.isChecked());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_setting_complete) {
            getDialog().dismiss();
        }
    }

    /**
     * Red:     50  - 255 - 255
     * Green:   170 - 240 - 0
     * Blue:    0   - 0   - 0
     */
    private String getColorSeekbar(int progress, int maxValue) {
        int r, g;
        if (progress <= maxValue / 2.0) {
            r = (int) (50 + (255 - 50) * progress / (float) maxValue);
            g = (int) (170 + (240 - 170) * progress / (float) maxValue);
        } else {
            r = 255;
            g = (int) (240 - 240 * progress / (float) maxValue);
        }
        return String.format("#%02X%02X00", r, g);
    }

    public class SettingsContentObserver extends ContentObserver {
        AudioManager audioManager;
        public SettingsContentObserver(Handler handler) {
            super(handler);
            audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(mSbSound != null) {
                mSbSound.setProgress(currentVolume);
            }
        }
    }

    private class VoiceIntentReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                    case 1:
                        int soundVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                        mSbSound.setProgress(soundVolume);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
        mContext.unregisterReceiver(mVoiceIntentReceiver);
    }
}
