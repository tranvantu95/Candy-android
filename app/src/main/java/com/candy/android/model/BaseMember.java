package com.candy.android.model;

import com.candy.android.model.member.MemberInfoChange;
import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 10/19/2016.
 * Description: This model contains basic properties of member.
 * Usage:       in PerformerList, PerformerDetail,...
 */

public class BaseMember {
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("point")
    private String point;
    @SerializedName("telauth")
    private String telauth;
    @SerializedName("telauthOk")
    private String telauthOk;
    @SerializedName("freeMail")
    private String freeMail;
    @SerializedName("notOpenCount")
    private String notOpenCount;


    public BaseMember() {
        //Empty constructor
    }

    public BaseMember(MemberInfoChange.Member member) {
        point = member.getPoint();
        notOpenCount = member.getNotOpenCount();
    }

    public int getCode() {
        try {
            return Integer.valueOf(code);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getName() {
        return name;
    }

    public int getPoint() {
        try {
            return Integer.valueOf(point);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getFreeMail() {
        try {
            return Integer.valueOf(freeMail);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getNotOpenCount() {
        try {
            return Integer.valueOf(notOpenCount);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getTelauth() {
        return telauth;
    }

    public String getTelauthOk() {
        return telauthOk;
    }
}
