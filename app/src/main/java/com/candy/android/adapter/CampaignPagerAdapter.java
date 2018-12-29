package com.candy.android.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.candy.android.R;
import com.candy.android.configs.Define;
import com.candy.android.model.Campaign;

import java.util.ArrayList;

/**
 * Created by quannt on 23/11/2016.
 * Des:
 */

public class CampaignPagerAdapter extends PagerAdapter {

    private ArrayList<Campaign> mCampaigns;
    private OnClickCampaignListener mOnClickCampaignListener;

    public CampaignPagerAdapter(ArrayList<Campaign> campaigns, OnClickCampaignListener onClickCampaignListener) {
        mCampaigns = campaigns;
        mOnClickCampaignListener = onClickCampaignListener;
    }

    @Override
    public int getCount() {
        if (mCampaigns != null) {
            return mCampaigns.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        Campaign campaign = mCampaigns.get(position);
        View rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.campaign_pager_item, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_campaign_content);
        Glide.with(container.getContext())
                .load(campaign.getImage())
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickCampaignListener != null) {
                    mOnClickCampaignListener.onClickCampaign(mCampaigns, position);
                }
            }
        });

        ImageView ivCampaignStatus = (ImageView) rootView.findViewById(R.id.iv_campaign_status);
        if (campaign.getComing().equalsIgnoreCase(Define.CAMPAIGN_STATUS_NOW)) {
            ivCampaignStatus.setImageResource(R.drawable.event_status_now);
        } else if (campaign.getComing().equalsIgnoreCase(Define.CAMPAIGN_STATUS_SOON)) {
            ivCampaignStatus.setImageResource(R.drawable.event_status_soon);
        } else {
            ivCampaignStatus.setImageResource(R.drawable.event_status_today);
        }

        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface OnClickCampaignListener {
        void onClickCampaign(ArrayList<Campaign> campaigns, int position);
    }
}
