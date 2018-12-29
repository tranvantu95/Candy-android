package com.candy.android.custom.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.model.CompletedMission;
import com.candy.android.model.Mission;
import com.candy.android.model.UncompletedMission;


/**
 * Mission progress
 * Created by brianhoang on 6/2/17.
 */

public class MissionProgress extends LinearLayout {

    private View mViewDivider;
    private TextView mTvTitle;
    private View mViewProgress;
    public static final String CLEAR = "CLEAR";
    public static final String EXCLAMATION_MARK = "!!";

    public MissionProgress(Context context) {
        super(context);
        initLayout(context);
    }

    public MissionProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public MissionProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MissionProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_mission_progress, this, false);
        mTvTitle = (TextView) view.findViewById(R.id.tv_progress);
        mViewProgress = view.findViewById(R.id.v_progress);
        //mViewDivider = view.findViewById(R.id.view_progress_divider);
        addView(view);
    }

    public void setData(Mission mission) {
        mTvTitle.setText(mission.getProgress());
        if (mission instanceof CompletedMission) {
            //SET Progress title
            if (mission.getProgress().equals(CLEAR)) {
                String tmpTitle = mission.getProgress() + EXCLAMATION_MARK;
                mTvTitle.setText(tmpTitle);
            }
            //mViewDivider.setBackgroundResource(R.color.gradient_color_green_start);
            mViewProgress.setBackgroundResource(R.drawable.bg_mission_progress_done);
            LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 100);
            mViewProgress.setLayoutParams(layoutParams);
        } else if (mission instanceof UncompletedMission) {
            UncompletedMission uncompletedMission = (UncompletedMission) mission;
            //SET Progress title
            if (mission.getProgress().equals(CLEAR)) {
                String tmpTitle = mission.getProgress() + EXCLAMATION_MARK;
                mTvTitle.setText(tmpTitle);
            }
            // SET SIZE
            if (uncompletedMission.isCompleted()) {
               // mViewDivider.setBackgroundResource(R.drawable.bg_mission_progress_done);
                mViewProgress.setBackgroundResource(R.drawable.bg_mission_progress_done);
                LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 100);
                mViewProgress.setLayoutParams(layoutParams);
            } else {
                float progressPercent = ((UncompletedMission) mission).getProgressPercent();
                //mViewDivider.setBackgroundResource(R.drawable.bg_mission_inprogress);
                mViewProgress.setBackgroundResource(R.drawable.bg_mission_inprogress);
                Log.e(VIEW_LOG_TAG, "Percent: " + progressPercent);
                float viewPercent = progressPercent > 1 ? 1 : progressPercent;
                LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, viewPercent * 100);
                mViewProgress.setLayoutParams(layoutParams);
            }
        }
    }
}
