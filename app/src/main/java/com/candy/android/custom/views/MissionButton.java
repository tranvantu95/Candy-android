package com.candy.android.custom.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.candy.android.R;

import java.util.Locale;

/**
 * Created by brianhoang on 6/1/17.
 */

public class MissionButton extends RelativeLayout {

    private LinearLayout mLnButtonLayout;
    private TextView mTvTitle;
    private TextView mTvPoint;
    private ImageView mIvMissionClear;

    public MissionButton(Context context) {
        super(context);
        initLayout(context);
    }

    public MissionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public MissionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MissionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_mission_button, this, false);
        mLnButtonLayout = view.findViewById(R.id.ln_button_layout);
        mTvTitle = view.findViewById(R.id.tv_mission_status_title);
        mTvPoint = view.findViewById(R.id.tv_mission_point);
        mIvMissionClear = view.findViewById(R.id.iv_mission_complete);
        addView(view);
    }

    public void setData(boolean isCompleted, boolean isGotPoint, int point) {

        mTvPoint.setText(String.format(Locale.JAPAN, getContext().getString(R.string.action_bar_n_pts)+"pts", point));
        if (isCompleted) {
            mTvTitle.setText(R.string.mission_receive_point);
            mLnButtonLayout.setBackgroundResource(R.drawable.btn_mission_completed_background);
            mTvPoint.setBackgroundResource(R.drawable.complete_mission_point_background);
            mIvMissionClear.setVisibility(isGotPoint? VISIBLE : GONE);
        } else {
            mTvTitle.setText(R.string.mission_show_detail);
            mLnButtonLayout.setBackgroundResource(R.drawable.btn_mission_uncompleted_background);
            mTvPoint.setBackgroundResource(R.drawable.bg_btn_mission_button_progress_2);
            mIvMissionClear.setVisibility(GONE);
        }
    }
}
