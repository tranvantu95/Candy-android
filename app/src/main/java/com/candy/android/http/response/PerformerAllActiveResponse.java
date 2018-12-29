package com.candy.android.http.response;

import com.candy.android.model.BaseMember;
import com.candy.android.model.PerformerOnline;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by quannt on 10/19/2016.
 * PerformerAllActive
 */

public class PerformerAllActiveResponse {
    @SerializedName("member")
    private BaseMember member;
    @SerializedName("performers")
    private ArrayList<PerformerOnline> performers;
    @SerializedName("status")
    private Integer status;

    public BaseMember getMember() {
        return member;
    }

    public ArrayList<PerformerOnline> getPerformers() {
        return performers;
    }

    public Integer getStatus() {
        return status;
    }
}
