package com.candy.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.custom.views.MissionButton;
import com.candy.android.custom.views.MissionProgress;
import com.candy.android.fragment.menu.ListMissionFragment;
import com.candy.android.model.CompletedMission;
import com.candy.android.model.Mission;
import com.candy.android.model.UncompletedMission;

import java.util.List;

/**
 * Mission adapter
 * Created by brianhoang on 6/2/17.
 */

public class MissionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Mission> mMissionList;
    private ListMissionFragment.OnMissionButtonClick mOnMissionClick;


    public MissionAdapter(List<Mission> missionList, ListMissionFragment.OnMissionButtonClick onMissionButtonClick) {
        this.mMissionList = missionList;
        mOnMissionClick = onMissionButtonClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(itemView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission_completed, parent, false);
            return new MissionItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MissionItemViewHolder) {
            Mission mission = mMissionList.get(position);
            ((MissionItemViewHolder) holder).bindData(mission);
        } else {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
            loadingHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return null == mMissionList ? 0 : mMissionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMissionList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadmore_progress);
        }
    }

    private class MissionItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        MissionProgress missionProgress;
        MissionButton missionButton;

        MissionItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_mission_title);
            missionProgress = itemView.findViewById(R.id.mission_progress);
            missionButton = itemView.findViewById(R.id.btn_mission_action);
        }

        void bindData(final Mission mission) {
            tvTitle.setText(mission.getTitle());
            int point = mission.getPoint();
            int color = itemView.getResources().getColor(R.color.mission_title_color);
            tvTitle.setTextColor(color);
            if (mission instanceof UncompletedMission) {
                boolean isCompleted = ((UncompletedMission) mission).isCompleted();
                missionButton.setData(isCompleted, false, point);
                missionProgress.setData(mission);
                missionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnMissionClick.onButtonClick((UncompletedMission) mission);
                    }
                });
            } else if (mission instanceof CompletedMission) {
                missionButton.setData(true, true, point);
                missionProgress.setData(mission);
            }
        }
    }
}
