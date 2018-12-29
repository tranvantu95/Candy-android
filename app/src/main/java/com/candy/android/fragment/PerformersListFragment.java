package com.candy.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.candy.android.BuildConfig;
import com.candy.android.R;
import com.candy.android.activity.VideoCallActivity;
import com.candy.android.adapter.CampaignPagerAdapter;
import com.candy.android.adapter.PerformerAdapter;
import com.candy.android.configs.Define;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.dialog.SearchPerformerDialog;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.PerformerAllActiveResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.BasePerformer;
import com.candy.android.model.Campaign;
import com.candy.android.model.PerformerFavorite;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.eventBus.RandomCallVideoEvent;
import com.candy.android.model.eventBus.UpdateBlockPerformerEvent;
import com.candy.android.model.eventBus.UpdateFavoritePerformerEvent;
import com.candy.android.model.eventBus.UpdatePerformerStatusEvent;
import com.candy.android.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jp.fmaru.app.livechatapp.ChatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Edit by TuTv on 09/08/2018.
 */

public class PerformersListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String SCREEN_NAME = "女の子一覧";
    private static final String TAG = "PerformersListFragment";

    private RecyclerView mPerformerList;
    private GridLayoutManager mLayoutManager;
    private PerformerAdapter mAdapter;

    private SwipeRefreshLayout mPerformerLayout;
    private View mNoContentLayout, searchEmptyLayout;
    public static ArrayList<PerformerOnline> sPerformerOnlines;
    private String mId, mPassword;
    private boolean isTablet;
    private SearchPerformerDialog mSearchPerformerDialog;
    private DialogBuilder.NoticeDialog mSearchNoResultDialog;

    public static PerformersListFragment newInstance() {

        Bundle args = new Bundle();

        PerformersListFragment fragment = new PerformersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEvent(UpdatePerformerStatusEvent event) {
        if (event == null)
            return;
        if (mAdapter != null) {
            //Data have updated in sPerformerOnlines, so only need to notifyDataSetChanged
            loadData();
            Collections.sort(sPerformerOnlines);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onEvent(UpdateBlockPerformerEvent event) {
        loadData();
    }

    @Subscribe
    public void onEvent(RandomCallVideoEvent event) {
        switch (event.result) {
            case 1:
                callVideoRandom();
                break;
            case 0:
                Collections.sort(sPerformerOnlines);
                mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onEvent(UpdateFavoritePerformerEvent event) {
        if (event == null)
            return;
        PerformerFavorite performerFavorite = event.getmPerformerfavorite();
        int actionType = event.getmActionType();
        if (mAdapter != null) {
            // If performerFavorite == null, event was posted from PerformerActivity. New Data have updated in sPerformerOnline
            if (performerFavorite == null) {
                mAdapter.notifyDataSetChanged();
            } else {
                int code = performerFavorite.getCode();
                if (actionType == UpdateFavoritePerformerEvent.TYPE_ADD_FAVORITE) {
                    for (int i = 0; i < sPerformerOnlines.size(); i++) {
                        if (code == sPerformerOnlines.get(i).getCode()) {
                            sPerformerOnlines.get(i).setAddFavorite("1");
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else if (actionType == UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE) {
                    for (int i = 0; i < sPerformerOnlines.size(); i++) {
                        if (code == sPerformerOnlines.get(i).getCode()) {
                            sPerformerOnlines.get(i).setAddFavorite("0");
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPerformerOnlines = new ArrayList<>();
        mId = SettingManager.getInstance(mContext).getMemberInformation().getId();
        mPassword = SettingManager.getInstance(mContext).getMemberInformation().getPass();
        isTablet = mContext.getResources().getBoolean(R.bool.isTablet);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mSearchNoResultDialog = DialogBuilder.buildNoticeDialog(getString(R.string.search_no_value), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_performers_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPerformerList = (RecyclerView) view.findViewById(R.id.performers_list);
        mPerformerList.setHasFixedSize(true);
        if (isTablet) {
            mLayoutManager = new GridLayoutManager(mContext, 3);
        } else {
            mLayoutManager = new GridLayoutManager(mContext, 2);
        }
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return !mAdapter.isNoHeader() && mAdapter.isHeader(position) ? mLayoutManager.getSpanCount() : 1;
            }
        });
        mPerformerList.setLayoutManager(mLayoutManager);
        mPerformerList.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new PerformerAdapter(mContext, sPerformerOnlines);
        mAdapter.setOnClickCampaignListener(mOnClickCampaignListener);
        mPerformerList.setAdapter(mAdapter);

        mPerformerLayout = (SwipeRefreshLayout) view.findViewById(R.id.swr_performer);
        mPerformerLayout.setOnRefreshListener(this);
        mNoContentLayout = view.findViewById(R.id.no_content_layout);
        searchEmptyLayout = view.findViewById(R.id.search_empty_layout);

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void showSearchDialog() {
        FragmentManager fm = getFragmentManager();
        if (mSearchPerformerDialog == null) {
            mSearchPerformerDialog = new SearchPerformerDialog();
            mSearchPerformerDialog.setTargetFragment(this, 0);
            mSearchPerformerDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
            mSearchPerformerDialog.setRetainInstance(true);
        }
        mSearchPerformerDialog.show(fm, TAG);
    }

    /**
     *
     */
    private void callVideoRandom() {
        if (!NetworkUtils.hasConnection(getActivity())) {
            DialogBuilder.buildNoticeDialog(getString(R.string.can_not_connect_to_server), null).show(getFragmentManager(), TAG);
            return;
        }
        Collections.sort(sPerformerOnlines);
        mAdapter.notifyDataSetChanged();
        mId = SettingManager.getInstance(mContext).getMemberInformation().getId();
        mPassword = SettingManager.getInstance(mContext).getMemberInformation().getPass();
        int point = SettingManager.getInstance(getActivity()).getMemberInformation().getPoint();
        ArrayList<PerformerOnline> mPerformerOnlinesWaitCall = new ArrayList<>();
        for (int i = 0; i < sPerformerOnlines.size(); i++) {
            if ((sPerformerOnlines.get(i).getStatus() == Define.PerformerStatus.CALL_WAITING || sPerformerOnlines.get(i).getStatus() == Define.PerformerStatus.WAITING)
                    && sPerformerOnlines.get(i).getIsPublic() != BasePerformer.NO_PUBLIC_INT && sPerformerOnlines.get(i).getPointPerMinute() <= point && sPerformerOnlines.get(i).getCode() != 24837704 && sPerformerOnlines.get(i).getCode() != 512514386) {
                mPerformerOnlinesWaitCall.add(sPerformerOnlines.get(i));
            }
        }
        boolean isEnoughPoint;
        Random random = new Random();
        int n = random.nextInt(mPerformerOnlinesWaitCall.size());
        isEnoughPoint = point >= mPerformerOnlinesWaitCall.get(n).getPointPerMinute();
        if (isEnoughPoint) {
            Intent webIntent = new Intent(getActivity(), VideoCallActivity.class);
            webIntent.putExtra(ChatActivity.EXTRA_ID, mId);
            webIntent.putExtra(ChatActivity.EXTRA_PASS, mPassword);
            webIntent.putExtra(ChatActivity.EXTRA_PERFORMER_CODE, String.valueOf(mPerformerOnlinesWaitCall.get(n).getCode()));
            webIntent.putExtra(ChatActivity.EXTRA_PERFORMER_NAME, mPerformerOnlinesWaitCall.get(n).getName());
            webIntent.putExtra(ChatActivity.EXTRA_PERFORMER_IMAGE, mPerformerOnlinesWaitCall.get(n).getProfileImageUrl());
            webIntent.putExtra(Define.IntentExtras.VIDEO_MODE, Define.VIDEO_MODE_VALUE_DIRECT);
            startActivityForResult(webIntent, 100);
        } else {

        }
    }


    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public void loadData() {
        //Set mSearchPerformerDialog null to refresh search state
        mSearchPerformerDialog = null;
        if (!NetworkUtils.hasConnection(mContext)) {
            if (mPerformerLayout != null && mPerformerLayout.isRefreshing()) {
                mPerformerLayout.setRefreshing(false);
            }

            DialogBuilder.OnClickListener mOnClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    mMainActivity.dismissRetryDialog();
                    loadData();
                }

                @Override
                public void onCancelClick() {

                }
            };
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(mOnClickRetry);
            }
            return;
        }
        if (mPerformerLayout != null) {
            mPerformerLayout.setRefreshing(true);
        }

        loadPerformer(null, null, null, null, null, null, null, new Callback<PerformerAllActiveResponse>() {
            @Override
            public void onResponse(Call<PerformerAllActiveResponse> call, Response<PerformerAllActiveResponse> response) {
                if (mPerformerLayout != null && mPerformerLayout.isRefreshing()) {
                    mPerformerLayout.setRefreshing(false);
                }
                if (response == null || response.body() == null)
                    return;
                int status = response.body().getStatus();
                if (status == 1) {
                    ArrayList<PerformerOnline> performerOnlines = response.body().getPerformers();
//                    for(int i =0;i<performerOnlines.size();i++){
//                        RkLogger.d(TAG,""+performerOnlines.get(i).getStatus()+"- "+performerOnlines.get(i).getStatusString()+"-isNew="+performerOnlines.get(i).getIsNew()+",name "+performerOnlines.get(i).getCode());
//                    }
                    if (performerOnlines != null) {
                        if (performerOnlines.size() == 0) {
//                            mNoContentLayout.setVisibility(View.VISIBLE);
                        } else {
//                            mNoContentLayout.setVisibility(View.GONE);
                            searchEmptyLayout.setVisibility(View.GONE);
                            mPerformerList.setVisibility(View.VISIBLE);
                        }
                        sPerformerOnlines.clear();
                        sPerformerOnlines.addAll(performerOnlines);
                        Collections.sort(sPerformerOnlines);

                        // #random_chat
//                        PerformerOnline performerRandom = new PerformerOnline();
//                        performerRandom.setStatus("1");
//                        performerRandom.setCharacter(getString(R.string.message_short_random_video_call));
//                        sPerformerOnlines.add(0, performerRandom);

                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    //error
                    // TODO: 10/19/2016 : do something in error case
//                    mNoContentLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PerformerAllActiveResponse> call, Throwable t) {
                if (mPerformerLayout != null && mPerformerLayout.isRefreshing()) {
                    mPerformerLayout.setRefreshing(false);
                }
            }
        });
    }

    public void loadPerformer(Integer isNew, String name, String area, Integer isAge10, Integer isAge20, Integer isAge30, Integer pickup, Callback<PerformerAllActiveResponse> callback) {
        if (isNew != null && isNew == 0) isNew = null;
        if (isAge10 != null && isAge10 == 0) isAge10 = null;
        if (isAge20 != null && isAge20 == 0) isAge20 = null;
        if (isAge30 != null && isAge30 == 0) isAge30 = null;
        if (pickup != null && pickup == 0) pickup = null;

        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) name = null;
        }
        if (area != null) {
            area = area.trim();
            if (area.isEmpty()) area = null;
        }

        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        Call<PerformerAllActiveResponse> call = apiService.searchPerformerOnline(mId, mPassword,
                isNew, name, area, isAge10, isAge20, isAge30, pickup, Define.Fields.FIELD_DEVICE_VALUE, BuildConfig.VERSION_CODE);
        call.enqueue(callback);
    }

    public void loadSearchData(final Integer isNew, final String name, final String area, final Integer isAge10, final Integer isAge20, final Integer isAge30, final Integer pickup, final boolean isLive) {
        if (!NetworkUtils.hasConnection(mContext)) {
            final DialogBuilder.NoticeDialog retryDialog = DialogBuilder.NoticeDialog.newInstance(mContext.getString(R.string.can_not_connect_to_server), mContext.getString(R.string.retry));
            retryDialog.setOnClickListener(new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    retryDialog.dismiss();
                    loadSearchData(isNew, name, area, isAge10, isAge20, isAge30, pickup, isLive);
                }

                @Override
                public void onCancelClick() {

                }
            });
            retryDialog.show(getChildFragmentManager(), TAG);
            return;
        }

        showCircleDialog();

        loadPerformer(isNew, name, area, isAge10, isAge20, isAge30, pickup, new Callback<PerformerAllActiveResponse>() {
            @Override
            public void onResponse(Call<PerformerAllActiveResponse> call, Response<PerformerAllActiveResponse> response) {
                hideCircleDialog();
                if (response == null || response.body() == null) return;

                int status = response.body().getStatus();
                if (status == 1) {
                    ArrayList<PerformerOnline> performerOnlines = response.body().getPerformers();
                    if (performerOnlines != null) {
                        sPerformerOnlines.clear();
//                        if (isChat) {
//                            if (isStandby) {
//                                sPerformerOnlines.addAll(filterStatusString(performerOnlines, STATUS_CHAT, STATUS_STANDBY));
//                            } else {
//                                sPerformerOnlines.addAll(filterStatusString(performerOnlines, STATUS_CHAT));
//                            }
//                        }
//                        else if (isStandby) {
//                            sPerformerOnlines.addAll(filterStatusString(performerOnlines, STATUS_STANDBY));
//                        }
                        sPerformerOnlines.addAll(!isLive ? performerOnlines : filterPerformerLive(performerOnlines));
                        Collections.sort(sPerformerOnlines);

                        // #random_chat
//                        PerformerOnline performerRandom = new PerformerOnline();
//                        performerRandom.setStatus("1");
//                        performerRandom.setCharacter(getString(R.string.message_short_random_video_call));
//                        sPerformerOnlines.add(0, performerRandom);

                        if (sPerformerOnlines.size() == 0) {
                            searchEmptyLayout.setVisibility(View.VISIBLE);
                            mPerformerList.setVisibility(View.GONE);
                        } else {
                            searchEmptyLayout.setVisibility(View.GONE);
                            mPerformerList.setVisibility(View.VISIBLE);
                        }

                        mAdapter.notifyDataSetChanged();
                        scrollToTop();
                    }
                } else {
                    //error
                    // TODO: 10/19/2016 : do something in error case
                }
            }

            @Override
            public void onFailure(Call<PerformerAllActiveResponse> call, Throwable t) {
                hideCircleDialog();
            }
        });
    }

    @NonNull
    private ArrayList<PerformerOnline> filterPerformerLive(ArrayList<PerformerOnline> performerOnlines) {
        ArrayList<PerformerOnline> result = new ArrayList<>();
        if (performerOnlines == null) return result;

        for (int i = 0; i < performerOnlines.size(); i++) {
            if (performerOnlines.get(i).isLive()) {
                result.add(performerOnlines.get(i));
            }
        }

        return result;
    }

    private ArrayList<PerformerOnline> filterStatusString(ArrayList<PerformerOnline> performerOnlines, String statusString) {
        ArrayList<PerformerOnline> result = new ArrayList<>();
        if (performerOnlines == null || statusString == null)
            return result;
        for (int i = 0; i < performerOnlines.size(); i++) {
            if (statusString.equals(performerOnlines.get(i).getStatusString())) {
                result.add(performerOnlines.get(i));
            }
        }
        return result;
    }

    private ArrayList<PerformerOnline> filterStatusString(ArrayList<PerformerOnline> performerOnlines, String statusString1, String statusString2) {
        ArrayList<PerformerOnline> result = new ArrayList<>();
        if (performerOnlines == null || statusString1 == null || statusString2 == null)
            return result;
        for (int i = 0; i < performerOnlines.size(); i++) {
            if (statusString1.equals(performerOnlines.get(i).getStatusString()) || statusString2.equals(performerOnlines.get(i).getStatusString())) {
                result.add(performerOnlines.get(i));
            }
        }
        return result;
    }

    @Override
    public String getTitle() {
        return SCREEN_NAME;
    }

    public void scrollToTop() {
        if (mPerformerList != null) {
            mPerformerList.scrollToPosition(0);
        }
    }

    private CampaignPagerAdapter.OnClickCampaignListener mOnClickCampaignListener = new CampaignPagerAdapter.OnClickCampaignListener() {
        @Override
        public void onClickCampaign(ArrayList<Campaign> campaigns, int position) {
            mMainActivity.showCampaignDialog(campaigns, position);
        }
    };

//    private RecyclerView.OnScrollListener mOnScrollPerformerListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
////            if(newState == RecyclerView.SCROLL_STATE_IDLE) {
////                mFabSearch.show();
////            }
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            if (dy > 0 && mFabSearch.isShown()) {
//                mFabSearch.hide();
//            }
//            if (dy < 0 && !mFabSearch.isShown()) {
//                mFabSearch.show();
//            }
//        }
//    };

    public void removePerformer(String performerCode) {
        if (TextUtils.isEmpty(performerCode) || !TextUtils.isDigitsOnly(performerCode))
            return;
        int code = Integer.valueOf(performerCode);
        for (int i = 0; i < sPerformerOnlines.size(); i++) {
            if (sPerformerOnlines.get(i).getCode() == code) {
                sPerformerOnlines.remove(i);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_CANCELED) {
                loadData();
            }
        }
    }
}
