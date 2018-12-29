package com.candy.android.model.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 8/29/2017.
 */

public class Ranking {
    @SerializedName("datetime")
    @Expose
    private String datetime;
//    @SerializedName("status")
//    @Expose
//    private int status;
    @SerializedName("performers")
    @Expose
    private List<BaseRankTime> performers = null;
    @SerializedName("prevDate")
    @Expose
    private String prevDate;
    @SerializedName("nextDate")
    @Expose
    private String nextDate;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }

    public List<BaseRankTime> getPerformers() {
        return performers;
    }

    public void setPerformers(List<BaseRankTime> performers) {
        this.performers = performers;
    }

    public String getPrevDate() {
        return prevDate;
    }

    public void setPrevDate(String prevDate) {
        this.prevDate = prevDate;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }
}