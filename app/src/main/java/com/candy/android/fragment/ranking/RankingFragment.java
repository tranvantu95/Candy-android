package com.candy.android.fragment.ranking;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.candy.android.R;
import com.candy.android.custom.CustomFragmentPagerAdapter;
import com.candy.android.dialog.DialogBuilder;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.http.ApiClientManager;
import com.candy.android.http.ApiInterface;
import com.candy.android.http.response.ApiRankingResponse;
import com.candy.android.manager.SettingManager;
import com.candy.android.model.ranking.BaseRankTime;
import com.candy.android.utils.NetworkUtils;
import com.candy.android.utils.RkLogger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String[] RANK_TYPE = new String[]{"daily", "monthly", "newcomer"};
    public static final int DAILY = 0;
    public static final int MONTHLY = 1;
    public static final int NEWCOMER = 2;
    public static final int ALL_POSITION = 1000;
    public static final String NOW_TIME = "現在";
    public static int TEMP;
    private String prevDate, nextDate;
    private String selectedDate, selectedMonth;
    private LinearLayout mLayout;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private CustomFragmentPagerAdapter mAdapter;
    public String datetime0, datetime1, datetime2;
    private String currentDate, currentMonth;

    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        Bundle args = new Bundle();
        rankingFragment.setArguments(args);
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayout = (LinearLayout) view.findViewById(R.id.swipe_target);
        mTabLayout = (TabLayout) view.findViewById(R.id.ranks_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.rank_list_viewpager);
        setupViewpager();
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        setRefreshLayout(view);
        setUpTabLayout();
        startRefresh();
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int availableHeight = mLayout.getMeasuredHeight();
                if (availableHeight > 0) {
                    mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    TEMP = availableHeight;
                }
            }
        });
    }

    private void setUpTabLayout() {
        View daily = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_rank_daily, null);
        daily.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTabLayout.getTabAt(0).setCustomView(daily);
        View monthly = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_rank_monthly, null);
        monthly.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTabLayout.getTabAt(1).setCustomView(monthly);
        View weekly = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_rank_weekly, null);
        weekly.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTabLayout.getTabAt(2).setCustomView(weekly);
    }

    private void setupViewpager() {
        final List<Fragment> lisfragment = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            lisfragment.add(RankingListFragment.newInstance(i));
        }

        mAdapter = new CustomFragmentPagerAdapter(lisfragment, null, getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(lisfragment.size() - 1);
        mViewPager.setCurrentItem(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public String getTitle() {
        return "ランキング";
    }

    public void loadData(boolean loadAll, int tabPosition, final String date) {
        if (!NetworkUtils.hasConnection(mContext)) {
            if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
                stopRefresh();
            }

            DialogBuilder.OnClickListener mOnClickRetry = new DialogBuilder.OnClickListener() {
                @Override
                public void onOkClick(Object object) {
                    mMainActivity.dismissRetryDialog();
                    loadData(true, ALL_POSITION, date);
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

        ApiInterface apiService = ApiClientManager.getApiClientManager().create(ApiInterface.class);
        String id = SettingManager.getInstance(getContext()).getID();
        String password = SettingManager.getInstance(getContext()).getPassword();
        //Get Daily Ranking
        if (loadAll || tabPosition == DAILY) {
            Call<ApiRankingResponse> callDaily = apiService.getPerformerRanking(id, password, RANK_TYPE[DAILY], date == null ? selectedDate : date);
            callDaily.enqueue(new Callback<ApiRankingResponse>() {

                @Override
                public void onResponse(Call<ApiRankingResponse> call, Response<ApiRankingResponse> response) {
                    hideRefreshingLayout();
                    if (response == null || response.body() == null) {
                        return;
                    }
                    if (response.body().getStatus() == 1) {
                        List<BaseRankTime> rankingList = response.body().getMember().getRanking().getPerformers();
                        prevDate = response.body().getMember().getRanking().getPrevDate();
                        nextDate = response.body().getMember().getRanking().getNextDate();
                        datetime0 = response.body().getMember().getRanking().getDatetime();

                        if (date != null) {
                            selectedDate = date;
                        }

                        if (datetime0.contains(NOW_TIME)) {
                            currentDate = datetime0;
                            selectedDate = null;
                        }
                        if (rankingList != null) {
                            bindData(rankingList, prevDate, nextDate, datetime0, currentDate, DAILY);
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiRankingResponse> call, Throwable t) {
                    hideRefreshingLayout();
                    showRetryConnectionDialog();
                }
            });
        }

        //Get Monthly Ranking
        if (loadAll || tabPosition == MONTHLY) {
            Call<ApiRankingResponse> callMonthly = apiService.getPerformerRanking(id, password, RANK_TYPE[MONTHLY], date == null ? selectedMonth : date);
            callMonthly.enqueue(new Callback<ApiRankingResponse>() {

                @Override
                public void onResponse(Call<ApiRankingResponse> call, Response<ApiRankingResponse> response) {
                    hideRefreshingLayout();
                    if (response == null || response.body() == null) {
                        return;
                    }
                    if (response.body().getStatus() == 1) {
                        List<BaseRankTime> rankingList = response.body().getMember().getRanking().getPerformers();
                        prevDate = response.body().getMember().getRanking().getPrevDate();
                        nextDate = response.body().getMember().getRanking().getNextDate();
                        datetime1 = response.body().getMember().getRanking().getDatetime();
                        if (date != null) {
                            selectedMonth = date;
                        }

                        if (datetime1.contains(NOW_TIME)) {
                            selectedMonth = null;
                            currentMonth = datetime1;
                        }
                        if (rankingList != null) {
                            bindData(rankingList, prevDate, nextDate, datetime1, currentMonth, MONTHLY);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiRankingResponse> call, Throwable t) {
                    hideRefreshingLayout();
                    showRetryConnectionDialog();
                }
            });
        }

        //Get Newcomer Ranking
        if (loadAll || tabPosition == NEWCOMER) {
            Call<ApiRankingResponse> callNewcomer = apiService.getPerformerRankingWithoutDate(id, password, RANK_TYPE[NEWCOMER]);
            callNewcomer.enqueue(new Callback<ApiRankingResponse>() {

                @Override
                public void onResponse(Call<ApiRankingResponse> call, Response<ApiRankingResponse> response) {
                    hideRefreshingLayout();
                    if (response == null || response.body() == null) {
                        return;
                    }
                    if (response.body().getStatus() == 1) {
                        List<BaseRankTime> rankingList = response.body().getMember().getRanking().getPerformers();
                        datetime2 = response.body().getMember().getRanking().getDatetime();
                        if (rankingList != null) {
                            bindData(rankingList, datetime2, NEWCOMER);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiRankingResponse> call, Throwable t) {
                    hideRefreshingLayout();
                    showRetryConnectionDialog();
                }
            });
        }
    }

    private void hideRefreshingLayout() {
        if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
            stopRefresh();
        }
    }

    private void bindData(List<BaseRankTime> ranking, String prevDate, String nextDate,
                          String datetime, String currentTime, int fragmentPosition) {
        ((RankingListFragment) mAdapter.getItem(fragmentPosition))
                .bindData(ranking, prevDate, nextDate, datetime, currentTime);
    }

    private void bindData(List<BaseRankTime> ranking, String datetime, int fragmentPosition) {
        ((RankingListFragment) mAdapter.getItem(fragmentPosition)).bindData(ranking, datetime);
    }

    public void setOnRefreshLayout(boolean isEnable) {
        swipeToLoadLayout.setEnabled(isEnable);
    }

    public SwipeRefreshLayout swipeToLoadLayout;
    private Handler mHandler;

    private void setRefreshLayout(View view) {
        mHandler = new Handler();
        swipeToLoadLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeToLoadLayout.setOnRefreshListener(this);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loadData(true, ALL_POSITION, null);
        }
    };

    void startRefresh() {
        swipeToLoadLayout.setRefreshing(true);
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, 500);
    }

    void stopRefresh() {
        swipeToLoadLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        startRefresh();
    }
}
