package com.candy.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.candy.android.R;
import com.candy.android.adapter.FootprintPerformerAdapter;
import com.candy.android.custom.views.RecycleItemDecoration;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiFootstepsResponse;
import com.candy.android.http.response.ApiPerformerFootsteps;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.PerformerFootprint;
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
 * Created by Welby Dev on 12/8/17.
 */

public class FootprintPerformerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "FootprintPerformer";
    @BindView(R.id.srl_swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rc_footprint_performer)
    RecyclerView mRcFootprintList;
    @BindView(R.id.no_content_layout)
    RelativeLayout mNoContentLayout;

    private List<PerformerFootprint> mListFootprint;
    private static final String FOOT_PRRINT = "足あと";

    public static FootprintPerformerFragment newInstance() {
        FootprintPerformerFragment fragment = new FootprintPerformerFragment();
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_footprint, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // Initialize layout content
        initLayoutContent();
        loadData();
    }

    private void initLayoutContent() {
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListFootprint = new ArrayList<>();

        mRcFootprintList.addItemDecoration(new RecycleItemDecoration(getResources().getDimensionPixelOffset(R.dimen.footprint_list_item_space)));
        FootprintPerformerAdapter adapter = new FootprintPerformerAdapter(getContext(), mListFootprint);
        mRcFootprintList.setAdapter(adapter);
    }

    @Override
    public String getTitle() {
        return FOOT_PRRINT;
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

    @Override
    public void onRefresh() {
        loadData();
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
                Call<ApiFootstepsResponse> call = apiService.getMemberFootsteps(id, password);
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // send call object
                call.enqueue(new Callback<ApiFootstepsResponse>() {
                    @Override
                    public void onResponse(Call<ApiFootstepsResponse> call, Response<ApiFootstepsResponse> response) {
                        // Turn of refreshing animation
                        mSwipeRefreshLayout.setRefreshing(false);
                        RkLogger.d(TAG, "onResponse" + response.body().getIsSuccess());
                        if (response.body() == null || !response.body().getIsSuccess()) {
                            mNoContentLayout.setVisibility(View.VISIBLE);
                            RkLogger.d(TAG, "onResponse" + response.body().getPerformers());
                            return;
                        }

                        // parse data
                        List<PerformerFootprint> mListFootsteps = new ArrayList<>();
                        List<ApiPerformerFootsteps> fetchedData = response.body().getPerformers();
                        for (int i = 0; i < fetchedData.size(); i++) {
                            mListFootsteps.add(fetchedData.get(i).getPerformer());
                        }
                        RkLogger.d(TAG, "onResponse" + fetchedData.size());
                        if (fetchedData.isEmpty()) {
                            mNoContentLayout.setVisibility(View.VISIBLE);
                        } else {
                            mNoContentLayout.setVisibility(View.GONE);
                        }
                        mListFootprint.clear();
                        mListFootprint.addAll(mListFootsteps);
                        mRcFootprintList.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<ApiFootstepsResponse> call, Throwable t) {
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

    @Subscribe
    public void onEvent(UpdateFavoritePerformerEvent event) {
        if (event == null)
            return;
        if (event.getmPerformerfavorite() != null) {
            loadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
