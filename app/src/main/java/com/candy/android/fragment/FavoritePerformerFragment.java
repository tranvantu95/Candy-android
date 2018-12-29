package com.candy.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.candy.android.R;
import com.candy.android.activity.MainActivity;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.adapter.FavoritePerformerAdapter;
import com.candy.android.configs.Define;
import com.candy.android.custom.callbacks.OnFavoritePerformerClickListener;
import com.candy.android.custom.views.RecycleItemDecoration;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.PerformerFavorite;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.eventBus.UpdateFavoritePerformerEvent;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by quannt on 10/13/2016.
 * Modified by NamHV: Show list favorite Performers
 */

public class FavoritePerformerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnFavoritePerformerClickListener {

    public static final String TAG = "FavoritePerformer";

    @BindView(R.id.srl_swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rc_favorites_performer)
    RecyclerView mRcFavoriteList;
    @BindView(R.id.no_content_layout)
    RelativeLayout mNoContentLayout;

    private List<PerformerFavorite> mListFavoritePerformers;

    DialogBuilder.ConfirmDialog confirmDialog;
    DialogBuilder.NoticeDialog noticeDelFinishDialog;

    public static FavoritePerformerFragment newInstance() {

        Bundle args = new Bundle();

        FavoritePerformerFragment fragment = new FavoritePerformerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEvent(UpdateFavoritePerformerEvent event) {
        if (event == null)
            return;
        if (event.getmPerformerfavorite() != null) {
            PerformerFavorite performerFavorite = event.getmPerformerfavorite();
            int actionType = event.getmActionType();
            if (performerFavorite != null && mListFavoritePerformers != null && mRcFavoriteList != null) {
                if (actionType == UpdateFavoritePerformerEvent.TYPE_ADD_FAVORITE) {
                    if (mListFavoritePerformers.size() == 0) {
                        mNoContentLayout.setVisibility(View.GONE);
                    }
                    mListFavoritePerformers.add(0, performerFavorite);
                    mRcFavoriteList.getAdapter().notifyDataSetChanged();
                } else if (actionType == UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE) {
                    for (int i = 0; i < mListFavoritePerformers.size(); i++) {
                        if (mListFavoritePerformers.get(i).getCode() == performerFavorite.getCode()) {
                            mListFavoritePerformers.remove(i);
                            if (mListFavoritePerformers.size() == 0) {
                                mNoContentLayout.setVisibility(View.VISIBLE);
                            }
                            mRcFavoriteList.getAdapter().notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        } else if (event.getmActionType() == UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE) {
            loadData();
        } else {
            loadData();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // Initialize layout content
        initLayoutContent();

        // Load data
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        RkLogger.d("Check screen >> ", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initLayoutContent() {
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Initialize list
        mListFavoritePerformers = new ArrayList<>();

        // Initialize RecycleView
        mRcFavoriteList.addItemDecoration(new RecycleItemDecoration(getResources().getDimensionPixelOffset(R.dimen.favorite_list_item_no_space)));

        // Set adapter
        FavoritePerformerAdapter adapter = new FavoritePerformerAdapter(mContext, mListFavoritePerformers, this);
        mRcFavoriteList.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        loadData();
    }


    @Override
    public void onChat(PerformerFavorite performer) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(Define.IntentActions.ACTION_CHAT);
        intent.putExtra(Define.IntentExtras.PERFORMER_CODE, performer.getCode());
        intent.putExtra(Define.IntentExtras.PERFORMER_NAME, performer.getOrignalName());
        intent.putExtra(Define.IntentExtras.PERFORMER_AGE, performer.getAge());
        intent.putExtra(Define.IntentExtras.PERFORMER_IMAGE, performer.getmProfileImageUrl());
        startActivity(intent);
    }

    @Override
    public void onRemove(final PerformerFavorite performer) {
        confirmDialog = DialogBuilder.buildConfirmDialog(mContext.getResources().getString(R.string.remove_performer_favorite_title), new DialogBuilder.OnClickListener() {
            @Override
            public void onOkClick(Object object) {
                if (null != confirmDialog) {
                    confirmDialog.dismiss();
                }

                removeFavorite(performer);
            }

            @Override
            public void onCancelClick() {
                if (null != confirmDialog) {
                    confirmDialog.dismiss();
                }
            }
        });
        confirmDialog.setTextOkButton(getString(R.string.delete));
        confirmDialog.show(getChildFragmentManager(), "");
    }

    @Override
    public void onOpenDetail(PerformerFavorite performer) {
        Intent intent = new Intent(mContext, PerformerProfileActivity.class);
        PerformerOnline performerOnline = new PerformerOnline();
        //Only pass code and profileImageUrl
        performerOnline.setCode(performer.getCode());
        performerOnline.setProfileImageUrl(performer.getmProfileImageUrl());
        intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
        intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
        startActivity(intent);
    }

    /**
     * Fetch list favorite performers
     */
    public void loadData() {
        if (!NetworkUtils.hasConnection(mContext)) {
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
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
        try {
            if (SettingManager.getInstance(mContext).hasMember()) {
                String id = SettingManager.getInstance(mContext).getMemberInformation().getId();
                String password = SettingManager.getInstance(mContext).getMemberInformation().getPass();

                ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

                RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
                //create call object
                Call<ApiMemberResponse> call = apiService.getMemberInfo(id, password);
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // send call object
                call.enqueue(new Callback<ApiMemberResponse>() {
                    @Override
                    public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                        // Turn of refreshing animation
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (response == null || response.body() == null || response.body().getMember() == null)
                            return;

                        // parse data
                        List<PerformerFavorite> fetchedData = response.body().getMember().getFavoriteList();
                        if (null != fetchedData) {
                            if (fetchedData.isEmpty()) {
                                mNoContentLayout.setVisibility(View.VISIBLE);
                            } else {
                                mNoContentLayout.setVisibility(View.GONE);
                            }
                            mListFavoritePerformers.clear();
                            mListFavoritePerformers.addAll(fetchedData);
                            mRcFavoriteList.getAdapter().notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                        // Turn of refreshing animation
                        mSwipeRefreshLayout.setRefreshing(false);

                        // Log error here since request failed
                        RkLogger.e(TAG, "onFailure", t);
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Turn of refreshing animation
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void removeFavorite(final PerformerFavorite performer) {
        if (!NetworkUtils.hasConnection(mContext)) {
            final DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (mMainActivity != null) {
                        mMainActivity.dismissRetryDialog();
                    }
                    removeFavorite(performer);
                }

                @Override
                public void onCancelClick() {

                }
            };
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(onClickRetry);
            }
        }
        try {
            if (SettingManager.getInstance(mContext).hasMember()) {
                String id = SettingManager.getInstance(mContext).getID();
                String password = SettingManager.getInstance(mContext).getPassword();

                ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

                RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
                //create call object
                Call<ApiFavoriteResponse> call = apiService.favoriteDelete(id, password, performer.getCode());

                // send call object
                call.enqueue(new Callback<ApiFavoriteResponse>() {
                    @Override
                    public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {
                        Log.e(TAG, "onResponse:" + response.message());
                        if (response.body() == null)
                            return;

                        if (response.body().isSuccess()) {
                            UpdateFavoritePerformerEvent updateFavoriteEvent = new UpdateFavoritePerformerEvent(performer, UpdateFavoritePerformerEvent.TYPE_DEL_FAVORITE);
                            EventBus.getDefault().post(updateFavoriteEvent);
                            noticeDelFinishDialog = DialogBuilder.buildNoticeDialog(mContext.getString(R.string.notice_delete_finish), new DialogBuilder.OnClickListener() {
                                @Override
                                public void onOkClick(Object object) {
                                    if (noticeDelFinishDialog != null) {
                                        noticeDelFinishDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelClick() {

                                }
                            });
                            noticeDelFinishDialog.show(getChildFragmentManager(), "");
                            mListFavoritePerformers.remove(performer);
                            if (mListFavoritePerformers.size() == 0) {
                                mNoContentLayout.setVisibility(View.VISIBLE);
                            }
                            mRcFavoriteList.getAdapter().notifyDataSetChanged();
                        } else {
                            //TODO show error mNoticeDialog
                            Log.e(TAG, "Delete favorite fail");
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiFavoriteResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure:" + t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.favorite_screen_title);
    }

    @Override
    public void updateUI() {
        if (mMainActivity != null) {
            // update title
            mMainActivity.setScreenTitle(getTitle());
            //update back button's visibility
            mMainActivity.setScreenUpActionDisplay(true);
        }
    }

    public void scrollToTop() {
        if (mRcFavoriteList != null) {
            mRcFavoriteList.scrollToPosition(0);
        }
    }
}
