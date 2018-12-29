package com.candy.android.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.fragment.menu.ListMissionFragment;
import com.candy.android.model.Mission;
import com.candy.android.utils.HimecasUtils;

import java.sql.Array;
import java.util.Locale;

/**
 * DIalog mission clear
 * Created by brianhoang on 6/5/17.
 */

public class DialogMissionClear extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "DialogMissionStart";
    public static final String ARG_MISSION_ID = "mission_id";
    public static final String ARG_MISSION_POINT = "mission_point";
    public static final String ARG_MISSION_TITLE = "mission_title";

    private int mMissionID;
    private String mMissionTitle;
    private int mMissionPoint;

    private ListMissionFragment.OnMissionDialogClearClick mOnMissionDialogClearClick;


    public static DialogMissionClear newInstance(Mission mission) {
        DialogMissionClear dialog = new DialogMissionClear();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MISSION_ID, mission.getId());
        bundle.putInt(ARG_MISSION_POINT, mission.getPoint());
        bundle.putString(ARG_MISSION_TITLE, mission.getTitle());
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMissionID = getArguments().getInt(ARG_MISSION_ID);
            mMissionPoint = getArguments().getInt(ARG_MISSION_POINT);
            mMissionTitle = getArguments().getString(ARG_MISSION_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mission_clear, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (ViewGroup.LayoutParams.MATCH_PARENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.bg_campaign_color)));
            } else {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(R.color.bg_campaign_color)));
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvMissionTitle = (TextView) view.findViewById(R.id.dialog_mission_clear_title);
        tvMissionTitle.setText(mMissionTitle);
        TextView tvMissionReward1 = (TextView) view.findViewById(R.id.dialog_mission_clear_reward_1);
        TextView tvMissionReward2 = (TextView) view.findViewById(R.id.dialog_mission_clear_reward_2);
        TextView tvMissionReward3 = (TextView) view.findViewById(R.id.dialog_mission_clear_reward_3);
        String rewardHtml = String.format(Locale.getDefault(), Define.MissionTextFormat.MISSION_REWARD_FORMAT, mMissionPoint);
        Spanned spannable = HimecasUtils.convertFromHtml(rewardHtml);
        String[] missionReward = spannable.toString().split(" ");
//        tvMissionReward.setText(spannable);
        tvMissionReward1.setText(missionReward[0]);
        tvMissionReward2.setText(missionReward[1]);
        tvMissionReward3.setText(missionReward[2]);

        // Buttons
        view.findViewById(R.id.btnOK).setOnClickListener(this);
        view.findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                if (mOnMissionDialogClearClick != null) {
                    mOnMissionDialogClearClick.onButtonOkClick(mMissionID);
                }
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }


    public void setOnMissionDialogClearClick(ListMissionFragment.OnMissionDialogClearClick mOnMissionDialogClearClick) {
        this.mOnMissionDialogClearClick = mOnMissionDialogClearClick;
    }

}
