package com.candy.android.fragment.ranking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.R;
import com.candy.android.activity.PerformerProfileActivity;
import com.candy.android.adapter.RankingAdapter;
import com.candy.android.configs.Define;
import com.candy.android.custom.views.RankItemDecoration;
import com.candy.android.fragment.BaseFragment;
import com.candy.android.model.PerformerOnline;
import com.candy.android.model.ranking.BaseRankTime;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingListFragment extends BaseFragment implements OnRankItemClickListener, RankingAdapter.DateSelectionListener {
    private static final String TAB_POSITION = "tab_position";
    public static final String[] RANK_TYPE = new String[]{"daily", "monthly", "newcomer"};
    public static final int DAILY = 0;
    public static final int MONTHLY = 1;
    public static final int NEWCOMER = 2;
    private static final long DELAYED_TIME = 0;
    private RecyclerView mRecyclerView;
    private List<BaseRankTime> mListDayData = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Integer tabPosition;
    private String date, prevDate, nextDate, datetime, currentTime;
    private RankingAdapter rankingAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabPosition = getArguments().getInt(TAB_POSITION);
        }
    }

    public static RankingListFragment newInstance(int pageIndex) {
        RankingListFragment fragment = new RankingListFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_POSITION, pageIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_header_ranking_daily, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListDayData = new ArrayList<>();
        prevDate = nextDate = null;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rc_performer_ranking);
        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        rankingAdapter = new RankingAdapter(mContext, mListDayData, this,
                tabPosition, prevDate, nextDate, datetime, currentTime);
        rankingAdapter.setDateSelectionListener(this);
        mRecyclerView.setAdapter(rankingAdapter);
        mRecyclerView.addItemDecoration(new RankItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public String getTitle() {
        return null;
    }

    public void bindData(List<BaseRankTime> performerRank, String prevDate, String nextDate,
                         String datetime, String currentTime) {
        mListDayData.clear();
        mListDayData.addAll(performerRank);
        this.prevDate = prevDate;
        this.nextDate = nextDate;
        this.datetime = datetime;
        this.currentTime = currentTime;
        rankingAdapter.setPrevDate(prevDate);
        rankingAdapter.setNextDate(nextDate);
        rankingAdapter.setDatetime(datetime);
        rankingAdapter.setCurrentTime(currentTime);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void bindData(List<BaseRankTime> performerRank, String datetime) {
        mListDayData.clear();
        mListDayData.addAll(performerRank);
        this.datetime = datetime;
        rankingAdapter.setDatetime(datetime);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onOpenDetail(BaseRankTime baseRankTime) {
        Intent intent = new Intent(mContext, PerformerProfileActivity.class);
        PerformerOnline performerOnline = new PerformerOnline();
        //Only pass code and profileImageUrl
        //performerOnline.setChatStatusString(baseRankTime.getChatStatusString());
        performerOnline.setPrimaryName(baseRankTime.getPrimaryName());
        performerOnline.setAge(String.valueOf(baseRankTime.getAge()));
        performerOnline.setCode(Integer.parseInt(baseRankTime.getCode()));
        performerOnline.setProfileImage(baseRankTime.getProfileImageUrl());
        performerOnline.setStatus(baseRankTime.getStatus());
        intent.putExtra(Define.IntentExtras.PERFORMER, performerOnline);
        intent.putExtra(Define.IntentExtras.POSITION, PerformerProfileActivity.NO_POSITION);
        startActivity(intent);
    }

    @Override
    public void onPrevDateSelect(final String prevDate) {
        final Fragment fragment = getParentFragment();
        if (fragment instanceof RankingFragment) {
            ((RankingFragment) fragment).swipeToLoadLayout.setRefreshing(true);
            ((RankingFragment) fragment).loadData(false, DAILY, prevDate);
        }
    }

    @Override
    public void onNextDateSelect(final String nextDate) {
        final Fragment fragment = getParentFragment();
        if (fragment instanceof RankingFragment) {
            ((RankingFragment) fragment).swipeToLoadLayout.setRefreshing(true);
            ((RankingFragment) fragment).loadData(false, DAILY, nextDate);
        }
    }

    @Override
    public void onPrevMonthSelect(final String prevDate) {
        final Fragment fragment = getParentFragment();
        if (fragment instanceof RankingFragment) {
            ((RankingFragment) fragment).swipeToLoadLayout.setRefreshing(true);
            ((RankingFragment) fragment).loadData(false, MONTHLY, prevDate);
        }
    }

    @Override
    public void onNextMonthSelect(final String nextDate) {
        final Fragment fragment = getParentFragment();
        if (fragment instanceof RankingFragment) {
            ((RankingFragment) fragment).swipeToLoadLayout.setRefreshing(true);
            ((RankingFragment) fragment).loadData(false, MONTHLY, nextDate);
        }
    }
}
