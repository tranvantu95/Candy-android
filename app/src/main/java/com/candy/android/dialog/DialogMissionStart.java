package com.candy.android.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.model.UncompletedMission;
import com.candy.android.utils.HimecasUtils;

/**
 * Dialog start mission
 * Created by brianhoang on 6/5/17.
 */

public class DialogMissionStart extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "DialogMissionStart";
    public static final String ARG_MISSION_TITLE = "mission_title";
    public static final String ARG_MISSION_MESSAGE = "mission_message";
    public static final String ARG_MISSION_HINT = "mission_hint";

    private String mMissionTitle;
    private String mMissionMessage;
    private String mMissionHint;


    public static DialogMissionStart newInstance(UncompletedMission mission) {
        DialogMissionStart dialog = new DialogMissionStart();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MISSION_TITLE, mission.getTitle());
        bundle.putString(ARG_MISSION_MESSAGE, mission.getDescription());
        bundle.putString(ARG_MISSION_HINT, mission.getMessage());
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMissionTitle = getArguments().getString(ARG_MISSION_TITLE);
            mMissionMessage = getArguments().getString(ARG_MISSION_MESSAGE);
            mMissionHint = getArguments().getString(ARG_MISSION_HINT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mission_start, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView currentView = (TextView) view.findViewById(R.id.mission_start_title);
        currentView.setText(mMissionTitle);
        currentView = (TextView) view.findViewById(R.id.mission_start_description);
        currentView.setText(mMissionMessage);
        currentView = (TextView) view.findViewById(R.id.mission_start_note);
        currentView.setText(HimecasUtils.convertFromHtml(Define.MissionTextFormat.MISSION_START_NOTE));
        currentView = (TextView) view.findViewById(R.id.mission_start_hint);
        currentView.setText(mMissionHint);

        /// OK button
        view.findViewById(R.id.btn_mission_start_ok).setOnClickListener(this);
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
    public void onClick(View v) {
        if (R.id.btn_mission_start_ok == v.getId()) {
            dismiss();
        }
    }
}
