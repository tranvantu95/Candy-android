package com.candy.android.fragment.menu;

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
import com.candy.android.adapter.BlockListAdapter;
import com.candy.android.custom.views.RecycleItemDecoration;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFavoriteResponse;
import com.candy.android.http.response.ApiMemberResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.PerformerFavorite;
import com.candy.android.model.eventBus.UpdateBlockPerformerEvent;
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

public class BlockListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "BlockListFragment";

    @BindView(R.id.srl_swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rc_unfavorite_performer)
    RecyclerView mRcUnFavoriteList;
    @BindView(R.id.no_content_layout)
    RelativeLayout mNoContentLayout;

    private List<PerformerFavorite> mListUnFavoritePerformers;

    private DialogBuilder.ConfirmDialog mConfirmDelUnFavoriteDialog;
    private DialogBuilder.NoticeDialog mDelUnFavoriteSuccessDialog;

    public static BlockListFragment newInstance() {

        Bundle args = new Bundle();

        BlockListFragment fragment = new BlockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEvent(UpdateBlockPerformerEvent event) {
        loadData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_block_list, container, false);
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

    private void initLayoutContent() {
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Initialize list
        mListUnFavoritePerformers = new ArrayList<>();

        // Initialize RecycleView
//        mRcUnFavoriteList.addItemDecoration(new RecycleItemDecoration(getResources().getDimensionPixelOffset(R.dimen.favorite_list_item_space)));

        // Set adapter
        BlockListAdapter adapter = new BlockListAdapter(mListUnFavoritePerformers, new BlockListAdapter.Callback() {
            @Override
            public void onOpenDetail(PerformerFavorite performerFavorite) {

            }

            @Override
            public void onRemove(final PerformerFavorite performer) {
                mConfirmDelUnFavoriteDialog = DialogBuilder.buildConfirmDialog(getString(R.string.confirm_dialog_message_del_un_favorite), new DialogBuilder.OnClickListener() {
                    @Override
                    public void onOkClick(Object object) {
                        if (null != mConfirmDelUnFavoriteDialog) {
                            mConfirmDelUnFavoriteDialog.dismiss();
                        }
                        removeFavorite(performer);
                    }

                    @Override
                    public void onCancelClick() {
                        if (null != mConfirmDelUnFavoriteDialog) {
                            mConfirmDelUnFavoriteDialog.dismiss();
                        }
                    }
                });
                mConfirmDelUnFavoriteDialog.setTextOkButton(getString(R.string.confirm_dialog_default_ok));
                mConfirmDelUnFavoriteDialog.show(getChildFragmentManager(), "");
            }
        });
        mRcUnFavoriteList.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * Fetch list favorite performers
     */
    private void loadData() {
        if (!NetworkUtils.hasConnection(mContext)) {
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    if (mMainActivity != null) {
                        mMainActivity.dismissRetryDialog();
                    }
                    loadData();
                }

                @Override
                public void onCancelClick() {

                }
            };
            if (mMainActivity != null) {
                mMainActivity.showRetryDialog(onClickRetry);
            }
            return;
        }
        try {
            if (SettingManager.getInstance(mContext).hasMember()) {
                String id = SettingManager.getInstance(mContext).getID();
                String password = SettingManager.getInstance(mContext).getPassword();

                ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

                RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
                //create call object
                Call<ApiMemberResponse> call = apiService.getMemberInfo(id, password);

                if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // send call object
                call.enqueue(new Callback<ApiMemberResponse>() {
                    @Override
                    public void onResponse(Call<ApiMemberResponse> call, Response<ApiMemberResponse> response) {
                        // Turn of refreshing animation
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (response == null || response.body() == null)
                            return;

                        // parse data
                        List<PerformerFavorite> fetchedData = response.body().getMember().getUnfavoriteList();
                        if (null != fetchedData && !fetchedData.isEmpty()) {
                            mListUnFavoritePerformers.clear();
                            mListUnFavoritePerformers.addAll(fetchedData);
                            mRcUnFavoriteList.getAdapter().notifyDataSetChanged();
                        }
                        invalidateView();
                    }

                    @Override
                    public void onFailure(Call<ApiMemberResponse> call, Throwable t) {
                        // Turn of refreshing animation
                        mSwipeRefreshLayout.setRefreshing(false);
                        initLayoutContent();
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

    private void invalidateView() {
        if (mListUnFavoritePerformers == null || mListUnFavoritePerformers.isEmpty()) {
            mNoContentLayout.setVisibility(View.VISIBLE);
            mRcUnFavoriteList.setVisibility(View.GONE);
        } else {
            mNoContentLayout.setVisibility(View.GONE);
            mRcUnFavoriteList.setVisibility(View.VISIBLE);
        }
    }

    private void removeFavorite(final PerformerFavorite performer) {
        if (!NetworkUtils.hasConnection(mContext)) {
            DialogBuilder.OnClickListener onClickRetry = new DialogBuilder.OnClickListener() {
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
                String id = SettingManager.getInstance(mContext).getMemberInformation().getId();
                String password = SettingManager.getInstance(mContext).getMemberInformation().getPass();

                ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);

                RkLogger.d(TAG, "stored id=" + id + ", pwd=" + password);
                //create call object
                Call<ApiFavoriteResponse> call = apiService.unfavoriteDelete(id, password, performer.getCode());

                // send call object
                call.enqueue(new Callback<ApiFavoriteResponse>() {
                    @Override
                    public void onResponse(Call<ApiFavoriteResponse> call, Response<ApiFavoriteResponse> response) {

                        if (response == null || response.body() == null)
                            return;

                        if (response.body().isSuccess()) {
                            mListUnFavoritePerformers.remove(performer);
                            mRcUnFavoriteList.getAdapter().notifyDataSetChanged();
                            showDelUnFavoriteSuccess();
                            invalidateView();
                            EventBus.getDefault().post(new UpdateBlockPerformerEvent());
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

    public void showDelUnFavoriteSuccess() {
        mDelUnFavoriteSuccessDialog = DialogBuilder.buildNoticeDialog(getString(R.string.notice_dialog_message_del_un_favorite_success), null);
        mDelUnFavoriteSuccessDialog.show(getChildFragmentManager(), "");
    }

    @Override
    public String getTitle() {
        return getString(R.string.block_list_screen_title);
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
}
