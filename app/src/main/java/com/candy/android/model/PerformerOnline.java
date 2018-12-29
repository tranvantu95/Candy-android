package com.candy.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by quannt on 10/17/2016.
 * Description: Property of performer online
 */

public class PerformerOnline extends BasePerformer implements Parcelable, Comparable<PerformerOnline> {

    @SerializedName("code")
    private String code;
    @SerializedName("addFavorite")
    private String addFavorite;
    @SerializedName("lastLoginDate")
    private String lastLoginDate;
    @SerializedName("lastActiveDate")
    private String lastActiveDate;

    public PerformerOnline() {
        super();
        code = "";
        addFavorite = "";
        lastLoginDate = "";
        lastActiveDate = "";
    }

    protected PerformerOnline(Parcel in) {
        super(in);
        code = in.readString();
        addFavorite = in.readString();
        lastLoginDate = in.readString();
        lastActiveDate = in.readString();
    }

    public static final Creator<PerformerOnline> CREATOR = new Creator<PerformerOnline>() {
        @Override
        public PerformerOnline createFromParcel(Parcel in) {
            return new PerformerOnline(in);
        }

        @Override
        public PerformerOnline[] newArray(int size) {
            return new PerformerOnline[size];
        }
    };

    public int getCode() {
        try {
            return Integer.valueOf(code);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setCode(int code) {
        this.code = String.valueOf(code);
    }

    public String getAddFavorite() {
        return addFavorite;
    }

    public void setAddFavorite(String addFavorite) {
        this.addFavorite = addFavorite;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public String getLastActiveDate() {
        return lastActiveDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(code);
        parcel.writeString(addFavorite);
        parcel.writeString(lastLoginDate);
        parcel.writeString(lastActiveDate);
    }

    @Override
    public int compareTo(PerformerOnline performerOnline) {

        /**Compare order:   status -> isNew -> count
         *                  status 1-2-3-4-5-0
         *                  isNew: decrease order (1-0)
         *                  count: decrease order
         * */
        int compareStatus = performerOnline.getStatus() == 0 ? 6 : performerOnline.getStatus();
        int status = getStatus() == 0 ? 6 : getStatus();

        if (status < compareStatus) {
            return -1;
        }

        if (status > compareStatus) {
            return 1;
        }

        int lastLoginResult = getLastLoginDate().compareTo(performerOnline.getLastLoginDate());
        if(lastLoginResult > 0){
            return -1;
        }

        if(lastLoginResult < 0){
            return 1;
        }

//        if (getIsNew() > performerOnline.getIsNew()) {
//            return -1;
//        }
//        if (getIsNew() < performerOnline.getIsNew()) {
//            return 1;
//        }
//
//        if (getCount() > performerOnline.getCount()) {
//            return -1;
//        }
//
//        if (getCount() < performerOnline.getCount()) {
//            return 1;
//        }

        return 0;
    }
}
