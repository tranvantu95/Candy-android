package com.candy.android.fragment.menu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candy.android.R;
import com.candy.android.adapter.MissionAdapter;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.DialogMissionClear;
import com.candy.android.dialog.DialogMissionStart;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.input.MemberInformation;
import com.candy.android.http.response.ApiRewardMissionResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.Mission;
import com.candy.android.model.UncompletedMission;
import com.candy.android.model.eventBus.UpdateMissionInfoEvent;
import com.candy.android.utils.Helpers;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Mission
 */
public abstract class ListMissionFragment<T extends Mission> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "ListMissionFragment";

    private static final int VISIBLE_THRESHOLD = 1;
    private static final int MISSION_PAGE_LIMIT = 6;

    protected TextView mTvNoContent;
    protected SwipeRefreshLayout mSwipeUpdateData;
    protected RecyclerView mRcMission;
    protected MissionAdapter mMissionAdapter;

    protected LinearLayoutManager mRcMissionLayoutManager;
    protected int mPage;


    public List<Mission> mMissionList;
    protected int mTotalMissionCount;
    private boolean isLoadingMore = false;

    public ListMissionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_mission, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvNoContent = (TextView) view.findViewById(R.id.tv_no_mission);
        mSwipeUpdateData = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe_layout);
        mSwipeUpdateData.setOnRefreshListener(this);
        mRcMission = (RecyclerView) view.findViewById(R.id.rc_list_mission);
        mMissionList = new ArrayList<>();
        mMissionAdapter = new MissionAdapter(mMissionList, onMissionButtonClick);

        mRcMissionLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRcMission.setLayoutManager(mRcMissionLayoutManager);
        mRcMission.setAdapter(mMissionAdapter);
        mRcMission.addOnScrollListener(mRcScrollListener);

        // Load first data
        loadFirstData();
    }

    public void loadFirstData() {
        // Load data
        mSwipeUpdateData.setRefreshing(true);
        mPage = 0;
        loadData();
    }

    public abstract void loadData();

    @Override
    public void onRefresh() {
        mPage = 0;
        loadData();
    }

    public void onLoadMore() {
        // Add loadmore view
        mMissionList.add(null);
        mMissionAdapter.notifyItemInserted(mMissionList.size() - 1);

        // load data
        loadMore();
    }

    public void loadMore() {
        mPage++;
        loadData();
    }


    public void handleResponseData(List<T> missions) {
        turnOfLoadingView();
        if (0 == mPage) {
            mMissionList.clear();
        }
        for (int i = 0; i < missions.size(); i++) {
            Mission mission = missions.get(i);
            if (!Define.MissionTextFormat.BLOCKED_MISSION_NAME.equals(mission.getTitle())) {
                mMissionList.add(mission);
            }
        }
        mMissionAdapter.notifyDataSetChanged();

        // Show hide no content
        if (mMissionList == null || mMissionList.isEmpty()) {
            mTvNoContent.setVisibility(View.VISIBLE);
            mRcMission.setVisibility(View.GONE);
        } else {
            mRcMission.setVisibility(View.VISIBLE);
            mTvNoContent.setVisibility(View.GONE);
        }

    }

    public void handleError() {
        mPage--;
        turnOfLoadingView();
    }

    private void turnOfLoadingView() {
        if (mSwipeUpdateData.isRefreshing()) {
            mSwipeUpdateData.setRefreshing(false);
        } else {
            //If not isRefreshing, it is loadmore case. So remove loading item
            if (mMissionList.size() > MISSION_PAGE_LIMIT) {
                mMissionList.remove(mMissionList.size() - 1);
                mMissionAdapter.notifyItemRemoved(mMissionList.size());
                isLoadingMore = false;
            }
        }
    }


    private void getReward(int missionId) {
        if (!NetworkUtils.hasConnection(getContext())) {
            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
            return;
        }
        MemberInformation memberInformation = SettingManager.getInstance(getContext()).getMemberInformation();
        String id = memberInformation.getId();
        String password = memberInformation.getPass();
        ApiInterface apiService =
                ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<ApiRewardMissionResponse> call = apiService.getRewardPointMission(missionId, id, password);

        // Show loadding
        Helpers.showCircleProgressDialog(getContext());
        // send call object
        call.enqueue(new Callback<ApiRewardMissionResponse>() {
            @Override
            public void onResponse(Call<ApiRewardMissionResponse> call, Response<ApiRewardMissionResponse> response) {
                Helpers.dismissCircleProgressDialog();
                if (response == null || response.body() == null) {
                    return;
                }
                if (response.body().isSuccess()) {
                    // Log success
                    Log.e(TAG, "Get point success");
                    // For 2 tab mission update data
                    EventBus.getDefault().post(new UpdateMissionInfoEvent());
                } else {
                    String errorMessage = response.body().getResponse();
                    if (isVisible()) {
                        DialogBuilder.buildNoticeDialog(errorMessage, null).show(getFragmentManager(), TAG);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiRewardMissionResponse> call, Throwable t) {
                // Log error here since request failed
                Helpers.dismissCircleProgressDialog();
                RkLogger.e(TAG, "MemberInfoChange", t);
                DialogBuilder.buildNoticeDialog(getString(R.string.mission_receive_award_failed), null).show(getFragmentManager(), TAG);
            }
        });
    }

    private OnMissionDialogClearClick onMissionDialogClearClick = new OnMissionDialogClearClick() {
        @Override
        public void onButtonOkClick(int missionID) {
            getReward(missionID);
        }
    };

    public static final long TIME_INTERVAL = 500;
    private long lastClickTime;

    private OnMissionButtonClick onMissionButtonClick = new OnMissionButtonClick() {
        @Override
        public void onButtonClick(UncompletedMission mission) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < TIME_INTERVAL) {
                lastClickTime = currentTime;
                return;
            }
            lastClickTime = currentTime;
            // Handle click

            if (mission.isCompleted()) {
                DialogMissionClear dialogMissionClear = DialogMissionClear.newInstance(mission);
                dialogMissionClear.setOnMissionDialogClearClick(onMissionDialogClearClick);
                dialogMissionClear.show(getFragmentManager(), dialogMissionClear.getClass().getSimpleName());
            } else {
                DialogMissionStart dialogMissionStart = DialogMissionStart.newInstance(mission);
                dialogMissionStart.show(getFragmentManager(), dialogMissionStart.getClass().getSimpleName());
            }
        }
    };

    RecyclerView.OnScrollListener mRcScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int totalItemCount = mRcMissionLayoutManager.getItemCount();
            int lastVisibleItem = mRcMissionLayoutManager.findLastVisibleItemPosition();

            if (!isLoadingMore && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD) && totalItemCount > 1) {
                if (mMissionList.size() < mTotalMissionCount && mMissionList.size() >= MISSION_PAGE_LIMIT) {
                    isLoadingMore = true;
                    onLoadMore();
                }
            }
        }

    };

    public interface OnMissionButtonClick {
        void onButtonClick(UncompletedMission mission);
    }

    public interface OnMissionDialogClearClick {
        void onButtonOkClick(int missionID);
    }
}
