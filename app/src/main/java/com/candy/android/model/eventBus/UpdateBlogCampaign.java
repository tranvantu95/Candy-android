package com.candy.android.model.eventBus;

import com.candy.android.model.Campaign;

import java.util.ArrayList;

/**
 * Created by quannt on 30/11/2016.
 * Des: Event update campaign category into blog list
 */

public class UpdateBlogCampaign extends BaseEvent {

    private ArrayList<Campaign> mCampaigns;

    public UpdateBlogCampaign(ArrayList<Campaign> campaigns) {
        mCampaigns = campaigns;
    }

    public ArrayList<Campaign> getmCampaigns() {
        return mCampaigns;
    }

    public void setmCampaigns(ArrayList<Campaign> mCampaigns) {
        this.mCampaigns = mCampaigns;
    }
}
