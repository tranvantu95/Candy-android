package com.candy.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candy.android.R;
import com.candy.android.model.Campaign;

/**
 * Created by quannt on 20/12/2016.
 * Campaign Detail
 */

public class CampaignDetailFragment extends BaseFragment {

    private static final String EXTRAS_CAMPAIGN = "campaign";

    private TextView mTvTitle, mTvDateTime, mTvContent;
    private ImageView mIvCampaign;

    private Campaign mCampaign;

    public static CampaignDetailFragment newInstance(Campaign campaign) {

        Bundle args = new Bundle();
        args.putParcelable(EXTRAS_CAMPAIGN, campaign);
        CampaignDetailFragment fragment = new CampaignDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCampaign = args.getParcelable(EXTRAS_CAMPAIGN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campaign_detail, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View view) {
        mTvTitle = (TextView) view.findViewById(R.id.tv_campaign_title);
        mTvDateTime = (TextView) view.findViewById(R.id.tv_date_time);
        mTvContent = (TextView) view.findViewById(R.id.tv_event_content);
        mIvCampaign = (ImageView) view.findViewById(R.id.iv_campaign);

        if (mCampaign != null) {
            setViewData();
        }
    }

    public void setViewData() {
        mTvTitle.setText(mCampaign.getTitle());
        mTvDateTime.setText(mCampaign.getDuration());
        mTvContent.setText(mCampaign.getMessage());
        Glide.with(mContext).load(mCampaign.getImage()).into(mIvCampaign);
    }

    @Override
    public String getTitle() {
        return null;
    }
}
