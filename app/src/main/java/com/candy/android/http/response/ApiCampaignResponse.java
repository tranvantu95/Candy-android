package com.candy.android.http.response;

import com.candy.android.model.BaseMember;
import com.candy.android.model.Campaign;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by quannt on 23/11/2016.
 * Des
 */

public class ApiCampaignResponse extends BaseApiResponse {

    @SerializedName("member")
    private BaseMember member;
    @SerializedName("campaign")
    private ArrayList<Campaign> campaigns;

    public BaseMember getMember() {
        return member;
    }

    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }
}
