package com.candy.android.model;

import com.candy.android.configs.Define;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NamHV on 10/27/2016.
 * Description: Push notification setting
 */

public class MemberNoticeSetting {
    @SerializedName("pushMail")
    private int pushMail;
    @SerializedName("pushLogin")
    private int pushLogin;
    @SerializedName("pushBlog")
    private int pushBlog;
    @SerializedName("pushMaga")
    private int pushMaga;
    @SerializedName("rejectFr")
    private int rejectFr;
    @SerializedName("rejectTo")
    private int rejectTo;
    @SerializedName("mailMaga")
    private int mailMaga;
    @SerializedName("mailLogin")
    private int mailLogin;


    public int getPushMail() {
        return pushMail;
    }

    public int getPushLogin() {
        return pushLogin;
    }

    public int getPushBlog() {
        return pushBlog;
    }

    public int getPushMaga() {
        return pushMaga;
    }

    public int getRejectFr() {
        return rejectFr;
    }

    public int getRejectTo() {
        return rejectTo;
    }

    public int getMailMaga() {
        return mailMaga;
    }

    public int getMailLogin() {
        return mailLogin;
    }

}
